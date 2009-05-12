package org.jetbrains.plugins.clojure.psi.resolve.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.plugins.clojure.psi.resolve.ClojureResolveResultImpl;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;

/**
 * @author ilyas
 */
public class SymbolResolveProcessor extends ResolveProcessor {

  private final Set<PsiElement> myProcessedElements = new HashSet<PsiElement>();
  private final PsiElement myPlace;
  private final boolean incompleteCode;

  public SymbolResolveProcessor(String myName, PsiElement myPlace, boolean incompleteCode) {
    super(myName);
    this.myPlace = myPlace;
    this.incompleteCode = incompleteCode;
  }


  public boolean execute(PsiElement element, ResolveState resolveState) {
    // todo add resolve kinds
    if (element instanceof PsiNamedElement && !myProcessedElements.contains(element)) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      boolean isAccessible = isAccessible(namedElement);
      myCandidates.add(new ClojureResolveResultImpl(namedElement, isAccessible));
      myProcessedElements.add(namedElement);
      return !ListDeclarations.isLocal(element);
      //todo specify as it's possible!
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
