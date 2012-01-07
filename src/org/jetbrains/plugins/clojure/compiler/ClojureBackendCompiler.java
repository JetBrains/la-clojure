package org.jetbrains.plugins.clojure.compiler;

import com.intellij.compiler.CompilerConfigurationImpl;
import com.intellij.compiler.OutputParser;
import com.intellij.compiler.impl.javaCompiler.ExternalCompiler;
import com.intellij.compiler.impl.javaCompiler.ModuleChunk;
import com.intellij.compiler.impl.javaCompiler.javac.JavacSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.MockJdkWrapper;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.config.ClojureConfigUtil;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

import java.io.*;
import java.util.*;

/**
 * @author ilyas
 */
public class ClojureBackendCompiler extends ExternalCompiler {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.compiler.ClojureBackendCompiler");

  private final Project myProject;
  private final List<File> myTempFiles = new ArrayList<File>();

  private final static HashSet<FileType> COMPILABLE_FILE_TYPES = new HashSet<FileType>(Arrays.asList(ClojureFileType.CLOJURE_FILE_TYPE));

  public ClojureBackendCompiler(Project project) {
    myProject = project;
  }

  public boolean checkCompiler(CompileScope scope) {
    VirtualFile[] files = scope.getFiles(ClojureFileType.CLOJURE_FILE_TYPE, true);
    if (files.length == 0) return true;

    final ProjectFileIndex index = ProjectRootManager.getInstance(myProject).getFileIndex();
    Set<Module> modules = new HashSet<Module>();
    for (VirtualFile file : files) {
      Module module = index.getModuleForFile(file);
      if (module != null) {
        modules.add(module);
      }
    }

    boolean hasJava = false;
    for (Module module : modules) {
      if (ClojureUtils.isSuitableModule(module)) {
        hasJava = true;
      }
    }
    if (!hasJava) return false; //this compiler work with only Java modules, so we don't need to continue.

    for (Module module : modules) {
      if (!(ClojureConfigUtil.isClojureConfigured(module) && ClojureUtils.isSuitableModule(module))) {
        Messages.showErrorDialog(myProject, ClojureBundle.message("cannot.compile.clojure.files.no.facet", module.getName()), ClojureBundle.message("cannot.compile"));
        return false;
      }
    }

    Set<Module> nojdkModules = new HashSet<Module>();
    for (Module module : scope.getAffectedModules()) {
      if (!(ClojureUtils.isSuitableModule(module))) continue;
      Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
      if (sdk == null || !(sdk.getSdkType() instanceof JavaSdkType)) {
        nojdkModules.add(module);
      }
    }

    if (!nojdkModules.isEmpty()) {
      final Module[] noJdkArray = nojdkModules.toArray(new Module[nojdkModules.size()]);
      if (noJdkArray.length == 1) {
        Messages.showErrorDialog(myProject, ClojureBundle.message("cannot.compile.clojure.files.no.sdk", noJdkArray[0].getName()), ClojureBundle.message("cannot.compile"));
      } else {
        StringBuffer modulesList = new StringBuffer();
        for (int i = 0; i < noJdkArray.length; i++) {
          if (i > 0) modulesList.append(", ");
          modulesList.append(noJdkArray[i].getName());
        }
        Messages.showErrorDialog(myProject, ClojureBundle.message("cannot.compile.clojure.files.no.sdk.mult", modulesList.toString()), ClojureBundle.message("cannot.compile"));
      }
      return false;
    }
    return true;
  }

  @NotNull
  public String getId() {
    return "ClojureCompiler";
  }

  @NotNull
  public String getPresentableName() {
    return ClojureBundle.message("clojure.compiler.name");
  }

  @NotNull
  public Configurable createConfigurable() {
    return null;
  }

  public OutputParser createErrorParser(@NotNull String outputDir, Process process) {
    return new ClojureOutputParser();
  }

  public OutputParser createOutputParser(String outputDir) {
    return new OutputParser() {
      @Override
      public boolean processMessageLine(Callback callback) {
        return super.processMessageLine(callback) || callback.getCurrentLine() != null;
      }
    };
  }

  @NotNull
  public String[] createStartupCommand(final ModuleChunk chunk, final CompileContext context, final String outputPath) throws IOException, IllegalArgumentException {
    final ArrayList<String> commandLine = new ArrayList<String>();
    final Exception[] ex = new Exception[]{null};
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        try {
          createStartupCommandImpl(chunk, commandLine, outputPath, context.getCompileScope());
        }
        catch (IllegalArgumentException e) {
          ex[0] = e;
        }
        catch (IOException e) {
          ex[0] = e;
        }
      }
    });
    if (ex[0] != null) {
      if (ex[0] instanceof IOException) {
        throw (IOException) ex[0];
      } else if (ex[0] instanceof IllegalArgumentException) {
        throw (IllegalArgumentException) ex[0];
      } else {
        LOG.error(ex[0]);
      }
    }
    return commandLine.toArray(new String[commandLine.size()]);
  }

  @NotNull
  @Override
  public Set<FileType> getCompilableFileTypes() {
    return COMPILABLE_FILE_TYPES;
  }

  private void createStartupCommandImpl(ModuleChunk chunk, ArrayList<String> commandLine, String outputPath, CompileScope scope) throws IOException {
    final Sdk jdk = getJdkForStartupCommand(chunk);
    final String versionString = jdk.getVersionString();
    if (versionString == null || "".equals(versionString)) {
      throw new IllegalArgumentException(ClojureBundle.message("javac.error.unknown.jdk.version", jdk.getName()));
    }
    final JavaSdkType sdkType = (JavaSdkType) jdk.getSdkType();

    final String toolsJarPath = sdkType.getToolsPath(jdk);
    if (toolsJarPath == null) {
      throw new IllegalArgumentException(ClojureBundle.message("javac.error.tools.jar.missing", jdk.getName()));
    }

    String javaExecutablePath = sdkType.getVMExecutablePath(jdk);
    commandLine.add(javaExecutablePath);

    // For debug
//    commandLine.add("-Xdebug");
//    commandLine.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=127.0.0.1:5448");


    final StringBuilder classPathBuilder = new StringBuilder();
    classPathBuilder.append(sdkType.getToolsPath(jdk)).append(File.pathSeparator);

    // Add classpath and sources

    for (Module module : chunk.getModules()) {
      if (ClojureUtils.isSuitableModule(module)) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        OrderEntry[] entries = moduleRootManager.getOrderEntries();
        Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
        for (OrderEntry orderEntry : entries) {
          cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.COMPILATION_CLASSES)));

          // Add module sources
          if (orderEntry instanceof ModuleSourceOrderEntry) {
            cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
          }
        }

        for (VirtualFile file : cpVFiles) {
          String path = file.getPath();
          int jarSeparatorIndex = path.indexOf(JarFileSystem.JAR_SEPARATOR);
          if (jarSeparatorIndex > 0) {
            path = path.substring(0, jarSeparatorIndex);
          }
          classPathBuilder.append(path).append(File.pathSeparator);
        }
      }
    }
    classPathBuilder.append(outputPath).append(File.separator);

    commandLine.add("-cp");
    commandLine.add(classPathBuilder.toString());

    //Add REPL class runner
    commandLine.add(ClojureUtils.CLOJURE_MAIN);


    try {
      File fileWithCompileScript = File.createTempFile("clojurekul", ".clj");
      fillFileWithClojureCompilerParams(chunk, fileWithCompileScript, outputPath, scope);

      commandLine.add(fileWithCompileScript.getPath());
    } catch (IOException e) {
      LOG.error(e);
    }
  }


  private static void fillFileWithClojureCompilerParams(ModuleChunk chunk, File fileWithParameters, String outputPath, CompileScope scope)
      throws FileNotFoundException {

    VirtualFile[] files = scope.getFiles(ClojureFileType.CLOJURE_FILE_TYPE, true);
    if (files.length == 0) return;

    PrintStream printer = new PrintStream(new FileOutputStream(fileWithParameters));

    //print output path
    printer.print("(binding [*compile-path* ");
    printer.print("\"" + outputPath + "\"]\n");

    boolean compileSources = (chunk.getSourcesFilter() & ModuleChunk.SOURCES) != 0;
    boolean compileTests = (chunk.getSourcesFilter() & ModuleChunk.TEST_SOURCES) != 0;

    final Module[] modules = chunk.getModules();
    if (modules.length > 0) {
      final Project project = modules[0].getProject();
      ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
      final PsiManager manager = PsiManager.getInstance(project);

//      printNicePrinter(printer);
      for (VirtualFile file : files) {
        Module module = index.getModuleForFile(file);
        ModuleFileIndex moduleFileIndex = ModuleRootManager.getInstance(module).getFileIndex();

        boolean isTest = moduleFileIndex.isInTestSourceContent(file);

        if ((!isTest && compileSources) || (isTest && compileTests)) {
          final PsiFile psiFile = manager.findFile(file);
          if (psiFile != null && (psiFile instanceof ClojureFile)) {
            final ClojureFile clojureFile = (ClojureFile) psiFile;
            final String ns = clojureFile.getNamespace();
            // Compile all compilable files
            // Compile only files with classes!
            if (ns != null && clojureFile.isClassDefiningFile()) {

              printer.print("(try ");
              printCompileFile(printer, ns);
              //(let [st (.getStackTrace e)] (intellij-nice-printer st))
              printer.print("(catch Exception e (. *err* println (str \"comp_err:" + file.getPath() +
                  ":" + ns + "@" + "\" (let [msg (.getMessage e)] msg)  ) ) )");
              printer.print(")");

            }
          }
        }
      }
    }

    printer.print(")");
    printer.close();
  }

  private static void printCompileFile(PrintStream printer, String ns) {
    printer.print("(. *err* println ");
    printer.print("\"compiling:" + ns + "\"");
    printer.print(")\n");


    printer.print("(compile '");
    printer.print(ns);
    printer.print(")\n");

    //Diagnostic log
    printer.print("(. *err* println ");
    printer.print("\"compiled:" + ns + ".class\"");
    printer.print(")\n");
  }

  private static void printNicePrinter(PrintStream printer) {
    printer.println("(defn intellij-nice-printer [arr]\n" +
        "  (reduce (fn [x y] (str (.toString x) \"^^\" y)) (. java.util.Arrays asList arr)))");
  }

  public void compileFinished() {
    FileUtil.asyncDelete(myTempFiles);
  }

  private Sdk getJdkForStartupCommand(final ModuleChunk chunk) {
    final Sdk jdk = chunk.getJdk();
    if (ApplicationManager.getApplication().isUnitTestMode() && JavacSettings.getInstance(myProject).isTestsUseExternalCompiler()) {
      final String jdkHomePath = CompilerConfigurationImpl.getTestsExternalCompilerHome();
      if (jdkHomePath == null) {
        throw new IllegalArgumentException("[TEST-MODE] Cannot determine home directory for JDK to use javac from");
      }
      // when running under Mock JDK use VM executable from the JDK on which the tests run
      return new MockJdkWrapper(jdkHomePath, jdk);
    }
    return jdk;
  }

}

