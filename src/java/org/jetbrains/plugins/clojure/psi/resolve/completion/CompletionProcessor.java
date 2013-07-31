package org.jetbrains.plugins.clojure.psi.resolve.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.plugins.clojure.psi.resolve.processors.ResolveKind;
import org.jetbrains.plugins.clojure.psi.resolve.processors.SymbolResolveProcessor;

/**
 * @author ilyas
 */
public class CompletionProcessor extends SymbolResolveProcessor {

  public CompletionProcessor(PsiElement myPlace, ResolveKind[] kinds) {
    super(null, myPlace, true, kinds);
  }

  public boolean execute(PsiElement element, ResolveState state) {
    super.execute(element, state);
    return true;
  }
}
