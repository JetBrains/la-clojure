/*
 * Copyright 2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.clojure.utils;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.config.ClojureFacet;
import org.jetbrains.plugins.clojure.config.ClojureFacetType;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public class ClojureUtils {

  public static final String CLOJURE_NOTIFICATION_GROUP = "Clojure";
  public static final String CLOJURE_REPL = "clojure.lang.Repl";
  @NonNls
  public static final String CLOJURE_MAIN = "clojure.main";

  public static final String CLOJURE_DEFAULT_JVM_PARAMS = "-Xss1m -server";

  public static boolean isClojureEditor(@NotNull Editor editor) {
    VirtualFile vfile = FileDocumentManager.getInstance().getFile(editor.getDocument());
    Project project = editor.getProject();
    if (vfile == null) return false;
    if (project == null) {
      // XXX this is a hack, but what to do if we cannot access the PSI manager ???
      return vfile.getName().endsWith(".clj");
    }
    PsiFile file = PsiManager.getInstance(project).findFile(vfile);
    if (file == null) {
      // XXX oops, I did it again !
      return vfile.getName().endsWith(".clj");
    }

    return ClojureFileType.CLOJURE_LANGUAGE.is(file.getLanguage());
  }

  public static boolean isSuitableModule(Module module) {
    if (module == null) return false;
    ModuleType type = ModuleType.get(module);
    return type instanceof JavaModuleType || "PLUGIN_MODULE".equals(type.getId());
  }

  @Nullable
  public static Module getModule(AnActionEvent e) {
    Module module = e.getData(DataKeys.MODULE);
    if (module == null) {
      final Project project = e.getData(DataKeys.PROJECT);
      if (project == null) return null;
      final Module[] modules = ModuleManager.getInstance(project).getModules();
      if (modules.length == 1) {
        module = modules[0];
      } else {
        for (Module m : modules) {
          final FacetManager manager = FacetManager.getInstance(m);
          final ClojureFacet clFacet = manager.getFacetByType(ClojureFacetType.INSTANCE.getId());
          if (clFacet != null) {
            module = m;
            break;
          }
        }
        if (module == null) {
          module = modules[0];
        }
      }
    }
    return module;
  }


}
