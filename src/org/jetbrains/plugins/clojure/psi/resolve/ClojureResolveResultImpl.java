package org.jetbrains.plugins.clojure.psi.resolve;

import com.intellij.psi.PsiElement;

/**
 * @author ilyas
 */
public class ClojureResolveResultImpl implements ClojureResolveResult {

  private  final PsiElement myElement;
  private final boolean myIsAccessible;

  public ClojureResolveResultImpl(PsiElement myElement, boolean myIsAccessible) {
    this.myElement = myElement;
    this.myIsAccessible = myIsAccessible;
  }


  public PsiElement getElement() {
    return myElement;
  }

  public boolean isValidResult() {
    return isAccessible();
  }
  
  public boolean isAccessible() {
    return myIsAccessible;
  }


}
