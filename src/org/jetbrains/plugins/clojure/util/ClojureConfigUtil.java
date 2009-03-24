package org.jetbrains.plugins.clojure.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author ilyas
 */
public class ClojureConfigUtil {
  public static final String CLOJURE_MAIN_CLASS_FILE = "clojure/main.class";

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
