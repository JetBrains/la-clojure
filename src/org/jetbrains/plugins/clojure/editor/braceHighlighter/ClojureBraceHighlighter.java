package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx;
import com.intellij.openapi.editor.ex.FocusChangeListener;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.Alarm;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.settings.ClojureProjectSettings;

/**
 * @author ilyas
 */
public class ClojureBraceHighlighter implements Annotator {
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement instanceof LeafPsiElement &&
        ClojureProjectSettings.getInstance(psiElement.getProject()).coloredParentheses) {
      IElementType type = ((LeafPsiElement) psiElement).getElementType();
      if (type == ClojureElementTypes.LEFT_PAREN || type == ClojureElementTypes.RIGHT_PAREN) {
        int level = getLevel(psiElement);
        if (level >= 0) {
          final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
          TextAttributes attrs = ClojureBraceAttributes.getBraceAttributes(level, scheme.getDefaultBackground());
          annotationHolder.createInfoAnnotation(psiElement, "").setEnforcedTextAttributes(attrs);
        }
      }
    }
  }

  private static int getLevel(PsiElement psiElement) {
    int level = -1;
    PsiElement eachParent = psiElement;
    while (eachParent != null) {
      if (eachParent instanceof ClList) {
        level++;
      }
      eachParent = eachParent.getParent();
    }
    return level;
  }

}
