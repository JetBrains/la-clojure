package org.jetbrains.plugins.clojure.psi.stubs.elements.ns;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClNsImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;

/**
 * @author ilyas
 */
public class ClNsElementType extends ClNsElementTypeBase {
  public ClNsElementType() {
    super("ns");
  }

  public PsiElement createElement(ASTNode node) {
    return new ClNsImpl(node);
  }

  public ClNs createPsi(ClNsStub stub) {
    return new ClNsImpl(stub, ClojureElementTypes.NS);
  }

  public ClNsStub createStub(ClNs psi, StubElement parentStub) {
    return new ClNsStub(parentStub, StringRef.fromString(psi.getDefinedName()), ClojureElementTypes.NS, psi.getTextOffset());
  }



}