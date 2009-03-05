package org.jetbrains.plugins.clojure.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.plugins.clojure.formatter.processors.ClojureIndentProcessor;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.psi.api.ClMap;
import org.jetbrains.plugins.clojure.psi.api.ClBraced;
import org.jetbrains.plugins.clojure.psi.api.ClLiteral;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class ClojureBlockGenerator {

  private static ASTNode myNode;
  private static Alignment myAlignment;
  private static Wrap myWrap;
  private static CodeStyleSettings mySettings;
  private static ClojureBlock myBlock;

  public static List<Block> generateSubBlocks(ASTNode node, Alignment alignment, Wrap wrap, CodeStyleSettings settings, ClojureBlock block) {
    myNode = node;
    myWrap = wrap;
    mySettings = settings;
    myAlignment = alignment;
    myBlock = block;

    PsiElement blockPsi = myBlock.getNode().getPsi();

    final ArrayList<Block> subBlocks = new ArrayList<Block>();
    ASTNode children[] = myNode.getChildren(null);
    ASTNode prevChildNode = null;


    final Alignment align1 = Alignment.createAlignment();
    for (ASTNode childNode : children) {
      if (canBeCorrectBlock(childNode)) {
        final Alignment align = mustAlign(blockPsi, childNode.getPsi()) ? align1 : null;
        final Indent indent = ClojureIndentProcessor.getChildIndent(myBlock, prevChildNode, childNode);
        subBlocks.add(new ClojureBlock(childNode, align, indent, myWrap, mySettings));
        prevChildNode = childNode;
      }
    }
    return subBlocks;
  }

  private static boolean mustAlign(PsiElement blockPsi, PsiElement child) {

    if (blockPsi instanceof ClVector || blockPsi instanceof ClMap) {
      return !(child instanceof LeafPsiElement);
    }
    if (blockPsi instanceof ClLiteral) {
      ASTNode node = blockPsi.getNode();
      assert node != null;
      ASTNode[] elements = node.getChildren(null);
      if (elements.length > 0 && elements[0].getElementType() == ClojureTokenTypes.STRING_LITERAL){
        return true;
      }
    }
    return false;
  }

  private static boolean canBeCorrectBlock(final ASTNode node) {
    return (node.getText().trim().length() > 0);
  }


}
