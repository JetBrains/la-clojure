package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.repl.ReplManager;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

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

  @Override
  public void initFacet() {
    super.initFacet();

    // lazy initialization of REPL toolwindow (remove this code to not add REPL toolwindow automatically)
//    final Module module = getModule();
//    final Project project = module.getProject();
//    ReplManager.getInstance(project).init(module);
  }
}
