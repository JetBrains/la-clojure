package org.jetbrains.plugins.clojure.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.runners.ExecutionEnvironment;
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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.runner.console.ClojureConsoleViewImpl;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 7, 2009
 * Time: 6:04:34 PM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ClojureScriptRunConfiguration extends ModuleBasedConfiguration {
  private ClojureScriptConfigurationFactory factory;
  private String scriptPath;
  private String workDir;
  private String vmParams;
  private String scriptParams;
  private boolean runInREPL;

  @NonNls
  private static final String CLOJURE_MAIN = "clojure.main";
  private static final String CLOJURE_REPL = "clojure.lang.Repl";
  private static final String JLINE_CONSOLE_RUNNER = "jline.ConsoleRunner";


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

  public String getAbsoluteWorkDir() {
    if (!new File(workDir).isAbsolute()) {
      return new File(getProject().getLocation(), workDir).getAbsolutePath();
    }
    return workDir;
  }

  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    readModule(element);
    scriptPath = JDOMExternalizer.readString(element, "path");
    vmParams = JDOMExternalizer.readString(element, "vmparams");
    scriptParams = JDOMExternalizer.readString(element, "params");
    workDir = JDOMExternalizer.readString(element, "workDir");
    runInREPL = Boolean.parseBoolean(JDOMExternalizer.readString(element, "repl"));
    workDir = getWorkDir();
  }

  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    JDOMExternalizer.write(element, "path", scriptPath);
    JDOMExternalizer.write(element, "vmparams", vmParams);
    JDOMExternalizer.write(element, "params", scriptParams);
    JDOMExternalizer.write(element, "workDir", workDir);
    JDOMExternalizer.write(element, "repl", runInREPL);
  }

  protected ModuleBasedConfiguration createInstance() {
    return new ClojureScriptRunConfiguration(factory, getConfigurationModule().getProject(), getName());
  }

  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new ClojureRunConfigurationEditor();
  }

  public static void configureScriptSystemClassPath(final JavaParameters params, final Module module) throws CantRunException {
    params.configureByModule(module, JavaParameters.JDK_ONLY);
    //params.getClassPath().add(CLOJURE_SDK);
    params.configureByModule(module, JavaParameters.JDK_AND_CLASSES);

    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries) {
      // Add module sources to classpath
      if (orderEntry instanceof ModuleSourceOrderEntry) {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    for (VirtualFile file : cpVFiles) {
      params.getClassPath().add(file.getPath());
    }

//    params.getClassPath().add("/home/ilya/work/clojure-plugin/lib/jline.jar");
  }

  private void configureJavaParams(JavaParameters params, Module module) throws CantRunException {

    // Setting up classpath
    configureScriptSystemClassPath(params, module);

    //Setworking dir
    params.setWorkingDirectory(getAbsoluteWorkDir());

    // add user parameters
    params.getVMParametersList().addParametersString(vmParams);

    if (runInREPL) {
      params.setMainClass(CLOJURE_REPL);
    } else {
      params.setMainClass(CLOJURE_MAIN);
    }
  }

  private boolean isJarFromJRE(String path, Module module) {
    if (path == null) return false;
    OrderEntry[] entries = ModuleRootManager.getInstance(module).getOrderEntries();
    for (OrderEntry entry : entries) {
      if (entry instanceof JdkOrderEntry) {
        JdkOrderEntry jdkEntry = (JdkOrderEntry) entry;
        for (VirtualFile file : jdkEntry.getFiles(OrderRootType.CLASSES)) {
          if (file.getPresentableUrl().equals(path)) return true;
        }
      }
    }
    return false;
  }

  public StringBuffer getClearClassPathString(JavaParameters params, final Module module) {
    List<String> list = params.getClassPath().getPathList();
    Sdk jdk = params.getJdk();
    StringBuffer buffer = new StringBuffer();
    if (jdk != null) {
      for (String libPath : list) {
        if (!isJarFromJRE(libPath, module) /*&& !isJarFromClojureLib(libPath, module)*/) {
          buffer.append(libPath).append(File.pathSeparator);
        }
      }
    }
    //buffer.append(CLOJURE_SDK);
    return buffer;
  }

  private void configureScript(JavaParameters params) {
    // add script
    params.getProgramParametersList().add(scriptPath);

    // add script parameters
    params.getProgramParametersList().addParametersString(scriptParams);
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

      ModulesConfigurator.showDialog(project, module.getName(), ClasspathEditor.NAME, false);
      return null;
    }

    final JavaCommandLineState state = new JavaCommandLineState(environment) {
      protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters params = new JavaParameters();
        configureJavaParams(params, module);
        configureScript(params);
        return params;
      }
    };

    final TextConsoleBuilderImpl builder = new TextConsoleBuilderImpl(project) {
      private final ArrayList<Filter> filters = new ArrayList<Filter>();

      @Override
      public ConsoleView getConsole() {
        final ClojureConsoleViewImpl view = new ClojureConsoleViewImpl(project);
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
    return state;

  }

  @Nullable
  private VirtualFile findScriptFile() {
    return VirtualFileManager.getInstance().findFileByUrl("file://" + scriptPath);
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
}
