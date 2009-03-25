package org.jetbrains.plugins.clojure.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.utils.LibrariesUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author ilyas
 */
public class ClojureConfigUtil {

  public static final String CLOJURE_JAR_NAME_PREFIX = "clojure";

  public static final String LIBRARY_PROPERTIES_PATH = "library.properties";

  public static final String CLOJURE_MAIN_CLASS_FILE = "clojure/main.class";

  public static final String VERSION_PROPERTY_KEY = "version.number";

  public static final String UNDEFINED_VERSION = "undefined";

  private static final Condition<Library> CLOJURE_LIB_CONDITION = new Condition<Library>() {
    public boolean value(Library library) {
      return isClojureLibrary(library);
    }
  };

  /**
   * Checks wheter given IDEA library contains Clojure Library classes
   */
  public static boolean isClojureLibrary(Library library) {
    return  library != null && checkLibrary(library, CLOJURE_JAR_NAME_PREFIX, CLOJURE_MAIN_CLASS_FILE);
  }

  static boolean checkLibrary(Library library, String jarNamePrefix, String necessaryClass) {
    boolean result = false;
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles) {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension())) {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists()) {
          try {
            JarFile jarFile = new JarFile(realFile);
            if (name.startsWith(jarNamePrefix)) {
              result = jarFile.getJarEntry(necessaryClass) != null;
            }
            jarFile.close();
          } catch (IOException e) {
            result = false;
          }
        }
      }
    }
    return result;
  }

  private static String getClojureVersion(@NotNull String jarPath) {
    String jarVersion = getClojureJarVersion(jarPath, LIBRARY_PROPERTIES_PATH);
    return jarVersion != null ? jarVersion : UNDEFINED_VERSION;
  }

  /**
   * Return value of Implementation-Version attribute in jar manifest
   * <p/>
   *
   * @param jarPath  path to jar file
   * @param propPath path to properties file in jar file
   * @return value of Implementation-Version attribute, null if not found
   */
  public static String getClojureJarVersion(String jarPath, String propPath) {
    try {
      File file = new File(jarPath);
      if (!file.exists()) {
        return null;
      }
      JarFile jarFile = new JarFile(file);
      JarEntry jarEntry = jarFile.getJarEntry(propPath);
      if (jarEntry == null) {
        return null;
      }
      Properties properties = new Properties();
      properties.load(jarFile.getInputStream(jarEntry));
      String version = properties.getProperty(VERSION_PROPERTY_KEY);
      jarFile.close();
      return version;
    }
    catch (Exception e) {
      return null;
    }
  }

  public static Library[] getProjectClojureLibraries(Project project) {
    if (project == null) return new Library[0];
    final LibraryTable table = ProjectLibraryTable.getInstance(project);
    final List<Library> all = ContainerUtil.findAll(table.getLibraries(), CLOJURE_LIB_CONDITION);
    return all.toArray(new Library[all.size()]);
  }

  public static Library[] getAllClojureLibraries(@Nullable Project project) {
    return ArrayUtil.mergeArrays(getGlobalClojureLibraries(), getProjectClojureLibraries(project), Library.class);
  }

  public static Library[] getGlobalClojureLibraries() {
    return LibrariesUtil.getGlobalLibraries(CLOJURE_LIB_CONDITION);
  }

  static String getSpecificJarForLibrary(Library library, String jarNamePrefix, String necessaryClass) {
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles) {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension())) {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists()) {
          try {
            JarFile jarFile = new JarFile(realFile);
            if (name.startsWith(jarNamePrefix) && jarFile.getJarEntry(necessaryClass) != null) {
              return path;
            }
            jarFile.close();
          } catch (IOException e) {
            //do nothing
          }
        }
      }
    }
    return "";
  }

  public static Library[] getClojureSdkLibrariesByModule(final Module module) {
    return LibrariesUtil.getLibrariesByCondition(module, CLOJURE_LIB_CONDITION);
  }

  @NotNull
  public static String getClojureSdkJarPath(Module module) {
    if (module == null) return "";
    Library[] libraries = getClojureSdkLibrariesByModule(module);
    if (libraries.length == 0) return "";
    final Library library = libraries[0];
    return getClojureJarPathForLibrary(library);
  }

  public static String getClojureJarPathForLibrary(Library library) {
    return getSpecificJarForLibrary(library, CLOJURE_JAR_NAME_PREFIX, CLOJURE_MAIN_CLASS_FILE);
  }


  public static boolean isClojureConfigured(final Module module) {
    ModuleRootManager manager = ModuleRootManager.getInstance(module);
    for (OrderEntry entry : manager.getOrderEntries()) {
      if (entry instanceof LibraryOrderEntry) {
        Library library = ((LibraryOrderEntry) entry).getLibrary();
        if (library != null) {
          for (VirtualFile file : library.getFiles(OrderRootType.CLASSES)) {
            String path = file.getPath();
            if (path.endsWith(".jar!/")) {
              if (file.findFileByRelativePath(CLOJURE_MAIN_CLASS_FILE) != null) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;

  }
}
