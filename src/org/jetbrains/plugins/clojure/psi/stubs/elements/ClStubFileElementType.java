package org.jetbrains.plugins.clojure.psi.stubs.elements;

import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;

import java.io.IOException;

import org.jetbrains.plugins.clojure.psi.stubs.ClojureFileStubBuilder;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureClassNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClojureFullScriptNameIndex;
import org.jetbrains.plugins.clojure.psi.stubs.impl.ClFileStubImpl;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClFileStub;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClStubFileElementType extends IStubFileElementType<ClFileStub> {
  private static final int CACHES_VERSION = 10;

  public ClStubFileElementType() {
    super(ClojureFileType.CLOJURE_LANGUAGE);
  }

  public StubBuilder getBuilder() {
    return new ClojureFileStubBuilder();
  }

  @Override
  public int getStubVersion() {
    return super.getStubVersion() + CACHES_VERSION;
  }

  public String getExternalId() {
    return "clojure.FILE";
  }

  @Override
  public void indexStub(PsiFileStub stub, IndexSink sink) {
    super.indexStub(stub, sink);
  }

  @Override
  public void serialize(final ClFileStub stub, final StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getPackageName().toString());
    dataStream.writeName(stub.getName().toString());
    dataStream.writeBoolean(stub.isClassDefinition());
  }

  @Override
  public ClFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    StringRef packName = dataStream.readName();
    StringRef name = dataStream.readName();
    boolean isScript = dataStream.readBoolean();
    return new ClFileStubImpl(packName, name, isScript);
  }

  public void indexStub(ClFileStub stub, IndexSink sink) {
    String name = stub.getName().toString();
    if (stub.isClassDefinition() && name != null) {
      sink.occurrence(ClojureClassNameIndex.KEY, name);
      final String pName = stub.getPackageName().toString();
      final String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
      sink.occurrence(ClojureFullScriptNameIndex.KEY, fqn.hashCode());
    }
  }

}