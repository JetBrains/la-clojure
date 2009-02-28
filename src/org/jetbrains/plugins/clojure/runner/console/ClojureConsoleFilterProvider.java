package org.jetbrains.plugins.clojure.runner.console;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.ExceptionFilter;
import com.intellij.execution.filters.YourkitFilter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClojureConsoleFilterProvider implements ConsoleFilterProvider {
  @NotNull
  public Filter[] getDefaultFilters(@NotNull Project project) {
    return new Filter[]{new ClojureFilter(project)};
  }
}
