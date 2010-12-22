package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ConsoleExecuteActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.codeStyle.HelperFactory;
import com.intellij.psi.impl.source.codeStyle.IndentHelper;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * @author ilyas
 */
public class ClojureConsoleExecuteActionHandler extends ConsoleExecuteActionHandler {

  private LanguageConsoleViewImpl myConsoleView;
  private ProcessHandler myProcessHandler;
  private Project myProject;
  private IndentHelper myIndentHelper;

  public ClojureConsoleExecuteActionHandler(LanguageConsoleViewImpl consoleView,
                                            ProcessHandler processHandler,
                                            Project project) {
    super(processHandler, false);
    myConsoleView = consoleView;
    myProcessHandler = processHandler;
    myProject = project;
    myIndentHelper = HelperFactory.createHelper(ClojureFileType.CLOJURE_FILE_TYPE, consoleView.getConsole().getProject());
  }

  @Override
  public void processLine(String text) {
    super.processLine(text);
  }

  @Override
  public void runExecuteAction(final LanguageConsoleImpl languageConsole, ConsoleHistoryModel consoleHistoryModel) {
    // Process input and add to history
    final Editor editor = languageConsole.getCurrentEditor();
    final Document document = editor.getDocument();
    final CaretModel caretModel = editor.getCaretModel();
    final int offset = caretModel.getOffset();
    final String text = document.getText();

    if (!"".equals(text.substring(offset).trim())) {
      final String before = text.substring(0, offset);
      final String after = text.substring(offset);
      final int indent = myIndentHelper.getIndent(before, false);
      final String spaces = myIndentHelper.fillIndent(indent);
      final String newText = before + "\n" + spaces + after;

      new WriteCommandAction(myProject) {
        @Override
        protected void run(Result result) throws Throwable {
          languageConsole.setInputText(newText);
          caretModel.moveToOffset(offset + indent + 1);
        }
      }.execute();

      return;
    }

    final String candidate = text.trim();

    if (ClojurePsiUtil.isValidClojureExpression(candidate, myProject) || "".equals(candidate)) { // S-expression contains no syntax errors
      super.runExecuteAction(languageConsole, consoleHistoryModel);
    } else {
      languageConsole.setInputText(text + "\n");
    }

  }

  private void scrollDown(final Editor currentEditor) {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      public void run() {
        currentEditor.getCaretModel().moveToOffset(currentEditor.getDocument().getTextLength());
      }
    });
  }
}
