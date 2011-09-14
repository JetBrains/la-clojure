package org.jetbrains.plugins.clojure.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

/**
 * @author ilyas
 */
public class ClojureClassNameIndex extends StringStubIndexExtension<ClojureFile> {
  public static final StubIndexKey<String, ClojureFile> KEY = StubIndexKey.createIndexKey("clj.class");

  public StubIndexKey<String, ClojureFile> getKey() {
    return KEY;
  }

  @Override
  public int getVersion() {
    return ClojureIndexVersion.VERSION;
  }
}
