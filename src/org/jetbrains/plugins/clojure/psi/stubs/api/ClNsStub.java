package org.jetbrains.plugins.clojure.psi.stubs.api;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClNsStub;

/**
 * @author ilyas
 */
public class ClNsStub extends StubBase<ClNs> implements NamedStub<ClNs> {
  private final StringRef myName;
  private final int myTextOffset;

  public ClNsStub(StubElement parent, StringRef name, final IStubElementType elementType, int textOffset) {
    super(parent, elementType);
    myName = name;
    myTextOffset = textOffset;
  }

  public int getTextOffset() {
    return myTextOffset;
  }

  public String getName() {
    return StringRef.toString(myName);
  }

}