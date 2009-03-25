package org.jetbrains.plugins.clojure.config;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Element;
import org.jetbrains.plugins.clojure.config.ui.ClojureFacetTab;

/**
 * @author ilyas
 */
@State(
    name = "ClojureFacetConfiguration",
    storages = {
      @Storage(
        id = "default",
        file = "$MODULE_FILE$"
      )
    }
)
public class ClojureFacetConfiguration implements FacetConfiguration, PersistentStateComponent<ClojureLibrariesConfiguration> {
  private final ClojureLibrariesConfiguration myClojureLibrariesConfiguration = new ClojureLibrariesConfiguration();

  public String getDisplayName() {
    return "Clojure";
  }

  public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
    return new FacetEditorTab[]{
        new ClojureFacetTab(editorContext, validatorsManager, myClojureLibrariesConfiguration)
    };
  }

  public void readExternal(Element element) throws InvalidDataException {
  }

  public void writeExternal(Element element) throws WriteExternalException {
  }

  public ClojureLibrariesConfiguration getMyClojureLibrariesConfiguration() {
    return myClojureLibrariesConfiguration;
  }

  public ClojureLibrariesConfiguration getState() {
    return myClojureLibrariesConfiguration;
  }

  public void loadState(ClojureLibrariesConfiguration state) {
    XmlSerializerUtil.copyBean(state, myClojureLibrariesConfiguration);
  }
}
