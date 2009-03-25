package org.jetbrains.plugins.clojure.compiler;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
@State(
  name = "ClojureCompilerSettings",
  storages = {
    @Storage(id = "default", file = "$PROJECT_FILE$")
   ,@Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/clojure_compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class ClojureCompilerSettings implements PersistentStateComponent<ClojureCompilerSettings>, ProjectComponent {
  public boolean COMPILE_CLOJURE = false;
  public boolean CLOJURE_BEFORE = true;
  public boolean COPY_CLJ_SOURCES = false;

  public ClojureCompilerSettings getState() {
    return this;
  }
                
  public void loadState(ClojureCompilerSettings state) {
    XmlSerializerUtil.copyBean(state, this);
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
