package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.openapi.project.Project;

/**
 * @author ilyas
 */
public class ClojureConsoleView extends LanguageConsoleViewImpl {
  public ClojureConsoleView(Project project, String title) {
    super(project, new ClojureConsole(project, title, true));
  }
}
