package org.jetbrains.jps.clojure.model.impl;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.clojure.model.JpsClojureExtensionService;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

import java.util.Collections;
import java.util.List;

/**
 * @author nik
 * @since 02.11.12
 */
public class JpsClojureModelSerializerExtension extends JpsModelSerializerExtension {
  public static final String CLOJURE_COMPILER_SETTINGS_COMPONENT_NAME = "ClojureCompilerSettings";
  public static final String CLOJURE_COMPILER_SETTINGS_FILE = "clojure_compiler.xml";

  @NotNull
  @Override
  public List<? extends JpsProjectExtensionSerializer> getProjectExtensionSerializers() {
    return Collections.singletonList(new JpsProjectExtensionSerializer(CLOJURE_COMPILER_SETTINGS_FILE, CLOJURE_COMPILER_SETTINGS_COMPONENT_NAME) {
      @Override
      public void loadExtension(@NotNull JpsProject jpsProject, @NotNull Element componentTag) {
        JpsClojureCompilerSettingsState state = XmlSerializer.deserialize(componentTag, JpsClojureCompilerSettingsState.class);
        if (state != null) {
          JpsClojureExtensionService.setExtension(jpsProject, new JpsClojureCompilerSettingsExtensionImpl(state));
        }
      }

      @Override
      public void saveExtension(@NotNull JpsProject jpsProject, @NotNull Element componentTag) {
      }
    });
  }
}
