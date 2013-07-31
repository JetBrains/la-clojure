package org.jetbrains.plugins.clojure.psi.stubs.api;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClDefStub;

/**
 * @author ilyas
 */
public class ClDefStub extends StubBase<ClDef> implements NamedStub<ClDef> {
  private final StringRef myName;
  private final int myTextOffset;

  public ClDefStub(StubElement parent, StringRef name, final IStubElementType elementType, int textOffset) {
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