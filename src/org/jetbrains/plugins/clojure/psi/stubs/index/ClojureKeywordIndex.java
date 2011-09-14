package org.jetbrains.plugins.clojure.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;

/**
 * @author ilyas
 */
public class ClojureKeywordIndex extends StringStubIndexExtension<ClKeyword> {
  public static final StubIndexKey<String, ClKeyword> KEY = StubIndexKey.createIndexKey("clj.keywords");

  public StubIndexKey<String, ClKeyword> getKey() {
    return KEY;
  }

  @Override
  public int getVersion() {
    return ClojureIndexVersion.VERSION;
  }
}