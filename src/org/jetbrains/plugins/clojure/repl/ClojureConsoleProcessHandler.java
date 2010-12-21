package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;

/**
 * @author ilyas
 */
public class ClojureConsoleProcessHandler extends ColoredProcessHandler {

  private final LanguageConsoleImpl myLanguageConsole;

  public ClojureConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console) {
    super(process, commandLine, CharsetToolkit.UTF8_CHARSET);
    myLanguageConsole = console;
  }

  @Override
  protected void textAvailable(String text, Key attributes) {
    ClojureConsoleHighlightingUtil.processOutput(myLanguageConsole, text, attributes);
  }

/*
  private static String processPrompts(final LanguageConsoleImpl console, String text) {
    if (text != null && text.matches(ClojureConsoleHighlightingUtil.LINE_WITH_PROMPT)) {

    }
    return text;
  }
*/
}
