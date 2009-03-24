package org.jetbrains.plugins.clojure.compiler;

import com.intellij.compiler.impl.javaCompiler.DependencyProcessor;
import com.intellij.compiler.make.DependencyCache;
import com.intellij.compiler.make.CacheCorruptedException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.ex.CompileContextEx;

/**
 * @author ilyas
 */
public class ClojureDependencyProcessor implements DependencyProcessor {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.compiler.ClojureDependencyProcessor");

  public void processDependencies(CompileContext context, int classQualifiedName) {
    final CompileContextEx contextEx = (CompileContextEx) context;
    final DependencyCache cache = contextEx.getDependencyCache();
    try {
      cache.resolve(classQualifiedName);
    } catch (CacheCorruptedException e) {
      LOG.info(e);
    }
  }
}