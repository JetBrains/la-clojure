package org.jetbrains.plugins.clojure.compiler;

import com.intellij.testFramework.CompilerTester;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.File;

/**
 * @author peter
 */
public class ClojureCompilerTest extends JavaCodeInsightFixtureTestCase {
  private CompilerTester myTester;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myTester = new CompilerTester(myModule);
    ClojureCompilerSettings.getInstance(getProject()).getState().COMPILE_CLOJURE = true;
    File jar = new File(TestUtils.getMockClojureLib());
    PsiTestUtil.addLibrary(myModule, "clojure", jar.getParent(), jar.getName());
    
  }

  @Override
  protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
    moduleBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
    super.tuneFixture(moduleBuilder);
  }

  public void testCompileClass() {
    myFixture.addClass("class Foo {}");
    myFixture.addFileToProject("hello.clj", "(ns hello (:gen-class))\n" +
        "(defn -main [arg]  (println (str \"Hello!\")))");
    assertEmpty(myTester.make());
    assertNotNull(myTester.findClassFile("hello", myModule));
  }

  public void testDontCompileNoGenClass() {
    myFixture.addClass("class Foo {}");
    myFixture.addFileToProject("hello.clj", "(ns hello)\n" +
        "(defn -main [arg]  (println (str \"Hello!\")))");
    assertEmpty(myTester.make());
    assertNull(myTester.findClassFile("hello__init", myModule));
  }

  @Override
  protected void tearDown() throws Exception {
    com.intellij.util.ui.UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      public void run() {
        myTester.tearDown();
        try {
          ClojureCompilerTest.super.tearDown();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
