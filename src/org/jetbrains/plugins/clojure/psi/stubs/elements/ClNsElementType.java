package org.jetbrains.plugins.clojure.psi.stubs.elements;

import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClNsNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClDefStubImpl;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClNsStubImpl;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.impl.defs.ClDefImpl;
import org.jetbrains.plugins.clojure.psi.impl.ns.ClNsImpl;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.PsiElement;
import com.intellij.util.io.StringRef;
import com.intellij.lang.ASTNode;

import java.io.IOException;

/**
 * @author ilyas
 */
public class ClNsElementType extends ClStubElementType<ClNsStub, ClNs> {

  public ClNsElementType() {
    super("element");
  }

  public void serialize(ClNsStub stub, StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  public ClNsStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef ref = dataStream.readName();
    return new ClNsStubImpl(parentStub, ref, this);
  }

  public PsiElement createElement(ASTNode node) {
    return new ClNsImpl(node);
  }

  public ClNs createPsi(ClNsStub stub) {
    return new ClNsImpl(stub, ClojureElementTypes.NS);
  }

  public ClNsStub createStub(ClNs psi, StubElement parentStub) {
    return new ClNsStubImpl(parentStub, StringRef.fromString(psi.getDefinedName()), ClojureElementTypes.NS);
  }

  @Override
  public void indexStub(ClNsStub stub, IndexSink sink) {
    final String name = stub.getName();
    if (name != null) {
      sink.occurrence(ClNsNameIndex.KEY, name);
    }
  }
}