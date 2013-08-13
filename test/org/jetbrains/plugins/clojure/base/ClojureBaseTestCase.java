package org.jetbrains.plugins.clojure.base;

import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LocalTimeCounter;
import junit.framework.Assert;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author ilyas
 */
public abstract class ClojureBaseTestCase extends LightPlatformCodeInsightFixtureTestCase {

  protected static final String SOURCE_FILE_EXT = ".clj";
  protected static final String TEST_FILE_EXT = ".test";

  private Project myProject;
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
  }

  protected void setUp() throws Exception {
    super.setUp();
    myFixture.setTestDataPath(getDataPath());
    myProject = myFixture.getProject();
    setSettings();
    setupLibraries();
  }

  private void setupLibraries() {
    ModifiableRootModel rootModel = null;
    final ModuleRootManager rootManager = ModuleRootManager.getInstance(myFixture.getModule());

    // Add Clojure Library
    OrderEnumerator libs = rootManager.orderEntries().librariesOnly();
    final ArrayList<Library.ModifiableModel> libModels = new ArrayList<Library.ModifiableModel>();

    rootModel = TestUtils.addLibrary(rootModel, rootManager, libs, libModels, "clojureLib", TestUtils.getMockClojureLib(), null);
    rootModel = TestUtils.addLibrary(rootModel, rootManager, libs, libModels, "clojureContrib", TestUtils.getMockClojureContribLib(), null);

    if (rootModel != null || !libModels.isEmpty()) {
      final ModifiableRootModel finalRootModel = rootModel;
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        public void run() {
          for (Library.ModifiableModel model : libModels) {
            model.commit();
          }
          if (finalRootModel != null) {
            finalRootModel.commit();
          }
          final StartupManagerImpl startupManager = (StartupManagerImpl) StartupManager.getInstance(myProject);
          startupManager.startCacheUpdate();
        }
      });
    }
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  protected PsiFile createPseudoPhysicalFile(final Project project, final String fileName, final String text) throws IncorrectOperationException {
    String tmpFile = project.getBaseDir() + fileName;
    return PsiFileFactory.getInstance(project).createFileFromText(tmpFile,
        FileTypeManager.getInstance().getFileTypeByFileName(fileName),
        text,
        LocalTimeCounter.currentTime(),
        true);
  }

  public String getTestName() {
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
        String line;
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
