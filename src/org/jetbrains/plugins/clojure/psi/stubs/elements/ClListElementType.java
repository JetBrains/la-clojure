package org.jetbrains.plugins.clojure.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.impl.defs.ClDefImpl;
import org.jetbrains.plugins.clojure.psi.impl.list.ClListImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClDefStubImpl;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;

import java.io.IOException;

/**
 * @author peter
 */
public class ClListElementType extends ClStubElementType<EmptyStub, ClListImpl> {

  public ClListElementType() {
    super("list");
  }

  public void serialize(EmptyStub stub, StubOutputStream dataStream) throws IOException {
  }

  public EmptyStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new EmptyStub(parentStub, this);
  }

  public PsiElement createElement(ASTNode node) {
    return new ClListImpl(node);
  }

  public ClListImpl createPsi(EmptyStub stub) {
    return new ClListImpl(stub, this);
  }

  public EmptyStub createStub(ClListImpl psi, StubElement parentStub) {
    return new EmptyStub(parentStub, this);
  }

}
