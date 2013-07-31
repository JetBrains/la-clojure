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
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.clojure.psi.api.ClBraced;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

/**
 * An action handler that operates on the current s-expression in the current editor.
 *
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
abstract class AbstractSexpActionHandler extends EditorWriteActionHandler {

  private final boolean myPrevious;

  /**
   * Creates a new action handler.
   *
   * @param previous should the action operate on the s-exp <i>behind</i> or <i>around</i> the caret.
   */
  protected AbstractSexpActionHandler(boolean previous) {
    myPrevious = previous;
  }

  @Override
  public void executeWriteAction(Editor editor, DataContext dataContext) {
    Project project = editor.getProject();
    if (project == null) { return; }

    ClBraced sexp = ClojurePsiUtil.findSexpAtCaret(editor, myPrevious);
    if (sexp == null) return;

    executeWriteAction(sexp, editor, project, dataContext);
  }

  protected abstract void executeWriteAction(ClBraced sexp, Editor editor, Project project, DataContext dataContext);

}
