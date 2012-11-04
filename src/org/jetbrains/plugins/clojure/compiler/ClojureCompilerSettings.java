package org.jetbrains.plugins.clojure.compiler;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.clojure.model.impl.JpsClojureCompilerSettingsState;
import org.jetbrains.jps.clojure.model.impl.JpsClojureModelSerializerExtension;

/**
 * @author ilyas
 */
@State(
  name = JpsClojureModelSerializerExtension.CLOJURE_COMPILER_SETTINGS_COMPONENT_NAME,
  storages = {
    @Storage(id = "default", file = "$PROJECT_FILE$")
   ,@Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/" + JpsClojureModelSerializerExtension.CLOJURE_COMPILER_SETTINGS_FILE, scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class ClojureCompilerSettings implements PersistentStateComponent<JpsClojureCompilerSettingsState>, ProjectComponent {
  private JpsClojureCompilerSettingsState myState = new JpsClojureCompilerSettingsState();

  public JpsClojureCompilerSettingsState getState() {
    return myState;
  }
                
  public void loadState(JpsClojureCompilerSettingsState state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  public void projectOpened() {
  }

  public void projectClosed() {
  }

  @NotNull
  public String getComponentName() {
    return "ClojureCompilerSettings";
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }

  public static ClojureCompilerSettings getInstance(Project project) {
    return project.getComponent(ClojureCompilerSettings.class);
  }

}
