package org.jetbrains.plugins.clojure.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

/**
 * @author ilyas
 */
public class ClojureModuleSettings {

  @NotNull
  public String myReplClass = ClojureUtils.CLOJURE_MAIN;

  @NotNull
  public String myJvmOpts = ClojureUtils.CLOJURE_DEFAULT_JVM_PARAMS;

  @NotNull
  public String myReplOpts = "";

}
