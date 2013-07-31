package org.jetbrains.plugins.clojure.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClKeywordStub;

/**
 * @author ilyas
 */
public class ClKeywordStubImpl extends StubBase<ClKeyword> implements ClKeywordStub {

  private final StringRef myName;

  public ClKeywordStubImpl(StubElement parent, StringRef name, IStubElementType elementType) {
    super(parent, elementType);
    myName = name;
  }

  public String getName() {
    return StringRef.toString(myName);
  }
}
