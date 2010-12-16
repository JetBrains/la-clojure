package org.jetbrains.plugins.clojure.debugger.filters;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.xdebugger.settings.XDebuggerSettings;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
@State(
    name = "ClojureDebuggerSettings",
    storages = {
    @Storage(
        id = "clojure_debugger",
        file = "$APP_CONFIG$/clojure_debug.xml"
    )}
)
public class ClojureDebuggerSettings extends XDebuggerSettings<ClojureDebuggerSettings> {

  public Boolean DEBUG_DISABLE_SPECIFIC_CLOJURE_METHODS = true;

  public ClojureDebuggerSettings() {
    super("clojure_debugger");
  }

  @NotNull
  public Configurable createConfigurable() {
    return new ClojureDebuggerSettingsConfigurable(this);
  }

  public ClojureDebuggerSettings getState() {
    return this;
  }

  public void loadState(final ClojureDebuggerSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static ClojureDebuggerSettings getInstance() {
    return getInstance(ClojureDebuggerSettings.class);
  }

}