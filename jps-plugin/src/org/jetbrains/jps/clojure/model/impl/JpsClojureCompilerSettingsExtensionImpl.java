package org.jetbrains.jps.clojure.model.impl;

import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.clojure.model.JpsClojureCompilerSettingsExtension;
import org.jetbrains.jps.model.ex.JpsElementBase;

/**
 * @author nik
 * @since 02.11.12
 */
public class JpsClojureCompilerSettingsExtensionImpl extends JpsElementBase<JpsClojureCompilerSettingsExtensionImpl> implements JpsClojureCompilerSettingsExtension {
  private final JpsClojureCompilerSettingsState myState;

  public JpsClojureCompilerSettingsExtensionImpl(JpsClojureCompilerSettingsState state) {
    myState = state;
  }

  @NotNull
  @Override
  public JpsClojureCompilerSettingsExtensionImpl createCopy() {
    return new JpsClojureCompilerSettingsExtensionImpl(XmlSerializerUtil.createCopy(myState));
  }

  @Override
  public void applyChanges(@NotNull JpsClojureCompilerSettingsExtensionImpl modified) {
  }

  public boolean isCompileClojure() {
    return myState.COMPILE_CLOJURE;
  }

  public boolean isClojureBefore() {
    return myState.CLOJURE_BEFORE;
  }

  public boolean isCopyCljSources() {
    return myState.COPY_CLJ_SOURCES;
  }
}
