package org.jetbrains.plugins.clojure.parser;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.junit.Test;
import junit.framework.TestCase;


/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 5, 2009
 * Time: 2:11:20 PM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ParserTest extends TestCase {

  protected Project myProject;
  protected Module myModule;
  protected IdeaProjectTestFixture myFixture;

  protected void setUp() {
    myFixture = createFixture();

    try {
      myFixture.setUp();
    }
    catch (Exception e) {
      throw new Error(e);
    }
    myModule = myFixture.getModule();
    myProject = myModule.getProject();
  }

  protected IdeaProjectTestFixture createFixture() {
    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
    return fixtureBuilder.getFixture();
  }

  protected void tearDown() {
    try {
      myFixture.tearDown();
    }
    catch (Exception e) {
      throw new Error(e);
    }
  }

  private static final String DATA_PATH = "/opt/clojure/src/clj/clojure"; //PathUtil.getDataPath(ParserTest.class);

  private PsiFile createPseudoPhysicalFile(final Project project, final String fileName, final String text) throws IncorrectOperationException {

    FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);


    return psiFileFactory.createFileFromText(
        fileName,
        fileType,
        text);
  }

  @Test
  public void testClojureFileType() {
    FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName("foo.clj");
    assert fileType != null;
  }

  public void parseit(String text) {
    //FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName("foo.clj");
    PsiFile psiFile = createPseudoPhysicalFile(myProject, "test.clj", text);
    String psiTree = DebugUtil.psiToString(psiFile, false);
    System.out.println(psiTree);
  }

  @Test
  public void testSymbol() {
    String sym =
        "foo";
    parseit(sym);
  }

  @Test
  public void testSymbol2() {
    String sym =
        ".foo*";
    parseit(sym);
  }

  @Test
  public void testInteger() {
    String sym =
        "123";
    parseit(sym);
  }

  @Test
  public void testFloat() {
    String sym =
        "123.456";
    parseit(sym);
  }

  @Test
  public void testString() {
    String sym =
        "\"123.456\"";
    parseit(sym);
  }

  @Test
  public void testMultilineString() {
    String sym =
        "\"this is\n" +
            "a multiline\n" +
            "string\"";
    parseit(sym);
  }


  @Test
  public void testSexp1() {

    String sexp =
        "(a b)";
    parseit(sexp);
  }

  @Test
  public void testSexp2() {

    String sexp =
        "(a b (c d))";
    parseit(sexp);
  }

  @Test
  public void testQuote() {

    String sexp =
        "'(a b (c d))";
    parseit(sexp);
  }

  @Test
  public void testVector() {

    String sexp =
        "[a b (c d)]";
    parseit(sexp);
  }

  @Test
  public void testEmptyList() {
    parseit("()");
  }

  @Test
  public void testEmptyVector() {
    parseit("[]");
  }

  @Test
  public void testEmptyMap() {
    parseit("{}");
  }

  @Test
  public void testMap() {

    String sexp =
        "{ :a a :b b :cd (c d)}";
    parseit(sexp);
  }

  @Test
  public void testMetadata() {

    String md =
        "#^{:foo \"bah\"}";
    parseit(md);
  }

  @Test
  public void testLet() {
    parseit("(let [[a b c & d :as e] [1 2 3 4 5 6 7]] [a b c d e])");
  }

  @Test
  public void testFn() {
    parseit("(fn [cs] (if (pos? (count cs))\n" +
        "                            (into-array (map totype cs))\n" +
        "                            (make-array Type 0)))");
  }

  @Test
  public void testSexp3() {
    parseit("(fn [[m p]] {(str m) [p]})");
  }

  @Test
  public void testSexp4() {
    parseit("(apply merge-with concat {} all-sigs)");
  }

  @Test
  public void testSexp45() {
    parseit("(:static ^foo)");
  }

  @Test
  public void testDefn() {

    String defn =
        "(defn\n" +
            "#^{:doc \"mymax [xs+] gets the maximum value in xs using > \"\n" +
            "   :test (fn []\n" +
            "             (assert (= 42  (max 2 42 5 4))))\n" +
            "   :user/comment \"this is the best fn ever!\"}\n" +
            "  mymax\n" +
            "  ([x] x)\n" +
            "  ([x y] (if (> x y) x y))\n" +
            "  ([x y & more]\n" +
            "   (reduce mymax (mymax x y) more)))";
    parseit(defn);
  }

  @Test
  public void testDefn2() {

    String defn = "(defn- non-private-methods [#^Class c]\n" +
        "  (loop [mm {}\n" +
        "         considered #{}\n" +
        "         c c]\n" +
        "    (if c\n" +
        "      (let [[mm considered]\n" +
        "            (loop [mm mm\n" +
        "                   considered considered\n" +
        "                   meths (concat\n" +
        "                          (seq (. c (getDeclaredMethods)))\n" +
        "                          (seq (. c (getMethods))))]\n" +
        "              (if meths\n" +
        "                (let [#^java.lang.reflect.Method meth (first meths)\n" +
        "                      mods (. meth (getModifiers))\n" +
        "                      mk (method-sig meth)]\n" +
        "                  (if (or (considered mk)\n" +
        "                          (. Modifier (isPrivate mods))\n" +
        "                          (. Modifier (isStatic mods))\n" +
        "                          (. Modifier (isFinal mods))\n" +
        "                          (= \"finalize\" (.getName meth)))\n" +
        "                    (recur mm (conj considered mk) (rest meths))\n" +
        "                    (recur (assoc mm mk meth) (conj considered mk) (rest meths))))\n" +
        "                [mm considered]))]\n" +
        "        (recur mm considered (. c (getSuperclass))))\n" +
        "      mm)))";

    parseit(defn);
  }

  @Test
  public void testDefn3() {

    String defn = "    (defn- non-private-methods [#^Class c]\n" +
        "  (loop [mm {}\n" +
        "         considered #{}\n" +
        "         c c]\n" +
        "    (if c\n" +
        "      (let [[mm considered]\n" +
        "            (loop [mm mm\n" +
        "                   considered considered\n" +
        "                   meths (concat\n" +
        "                          (seq (. c (getDeclaredMethods)))\n" +
        "                          (seq (. c (getMethods))))]\n" +
        "              (if meths\n" +
        "                (let [#^java.lang.reflect.Method meth (first meths)\n" +
        "                      mods (. meth (getModifiers))\n" +
        "                      mk (method-sig meth)]\n" +
        "                  (if (or (considered mk)\n" +
        "                          (. Modifier (isPrivate mods))\n" +
        "                          (. Modifier (isStatic mods))\n" +
        "                          (. Modifier (isFinal mods))\n" +
        "                          (= \"finalize\" (.getName meth)))\n" +
        "                    (recur mm (conj considered mk) (rest meths))\n" +
        "                    (recur (assoc mm mk meth) (conj considered mk) (rest meths))))\n" +
        "                [mm considered]))]\n" +
        "        (recur mm considered (. c (getSuperclass))))\n" +
        "      mm)))";

    parseit(defn);
  }

}
