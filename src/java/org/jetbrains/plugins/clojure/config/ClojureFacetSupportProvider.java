package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.facet.ui.libraries.LibraryInfo;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class ClojureFacetSupportProvider extends FacetBasedFrameworkSupportProvider<ClojureFacet> {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.config.ClojureFacetSupportProvider");

  protected ClojureFacetSupportProvider() {
    super(ClojureFacetType.INSTANCE);
  }

  @NotNull
  @NonNls
  public String getLibraryName(final String name) {
    return "clojure";
  }

  @NonNls
  public String getTitle() {
    return ClojureBundle.message("clojure.facet.title");
  }

  @NotNull
  public List<FrameworkVersion> getVersions() {
    List<FrameworkVersion> versions = new ArrayList<FrameworkVersion>();
    for (ClojureVersion version : ClojureVersion.values()) {
      versions.add(new FrameworkVersion(version.toString(), getLibraryName(version.toString()), getLibraries(version.toString())));;
    }
    return versions;
  }

  private static ClojureVersion getVersion(String versionName) {
    for (ClojureVersion version : ClojureVersion.values()) {
      if (versionName.equals(version.toString())) {
        return version;
      }
    }
    LOG.error("invalid Clojure version: " + versionName);
    return null;
  }

  @NotNull
  protected LibraryInfo[] getLibraries(final String selectedVersion) {
    ClojureVersion version = getVersion(selectedVersion);
    LOG.assertTrue(version != null);
    return version.getJars();
  }

  @Override
  protected void setupConfiguration(ClojureFacet facet, ModifiableRootModel rootModel, FrameworkVersion version) {
    // do nothing
  }
}