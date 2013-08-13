package org.jetbrains.plugins.clojure.base;

import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Alefas
 * @since 16.01.13
 */
public abstract class ClojureLightPlatformCodeInsightTestCase extends LightPlatformCodeInsightTestCase {
  private static String JDK_HOME = TestUtils.getMockJdk();

  public String getTestDataPath() {
    return TestUtils.getTestDataPath();
  }

  protected boolean isConfigureSourceFolder() {
    return false;
  }

  @Override
  protected Sdk getProjectJDK() {
    return JavaSdk.getInstance().createJdk("java sdk", JDK_HOME, false);
  }

  protected void setUp() throws Exception {
    super.setUp();

    ModifiableRootModel rootModel = null;
    final ModuleRootManager rootManager = ModuleRootManager.getInstance(getModule());

    if (isConfigureSourceFolder()) {
      rootModel = ModuleRootManager.getInstance(getModule()).getModifiableModel();
      final String testDir = getTestFolderPath();

      // Configure source folder
      final File dir = new File(testDir);
      assertTrue(dir.exists());

      VirtualFile vDir = LocalFileSystem.getInstance().
          refreshAndFindFileByPath(dir.getCanonicalPath().replace(File.separatorChar, '/'));
      assertNotNull(vDir);
      ContentEntry contentEntry = rootModel.addContentEntry(vDir);
      contentEntry.addSourceFolder(vDir, false);
    }

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
          final StartupManagerImpl startupManager = (StartupManagerImpl) StartupManager.getInstance(ourProject);
          startupManager.startCacheUpdate();
        }
      });
    }
  }

  public String getTestFolderPath() {
    VirtualFile testDataRoot = LocalFileSystem.getInstance().findFileByPath(getTestDataPath());
    assertNotNull(testDataRoot);
    return testDataRoot.getPath() + File.separatorChar + getTestName(true) + File.separatorChar;
  }


}
