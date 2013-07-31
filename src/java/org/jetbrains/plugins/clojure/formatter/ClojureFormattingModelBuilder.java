package org.jetbrains.plugins.clojure.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ilyas
 */
public class ClojureFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  public FormattingModel createModel(final PsiElement element, final CodeStyleSettings settings) {
    ASTNode node = element.getNode();
    assert node != null;
    PsiFile containingFile = element.getContainingFile();
    ASTNode astNode = containingFile.getNode();
    assert astNode != null;
    return FormattingModelProvider.createFormattingModelForPsiFile(containingFile,
            new ClojureBlock(astNode, null, Indent.getAbsoluteNoneIndent(), null, settings), settings);
  }

  @Nullable
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
