package org.jetbrains.plugins.clojure.resolve;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.ResolveTestCase;
import org.jetbrains.plugins.clojure.ClojureLoader;
import org.jetbrains.plugins.clojure.util.TestUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author ilyas
 */
public abstract class ClojureResolveTestCaseBase extends ResolveTestCase {
  private static String JDK_HOME = TestUtils.getMockJdk();

  public abstract String getTestDataPath();

  private void configureFile(final VirtualFile vFile, String exceptName, final VirtualFile newDir) {
    if (vFile.isDirectory()) {
      for (VirtualFile file : vFile.getChildren()) {
        configureFile(file, exceptName, newDir);
      }
    } else {
      if (vFile.getName().equals(exceptName)) {
        return;
      }
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        public void run() {
          try {
            vFile.copy(null, newDir, vFile.getName());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }

  protected void setUp() throws Exception {
    super.setUp();
    ClojureLoader.loadClojure();

    final ModifiableRootModel rootModel = ModuleRootManager.getInstance(getModule()).getModifiableModel();

    final String testDir = getTestFolderPath();

    // Configure source folder
    final File dir = new File(testDir);
    assertTrue(dir.exists());

    VirtualFile vDir = LocalFileSystem.getInstance().
        refreshAndFindFileByPath(dir.getCanonicalPath().replace(File.separatorChar, '/'));
    assertNotNull(vDir);
    ContentEntry contentEntry = rootModel.addContentEntry(vDir);
    contentEntry.addSourceFolder(vDir, false);

    // Set Java SDK
    rootModel.setSdk(JavaSdk.getInstance().createJdk("java sdk", JDK_HOME, false));

    // Add Clojure Library
    LibraryTable libraryTable = rootModel.getModuleLibraryTable();
    Library clojureLib = libraryTable.createLibrary("clojureLib");
    final Library.ModifiableModel libModel = clojureLib.getModifiableModel();
    File lib = new File(TestUtils.getMockClojureLib());
    File contrib = new File(TestUtils.getMockClojureContribLib());
    assertTrue(lib.exists());
    assertTrue(contrib.exists());

    libModel.addRoot(VfsUtil.getUrlForLibraryRoot(lib), OrderRootType.CLASSES);
    libModel.addRoot(VfsUtil.getUrlForLibraryRoot(contrib), OrderRootType.CLASSES);

    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        libModel.commit();
        rootModel.commit();
      }
    });
  }

  public String getTestFolderPath() {
    VirtualFile testDataRoot = LocalFileSystem.getInstance().findFileByPath(getTestDataPath());
    assertNotNull(testDataRoot);
    return testDataRoot.getPath() + File.separatorChar + getTestName(true) + File.separatorChar;
  }
}
