package org.jetbrains.plugins.clojure.psi.stubs.elements;

import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.impl.defs.ClDefImpl;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.lang.ASTNode;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * @author ilyas
 */
public class ClDefElementType extends ClStubElementType<ClDefStub, ClDef> {

  public ClDefElementType() {
    super("def-element");
  }

  public void serialize(ClDefStub stub, StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeInt(stub.getTextOffset());
  }

  public ClDefStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef ref = dataStream.readName();
    int textOffset = dataStream.readInt();
    return new ClDefStub(parentStub, ref, this, textOffset);
  }

  public PsiElement createElement(ASTNode node) {
    return new ClDefImpl(node);
  }

  public ClDef createPsi(ClDefStub stub) {
    return new ClDefImpl(stub, ClojureElementTypes.DEF);
  }

  public ClDefStub createStub(ClDef psi, StubElement parentStub) {
    return new ClDefStub(parentStub, StringRef.fromString(psi.getName()), ClojureElementTypes.DEF, psi.getTextOffset());
  }

  @Override
  public void indexStub(ClDefStub stub, IndexSink sink) {
    final String name = stub.getName();
    if (name != null) {
      sink.occurrence(ClDefNameIndex.KEY, name);
    }
  }
}
