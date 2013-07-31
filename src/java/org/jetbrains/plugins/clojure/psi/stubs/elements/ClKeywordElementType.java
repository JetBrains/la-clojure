package org.jetbrains.plugins.clojure.psi.stubs.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.impl.ClKeywordImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClKeywordStub;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClKeywordStubImpl;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureKeywordIndex;

import java.io.IOException;

/**
 * @author ilyas
 */
public class ClKeywordElementType extends ClStubElementType<ClKeywordStub, ClKeyword> {

  public ClKeywordElementType() {
    super("key definition");
  }

  public void serialize(ClKeywordStub stub, StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  public ClKeywordStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef ref = dataStream.readName();
    return new ClKeywordStubImpl(parentStub, ref, this);
  }

  public PsiElement createElement(ASTNode node) {
    return new ClKeywordImpl(node);
  }

  public ClKeyword createPsi(ClKeywordStub stub) {
    return new ClKeywordImpl(stub, ClojureElementTypes.KEYWORD);
  }

  public ClKeywordStub createStub(ClKeyword psi, StubElement parentStub) {
    return new ClKeywordStubImpl(parentStub, StringRef.fromString(psi.getName()), ClojureElementTypes.KEYWORD);
  }

  @Override
  public void indexStub(ClKeywordStub stub, IndexSink sink) {
    final String name = stub.getName();
    if (name != null) {
      sink.occurrence(ClojureKeywordIndex.KEY, name);
    }
  }
}