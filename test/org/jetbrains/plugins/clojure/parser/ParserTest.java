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
import org.jetbrains.plugins.clojure.util.PathUtil;
import junit.framework.TestCase;
import junit.framework.Assert;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
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
public class ParserTest extends TestCase {

  protected Project myProject;
  protected Module myModule;
  private static final String DATA_PATH = PathUtil.getDataPath(ParserTest.class);

  protected IdeaProjectTestFixture myFixture;
  private static final String TEST_FILE_EXT = ".test";

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
    Assert.assertNotNull(FileTypeManager.getInstance().getFileTypeByFileName("foo.clj"));
  }

  public void parseit(String fileName) {
    File file = new File(DATA_PATH + fileName + TEST_FILE_EXT);
    Assert.assertTrue(file.exists());

    StringBuilder contents = new StringBuilder();
    try {
      BufferedReader input = new BufferedReader(new FileReader(file));
      try {
        String line = null;
        if ((line = input.readLine()) != null) {
          contents.append(line);
        }
        while ((line = input.readLine()) != null) {
          contents.append(System.getProperty("line.separator"));
          contents.append(line);
        }
      }
      finally {
        input.close();
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

    PsiFile psiFile = createPseudoPhysicalFile(myProject, "test.clj", contents.toString());
    String psiTree = DebugUtil.psiToString(psiFile, false);
    System.out.println(psiTree);
  }

  @Test
  public void testSymbol() {
    parseit("symbol");
  }

  @Test
  public void testSymbol2() {
    parseit("symbol2");
  }

  @Test
  public void testInteger() {
    parseit("integer");
  }

  @Test
  public void testFloat() {
    parseit("float");
  }

  @Test
  public void testString() {
    parseit("string");
  }

  public void testMultilineString() {
    parseit("multiline_string");
  }


  @Test
  public void testSexp1() {
    parseit("sexp");
  }

  @Test
  public void testSexp2() {
    parseit("sexp2");
  }

  @Test
  public void testQuote() {
    parseit("quote");
  }

  @Test
  public void testVector() {
    parseit("vector");
  }

  @Test
  public void testEmptyList() {
    parseit("empty_list");
  }

  @Test
  public void testEmptyVector() {
    parseit("empty_vector");
  }

  @Test
  public void testEmptyMap() {
    parseit("empty_map");
  }

  @Test
  public void testMap() {
    parseit("map");
  }

  @Test
  public void testMetadata() {
    parseit("meta");
  }

  @Test
  public void testLet() {
    parseit("let");
  }

  @Test
  public void testFn() {
    parseit("fn");
  }

  @Test
  public void testSexp3() {
    parseit("sexp3");
  }

  @Test
  public void testSexp4() {
    parseit("sexp4");
  }

  @Test
  public void testSexp45() {
    parseit("sexp45");
  }

  @Test
  public void testDefn() {
    parseit("defn");
  }

  @Test
  public void testDefn2() {
    parseit("defn2");
  }

  public void testDefn3() {
    parseit("defn3");
  }

  public void testString1() {
    parseit("str1");
  }

  public void testString2() {
    parseit("uncompl");
  }

  public void testString4() {
    parseit("str4");
  }

  public void testSym1() {
    parseit("symbols/sym1");
  }

  public void testSym2() {
    parseit("symbols/sym2");
  }

  public void testSym3() {
    parseit("symbols/sym3");
  }

  public void testSym4() {
    parseit("symbols/sym4");
  }

  public void testSym5() {
    parseit("symbols/sym5");
  }

}
