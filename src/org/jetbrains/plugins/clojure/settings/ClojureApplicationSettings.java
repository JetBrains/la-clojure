package org.jetbrains.plugins.clojure.settings;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * @author ilyas
 */
@State(
    name = "ClojureApplicationSettings",
    storages = {
    @Storage(
        id = "scala_config",
        file = "$APP_CONFIG$/clojure_config.xml"
    )}
)
public class ClojureApplicationSettings implements PersistentStateComponent<ClojureApplicationSettings> {

  public String[] CONSOLE_HISTORY = new String[0];

  public ClojureApplicationSettings getState() {
    return this;
  }

  public void loadState(ClojureApplicationSettings clojureApplicationSettings) {
    XmlSerializerUtil.copyBean(clojureApplicationSettings, this);
  }

  public static ClojureApplicationSettings getInstance() {
    return ServiceManager.getService(ClojureApplicationSettings.class);
  }

}
