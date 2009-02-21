package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.impl.*;

/**
 * @author ilyas
 */
public class ClojurePsiCreator {

  public static PsiElement createElement(ASTNode node) {
    final IElementType elementType = node.getElementType();

    if (elementType == ClojureElementTypes.LIST) return new ClList(node);
    if (elementType == ClojureElementTypes.VECTOR) return new ClVector(node);
    if (elementType == ClojureElementTypes.MAP) return new ClMap(node);
    if (elementType == ClojureElementTypes.SET) return new ClMap(node);

    if (elementType == ClojureElementTypes.QUOTED_FORM) return new ClQuotedForm(node);
    if (elementType == ClojureElementTypes.META_FORM) return new ClMetaForm(node);
    if (elementType == ClojureElementTypes.METADATA) return new ClMetadata(node);

    if (elementType == ClojureElementTypes.VARIABLE) return new ClSymbol(node);
    if (elementType == ClojureElementTypes.DEFN) return new ClDefn(node);
    if (elementType == ClojureElementTypes.DEFNDASH) return new ClDefnDash(node);
    if (elementType == ClojureElementTypes.DEF) return new ClDef(node);
    if (elementType == ClojureElementTypes.BINDINGS) return new ClBindings(node);
    if (elementType == ClojureElementTypes.KEY) return new ClKey(node);
    if (elementType == ClojureElementTypes.LITERAL) return new ClLiteral(node);
    if (elementType == ClojureElementTypes.TOPLIST) return new ClTopLevelList(node);
    if (elementType == ClojureElementTypes.BACKQUOTED_EXPRESSION) return new ClBackQuotedExpression(node);
    if (elementType == ClojureElementTypes.SHARP_EXPRESSION) return new ClSharp(node);
    if (elementType == ClojureElementTypes.TILDA_EXPRESSION) return new ClTilda(node);
    if (elementType == ClojureElementTypes.AT_EXPRESSION) return new ClAt(node);
    if (elementType == ClojureElementTypes.TILDAAT_EXPRESSION) return new ClTildaAt(node);

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }


}
