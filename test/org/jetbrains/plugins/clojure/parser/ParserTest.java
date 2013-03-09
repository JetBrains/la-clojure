package org.jetbrains.plugins.clojure.parser;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.jetbrains.plugins.clojure.ClojureBaseTestCase;
import org.junit.Test;
import junit.framework.Assert;

import java.io.File;
import java.io.IOException;


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
public class ParserTest extends ClojureBaseTestCase {

  private static final String DATA_PATH = System.getProperty("user.dir") + "/testdata/parser/";

  public String getDataPath() {
    return DATA_PATH;
  }

  public void doParse(String fileName) {
    String contents = fetchFile("", fileName, TEST_FILE_EXT);
    PsiFile psiFile = createPseudoPhysicalFile(getProject(), "clj_98.clj", contents);
    String psiTree = DebugUtil.psiToString(psiFile, false);

    try {
      assertEquals(FileUtil.loadFile(new File(getDataPath() + fileName + "-tree.txt")), psiTree);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testClojureFileType() {
    Assert.assertNotNull(FileTypeManager.getInstance().getFileTypeByFileName("foo.clj"));
  }

  @Test
  public void testSymbol() {
    doParse("symbol");
  }

  @Test
  public void testSymbol2() {
    doParse("symbol2");
  }

  @Test
  public void testInteger() {
    doParse("integer");
  }

  @Test
  public void testFloat() {
    doParse("float");
  }

  @Test
  public void testString() {
    doParse("string");
  }

  public void testMultilineString() {
    doParse("multiline_string");
  }

  public void testUnicodeSymbol() {
    doParse("unicode_symbol");
  }

  @Test
  public void testSexp1() {
    doParse("sexp");
  }

  @Test
  public void testSexp2() {
    doParse("sexp2");
  }

  @Test
  public void testQuote() {
    doParse("quote");
  }

  @Test
  public void testVector() {
    doParse("vector");
  }

  @Test
  public void testEmptyList() {
    doParse("empty_list");
  }

  @Test
  public void testEmptyVector() {
    doParse("empty_vector");
  }

  @Test
  public void testEmptyMap() {
    doParse("empty_map");
  }

  @Test
  public void testMap() {
    doParse("map");
  }

  @Test
  public void testMetadata() {
    doParse("meta");
  }

  @Test
  public void testLet() {
    doParse("let");
  }

  @Test
  public void testFn() {
    doParse("fn");
  }

  @Test
  public void testSexp3() {
    doParse("sexp3");
  }

  @Test
  public void testSexp4() {
    doParse("sexp4");
  }

  @Test
  public void testSexp45() {
    doParse("sexp45");
  }

  @Test
  public void testDefn() {
    doParse("defn");
  }

  @Test
  public void testDefn2() {
    doParse("defn2");
  }

  public void testDefn3() {
    doParse("defn3");
  }

  public void testString1() {
    doParse("str1");
  }

  public void testString2() {
    doParse("uncompl");
  }

  public void testString4() {
    doParse("str4");
  }

  public void testSym1() {
    doParse("symbols/sym1");
  }

  public void testSym2() {
    doParse("symbols/sym2");
  }

  public void testSym3() {
    doParse("symbols/sym3");
  }

  public void testSym4() {
    doParse("symbols/sym4");
  }

  public void testSym5() {
    doParse("symbols/sym5");
  }

}
