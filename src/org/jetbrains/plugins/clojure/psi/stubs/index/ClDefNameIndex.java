package org.jetbrains.plugins.clojure.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;

/**
 * @author ilyas
 */
public class ClDefNameIndex extends StringStubIndexExtension<ClDef> {
  public static final StubIndexKey<String, ClDef> KEY = StubIndexKey.createIndexKey("clj.def.name");

  public StubIndexKey<String, ClDef> getKey() {
    return KEY;
  }

  @Override
  public int getVersion() {
    return ClojureIndexVersion.VERSION;
  }
}