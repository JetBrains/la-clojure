package org.jetbrains.plugins.clojure.config.util;

import com.intellij.facet.ui.libraries.LibraryInfo;
import org.jetbrains.annotations.NonNls;

/**
 * @author ilyas
 */
public class ClojureMavenLibraryUtil {

  @NonNls
  private static final String DOWNLOAD_JETBRAINS_COM = "http://download.jetbrains.com";
  @NonNls
  private static final String DOWNLOADING_URL = DOWNLOAD_JETBRAINS_COM + "/idea/clojure/";

  @NonNls
  private static final String DOWNLOAD_MAVEN_ORG = "http://repo1.maven.org";
  @NonNls
  private static final String MAVEN_DOWNLOADING_URL = DOWNLOAD_MAVEN_ORG + "/maven2/org/clojure/clojure/";

  private ClojureMavenLibraryUtil() {
  }

  public static LibraryInfo createJarDownloadInfo(final boolean useBrainsUrl, final String jarName, final String version,
                                                  final String... requiredClasses) {
    final String v = version == null || version.length() == 0 ? "" : "/" + version + "/";
    if (useBrainsUrl)
      return new LibraryInfo(jarName, DOWNLOADING_URL + v + jarName, DOWNLOAD_JETBRAINS_COM, null,
          requiredClasses);
    else
      return new LibraryInfo(jarName, MAVEN_DOWNLOADING_URL + v
          + jarName.substring(0, jarName.lastIndexOf('.')) + "-" + version + ".jar", DOWNLOAD_MAVEN_ORG, null,
          requiredClasses);
  }

}
