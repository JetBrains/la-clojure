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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public class ClojureUtils {

  public static boolean isClojureEditor(@NotNull Editor editor) {
    VirtualFile vfile = FileDocumentManager.getInstance().getFile(editor.getDocument());
    Project project = editor.getProject();
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
    ModuleType type = module.getModuleType();
    return type instanceof JavaModuleType || "PLUGIN_MODULE".equals(type.getId());
  }

}
