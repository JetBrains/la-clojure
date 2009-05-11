package org.jetbrains.plugins.clojure.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;

/**
 * @author ilyas
 */
public class ClojureNsNameIndex extends StringStubIndexExtension<ClNs> {
  public static final StubIndexKey<String, ClNs> KEY = StubIndexKey.createIndexKey("clj.ns.name");

  public StubIndexKey<String, ClNs> getKey() {
    return KEY;
  }
}