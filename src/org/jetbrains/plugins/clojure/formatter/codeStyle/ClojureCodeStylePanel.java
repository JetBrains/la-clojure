package org.jetbrains.plugins.clojure.formatter.codeStyle;

import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.highlighter.ClojureEditorHighlighter;

import javax.swing.*;

/**
 * @author ilyas
 */
public class ClojureCodeStylePanel extends CodeStyleAbstractPanel {
  private final CodeStyleSettings mySettings;
  private JPanel myPanel;
  private JCheckBox alignCheckBox;
  private JTabbedPane myTabbedPane;
  private JPanel myAlignPanel;
  private JPanel myPreviewPanel;

  protected ClojureCodeStylePanel(CodeStyleSettings settings) {
    super(settings);
    mySettings = settings;
    ClojureCodeStyleSettings css = settings.getCustomSettings(ClojureCodeStyleSettings.class);
    setSettings(css);
    installPreviewPanel(myPreviewPanel);
  }

  protected EditorHighlighter createHighlighter(EditorColorsScheme scheme) {
    return new ClojureEditorHighlighter(scheme);
  }

  protected int getRightMargin() {
    return 0;
  }

  protected void prepareForReformat(PsiFile psiFile) {
  }

  @NotNull
  protected FileType getFileType() {
    return ClojureFileType.CLOJURE_FILE_TYPE;
  }

  protected String getPreviewText() {
    return "(print \"type = \" (or type \"!!YIKES!NO TYPE!!!\") \"$%$% \"\n" +
            "  (if (= \"\"\n" +
            "    text) \"!!NO TEXT!!!\" text))";
  }

  public void apply(CodeStyleSettings settings) {
    ClojureCodeStyleSettings cljSettings = settings.getCustomSettings(ClojureCodeStyleSettings.class);
    cljSettings.ALIGN_CLOJURE_FORMS = alignCheckBox.isSelected();
  }

  public boolean isModified(CodeStyleSettings settings) {
    ClojureCodeStyleSettings cljSettings = settings.getCustomSettings(ClojureCodeStyleSettings.class);
    if (alignCheckBox.isSelected() ^ cljSettings.ALIGN_CLOJURE_FORMS) return true;
    return false;
  }

  public JComponent getPanel() {
    return myPanel;
  }

  protected void resetImpl(CodeStyleSettings settings) {
    ClojureCodeStyleSettings cljSettings = settings.getCustomSettings(ClojureCodeStyleSettings.class);
    setSettings(cljSettings);
    updatePreview(true);
  }

  private void setSettings(ClojureCodeStyleSettings settings) {
    setValue(alignCheckBox, settings.ALIGN_CLOJURE_FORMS);
    //todo add more
  }

  private static void setValue(final JCheckBox box, final boolean value) {
    box.setSelected(value);
  }

}
