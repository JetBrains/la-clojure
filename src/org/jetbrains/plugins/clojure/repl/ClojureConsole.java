package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.ClojureLanguage;

/**
 * @author ilyas
 */
public class ClojureConsole extends LanguageConsoleImpl {

  public ClojureConsole(Project project, String title, boolean doSaveErrorsToHistory) {
    super(project, title, ClojureLanguage.getInstance());
  }

  // TODO implement console communication
}
