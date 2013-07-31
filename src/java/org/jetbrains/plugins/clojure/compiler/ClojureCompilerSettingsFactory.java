package org.jetbrains.plugins.clojure.compiler;

import com.intellij.compiler.CompilerSettingsFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

/**
 * @author ilyas
 */
public class ClojureCompilerSettingsFactory implements CompilerSettingsFactory {
  public Configurable create(Project project) {
    return new ClojureCompilerConfigurable(ClojureCompilerSettings.getInstance(project), project);
  }
}