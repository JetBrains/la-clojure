package org.jetbrains.plugins.clojure.psi.resolve.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResultImpl;

/**
 * @author ilyas
 */
public class SymbolResolveProcessor extends ResolveProcessor {

  private final Set<String> myProcessedClasses = new HashSet<String>();
  private final PsiElement myPlace;
  private final boolean incompleteCode;

  public SymbolResolveProcessor(String myName, PsiElement myPlace, boolean incompleteCode) {
    super(myName);
    this.myPlace = myPlace;
    this.incompleteCode = incompleteCode;
  }


  public boolean execute(PsiElement element, ResolveState resolveState) {
    // todo add resolve kinds
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      boolean isAccessible = isAccessible(namedElement);
      myCandidates.add(new ClojureResolveResultImpl(namedElement, isAccessible));
      return !isAccessible;
    }

    return true;
  }

  public PsiElement getPlace() {
    return myPlace;
  }

  public String getName(ResolveState resolveState) {
    return myName;
  }

  public boolean shouldProcess(Class aClass) {
    return true;
  }

  protected boolean isAccessible(PsiNamedElement namedElement) {
    //todo implement me
    return true;
  }

}
