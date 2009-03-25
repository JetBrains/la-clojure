package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClojureFacet extends Facet<ClojureFacetConfiguration> {

  public static final String FACET_TYPE_ID_STRING = "clojure";
  public final static FacetTypeId<ClojureFacet> ID = new FacetTypeId<ClojureFacet>(FACET_TYPE_ID_STRING);

  public ClojureFacet(@NotNull Module module) {
    this(FacetTypeRegistry.getInstance().findFacetType(FACET_TYPE_ID_STRING), module, "Clojure", new ClojureFacetConfiguration(), null);
  }


  public ClojureFacet(final FacetType facetType, final Module module, final String name, final ClojureFacetConfiguration configuration, final Facet underlyingFacet) {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  public static ClojureFacet getInstance(@NotNull Module module){
    return FacetManager.getInstance(module).getFacetByType(ID);
  }

}
