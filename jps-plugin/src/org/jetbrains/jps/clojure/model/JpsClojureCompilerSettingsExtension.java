package org.jetbrains.jps.clojure.model;

import org.jetbrains.jps.model.JpsElement;

/**
 * @author nik
 * @since 02.11.12
 */
public interface JpsClojureCompilerSettingsExtension extends JpsElement {

  boolean isCompileClojure();

  boolean isClojureBefore();

  boolean isCopyCljSources();
}
