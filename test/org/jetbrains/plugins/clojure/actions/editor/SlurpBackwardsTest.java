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

import com.intellij.testFramework.EditorActionTestCase;
import org.junit.Test;

/**
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public class SlurpBackwardsTest extends EditorActionTestCase {

  @Override
  protected String getActionId() {
    return SlurpBackwardsAction.class.getName();
  }

  @Test
  public void testAction() throws Exception {
    doTextTest("SlurpBackwards.clj", "(a b (<caret>c d) e)", "(a (b c d) e)", true);
  }

}
