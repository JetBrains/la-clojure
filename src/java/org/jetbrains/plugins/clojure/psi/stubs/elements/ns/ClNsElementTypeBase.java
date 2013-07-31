package org.jetbrains.plugins.clojure.psi.stubs.elements.ns;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.plugins.clojure.psi.ClStubElementType;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureNsNameIndex;

import java.io.IOException;
import java.util.List;

/**
 * @author ilyas
 */
public abstract class ClNsElementTypeBase extends ClStubElementType<ClNsStub, ClNs> {

  public ClNsElementTypeBase(String dName) {
    super(dName);
  }

  public void serialize(ClNsStub stub, StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeInt(stub.getTextOffset());
  }

  public ClNsStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef ref = dataStream.readName();
    int textOffset = dataStream.readInt();
    return new ClNsStub(parentStub, ref, this, textOffset);
  }

  @Override
  public void indexStub(ClNsStub stub, IndexSink sink) {
    final String name = stub.getName();
    if (name != null && name.trim().length() > 0) {
      final List<String> parcels = StringUtil.split(name, ".");
      final StringBuffer buffer = new StringBuffer();
      buffer.append(parcels.remove(0));
      sink.occurrence(ClojureNsNameIndex.KEY, buffer.toString());
      
      for (String parcel : parcels) {
        buffer.append(".").append(parcel);
        sink.occurrence(ClojureNsNameIndex.KEY, buffer.toString());
      }
    }
  }
}