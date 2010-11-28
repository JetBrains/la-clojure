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

  private ClojureMavenLibraryUtil() {
  }

  public static LibraryInfo createJarDownloadInfo(final String jarName, final String version,
                                                  final String... requiredClasses) {
    final String v = version == null || version.length() == 0 ? "" : "/" + version + "/";
    return new LibraryInfo(jarName, DOWNLOADING_URL + v + jarName, DOWNLOAD_JETBRAINS_COM, null,
        requiredClasses);
  }

}
