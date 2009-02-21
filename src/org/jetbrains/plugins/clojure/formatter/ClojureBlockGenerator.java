package org.jetbrains.plugins.clojure.formatter;

import com.intellij.lang.ASTNode;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.codeStyle.CodeStyleSettings;

import java.util.List;
import java.util.ArrayList;

import org.jetbrains.plugins.clojure.formatter.processors.ClojureIndentProcessor;

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

    // For other cases
    final ArrayList<Block> subBlocks = new ArrayList<Block>();
    ASTNode children[] = myNode.getChildren(null);
    ASTNode prevChildNode = null;
    for (ASTNode childNode : children) {
      if (canBeCorrectBlock(childNode)) {
        final Indent indent = ClojureIndentProcessor.getChildIndent(myBlock, prevChildNode, childNode);
        subBlocks.add(new ClojureBlock(childNode, myAlignment, indent, myWrap, mySettings));
        prevChildNode = childNode;
      }
    }
    return subBlocks;
  }

  private static boolean canBeCorrectBlock(final ASTNode node) {
    return (node.getText().trim().length() > 0);
  }
  



}
