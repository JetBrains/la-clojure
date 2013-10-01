package org.jetbrains.plugins.clojure;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import com.intellij.openapi.components.ApplicationComponent;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;

public class ClojureLoader implements ApplicationComponent {
  private static final Logger LOG = Logger.getLogger(ClojureLoader.class);

  private static final String INIT_CLOJURE = "org.jetbrains.plugins.clojure.init-clojure";

  public void initComponent() {
    ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();

    try {
      ClassLoader loader = ClojureLoader.class.getClassLoader();
      Thread.currentThread().setContextClassLoader(loader);

      StringWriter writer = new StringWriter();

      Class.forName("clojure.lang.RT");

      Var.pushThreadBindings(RT.map(clojure.lang.Compiler.LOADER, loader,
          RT.var("clojure.core", "*warn-on-reflection*"), true,
          RT.ERR, writer));

      RT.var("clojure.core", "require").invoke(Symbol.intern(INIT_CLOJURE));
      Var.find(Symbol.intern(INIT_CLOJURE + "/init")).invoke();

      String result = writer.toString();
      LOG.error("Reflection warnings:\n" + result);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      Var.popThreadBindings();
      Thread.currentThread().setContextClassLoader(oldLoader);
    }
  }


  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "clojure.support.loader";
  }

}
