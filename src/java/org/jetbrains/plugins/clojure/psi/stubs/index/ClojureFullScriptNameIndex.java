package org.jetbrains.plugins.clojure.psi.stubs.index;

import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;

import java.util.Collection;

import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.impl.search.ClojureSourceFilterScope;

/**
 * @author ilyas
 */
public class ClojureFullScriptNameIndex extends IntStubIndexExtension<ClojureFile> {
  public static final StubIndexKey<Integer, ClojureFile> KEY = StubIndexKey.createIndexKey("clj.script.fqn");

  private static final ClojureFullScriptNameIndex ourInstance = new ClojureFullScriptNameIndex();
  public static ClojureFullScriptNameIndex getInstance() {
    return ourInstance;
  }

  public StubIndexKey<Integer, ClojureFile> getKey() {
    return KEY;
  }

  public Collection<ClojureFile> get(final Integer integer, final Project project, final GlobalSearchScope scope) {
    return super.get(integer, project, new ClojureSourceFilterScope(scope, project));
  }

  @Override
  public int getVersion() {
    return ClojureIndexVersion.VERSION;
  }
}