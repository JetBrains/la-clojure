package org.jetbrains.plugins.clojure.config;

import com.intellij.facet.ui.libraries.LibraryInfo;
import org.jetbrains.annotations.NonNls;
import static org.jetbrains.plugins.clojure.config.util.ClojureMavenLibraryUtil.createJarDownloadInfo;


/**
 * @author ilyas
 */
public enum ClojureVersion {

  Clojure_1_0("1.0", new LibraryInfo[]{
      createJarDownloadInfo("clojure.jar", "", "clojure.main"),
      createJarDownloadInfo("clojure-contrib.jar", ""),
  })
  ;

  private final String myName;
  private final LibraryInfo[] myJars;

  private ClojureVersion(@NonNls String name, LibraryInfo[] infos) {
    myName = name;
    myJars = infos;
  }

  public LibraryInfo[] getJars() {
    return myJars;
  }

  public String toString() {
    return myName;
  }

}