package org.jetbrains.plugins.clojure.psi.stubs.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.stubs.api.ClFileStub;

/**
 * @author ilyas
 */
public class ClFileStubImpl extends PsiFileStubImpl<ClojureFile> implements ClFileStub {
  private final StringRef myPackageName;
  private final StringRef myClassName;
  private final boolean isClassDefinition;

  public ClFileStubImpl(ClojureFile file) {
    super(file);
    myPackageName = StringRef.fromString(file.getPackageName());
    isClassDefinition = file.isClassDefiningFile();
    myClassName = StringRef.fromString(file.getClassName());
  }

  public ClFileStubImpl(StringRef packName, StringRef name, boolean isScript) {
    super(null);
    myPackageName = packName;
    myClassName = name;
    this.isClassDefinition = isScript;
  }

  public IStubFileElementType getType() {
    return ClojureElementTypes.FILE;
  }

  public StringRef getPackageName() {
    return myPackageName;
  }

  public StringRef getClassName() {
    return myClassName;
  }

  public boolean isClassDefinition() {
    return isClassDefinition;
  }
}