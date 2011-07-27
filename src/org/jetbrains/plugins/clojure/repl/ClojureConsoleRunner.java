package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
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
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.config.ClojureConfigUtil;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ilyas
 */
public class ClojureConsoleRunner {

  public static final String REPL_TITLE = ClojureBundle.message("repl.toolWindowName");
  public static final String EXECUTE_ACTION_IMMEDIATELY_ID = "Clojure.Console.Execute.Immediately";
  public static final String EXECUTE_ACTION_ID = "Clojure.Console.Execute";


  private final Module myModule;
  private final Project myProject;
  private final String myConsoleTitle;
  private final CommandLineArgumentsProvider myProvider;
  private final String myWorkingDir;
  private final ConsoleHistoryModel myHistory;

  private ClojureConsoleView myConsoleView;
  private ProcessHandler myProcessHandler;

  private ClojureConsoleExecuteActionHandler myConsoleExecuteActionHandler;
  private AnAction myRunAction;


  public ClojureConsoleRunner(@NotNull Module module,
                              @NotNull String consoleTitle,
                              @NotNull CommandLineArgumentsProvider provider,
                              @Nullable String workingDir) {
    myModule = module;
    myProject = module.getProject();
    myConsoleTitle = consoleTitle;
    myProvider = provider;
    myWorkingDir = workingDir;
    myHistory = new ConsoleHistoryModel();
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
    // Create Server process
    final Process process = createProcess(myProvider);
    // !!! do not change order!!!
    myConsoleView = createConsoleView();
    myProcessHandler = new ClojureConsoleProcessHandler(process, myProvider.getCommandLineString(), getLanguageConsole());
    myConsoleExecuteActionHandler = new ClojureConsoleExecuteActionHandler(getProcessHandler(), getProject(), false);
    getLanguageConsole().setExecuteHandler(myConsoleExecuteActionHandler);

    // Init a console view
    ProcessTerminatedListener.attach(myProcessHandler);

    myProcessHandler.addProcessListener(new ProcessAdapter() {
      @Override
      public void processTerminated(ProcessEvent event) {
        myRunAction.getTemplatePresentation().setEnabled(false);
        myConsoleView.getConsole().setPrompt("");
        myConsoleView.getConsole().getConsoleEditor().setRendererMode(true);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          public void run() {
            myConsoleView.getConsole().getConsoleEditor().getComponent().updateUI();
          }
        });
      }
    });

    // Attach a console view to the process
    myConsoleView.attachToProcess(myProcessHandler);

    // Runner creating
    final Executor defaultExecutor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
    final DefaultActionGroup toolbarActions = new DefaultActionGroup();
    final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);

    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(actionToolbar.getComponent(), BorderLayout.WEST);
    panel.add(myConsoleView.getComponent(), BorderLayout.CENTER);

    final RunContentDescriptor myDescriptor =
        new RunContentDescriptor(myConsoleView, myProcessHandler, panel, myConsoleTitle);

    // tool bar actions
    final AnAction[] actions = fillToolBarActions(toolbarActions, defaultExecutor, myDescriptor);
    registerActionShortcuts(actions, getLanguageConsole().getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    // enter action
    createAndRegisterEnterAction(panel);

    // Show in run tool window
    ExecutionManager.getInstance(myProject).getContentManager().showRunContent(defaultExecutor, myDescriptor);

    // Request focus
    final ToolWindow window = ToolWindowManager.getInstance(myProject).getToolWindow(defaultExecutor.getId());
    window.activate(new Runnable() {
      public void run() {
        IdeFocusManager.getInstance(myProject).requestFocus(getLanguageConsole().getCurrentEditor().getContentComponent(), true);
      }
    });

    // Run
    myProcessHandler.startNotify();


    final ClojureConsole console = getConsoleView().getConsole();
    for (String statement : statements2execute) {
      final String st = statement + "\n";
      ClojureConsoleHighlightingUtil.processOutput(console, st, ProcessOutputTypes.SYSTEM);
      final ClojureConsoleExecuteActionHandler actionHandler = getConsoleExecuteActionHandler();
      actionHandler.processLine(st);
    }

  }

  private void createAndRegisterEnterAction(JPanel panel) {
    final AnAction enterAction = new ClojureConsoleEnterAction(getLanguageConsole(), getProcessHandler(), getConsoleExecuteActionHandler());
    enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(), getLanguageConsole().getConsoleEditor().getComponent());
    enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(), panel);
  }

  private static void registerActionShortcuts(final AnAction[] actions, final JComponent component) {
    for (AnAction action : actions) {
      if (action.getShortcutSet() != null) {
        action.registerCustomShortcutSet(action.getShortcutSet(), component);
      }
    }
  }


  protected AnAction[] fillToolBarActions(final DefaultActionGroup toolbarActions,
                                          final Executor defaultExecutor,
                                          final RunContentDescriptor myDescriptor) {

    ArrayList<AnAction> actionList = new ArrayList<AnAction>();

    //stop
    final AnAction stopAction = createStopAction();
    actionList.add(stopAction);

    //close
    final AnAction closeAction = createCloseAction(defaultExecutor, myDescriptor);
    actionList.add(closeAction);

    // run and history actions
    ArrayList<AnAction> executionActions = createConsoleExecActions(getLanguageConsole(),
        myProcessHandler, myConsoleExecuteActionHandler, getHistoryModel());
    myRunAction = executionActions.get(0);
    actionList.addAll(executionActions);

    // help action
    actionList.add(CommonActionsManager.getInstance().createHelpAction("interactive_console"));

    AnAction[] actions = actionList.toArray(new AnAction[actionList.size()]);
    toolbarActions.addAll(actions);
    return actions;
  }

  public static ArrayList<AnAction> createConsoleExecActions(final ClojureConsole languageConsole,
                                                             final ProcessHandler processHandler,
                                                             final ClojureConsoleExecuteActionHandler consoleExecuteActionHandler,
                                                             final ConsoleHistoryModel historyModel) {

    final AnAction runImmediatelyAction = new ClojureExecuteImmediatelyAction(languageConsole, processHandler, consoleExecuteActionHandler);

    final ConsoleHistoryController historyController = new ConsoleHistoryController("clojure", null, languageConsole, historyModel);
        historyController.install();

    final AnAction upAction = historyController.getHistoryPrev();
    final AnAction downAction = historyController.getHistoryNext();

    final ArrayList<AnAction> list = new ArrayList<AnAction>();
    list.add(runImmediatelyAction);
    list.add(downAction);
    list.add(upAction);
//    list.add(enterAction);
    return list;
  }


  protected AnAction createCloseAction(final Executor defaultExecutor, final RunContentDescriptor myDescriptor) {
    return new CloseAction(defaultExecutor, myDescriptor, myProject);
  }

  protected AnAction createStopAction() {
    return ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM);
  }


  protected ClojureConsoleView createConsoleView() {
    return new ClojureConsoleView(getProject(), getConsoleTitle(), getHistoryModel(), getConsoleExecuteActionHandler());
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

    private GeneralCommandLine createCommandLine(Module module, String workingDir) throws CantRunException {
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

    //final ArrayList<String> cmd = new ArrayList<String>();
    //cmd.add(executablePath);

   // cmd.addAll(line.getParametersList().getList());

    Map envParams = new HashMap();
    envParams.putAll(System.getenv());

    line.setEnvParams(envParams);

    if (!sdkConfigured) {
      ClojureConfigUtil.warningDefaultClojureJar(module);
    }
    return line;
  }

  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException {

    final GeneralCommandLine cmdline = createCommandLine(myModule, getWorkingDir());

    Process process = null;
    try {
      process = cmdline.createProcess();
    } catch (Exception e) {
      ExecutionHelper.showErrors(getProject(), Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;

  }


  /*
  A bunch of getters
   */
  public Project getProject() {
    return myProject;
  }

  public String getConsoleTitle() {
    return myConsoleTitle;
  }

  public ClojureConsole getLanguageConsole() {
    return myConsoleView.getConsole();
  }

  public ClojureConsoleView getConsoleView() {
    return myConsoleView;
  }

  public ProcessHandler getProcessHandler() {
    return myProcessHandler;
  }

  public ClojureConsoleExecuteActionHandler getConsoleExecuteActionHandler() {
    return myConsoleExecuteActionHandler;
  }

  public ConsoleHistoryModel getHistoryModel() {
    return myHistory;
  }

  public String getWorkingDir() {
    return myWorkingDir;
  }


}
