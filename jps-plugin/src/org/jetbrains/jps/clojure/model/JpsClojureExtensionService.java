package org.jetbrains.jps.clojure.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.clojure.model.impl.JpsClojureCompilerSettingsExtensionImpl;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

/**
 * @author nik
 * @since 02.11.12
 */
public class JpsClojureExtensionService {
  public static final JpsElementChildRoleBase<JpsClojureCompilerSettingsExtension> COMPILER_SETTINGS_ROLE = JpsElementChildRoleBase.create("clojure compiler settings");

  @Nullable
  public static JpsClojureCompilerSettingsExtension getExtension(@NotNull JpsProject project) {
    return project.getContainer().getChild(COMPILER_SETTINGS_ROLE);
  }


  public static void setExtension(@NotNull JpsProject project, JpsClojureCompilerSettingsExtension extension) {
    project.getContainer().setChild(COMPILER_SETTINGS_ROLE, extension);
  }

}
