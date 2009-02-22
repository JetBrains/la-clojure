package org.jetbrains.plugins.clojure.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.FileTypeIndentOptionsProvider;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

/**
 * @author ilyas
 */
public class ClojureIndentOptionsProvider implements FileTypeIndentOptionsProvider {
  public CodeStyleSettings.IndentOptions createIndentOptions() {
    final CodeStyleSettings.IndentOptions indentOptions = new CodeStyleSettings.IndentOptions();
    indentOptions.INDENT_SIZE = 2;
    indentOptions.TAB_SIZE = 2;
    return indentOptions;
  }

  public FileType getFileType() {
    return ClojureFileType.CLOJURE_FILE_TYPE;
  }

  public IndentOptionsEditor createOptionsEditor() {
    return new SmartIndentOptionsEditor();
  }

  public String getPreviewText() {
    return "(defn relay [x i]\n" +
        "  (when (:next x)\n" +
        "    (send (:next x) relay i))\n" +
        "  (when (and (zero? i) (:report-queue x))\n" +
        "    (.put (:report-queue x) i))\n" +
        "  x)\n" +
        "\n" +
        "(defn run [m n]\n" +
        "  (let [q (new java.util.concurrent.SynchronousQueue)\n" +
        "    hd (reduce (fn [next _] (agent {:next next}))\n" +
        "      (agent {:report-queue q}) (range (dec m)))]\n" +
        "    (doseq [i (reverse (range n))]\n" +
        "      (send hd relay i))\n" +
        "    (.take q)))\n" +
        "\n" +
        "; 1 million message sends:\n" +
        "(time (run 1000 1000))";
  }

  public void prepareForReformat(final PsiFile psiFile) {
  }

}
