package org.jetbrains.plugins.clojure.psi.api;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;

/**
 * @author ilyas
 */
public interface ClKeyword extends ClojurePsiElement, PsiNamedElement, PsiReference {
}
