package org.jetbrains.plugins.clojure.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;

import java.util.regex.Matcher;

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
    final String string = processPrompts(myLanguageConsole, StringUtil.convertLineSeparators(text));
    ClojureConsoleHighlightingUtil.processOutput(myLanguageConsole, string, attributes);
  }

  private static String processPrompts(final LanguageConsoleImpl console, String text) {
    if (text != null && text.matches(ClojureConsoleHighlightingUtil.LINE_WITH_PROMPT)) {
      final Matcher matcher = ClojureConsoleHighlightingUtil.CLOJURE_PROMPT_PATTERN.matcher(text);
      matcher.find();
      final String prefix = matcher.group();
      final String trimmed = StringUtil.trimStart(text, prefix).trim();
      console.setPrompt(prefix + " ");
      return trimmed;
    }
    return text;
  }

  public LanguageConsoleImpl getLanguageConsole() {
    return myLanguageConsole;
  }

}
