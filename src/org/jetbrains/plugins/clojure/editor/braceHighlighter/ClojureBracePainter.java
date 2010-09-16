package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.Alarm;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author ilyas
 */
public class ClojureBracePainter {

  private final Project myProject;
  private final Editor myEditor;
  private final Alarm myAlarm;
  private final PsiFile myFile;
  private DocumentEx myDocument;
  private Stack<Color> myColorStack = new Stack<Color>();

  private static final Key<List<RangeHighlighter>> CLOJURE_BRACE_PAINTER_KEY = Key.create("ClojureBracePainter.CLOJURE_BRACE_PAINTER_KEY");

  public ClojureBracePainter(Project project, Editor newEditor, Alarm alarm, PsiFile file) {
    myProject = project;
    myEditor = newEditor;
    myAlarm = alarm;
    myFile = file;
    myDocument = (DocumentEx) myEditor.getDocument();
  }

  static void lookForInjectedAndMatchBracesInOtherThread(@NotNull final Editor editor, @NotNull final Alarm alarm, @NotNull final Processor<ClojureBracePainter> processor) {
    final Project project = editor.getProject();
    if (project == null) return;
    PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
    if (psiFile instanceof ClojureFile) {
      final ClojureFile file = (ClojureFile) psiFile;
      Editor newEditor = InjectedLanguageUtil.getInjectedEditorForInjectedFile(editor, file);
      ClojureBracePainter handler = new ClojureBracePainter(project, newEditor, alarm, file);
      processor.process(handler);
    }


/*    JobUtil.submitToJobThread(new Runnable() {
 public void run() {
   if (isReallyDisposed(editor, project)) return;
   final PsiFile psiFile = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>() {
     public PsiFile compute() {
       PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
       return null != psiFile ? getInjectedFileIfAny(editor, project, offset, psiFile, alarm) : null;
     }
   });

   if (psiFile instanceof ClojureFile) {
     final ClojureFile file = (ClojureFile) psiFile;
     ApplicationManager.getApplication().invokeLater(new DumbAwareRunnable() {
       public void run() {
         if (!isReallyDisposed(editor, project)) {
           Editor newEditor = InjectedLanguageUtil.getInjectedEditorForInjectedFile(editor, file);
           ClojureBracePainter handler = new ClojureBracePainter(project, newEditor, alarm, file);
           processor.process(handler);
         }
       }
     }, ModalityState.stateForComponent(editor.getComponent()));
   } else {
     return;
   }
 }
}, Job.DEFAULT_PRIORITY);*/
  }


  public void updateBraces() {
    if (myFile == null) return;
    final HighlighterIterator iterator = ((EditorEx) myEditor).getHighlighter().createIterator(0);
    final CharSequence chars = myEditor.getDocument().getCharsSequence();

    int level = 0;

    while (!iterator.atEnd()) {

      if (BraceMatchingUtil.isLBraceToken(iterator, chars, ClojureFileType.CLOJURE_FILE_TYPE)) {
        final Color color = level % 2 == 0 ? Color.GREEN : Color.BLUE;
        myColorStack.push(color);
        final int start = iterator.getStart();
        addHighlightRequest(color, start);
        level++;
      } else if (BraceMatchingUtil.isRBraceToken(iterator, chars, ClojureFileType.CLOJURE_FILE_TYPE)) {
        if (myColorStack.isEmpty()) break;
        level--;
        final Color color = myColorStack.pop();
        final int start = iterator.getStart();
        addHighlightRequest(color, start);
      }

      iterator.advance();
    }

  }

  private void addHighlightRequest(final Color color, final int start) {
    if (myProject.isDisposed() || myEditor.isDisposed()) return;
    highlightBrace(start, color);
  }

  private void registerHighlighter(RangeHighlighter highlighter) {
    List<RangeHighlighter> highlighters = myEditor.getUserData(CLOJURE_BRACE_PAINTER_KEY);
    if (highlighters == null) {
      highlighters = new ArrayList<RangeHighlighter>();
      myEditor.putUserData(CLOJURE_BRACE_PAINTER_KEY, highlighters);
    }

    highlighters.add(highlighter);
  }

  private void highlightBrace(int rBraceOffset, Color color) {
    EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
    final TextAttributes attributes =
        scheme.getAttributes(CodeInsightColors.MATCHED_BRACE_ATTRIBUTES).clone();
    attributes.setForegroundColor(color);
    attributes.setBackgroundColor(Color.WHITE);


    RangeHighlighter rbraceHighlighter =
        myEditor.getMarkupModel().addRangeHighlighter(
            rBraceOffset, rBraceOffset + 1, HighlighterLayer.LAST + 1, attributes, HighlighterTargetArea.EXACT_RANGE);
    rbraceHighlighter.setGreedyToLeft(false);
    rbraceHighlighter.setGreedyToRight(false);
    registerHighlighter(rbraceHighlighter);
  }


}
