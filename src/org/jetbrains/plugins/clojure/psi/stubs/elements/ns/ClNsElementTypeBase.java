package org.jetbrains.plugins.clojure.psi.stubs.elements.ns;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClNsStubImpl;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;

import java.io.IOException;

/**
 * @author ilyas
 */
public abstract class ClNsElementTypeBase extends ClStubElementType<ClNsStub, ClNs> {

  public ClNsElementTypeBase(String dName) {
    super(dName);
  }

  public void serialize(ClNsStub stub, StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  public ClNsStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef ref = dataStream.readName();
    return new ClNsStubImpl(parentStub, ref, this);
  }

  @Override
  public void indexStub(ClNsStub stub, IndexSink sink) {
    final String name = stub.getName();
    if (name != null) {
      sink.occurrence(ClojureNsNameIndex.KEY, name);
    }
  }
}