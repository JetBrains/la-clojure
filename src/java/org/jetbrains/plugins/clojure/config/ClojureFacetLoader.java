package org.jetbrains.plugins.clojure.config;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.facet.FacetTypeRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClojureFacetLoader implements ApplicationComponent {

  public static final String PLUGIN_MODULE_ID = "PLUGIN_MODULE";


  public static ClojureFacetLoader getInstance() {
    return ApplicationManager.getApplication().getComponent(ClojureFacetLoader.class);
  }

  public ClojureFacetLoader() {
  }

  public void initComponent() {
    FacetTypeRegistry.getInstance().registerFacetType(ClojureFacetType.INSTANCE);
  }

  public void disposeComponent() {
    FacetTypeRegistry instance = FacetTypeRegistry.getInstance();
    instance.unregisterFacetType(instance.findFacetType(ClojureFacet.ID));
  }

  @NotNull
  public String getComponentName() {
    return "ClojureFacetLoader";
  }


}
