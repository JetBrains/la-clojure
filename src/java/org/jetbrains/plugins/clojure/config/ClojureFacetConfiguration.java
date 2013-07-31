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
import org.jetbrains.plugins.clojure.config.ui.ClojureFacetSettingsTab;

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
public class ClojureFacetConfiguration implements FacetConfiguration, PersistentStateComponent<ClojureModuleSettings> {
  private final ClojureModuleSettings mySettings = new ClojureModuleSettings();

  public String getDisplayName() {
    return "Clojure";
  }

  public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
    return new FacetEditorTab[]{
        new ClojureFacetSettingsTab(editorContext, validatorsManager, mySettings)
    };
  }

  public void readExternal(Element element) throws InvalidDataException {
  }

  public void writeExternal(Element element) throws WriteExternalException {
  }

  public ClojureModuleSettings getState() {
    return mySettings;
  }

  public void loadState(ClojureModuleSettings state) {
    XmlSerializerUtil.copyBean(state, mySettings);
  }
}
