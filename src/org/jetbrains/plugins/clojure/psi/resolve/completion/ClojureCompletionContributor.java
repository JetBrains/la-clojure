package org.jetbrains.plugins.clojure.psi.resolve.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

/**
 * @author Alefas
 * @since 15.01.13
 */
public class ClojureCompletionContributor extends CompletionContributor {
  @Override
  public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
    super.fillCompletionVariants(parameters, result);
    if (parameters.getCompletionType() != CompletionType.BASIC) return; //only basic completion is here
    result.restartCompletionWhenNothingMatches();
    final PsiElement position = parameters.getPosition();
    final PsiElement parent = position.getParent();
    final boolean isClassName = ClojureClassNameCompletionContributor.shouldRunClassName(parameters,
        result.getPrefixMatcher(), true);
    if (parent instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) parent;
      final Object[] variants = symbol.getVariants();
      Ref<Boolean> elementAdded = new Ref<Boolean>(Boolean.FALSE);
      for (Object variant : variants) {
        if (variant instanceof ClojureLookupItem) {
          ClojureLookupItem lookupItem = (ClojureLookupItem) variant;
          final PsiElement element = lookupItem.getPsiElement();
          if (element instanceof PsiClass) {
            final PsiClass clazz = (PsiClass) element;
            boolean isExcluded = ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
              public Boolean compute() {
                return JavaCompletionUtil.isInExcludedPackage(clazz, true);
              }
            });
            if (!isExcluded && !isClassName) {
              addElement(result, lookupItem, elementAdded);
            }
          } else {
            addElement(result, lookupItem, elementAdded);
          }
        } else if (variant instanceof LookupElement) {
          addElement(result, (LookupElement) variant, elementAdded);
        }
      }
      if (!elementAdded.get() && !isClassName && ClojureClassNameCompletionContributor.shouldRunClassName(parameters,
          result.getPrefixMatcher(), false)) {
        ClojureClassNameCompletionContributor.completeClassName(parameters, result);
      }
      result.stopHere(); //we want to handle all Clojure completion, so we don't need anything more
    }
  }

  private void addElement(CompletionResultSet result, LookupElement lookupItem, Ref<Boolean> elementAdded) {
    if (result.getPrefixMatcher().prefixMatches(lookupItem)) {
      elementAdded.set(Boolean.TRUE);
    }
    result.addElement(lookupItem);
  }
}
