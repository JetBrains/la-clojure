package org.jetbrains.plugins.clojure.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.formatter.processors.ClojureSpacingProcessor;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

import java.util.List;

/**
 * @author ilyas
 */
public class ClojureBlock implements Block, ClojureElementTypes{

  final protected ASTNode myNode;
  final protected Alignment myAlignment;
  final protected Indent myIndent;
  final protected Wrap myWrap;
  final protected CodeStyleSettings mySettings;
  protected Alignment myChildAlignment = Alignment.createAlignment();

  protected List<Block> mySubBlocks = null;


  public ClojureBlock(@NotNull final ASTNode node, @Nullable final Alignment alignment, @NotNull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings) {
    myNode = node;
    myAlignment = alignment;
    setAlignment(alignment);
    myIndent = indent;
    myWrap = wrap;
    mySettings = settings;
  }

  @NotNull
  public ASTNode getNode() {
    return myNode;
  }

  @NotNull
  public CodeStyleSettings getSettings() {
    return mySettings;
  }

  @NotNull
  public TextRange getTextRange() {
    return myNode.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks() {
    if (mySubBlocks == null) {
      mySubBlocks = ClojureBlockGenerator.generateSubBlocks(myNode, myAlignment, myWrap, mySettings, this);
    }
    return mySubBlocks;
  }

  @Nullable
  public Wrap getWrap() {
    return myWrap;
  }

  @Nullable
  public Indent getIndent() {
    return myIndent;
  }

  @Nullable
  public Alignment getAlignment() {
    return myAlignment;
  }

  public Spacing getSpacing(Block child1, Block child2) {
    return ClojureSpacingProcessor.getSpacing(child1, child2);
  }

  @NotNull
  public ChildAttributes getChildAttributes(final int newChildIndex) {
    return getAttributesByParent();
  }

  private ChildAttributes getAttributesByParent() {
    ASTNode astNode = getNode();
    final PsiElement psiParent = astNode.getPsi();
    if (psiParent instanceof ClojureFile) {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    if (LIST_LIKE_FORMS.contains(astNode.getElementType())) {
      return new ChildAttributes(Indent.getNormalIndent(), myChildAlignment);
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }


  public boolean isIncomplete() {
    return isIncomplete(myNode);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@NotNull final ASTNode node) {
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
        (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null &&
        (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

  public void setAlignment(Alignment alignment) {
    myChildAlignment = alignment;
  }
}
