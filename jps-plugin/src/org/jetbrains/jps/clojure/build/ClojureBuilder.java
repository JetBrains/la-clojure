package org.jetbrains.jps.clojure.build;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.builders.storage.SourceToOutputMapping;
import org.jetbrains.jps.clojure.model.JpsClojureCompilerSettingsExtension;
import org.jetbrains.jps.clojure.model.JpsClojureExtensionService;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.java.JpsJavaModuleType;
import org.jetbrains.jps.model.java.JpsJavaSdkType;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.service.SharedThreadPool;

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author nik
 * @since 02.11.12
 */
public class ClojureBuilder extends ModuleLevelBuilder {
  public static final String COMPILER_NAME = "Clojure Compiler";
  public static final String CLOJURE_MAIN = "clojure.main";

  public static final String COMPILING_PREFIX = "[compiling]:";
  public static final String COMPILED_PREFIX = "[compiled]:";
  public static final String ERROR_PREFIX = "[error]:";
  public static final String WRITING_PREFIX = "[writing]:";

  public ClojureBuilder() {
    super(BuilderCategory.TRANSLATOR);
  }

  @Override
  public ExitCode build(final CompileContext context, ModuleChunk chunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder) throws ProjectBuildException, IOException {
    JpsProject project = context.getProjectDescriptor().getProject();
    JpsClojureCompilerSettingsExtension extension = JpsClojureExtensionService.getExtension(project);
    if (extension != null && !extension.isCompileClojure()) return ExitCode.NOTHING_DONE;

    final List<File> toCompile = new ArrayList<File>();
    final HashMap<File, String> toCompileNamespace = new HashMap<File, String>();
    List<JpsModule> javaModules = new ArrayList<JpsModule>();
    for (JpsModule module : chunk.getModules()) {
      if (module.getModuleType().equals(JpsJavaModuleType.INSTANCE)) {
        javaModules.add(module);
      }
    }
    for (JpsModule module : javaModules) {
      for (final JpsModuleSourceRoot root : module.getSourceRoots()) {
        FileUtil.processFilesRecursively(root.getFile(), new Processor<File>() {
          public boolean process(File file) {
            if (file.getName().endsWith(".clj")) {
              toCompile.add(file);
              String filePath = file.getAbsolutePath();
              toCompileNamespace.put(file, filePath.substring(root.getFile().getAbsolutePath().length() + 1, filePath.length() - 4).
                  replace(File.separator, "."));
            }
            return true;
          }
        });
      }
    }

    if (toCompile.isEmpty()) return ExitCode.NOTHING_DONE;
/*
    dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
      public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
        if (file.getName().endsWith(".clj")) {
          toCompile.add(file);
        }
        return true;
      }
    });
*/

    JpsSdk<JpsDummyElement> sdk = chunk.representativeTarget().getModule().getSdk(JpsJavaSdkType.INSTANCE);
    if (sdk == null) {
      context.processMessage(new CompilerMessage(COMPILER_NAME, BuildMessage.Kind.ERROR, "JDK is not specified"));
      return ExitCode.ABORT;
    }

    String javaExecutable = JpsJavaSdkType.getJavaExecutable(sdk);

    List<String> classpath = new ArrayList<String>();
    for (File root : JpsJavaExtensionService.getInstance().enumerateDependencies(javaModules).classes().getRoots()) {
      classpath.add(root.getAbsolutePath());
    }
    for (JpsModule module : javaModules) {
      for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
        classpath.add(sourceRoot.getFile().getAbsolutePath());
      }
    }

    List<String> vmParams = new ArrayList<String>();
    List<String> programParams = new ArrayList<String>();
    File outputDir = chunk.representativeTarget().getOutputDir();
    File fileWithCompileScript = File.createTempFile("clojurekul", ".clj");
    fillFileWithClojureCompilerParams(toCompile, toCompileNamespace, fileWithCompileScript, outputDir);
    programParams.add(fileWithCompileScript.getAbsolutePath());
    List<String> commandLine =
        ExternalProcessUtil.buildJavaCommandLine(javaExecutable, CLOJURE_MAIN, Collections.<String>emptyList(), classpath, vmParams, programParams);

    Process process = Runtime.getRuntime().exec(ArrayUtil.toStringArray(commandLine));

    BaseOSProcessHandler handler = new BaseOSProcessHandler(process, null, null) {
      @Override
      protected Future<?> executeOnPooledThread(Runnable task) {
        return SharedThreadPool.getInstance().executeOnPooledThread(task);
      }
    };

    final SourceToOutputMapping sourceToOutputMap = context.getProjectDescriptor().dataManager.getSourceToOutputMap(chunk.representativeTarget());

    final HashSet<String> outputs = new HashSet<String>();

    handler.addProcessListener(new ProcessAdapter() {
      @Override
      public void onTextAvailable(ProcessEvent event, Key outputType) {
        if (outputType != ProcessOutputTypes.STDERR) return;
        String text = event.getText().trim();
        context.processMessage(new ProgressMessage(text));
        if (text.startsWith(ERROR_PREFIX)) {
          context.processMessage(new CompilerMessage("Clojure", BuildMessage.Kind.ERROR, text)); //todo: parse with source and line number
        } else if (text.startsWith(COMPILING_PREFIX)) {
          //we don't need to do anything
        } else if (text.startsWith(WRITING_PREFIX)) {
          outputs.add(text.substring(WRITING_PREFIX.length()));
        } else if (text.startsWith(COMPILED_PREFIX)) {
          for (String output : outputs) {
            try {
              sourceToOutputMap.appendOutput(text.substring(COMPILED_PREFIX.length()), output);
            } catch (IOException e) {
              context.processMessage(new BuildMessage(e.getMessage(), BuildMessage.Kind.ERROR) {});
            }
          }
          outputs.clear();
        }
      }
    });

    handler.startNotify();
    handler.waitFor();

    return ExitCode.OK;
  }

  private void fillFileWithClojureCompilerParams(List<File> toCompile, HashMap<File, String> toCompileNamespace,
                                                 File fileWithCompileScript, File outputDir) throws FileNotFoundException {
    PrintStream printer = new PrintStream(new FileOutputStream(fileWithCompileScript));

    printer.print("(import (java.io File))\n" +
        "(import (java.util HashSet))\n");

    //print output path
    printer.print("(binding [*compile-path* ");
    String outputDirPath = outputDir.getAbsolutePath().replace("\\", "\\\\");
    printer.print("\"" + outputDirPath + "\" *compile-files* true]\n");

    for (File file : toCompile) {
      //collecting current outputs in output directory
      printer.print(
              "(def outputDir \"" + outputDirPath + "\")\n" +
              "(def output (new HashSet))\n" +
              "(def outputFile (new File outputDir))\n" +
              "(defn scanOutput [#^HashSet out #^File file]\n" +
              "  (if (.isDirectory file)\n" +
              "    (doseq [i (.listFiles file)] (scanOutput out i))\n" +
              "    (.add out (.getAbsolutePath file))))\n" +
              "\n" +
              "(scanOutput output outputFile)\n");

      printer.print("(try ");
      String absolutePath = file.getAbsolutePath().replace("\\", "\\\\");
      printer.print("(. *err* println ");
      printer.print("\"" + COMPILING_PREFIX + absolutePath + "\"");
      printer.print(")\n");

      printer.print("(load-file \"");
      printer.print(absolutePath);
      printer.print("\")\n");

      printer.print("(catch Exception e (. *err* println (str \"" + ERROR_PREFIX + absolutePath +
          "@" + "\" (let [msg (.getMessage e)] msg)  ) ) )");
      printer.print(")\n");

      //we need to compile namespace init class, otherwise we will get CNFE on Runtime
      String namespace = toCompileNamespace.get(file);
      if (namespace != null) {
        printer.print("(try ");
        printer.print("(compile \'");
        printer.print(namespace);
        printer.print(")\n");
        printer.print("(catch Exception e ())"); //all compile error should be found in file compilation
        printer.print(")\n");
      }

      //let's print information about all new created files in output directory
      printer.print("(defn printNewFiles [#^File file]\n" +
          "  (if (.isDirectory file)\n" +
          "    (doseq [i (.listFiles file)] (printNewFiles i))\n" +
          "    (if (not (.contains output (.getAbsolutePath file))) (. *err* println " +
          "(.concat \"" + WRITING_PREFIX + "\" (.getAbsolutePath file))))))\n" +
          "(printNewFiles outputFile)\n" +
          "(.clear output)");

      printer.print("(. *err* println ");
      printer.print("\"" + COMPILED_PREFIX + absolutePath + "\"");
      printer.print(")\n");
    }

    printer.print(")");
    printer.close();
  }

  @Override
  public String getName() {
    return "Clojure Compiler";
  }

  @Override
  public String getDescription() {
    return COMPILER_NAME;
  }
}
