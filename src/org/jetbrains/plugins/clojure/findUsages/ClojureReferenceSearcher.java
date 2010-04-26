package org.jetbrains.plugins.clojure.findUsages;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

import java.util.List;

/**
 * @author ilyas
 */
public class ClojureReferenceSearcher implements QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
  public boolean execute(ReferencesSearch.SearchParameters params, final Processor<PsiReference> consumer) {
    final PsiElement elem = params.getElementToSearch();
    final SearchScope scope = params.getScope();
    if (elem instanceof PsiNamedElement) {
      final PsiNamedElement symbolToSearch = (PsiNamedElement) elem;
      final String name = symbolToSearch.getName();
      if (name != null) {
        final List<String> wordsIn = StringUtil.getWordsIn(name);
        final TextOccurenceProcessor processor = new TextOccurenceProcessor() {
          public boolean execute(PsiElement element, int offsetInElement) {
            if (element instanceof ClSymbol) {
              ClSymbol refSymbol = (ClSymbol) element;
              for (PsiReference ref : refSymbol.getReferences()) {
                if (ref.getRangeInElement().contains(offsetInElement) &&
                    // atom may refer to definition or to the symbol in it
                    (ref.resolve() == symbolToSearch ||
                        ref.resolve() == symbolToSearch.getParent())) {
                  if (!consumer.process(ref)) return false;
                }
              }
            }
            return true;
          }
        };
        final PsiSearchHelper helper = PsiManager.getInstance(elem.getProject()).getSearchHelper();
        for (String word : wordsIn) {
          helper.processElementsWithWord(processor, scope, word, UsageSearchContext.ANY, true);
        }
      }
    }
    return true;
  }

}
