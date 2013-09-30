package org.jetbrains.plugins.clojure.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.config.ClojureConfigUtil;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

import java.io.File;
import java.util.*;

public class ClojureScriptRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters {
  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.runner.ClojureScriptRunConfiguration");
  private ClojureScriptConfigurationFactory factory;
  private String scriptPath;
  private String workDir;
  private String vmParams;
  private String scriptParams;
  private boolean runInREPL;
  private boolean runMainFunction;
  private final Map<String, String> envs = new com.intellij.util.containers.hash.LinkedHashMap<String, String>();
  public boolean passParentEnv = true;

  //  private static final String JLINE_CONSOLE_RUNNER = "jline.ConsoleRunner";

  public ClojureScriptRunConfiguration(ClojureScriptConfigurationFactory factory, Project project, String name) {
    super(name, new RunConfigurationModule(project), factory);
    this.factory = factory;
  }

  public Collection<Module> getValidModules() {
    Module[] modules = ModuleManager.getInstance(getProject()).getModules();
    ArrayList<Module> res = new ArrayList<Module>();
    for (Module module : modules) {
      res.add(module);
    }
    return res;
  }

  public void setWorkDir(String dir) {
    workDir = dir;
  }

  public String getWorkDir() {
    return workDir;
  }

  public void readExternal(Element element) throws InvalidDataException {
    PathMacroManager.getInstance(getProject()).expandPaths(element);
    super.readExternal(element);
    readModule(element);
    scriptPath = JDOMExternalizer.readString(element, "path");
    vmParams = JDOMExternalizer.readString(element, "vmparams");
    scriptParams = JDOMExternalizer.readString(element, "params");
    workDir = JDOMExternalizer.readString(element, "workDir");
    runInREPL = Boolean.parseBoolean(JDOMExternalizer.readString(element, "repl"));
    runMainFunction = Boolean.parseBoolean(JDOMExternalizer.readString(element, "main"));
    workDir = getWorkDir();

    envs.clear();
    JDOMExternalizer.readMap(element, envs, null, "env");
  }

  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    JDOMExternalizer.write(element, "path", scriptPath);
    JDOMExternalizer.write(element, "vmparams", vmParams);
    JDOMExternalizer.write(element, "params", scriptParams);
    JDOMExternalizer.write(element, "workDir", workDir);
    JDOMExternalizer.write(element, "repl", runInREPL);
    JDOMExternalizer.write(element, "main", runMainFunction);
    JDOMExternalizer.writeMap(element, envs, null, "env");
    PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
  }

  public void setEnvs(@NotNull Map<String, String> envs) {
    this.envs.clear();
    this.envs.putAll(envs);
  }

  @NotNull
  public Map<String, String> getEnvs() {
    return envs;
  }

  protected ModuleBasedConfiguration createInstance() {
    return new ClojureScriptRunConfiguration(factory, getConfigurationModule().getProject(), getName());
  }

  @NotNull
  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new ClojureRunConfigurationEditor();
  }

  private static void configureScriptSystemClassPath(final ClojureConfigUtil.RunConfigurationParameters params, final Module module) throws CantRunException {
    params.setJdk(JavaParameters.getModuleJdk(module));
    params.configureByModule(module, JavaParameters.CLASSES_AND_TESTS);

    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> cpVFiles = new LinkedHashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries) {
      // Add module sources to classpath
      if (orderEntry instanceof ModuleSourceOrderEntry) {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    for (VirtualFile file : cpVFiles) {
      params.getClassPath().add(file.getPath());
    }

    if (!ClojureConfigUtil.isClojureConfigured(module)) {
      params.getClassPath().add(ClojureConfigUtil.CLOJURE_SDK);
      params.setDefaultClojureJarUsed(true);
    }
  }

  private void configureJavaParams(ClojureConfigUtil.RunConfigurationParameters params, Module module) throws CantRunException {

    // Setting up classpath
    configureScriptSystemClassPath(params, module);

    // add user parameters
    params.getVMParametersList().addParametersString(vmParams);

    params.setMainClass(ClojureUtils.CLOJURE_MAIN);
  }

  private void configureScript(ParametersList list) {
    if (runInREPL) list.add("-i");
    list.add(scriptPath);

    if (runInREPL) list.add("-r");
    list.addParametersString(scriptParams);
  }

  private void configureMainFunction(ParametersList list, String namespace) {
    list.add("--main");
    list.add(namespace);
  }

  public Module getModule() {
    return getConfigurationModule().getModule();
  }

  public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
    final Module module = getModule();
    if (module == null) {
      throw new ExecutionException("Module is not specified");
    }

    final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
    final Sdk sdk = rootManager.getSdk();
    if (sdk == null || !(sdk.getSdkType() instanceof JavaSdkType)) {
      throw CantRunException.noJdkForModule(getModule());
    }

    final Project project = module.getProject();
    if (!org.jetbrains.plugins.clojure.config.ClojureConfigUtil.isClojureConfigured(module)) {
      Messages.showErrorDialog(project,
          ClojureBundle.message("error.running.configuration.with.error.error.message", getName(),
              ClojureBundle.message("clojure.lib.is.not.attached")),
          ClojureBundle.message("run.error.message.title"));

      ModulesConfigurator.showDialog(project, module.getName(), ClasspathEditor.NAME);
      return null;
    }

    final ClojureConfigUtil.RunConfigurationParameters params =
        new ClojureConfigUtil.RunConfigurationParameters();

    final String namespace;

    if (runMainFunction) {
      final VirtualFile virtualFile = VfsUtil.findFileByIoFile(new File(scriptPath), true);
      if (virtualFile == null) {
        showCannotDetermineNamespaceError();
        return null;
      }

      final PsiFile file = PsiManager.getInstance(getProject()).findFile(virtualFile);
      if (!(file instanceof ClojureFile)) {
        showCannotDetermineNamespaceError();
        return null;
      }

      final String ns = ((ClojureFile) file).getNamespace();
      if (ns == null) {
        showCannotDetermineNamespaceError();
        return null;
      }

      namespace = ns;
    } else {
      namespace = null;
    }

    final JavaCommandLineState state = new JavaCommandLineState(environment) {
      protected JavaParameters createJavaParameters() throws ExecutionException {
        ProgramParametersUtil.configureConfiguration(params, ClojureScriptRunConfiguration.this);

        configureJavaParams(params, module);

        final ParametersList list = params.getProgramParametersList();

        if (runMainFunction) {
          configureMainFunction(list, namespace);
        } else {
          configureScript(list);
        }

        return params;
      }
    };

    final TextConsoleBuilderImpl builder = new TextConsoleBuilderImpl(project) {
      private final ArrayList<Filter> filters = new ArrayList<Filter>();

      @Override
      public ConsoleView getConsole() {
        final ConsoleViewImpl view = new ConsoleViewImpl(project, false);
        view.setFileType(ClojureFileType.CLOJURE_FILE_TYPE);
        for (Filter filter : filters) {
          view.addMessageFilter(filter);
        }
        return view;
      }

      @Override
      public void addFilter(Filter filter) {
        filters.add(filter);
      }
    };

    state.setConsoleBuilder(builder);

    if (params.isDefaultClojureJarUsed()) {
      ClojureConfigUtil.warningDefaultClojureJar(module);
    }
    return state;

  }

  private void showCannotDetermineNamespaceError() {
    Messages.showErrorDialog(getProject(),
        ClojureBundle.message("error.running.configuration.with.error.error.message", getName(),
            ClojureBundle.message("cannot.determine.namespace", scriptPath)),
        ClojureBundle.message("run.error.message.title"));
  }

  public void setScriptPath(String path) {
    this.scriptPath = path;
  }

  public String getScriptPath() {
    return scriptPath;
  }

  public String getVmParams() {
    return vmParams;
  }

  public String getScriptParams() {
    return scriptParams;
  }

  public void setVmParams(String params) {
    vmParams = params;
  }

  public void setRunInREPL(boolean isEnabled) {
    runInREPL = isEnabled;
  }

  public void setScriptParams(String params) {
    scriptParams = params;
  }

  public boolean getRunInREPL() {
    return runInREPL;
  }

  public boolean getRunMainFunction() {
    return runMainFunction;
  }

  public void setRunMainFunction(boolean b) {
    runMainFunction = b;
  }

  public void setPassParentEnvs(boolean passParentEnvs) {
    this.passParentEnv = passParentEnvs;
  }

  public boolean isPassParentEnvs() {
    return passParentEnv;
  }

  public void setProgramParameters(@Nullable String s) {
    LOG.error("Don't add program parameters to Clojure script run configuration. Use Script parameters instead");
  }

  @Nullable
  public String getProgramParameters() {
    return null;
  }

  public void setWorkingDirectory(@Nullable String value) {
    workDir = value;
  }

  public String getWorkingDirectory() {
    return workDir;
  }

}
