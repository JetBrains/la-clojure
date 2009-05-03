package org.jetbrains.plugins.clojure.psi.resolve.processors;

import com.intellij.psi.scope.ElementClassHint;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.HashSet;
import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResult;

/**
 * @author ilyas
 */
public abstract class ResolveProcessor implements PsiScopeProcessor, NameHint, ElementClassHint {

  protected HashSet<ClojureResolveResult> myCandidates = new HashSet<ClojureResolveResult>();
  protected final String myName;

  public ResolveProcessor(String myName) {
    this.myName = myName;
  }

  public ClojureResolveResult[] getCandidates() {
    return myCandidates.toArray(new ClojureResolveResult[myCandidates.size()]);
  }

  public <T> T getHint(Class<T> hintClass) {
    if (NameHint.class == hintClass && myName != null) {
      return (T) this;
    } else if (ElementClassHint.class == hintClass) {
      return (T) this;
    }

    return null;
  }

  public void handleEvent(Event event, Object o) {
  }

  public boolean hasCandidates() {
    return myCandidates.size() > 0;
  }

}
