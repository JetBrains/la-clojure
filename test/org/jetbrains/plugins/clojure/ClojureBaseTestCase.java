package org.jetbrains.plugins.clojure;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LocalTimeCounter;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.formatter.codeStyle.ClojureCodeStyleSettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author ilyas
 */
public abstract class ClojureBaseTestCase extends PsiTestCase {

  protected static final String SOURCE_FILE_EXT = ".clj";
  protected static final String TEST_FILE_EXT = ".test";

  private Project myProject;
  protected IdeaProjectTestFixture myFixture;
  protected CodeStyleSettings mySettings;

  public Project getProject() {
    return myProject;
  }

  public abstract String getDataPath();

  protected CodeStyleSettings getSettings() {
    return CodeStyleSettingsManager.getSettings(myProject);
  }

  protected void setSettings() {
    final ClojureFileType fileType = ClojureFileType.CLOJURE_FILE_TYPE;
    mySettings = getSettings();
    mySettings.getIndentOptions(fileType).INDENT_SIZE = 2;
    mySettings.getIndentOptions(fileType).CONTINUATION_INDENT_SIZE = 2;
    mySettings.getIndentOptions(fileType).TAB_SIZE = 2;

    ClojureCodeStyleSettings css = mySettings.getCustomSettings(ClojureCodeStyleSettings.class);
//    css.ALIGN_CLOJURE_FORMS = true;
  }

  protected void setUp() {
    myFixture = createFixture();

    try {
      myFixture.setUp();
    } catch (Exception e) {
      e.printStackTrace();
    } catch (AssertionFailedError ae) {
      // mute
    } catch (AssertionError ae) {
      // mute
    }
    myProject = myFixture.getProject();
    ClojureLoader.loadClojure();
    setSettings();
  }

  protected IdeaProjectTestFixture createFixture() {
    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
    return fixtureBuilder.getFixture();
  }

  protected void tearDown() {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      public void run() {
        try {
          myFixture.tearDown();
        } catch (Exception e) {
          // mute
        }
      }
    });
  }

  protected PsiFile createPseudoPhysicalFile(final Project project, final String fileName, final String text) throws IncorrectOperationException {
    String tmpFile = project.getBaseDir() + fileName;
    return PsiFileFactory.getInstance(project).createFileFromText(tmpFile,
        FileTypeManager.getInstance().getFileTypeByFileName(fileName),
        text,
        LocalTimeCounter.currentTime(),
        true);
  }

  public  String getTestName() {
    final String s = getName().substring(4);
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  protected String fetchFile(String dir, String fileName, String extension) {
    File file = new File(getDataPath() +
        (dir == null || dir.trim().equals("") ? "" : dir + "/") +
        fileName +
        extension);

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
          contents.append("\n");
          contents.append(line);
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return contents.toString();
  }
}
