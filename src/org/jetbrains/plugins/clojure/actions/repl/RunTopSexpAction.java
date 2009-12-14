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
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * An action to run the top most s-expression around the cursor.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public final class RunTopSexpAction extends ClojureReplAction {

  public RunTopSexpAction(){
    getTemplatePresentation().setIcon(ClojureIcons.REPL_EVAL);
  }

  public void actionPerformed(AnActionEvent event) {
    Editor editor = event.getData(DataKeys.EDITOR);
    if (editor == null) { return; }

    PsiElement sexp = ClojurePsiUtil.findTopSexpAroundCaret(editor);
    if (sexp == null) { return; }

    evaluateInCurrentRepl(sexp.getText(), event);
  }

}