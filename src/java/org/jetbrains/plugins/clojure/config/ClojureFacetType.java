package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetModel;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.autodetecting.DetectedFacetPresentation;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

import javax.swing.*;
import java.util.Collection;

/**
 * @author ilyas
 */
public class ClojureFacetType extends FacetType<ClojureFacet, ClojureFacetConfiguration> {

  public static final ClojureFacetType INSTANCE = new ClojureFacetType();

  private ClojureFacetType() {
    super(ClojureFacet.ID, "Clojure", "Clojure");
  }

  public ClojureFacetConfiguration createDefaultConfiguration() {
    return new ClojureFacetConfiguration();
  }

  public ClojureFacet createFacet(@NotNull Module module,
                                 String name,
                                 @NotNull ClojureFacetConfiguration configuration,
                                 @Nullable Facet underlyingFacet) {
    return new ClojureFacet(this, module, name, configuration, underlyingFacet);
  }

  public Icon getIcon() {
    return ClojureIcons.CLOJURE_ICON_16x16;
  }

  public boolean isSuitableModuleType(ModuleType moduleType) {
    return (moduleType instanceof JavaModuleType || "PLUGIN_MODULE".equals(moduleType.getId()));
  }

  public void registerDetectors(final FacetDetectorRegistry<ClojureFacetConfiguration> registry) {
    FacetDetector<VirtualFile, ClojureFacetConfiguration> detector = new ClojureFacetDetector();

    final Ref<Boolean> alreadyDetected = new Ref<Boolean>(false);
    VirtualFileFilter filter = new VirtualFileFilter() {
      public boolean accept(VirtualFile virtualFile) {
        if (alreadyDetected.get()) return true;
        alreadyDetected.set(true);
        if (ClojureFileType.CLOJURE_DEFAULT_EXTENSION.equals(virtualFile.getExtension())) {
          registry.customizeDetectedFacetPresentation(new ClojureFacetPresentation());
          return true;
        }

        return false;
      }
    };

    registry.registerUniversalDetector(ClojureFileType.CLOJURE_FILE_TYPE, filter, detector);
  }

  public static ClojureFacetType getInstance() {
    final ClojureFacetType facetType = (ClojureFacetType) FacetTypeRegistry.getInstance().findFacetType(ClojureFacet.ID);
    assert facetType != null;
    return facetType;
  }

  private class ClojureFacetDetector extends FacetDetector<VirtualFile, ClojureFacetConfiguration> {
    public ClojureFacetDetector() {
      super("clojure-detector");
    }

    public ClojureFacetConfiguration detectFacet(VirtualFile source, Collection<ClojureFacetConfiguration> existentFacetConfigurations) {
      if (!existentFacetConfigurations.isEmpty()) {
        return existentFacetConfigurations.iterator().next();
      }
      return createDefaultConfiguration();
    }

    public void beforeFacetAdded(@NotNull Facet facet, FacetModel facetModel, @NotNull ModifiableRootModel model) {
    }
  }

  private static class ClojureFacetPresentation extends DetectedFacetPresentation {

    @Override
    public String getAutodetectionPopupText(@NotNull Module module, @NotNull FacetType facetType, @NotNull String facetName, @NotNull VirtualFile[] files) {
      return ClojureBundle.message("new.clojure.facet.detected");
    }

  }

}