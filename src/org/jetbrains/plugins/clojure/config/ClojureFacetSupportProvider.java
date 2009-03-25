package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.impl.ui.FacetTypeFrameworkSupportProvider;
import com.intellij.facet.ui.libraries.LibraryInfo;
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
public class ClojureFacetSupportProvider extends FacetTypeFrameworkSupportProvider<ClojureFacet> {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.config.ClojureFacetSupportProvider");

  protected ClojureFacetSupportProvider() {
    super(ClojureFacetType.INSTANCE);
  }

  @NotNull
  @NonNls
  public String getLibraryName(final String name) {
    return "clojure-" + name;
  }

  @NonNls
  public String getTitle() {
    return ClojureBundle.message("clojure.facet.title");
  }

  @NotNull
  public String[] getVersions() {
    List<String> versions = new ArrayList<String>();
    for (ClojureVersion version : ClojureVersion.values()) {
      versions.add(version.toString());
    }
    return versions.toArray(new String[versions.size()]);
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


  protected void setupConfiguration(ClojureFacet facet, ModifiableRootModel rootModel, String v) {
    //do nothing
  }

}