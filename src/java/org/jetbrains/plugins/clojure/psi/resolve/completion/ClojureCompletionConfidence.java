package org.jetbrains.plugins.clojure.psi.resolve.completion;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.repl.ClojureConsole;

/**
 * @author peter
 */
public class ClojureCompletionConfidence extends CompletionConfidence {
  @NotNull
  @Override
  public ThreeState shouldFocusLookup(@NotNull CompletionParameters completionParameters) {
    return ThreeState.UNSURE;
  }

  @NotNull
  @Override
  public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
    Document document = psiFile.getViewProvider().getDocument();
    if (document != null && 
        CodeInsightSettings.getInstance().SELECT_AUTOPOPUP_SUGGESTIONS_BY_CHARS && 
        document.getUserData(ClojureConsole.CLOJURE_CONSOLE_EDITOR) == Boolean.TRUE) {
      return ThreeState.YES;
    }
    return ThreeState.UNSURE;
  }
}
