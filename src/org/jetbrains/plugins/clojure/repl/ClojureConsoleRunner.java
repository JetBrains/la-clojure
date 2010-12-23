package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.CommandLineArgumentsProvider;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.execution.runners.ConsoleExecuteActionHandler;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.config.ClojureConfigUtil;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ilyas
 */
public class ClojureConsoleRunner extends AbstractConsoleRunnerWithHistory {

  public static final String REPL_TITLE = ClojureBundle.message("repl.toolWindowName");


  private Module myModule;

  public ClojureConsoleRunner(@NotNull Module module,
                              @NotNull String consoleTitle,
                              @NotNull CommandLineArgumentsProvider provider,
                              @Nullable String workingDir) {
    super(module.getProject(), consoleTitle, provider, workingDir);
    myModule = module;
  }

  public static void run(@NotNull final Module module,
                         final String workingDir,
                         final String... statements2execute) throws CantRunException {
    final ArrayList<String> args = createRuntimeArgs(module, workingDir);

    final CommandLineArgumentsProvider provider = new CommandLineArgumentsProvider() {
      public String[] getArguments() {
        return args.toArray(new String[args.size()]);
      }

      public boolean passParentEnvs() {
        return false;
      }

      public Map<String, String> getAdditionalEnvs() {
        // todo add extra env. variables
        return new HashMap<String, String>();
      }
    };

    final Project project = module.getProject();
    final ClojureConsoleRunner runner = new ClojureConsoleRunner(module, REPL_TITLE, provider, workingDir);

    try {
      runner.initAndRun(statements2execute);
    } catch (ExecutionException e) {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), REPL_TITLE, null);
    }
  }

  public void initAndRun(final String... statements2execute) throws ExecutionException {
    super.initAndRun();

    final LanguageConsoleImpl console = getConsoleView().getConsole();
    for (String statement : statements2execute) {
      final String st = statement + "\n";
      ClojureConsoleHighlightingUtil.processOutput(console, st, ProcessOutputTypes.SYSTEM);
      final ConsoleExecuteActionHandler actionHandler = getConsoleExecuteActionHandler();
      actionHandler.processLine(st);
    }

  }

  @Override
  protected LanguageConsoleViewImpl createConsoleView() {
    return new ClojureConsoleView(getProject(), getConsoleTitle());
  }

  @Override
  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException {
    final ArrayList<String> cmd = createRuntimeArgs(myModule, getWorkingDir());

    Process process = null;
    try {
      process = Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]), new String[0], new File(getWorkingDir()));
    } catch (IOException e) {
      ExecutionHelper.showErrors(getProject(), Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;
  }

  private static ArrayList<String> createRuntimeArgs(Module module, String workingDir) throws CantRunException {
    final JavaParameters params = new JavaParameters();
    params.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
    // To avoid NCDFE while starting REPL

    final boolean sdkConfigured = ClojureConfigUtil.isClojureConfigured(module);
    if (!sdkConfigured) {
      final String jarPath = ClojureConfigUtil.CLOJURE_SDK;
      assert jarPath != null;
      params.getClassPath().add(jarPath);
    }

    Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    for (OrderEntry orderEntry : entries) {
      // Add module sources to classpath
      if (orderEntry instanceof ModuleSourceOrderEntry) {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    for (VirtualFile file : cpVFiles) {
      params.getClassPath().add(file.getPath());
    }

    params.setMainClass(ClojureUtils.CLOJURE_MAIN);
    params.setWorkingDirectory(new File(workingDir));

    final GeneralCommandLine line = CommandLineBuilder.createFromJavaParameters(params, PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()), true);

    final Sdk sdk = params.getJdk();
    assert sdk != null;
    final SdkType type = sdk.getSdkType();
    final String executablePath = ((JavaSdkType) type).getVMExecutablePath(sdk);

    final ArrayList<String> cmd = new ArrayList<String>();
    cmd.add(executablePath);
    cmd.addAll(line.getParametersList().getList());

    if (!sdkConfigured) {
      ClojureConfigUtil.warningDefaultClojureJar(module);
    }
    return cmd;
  }


  @Override
  protected OSProcessHandler createProcessHandler(Process process, String commandLine) {
    final LanguageConsoleImpl console = getConsoleView().getConsole();
    return new ClojureConsoleProcessHandler(process, commandLine, console);
  }

  @NotNull
  @Override
  protected ConsoleExecuteActionHandler createConsoleExecuteActionHandler() {
    return new ClojureConsoleExecuteActionHandler(getConsoleView(), getProcessHandler(), getProject());
  }

}
