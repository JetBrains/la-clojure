package org.jetbrains.plugins.clojure.psi.resolve.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;

/**
 * @author Alefas
 * @since 15.01.13
 */
public class ClojureClassNameCompletionContributor extends CompletionContributor {
  public static void completeClassName(final CompletionParameters parameters, final CompletionResultSet result) {
    final PsiElement position = parameters.getPosition();
    final PsiElement parent = position.getParent();
    if (!(parent instanceof ClSymbol)) return;
    ClSymbol symbol = (ClSymbol) parent;
    if (symbol.getQualifierSymbol() != null) return;
    final PsiElement list = symbol.getParent();
    final boolean isInImport;
    if (list instanceof ClList) {
      isInImport = ((ClList) list).getFirstSymbol().getNameString().equals(ListDeclarations.IMPORT);
    } else {
      isInImport = false;
    }
    final PrefixMatcher prefixMatcher = result.getPrefixMatcher();
    final boolean filterByScope = parameters.getInvocationCount() <= 1;
    AllClassesGetter.processJavaClasses(parameters, prefixMatcher, filterByScope,
        new Consumer<PsiClass>() {
          public void consume(final PsiClass clazz) {
            if (!AllClassesGetter.isAcceptableInContext(position, clazz, filterByScope, true)) return;
            final ClojureLookupItem lookupItem = new ClojureLookupItem(clazz);
            lookupItem.setClassName(true);
            lookupItem.setInImport(isInImport);
            result.addElement(lookupItem);
          }
        });
  }

  public static boolean shouldRunClassName(CompletionParameters parameters, PrefixMatcher prefixMatcher,
                                           boolean checkInvocationCount) {
    final PsiElement position = parameters.getPosition();
    if (checkInvocationCount && parameters.getInvocationCount() < 2) return false;
    final PsiElement parent = position.getParent();
    if (!(parent instanceof ClSymbol)) return false;
    if (((ClSymbol) parent).getQualifierSymbol() != null) return false;
    if (checkInvocationCount && parameters.getInvocationCount() >= 2) return true;
    final String prefix = prefixMatcher.getPrefix();
    return prefix.length() > 0 && prefix.substring(0, 1).toUpperCase().equals(prefix.substring(0, 1));
  }

  @Override
  public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
    if (parameters.getCompletionType() != CompletionType.BASIC) return; //only basic completion is here
    if (shouldRunClassName(parameters, result.getPrefixMatcher(), true)) {
      completeClassName(parameters, result);
    }
  }
}
