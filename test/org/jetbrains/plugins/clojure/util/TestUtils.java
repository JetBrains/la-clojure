package org.jetbrains.plugins.clojure.util;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author ilyas
 */
public class TestUtils {

  private static final Logger LOG = Logger.getInstance("org.jetbrains.plugins.clojure.util.TestUtils");

  private static final String[] RUN_PATHES = new String[]{
      "out/test/clojure-plugin",
// if tests are run using ant script
      "dist/testClasses"};

  private static String TEST_DATA_PATH = null;

  public static final String CARET_MARKER = "<caret>";
  public static final String BEGIN_MARKER = "<begin>";
  public static final String END_MARKER = "<end>";

  public static String getTestDataPath() {
    if (TEST_DATA_PATH == null) {
      ClassLoader loader = TestUtils.class.getClassLoader();
      URL resource = loader.getResource("testdata");
      try {
        TEST_DATA_PATH = new File("testdata").getAbsolutePath();
        if (resource != null) {
          TEST_DATA_PATH = new File(resource.toURI()).getPath().replace(File.separatorChar, '/');
        }
      } catch (URISyntaxException e) {
        LOG.error(e);
        return null;
      }
    }
    return TEST_DATA_PATH;
  }

  public static String getMockJdk() {
    return getTestDataPath() + "/mockJDK";
  }


  public static String getMockClojureLib() {
    return getTestDataPath() + "/mockClojureLib/clojure-1.5.jar" ;
  }

  public static String getMockClojureContribLib() {
    return getTestDataPath() + "/mockClojureLib/clojure-contrib.jar" ;
  }

  @Nullable
  public static String getDataPath(@NotNull Class clazz) {
    final String classDir = getClassRelativePath(clazz);
    String moduleDir = getModulePath(clazz);
    return classDir != null && moduleDir != null ? moduleDir + "/" + classDir + "/data/" : null;
  }

  public static String getOutputPath(final Class clazz) {
    final String classDir = getClassRelativePath(clazz);
    String moduleDir = getModulePath(clazz);
    return classDir != null && moduleDir != null ? moduleDir + "/" + classDir + "/output/" : null;
  }

  @Nullable
  public static String getDataPath(@NotNull Class s, @NotNull final String relativePath) {
    return getDataPath(s) + "/" + relativePath;
  }

  @Nullable
  public static String getClassRelativePath(@NotNull Class s) {
    String classFullPath = getClassFullPath(s);
    for (String path : RUN_PATHES) {
      final String dataPath = getClassDirPath(classFullPath, path);
      if (dataPath != null) {
        return dataPath;
      }
    }
    return null;
  }

  @Nullable
  public static String getModulePath(@NotNull Class s) {
    String classFullPath = getClassFullPath(s);
    for (String path : RUN_PATHES) {
      final String dataPath = getModulePath(classFullPath, path);
      if (dataPath != null) {
        return dataPath;
      }
    }
    return null;
  }

  public static String getClassFullPath(@NotNull final Class s) {
    String name = s.getSimpleName() + ".class";
    final URL url = s.getResource(name);
    return url.getPath();
  }

  @Nullable
  private static String getModulePath(@NotNull String s, @NotNull final String indicator) {
    int n = s.indexOf(indicator);
    if (n == -1) {
      return null;
    }
    return s.substring(0, n - 1);
  }

  @Nullable
  private static String getClassDirPath(@NotNull String s, @NotNull final String indicator) {
    int n = s.indexOf(indicator);
    if (n == -1) {
      return null;
    }
    s = "test" + s.substring(n + indicator.length());
    s = s.substring(0, s.lastIndexOf('/'));
    return s;
  }
}
