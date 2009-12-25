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
import org.jetbrains.plugins.clojure.psi.api.ClBraced;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * An action to mimic the barf command from <i>paredit.el</i>.
 * <p>
 * Barfing a sexp makes it 'spit out' the last expression.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class BarfForwardsAction extends ClojureEditorAction {

  // TODO: Automatically reindent the slurped s-exp.

  public BarfForwardsAction() {
    super(new BarfForwardsActionHandler());
  }

  private static class BarfForwardsActionHandler extends AbstractSexpActionHandler {
    protected BarfForwardsActionHandler() {
      super(false);
    }
    @Override
    protected void executeWriteAction(ClBraced sexp, Editor editor, Project project, DataContext dataContext) {
      PsiElement barfee = ClojurePsiUtil.lastChildSexp(sexp);
      if (barfee == null) { return; }

      PsiElement copy = barfee.copy();
      barfee.delete();
      sexp.getParent().addAfter(copy, sexp);

      if (sexp.getChildren().length == 0) {
        sexp.delete();
      }
    }
  }

}
