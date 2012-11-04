package org.jetbrains.jps.clojure.build;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author nik
 * @since 02.11.12
 */
public class ClojureBuilder extends ModuleLevelBuilder {
  public static final String COMPILER_NAME = "Clojure Compiler";
  public static final String CLOJURE_MAIN = "clojure.main";

  public ClojureBuilder() {
    super(BuilderCategory.TRANSLATOR);
  }

  @Override
  public ExitCode build(final CompileContext context, ModuleChunk chunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder) throws ProjectBuildException, IOException {
    JpsProject project = context.getProjectDescriptor().getProject();
    JpsClojureCompilerSettingsExtension extension = JpsClojureExtensionService.getExtension(project);
    if (extension != null && !extension.isCompileClojure()) return ExitCode.NOTHING_DONE;

    final List<File> toCompile = new ArrayList<File>();
    final List<String> toCompileNamespace = new ArrayList<String>();
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
              toCompileNamespace.add(filePath.substring(root.getFile().getAbsolutePath().length() + 1, filePath.length() - 4).
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

    handler.addProcessListener(new ProcessAdapter() {
      @Override
      public void onTextAvailable(ProcessEvent event, Key outputType) {
        context.processMessage(new ProgressMessage(event.getText()));
      }
    });

    handler.startNotify();
    handler.waitFor();

    return ExitCode.OK;
  }

  private void fillFileWithClojureCompilerParams(List<File> toCompile, List<String> toCompileNamespace,
                                                 File fileWithCompileScript, File outputDir) throws FileNotFoundException {
    PrintStream printer = new PrintStream(new FileOutputStream(fileWithCompileScript));

    //print output path
    printer.print("(binding [*compile-path* ");
    printer.print("\"" + outputDir.getAbsolutePath().replace("\\", "\\\\") + "\" *compile-files* true]\n");

    for (File file : toCompile) {

      printer.print("(try ");
      String absolutePath = file.getAbsolutePath().replace("\\", "\\\\");
      printer.print("(. *err* println ");
      printer.print("\"compiling:" + absolutePath + "\"");
      printer.print(")\n");


      printer.print("(load-file \"");
      printer.print(absolutePath);
      printer.print("\")\n");

      //Diagnostic log
      printer.print("(. *err* println ");
      printer.print("\"compiled:" + absolutePath + ".class\"");
      printer.print(")\n");
      printer.print("(catch Exception e (. *err* println (str \"comp_err:" + absolutePath +
          "@" + "\" (let [msg (.getMessage e)] msg)  ) ) )");
      printer.print(")");

    }

    //let's compile init classes for namespaces
    for (String namespace : toCompileNamespace) {
      printer.print("(try ");
      printer.print("(. *err* println ");
      printer.print("\"compiling namespace:" + namespace + "\"");
      printer.print(")\n");


      printer.print("(compile \'");
      printer.print(namespace);
      printer.print(")\n");

      //Diagnostic log
      printer.print("(. *err* println ");
      printer.print("\"compiled namespace:" + namespace + ".class\"");
      printer.print(")\n");
      printer.print("(catch Exception e ())"); //all compile error should be found in file compilation
      printer.print(")");
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
