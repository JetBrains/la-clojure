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
package org.jetbrains.plugins.clojure.actions.repl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiFactory;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.repl.ReplPanel;

/**
 * An action to run the s-expression behind the cursor.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class RunLastSexpAction extends ClojureReplAction {

  public RunLastSexpAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_EVAL);
  }

  @Override
  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    presentation.setEnabled(getCurrentRepl(e) != null);
  }

  public void actionPerformed(AnActionEvent event) {
    Editor editor = event.getData(DataKeys.EDITOR);
    if (editor == null) { return; }

    Project project = editor.getProject();
    if (project == null) { return; }

    PsiElement sexp = ClojurePsiUtil.findSexpAtCaret(editor, true);
    if (sexp == null) { return; }

    String text = sexp.getText();
    if (ClojurePsiFactory.getInstance(project).hasSyntacticalErrors(text)) {
      Messages.showErrorDialog(project,
            ClojureBundle.message("evaluate.incorrect.sexp"),
            ClojureBundle.message("evaluate.incorrect.cannot.evaluate"));
      return;
    }

    final ReplPanel repl = getCurrentRepl(event);
    if (repl != null) repl.writeToCurrentRepl(text, false);
  }

}