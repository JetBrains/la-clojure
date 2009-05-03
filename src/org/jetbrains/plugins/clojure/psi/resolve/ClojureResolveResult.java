package org.jetbrains.plugins.clojure.psi.resolve;

import com.intellij.psi.ResolveResult;

/**
 * @author ilyas
 */
public interface ClojureResolveResult extends ResolveResult {

  public ClojureResolveResult[] EMPTY_ARRAY = new ClojureResolveResult[0];

  boolean isAccessible();
}
