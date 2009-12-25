/*
 * Copyright 2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.CantRunException;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.PopupHandler;
import com.intellij.util.Function;
import com.intellij.util.PathsList;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.settings.ClojureProjectSettings;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.settings.ClojureApplicationSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.*;
import java.util.List;

/**
 * @author Kurt Christensen, ilyas
 */

public class ReplPanel extends JPanel implements Disposable {

  public static final String REPL_TOOLWINDOW_PLACE = "REPL.ToolWindow";
  public static final String REPL_TOOLWINDOW_POPUP_PLACE = "REPL.ToolWindow.Popup";

  private static final String CLOJURE_REPL_ACTION_GROUP = "Clojure.REPL.PanelGroup";

  private Project myProject;
  private Repl myRepl;

  public ReplPanel(@NotNull final Project project, @NotNull final Module module) throws IOException, ConfigurationException, CantRunException {
    setLayout(new BorderLayout());

    myProject = project;
    myRepl = new Repl(module);

    final ActionGroup actions = getActions();

    final JPanel toolbarPanel = new JPanel(new GridLayout());
    toolbarPanel.add(ActionManager.getInstance().createActionToolbar(REPL_TOOLWINDOW_PLACE, actions, false).getComponent());

    add(toolbarPanel, BorderLayout.WEST);
    add(myRepl.getView().getComponent(), BorderLayout.CENTER);

    Disposer.register(this, myRepl);
  }

  private ActionGroup getActions() {
    return (ActionGroup) ActionManager.getInstance().getAction(CLOJURE_REPL_ACTION_GROUP);
  }

  public String writeToCurrentRepl(String s) {
    return writeToCurrentRepl(s, true);
  }

  public String writeToCurrentRepl(String input, boolean requestFocus) {
    if (myRepl != null) {
      final PipedWriter pipeOut;
      PipedReader pipeIn = null;
      try {
        if (requestFocus) requestFocus();

        pipeOut = new PipedWriter();
        pipeIn = new PipedReader(pipeOut);
        BufferedReader in = new BufferedReader(pipeIn);

        ProcessListener processListener = new ProcessAdapter() {
          @Override
          public void onTextAvailable(ProcessEvent event, Key outputType) {
            try {
              pipeOut.write(event.getText());
              pipeOut.flush();
              pipeOut.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        };
        myRepl.processHandler.addProcessListener(processListener);
        final ConsoleView consoleView = myRepl.view;
        if (consoleView instanceof ConsoleViewImpl) {
          final ConsoleViewImpl cView = (ConsoleViewImpl) consoleView;
          final List<String> oldHistory = cView.getHistory();
          final ArrayList<String> newHistory = new ArrayList<String>(oldHistory.size() + 1);
          newHistory.addAll(oldHistory);
          newHistory.add(input);
          cView.importHistory(newHistory);
        }

        consoleView.print(input + "\r\n", ConsoleViewContentType.USER_INPUT);

        StringBuffer buf = new StringBuffer();
        //if (pipeIn.ready()) {
        String str;
        while ((str = in.readLine()) != null) {
          buf.append(str);
        }
        //}
        myRepl.processHandler.removeProcessListener(processListener);

        return buf.toString();

      } catch (IOException e) {
        e.printStackTrace();
        return null;
      } finally {
        if (pipeIn != null) {
          try {
            pipeIn.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  private static String getClassPath(Module module) {
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries) {
      // Add module sources to classpath
      cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.CLASSES_AND_OUTPUT)));
      if (orderEntry instanceof ModuleSourceOrderEntry) {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    final List<String> paths = ContainerUtil.map(cpVFiles, new Function<VirtualFile, String>() {
      public String fun(VirtualFile virtualFile) {
        return virtualFile.getPath();
      }
    });

    final PathsList list = new PathsList();
    list.addAll(paths);

    return list.getPathsString();
  }

  public void dispose() {
    myProject = null;
    myRepl = null;
  }

  private class Repl implements Disposable {
    public ConsoleView view;
    private ProcessHandler processHandler;

    public Repl(Module module) throws IOException, ConfigurationException, CantRunException {
      final TextConsoleBuilderImpl builder = new TextConsoleBuilderImpl(myProject) {
        private final ArrayList<Filter> filters = new ArrayList<Filter>();

        @Override
        public ConsoleView getConsole() {
          final ConsoleViewImpl view = new ConsoleViewImpl(myProject, false);
          view.setFileType(ClojureFileType.CLOJURE_FILE_TYPE);
          view.importHistory(new ArrayList<String>(Arrays.asList(ClojureApplicationSettings.getInstance().CONSOLE_HISTORY)));

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
      view = builder.getConsole();

      // TODO - What does the "help ID" give us??

//      final VirtualFile baseDir = myProject.getBaseDir();
      final String baseDir = module.getModuleFile().getParent().getPath();
      ClojureProjectSettings settings = ClojureProjectSettings.getInstance(myProject);
      processHandler = new ClojureReplProcessHandler(baseDir, settings.getCommandLineArgs(), module);
      ProcessTerminatedListener.attach(processHandler);
      processHandler.startNotify();
      view.attachToProcess(processHandler);

      final EditorEx ed = getEditor();
      ed.getContentComponent().addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent event) {
          // TODO - This is probably wrong, actually, but it's a start...
//          ed.getCaretModel().moveToOffset(view.getContentSize());
//          ed.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        }
      });

/* TODO - I may want this, but right now it pukes when you "Run Selected Text" from the editor and the result is an error...
            ed.getContentComponent().addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent event) {
                    // TODO - This is probably wrong, actually, but it's a start...
                    ed.getCaretModel().moveToOffset(view.getContentSize());
                    ed.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                }
            });
*/

      // TODO - Experimental... Play around with what widgetry we'd like to see in the REPL
      ed.getSettings().setSmartHome(true);
      ed.getSettings().setVariableInplaceRenameEnabled(true);
      ed.getSettings().setAnimatedScrolling(true);
      ed.getSettings().setFoldingOutlineShown(true);
      //e.getSettings().setLineNumbersShown(true);

      final ActionManager am = ActionManager.getInstance();
      PopupHandler.installPopupHandler(ed.getContentComponent(), 
          (ActionGroup) am.getAction("Clojure.REPL.Group"), REPL_TOOLWINDOW_POPUP_PLACE, am);
    }

    public ConsoleView getView() {
      return view;
    }

    public void dispose() {
      if (processHandler != null) {
        processHandler.destroyProcess();
      }
    }

    public EditorEx getEditor() {
      EditorComponentImpl eci = (EditorComponentImpl) view.getPreferredFocusableComponent();
      return eci.getEditor();
    }
  }
}
