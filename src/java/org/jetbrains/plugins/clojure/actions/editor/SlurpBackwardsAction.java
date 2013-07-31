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
package org.jetbrains.plugins.clojure.actions.editor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClBraced;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * An action to mimic the slurp command from <i>paredit.el</i>.
 * <p>
 * Slurping a sexp makes it 'swallow' the preceeding expression.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class SlurpBackwardsAction extends ClojureEditorAction {

  // TODO: Automatically reindent the slurped s-exp.

  public SlurpBackwardsAction() {
    super(new SlurpBackwardsActionHandler());
  }

  private static class SlurpBackwardsActionHandler extends AbstractSexpActionHandler {
    protected SlurpBackwardsActionHandler() {
      super(false);
    }
    @Override
    protected void executeWriteAction(ClBraced sexp, Editor editor, Project project, DataContext dataContext) {
      PsiElement slurpee = PsiTreeUtil.getPrevSiblingOfType(sexp, ClojurePsiElement.class);
      if (slurpee == null) { return; }

      PsiElement copy = slurpee.copy();
      slurpee.delete();
      sexp.addBefore(copy, ClojurePsiUtil.firstChildSexp(sexp));
    }
  }

}
