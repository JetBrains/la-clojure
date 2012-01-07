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
package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.plugins.clojure.psi.api.ClBraced;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.impl.ClKeywordImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Trinity;
import com.intellij.util.containers.HashSet;

import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;

/**
 * @author ilyas
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public class ClojurePsiUtil {
  public static final String JAVA_LANG = "java.lang";
  public static final String CLOJURE_LANG = "clojure.lang";

  public static final Set<String> DEFINITION_FROM_NAMES = new HashSet<String>();

  static {
    DEFINITION_FROM_NAMES.addAll(Arrays.asList("fn"));
  }

  @Nullable
  public static ClList findFormByName(ClojurePsiElement container, @NotNull String name) {
    for (PsiElement element : container.getChildren()) {
      if (element instanceof ClList) {
        ClList list = (ClList) element;
        final ClSymbol first = list.getFirstSymbol();
        if (first != null && name.equals(first.getNameString())) {
          return list;
        }
      }
    }
    return null;
  }

  @Nullable
  public static ClList findFormByNameSet(ClojurePsiElement container, @NotNull Set<String> names) {
    for (PsiElement element : container.getChildren()) {
      if (element instanceof ClList) {
        ClList list = (ClList) element;
        final ClSymbol first = list.getFirstSymbol();
        if (first != null && names.contains(first.getNameString())) {
          return list;
        }
      }
    }
    return null;
  }

  @Nullable
  public static ClKeywordImpl findNamespaceKeyByName(ClList ns, String keyName) {
    ClList list = ns.findFirstChildByClass(ClList.class);

    while (list != null) {
      for (PsiElement element : list.getChildren()) {
        if (element instanceof ClKeywordImpl) {
          ClKeywordImpl key = (ClKeywordImpl) element;
          if (keyName.equals(key.getText())) {
            return key;
          }
        }
      }
      list = getNextSiblingByClass(list, ClList.class);
    }
    return null;
  }

  @Nullable
  public static PsiElement getNextNonWhiteSpace(PsiElement element) {
    PsiElement next = element.getNextSibling();
    while (next != null && (next instanceof PsiWhiteSpace)) {
      next = next.getNextSibling();
    }
    return next;
  }

  @Nullable
  public static <T> T getNextSiblingByClass(PsiElement element, Class<T> aClass) {
    PsiElement next = element.getNextSibling();
    while (next != null && !aClass.isInstance(next)) {
      next = next.getNextSibling();
    }
    return aClass.cast(next);
  }

  @NotNull
  public static Trinity<PsiElement, PsiElement, PsiElement> findCommonParentAndLastChildren(@NotNull PsiElement element1, @NotNull PsiElement element2) {
    if (element1 == element2) return new Trinity<PsiElement, PsiElement, PsiElement>(element1, element1, element1);
    final PsiFile containingFile = element1.getContainingFile();
    final PsiElement topLevel = containingFile == element2.getContainingFile() ? containingFile : null;

    ArrayList<PsiElement> parents1 = getParents(element1, topLevel);
    ArrayList<PsiElement> parents2 = getParents(element2, topLevel);
    int size = Math.min(parents1.size(), parents2.size());
    PsiElement parent = topLevel;
    for (int i = 1; i <= size; i++) {
      PsiElement parent1 = parents1.get(parents1.size() - i);
      PsiElement parent2 = parents2.get(parents2.size() - i);

      if (!parent1.equals(parent2)) {
        return new Trinity<PsiElement, PsiElement, PsiElement>(parent, parent1, parent2);
      }
      parent = parent1;
    }
    return new Trinity<PsiElement, PsiElement, PsiElement>(parent, parent, parent);
  }

  public static boolean lessThan(PsiElement elem1, PsiElement elem2) {
    if (elem1.getParent() != elem2.getParent() || elem1 == elem2) {
      return false;
    }
    PsiElement next = elem1;
    while (next != null && next != elem2) {
      next = next.getNextSibling();
    }
    return next != null;
  }

  @NotNull
  public static ArrayList<PsiElement> getParents(@NotNull PsiElement element, @Nullable PsiElement topLevel) {
    ArrayList<PsiElement> parents = new ArrayList<PsiElement>();
    PsiElement parent = element;
    while (parent != topLevel && parent != null) {
      parents.add(parent);
      parent = parent.getParent();
    }
    return parents;
  }

  private static boolean isParameterSymbol(ClSymbol symbol) {
    //todo implement me!
    return false;
  }

  private static boolean anyOf(char c, String s) {
    return s.indexOf(c) != -1;
  }

  /**
   * Find the s-expression at the caret in a given editor.
   *
   * @param editor the editor to search in.
   * @param previous should the s-exp <i>behind</i> the caret be returned (rather than <i>around</i> the caret).
   * @return the s-expression, or {@code null} if none could be found.
   */
  public static @Nullable ClBraced findSexpAtCaret(@NotNull Editor editor, boolean previous) {
    Project project = editor.getProject();
    if (project == null) { return null; }

    VirtualFile vfile = FileDocumentManager.getInstance().getFile(editor.getDocument());

    if (vfile == null) return null;

    PsiFile file = PsiManager.getInstance(project).findFile(vfile);
    if (file == null) { return null; }

    CharSequence chars = editor.getDocument().getCharsSequence();
    int offset = editor.getCaretModel().getOffset();
    if (previous) {
      while (offset != 0 && offset < chars.length() && !anyOf(chars.charAt(offset), "]})")) {
        --offset;
      }
    }
    if (offset == 0) { return null; }

    PsiElement element = file.findElementAt(offset);
    while (element != null && !(element instanceof ClBraced)) {
      element = element.getParent();
    }
    return (ClBraced) element;
  }

  /**
   * Find the top most s-expression around the caret.
   *
   * @param editor the editor to search in.
   * @return the s-expression, or {@code null} if not currently inside one.
   */
  public static @Nullable ClList findTopSexpAroundCaret(@NotNull Editor editor) {
    Project project = editor.getProject();
    if (project == null) { return null; }

    Document document = editor.getDocument();
    PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (file == null) { return null; }

    PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
    ClList sexp = null;
    while (element != null) {
      if (element instanceof ClList) { sexp = (ClList) element; }
      element = element.getParent();
    }
    return sexp;
  }

  public static PsiElement firstChildSexp(PsiElement element) {
    PsiElement[] children = element.getChildren();
    return children.length != 0 ? children[0] : null;
  }

  public static PsiElement lastChildSexp(PsiElement element) {
    PsiElement[] children = element.getChildren();
    return children[children.length - 1];
  }

  public static boolean isValidClojureExpression(String text, @NotNull Project project) {
    if (text == null) return false;
    text = text.trim();
    final ClojurePsiFactory factory = ClojurePsiFactory.getInstance(project);
    final ClojureFile file = factory.createClojureFileFromText(text);
    final PsiElement[] children = file.getChildren();

    if (children.length == 0) return false;
    for (PsiElement child : children) {
      if (containsSyntaxErrors(child)) {
        return false;
      }
    }

    return true;
  }

  private static boolean containsSyntaxErrors(PsiElement elem) {
    if (elem instanceof PsiErrorElement) {
      return true;
    }
    for (PsiElement child : elem.getChildren()) {
      if (containsSyntaxErrors(child)) return true;
    }
    return false;
  }

  public static boolean isStrictlyBefore(PsiElement e1, PsiElement e2) {
    final Trinity<PsiElement, PsiElement, PsiElement> result = findCommonParentAndLastChildren(e1, e2);
    return result.second.getTextRange().getStartOffset() < result.third.getTextRange().getStartOffset();
  }

}
