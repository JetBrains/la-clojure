package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.FacetManagerAdapter;
import com.intellij.facet.FacetManager;
import com.intellij.facet.Facet;
import com.intellij.facet.ProjectFacetManager;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.repl.ReplManager;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

import java.util.List;

/**
 * @author ilyas
 */
public class ClojureFacetListener extends FacetManagerAdapter implements ModuleComponent {
  private MessageBusConnection myConnection;

  private Module myModule;

  public ClojureFacetListener(Module module) {
    myModule = module;
  }

  public void initComponent() {
    myConnection = myModule.getMessageBus().connect();
    myConnection.subscribe(FacetManager.FACETS_TOPIC, new FacetManagerAdapter() {
      public void facetAdded(@NotNull final Facet facet) {
      }

      public void facetRemoved(@NotNull Facet facet) {
        if (facet.getTypeId() == ClojureFacet.ID) {
          removeReplToolWindow();
        }
      }
    });
  }

  private void removeReplToolWindow() {
    final Project project = myModule.getProject();

    final List<ClojureFacet> facets = ProjectFacetManager.getInstance(project).getFacets(ClojureFacet.ID);
    if (facets.size() == 0) {
      ReplManager.getInstance(project).closeReplToolWindow();
    }
  }

  public void disposeComponent() {
    myConnection.disconnect();
  }

  @NotNull
  public String getComponentName() {
    return "ClojureFacetListener";
  }

  public void projectOpened() {
    // called when myProject is opened
  }

  public void projectClosed() {
    // called when myProject is being closed
  }

  public void moduleAdded() {
    // Invoked when the module corresponding to this component instance has been completely
    // loaded and added to the myProject.
  }
}
