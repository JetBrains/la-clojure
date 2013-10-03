package org.jetbrains.plugins.clojure.runner.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.execution.testframework.stacktrace.DiffHyperlink;
import com.intellij.ide.DataManager;
import com.intellij.ide.util.EditSourceUtil;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ilyas
 */
public class ClojureFilter implements Filter {
  private final Project myProject;

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.runner.console.ClojureFilter");

  private static final Pattern FILE_PATTERN = Pattern.compile(".*\\((\\w*\\.clj):(\\d+)(:(\\d+))?\\)(\\s|.)*");
  private static final Pattern ASSERT_PATTERN = Pattern.compile("  actual: \\((not \\(=) \"(.*)\" \"(.*)\"\\)\\)\n");

  public ClojureFilter(Project project) {
    myProject = project;
  }

  public Result applyFilter(String line, int entireLength) {
    Result result = matchFileName(line, entireLength);
    return result == null ? matchComparisonFailure(line, entireLength) : result;
  }

  private Result matchFileName(String line, int entireLength) {
    try {
      final Matcher matcher = FILE_PATTERN.matcher(line);
      if (matcher.matches()) {
        final String fileName = matcher.group(1);
        final int lineNumber = Integer.parseInt(matcher.group(2));
        String colGroup = matcher.group(4);
        boolean hasColumn = !StringUtil.isEmpty(colGroup);

        final int textStartOffset = entireLength - line.length();

        final PsiFile[] psiFiles = FilenameIndex.getFilesByName(myProject, fileName, GlobalSearchScope.allScope(myProject));
        if (psiFiles.length == 0) return null;


        final HyperlinkInfo info = psiFiles.length == 1 ?
            new OpenFileHyperlinkInfo(myProject, psiFiles[0].getVirtualFile(), lineNumber - 1, hasColumn ? Integer.parseInt(colGroup) - 1 : 0) :
            new MyHyperlinkInfo(psiFiles);

        return new Result(textStartOffset + matcher.start(1), textStartOffset + matcher.end(hasColumn ? 4 : 2), info);
      }
    }
    catch (NumberFormatException e) {
      LOG.debug(e);
    }

    return null;
  }

  private static Result matchComparisonFailure(String line, int entireLength) {
    Matcher matcher = ASSERT_PATTERN.matcher(line);
    if (matcher.matches()) {
      String expected = StringUtil.replace(matcher.group(2), "\\n", "\n");
      String actual = StringUtil.replace(matcher.group(3), "\\n", "\n");
      if (expected.contains("\n") && actual.contains("\n")) {
        final int textStartOffset = entireLength - line.length();
        DiffHyperlink diffHyperlink = new DiffHyperlink(expected, actual, null);
        DiffHyperlink.DiffHyperlinkInfo hyperlinkInfo = diffHyperlink.new DiffHyperlinkInfo();
        return new Result(textStartOffset + matcher.start(1), textStartOffset + matcher.end(1), hyperlinkInfo);
      }
    }
    return null;
  }

  private static class MyHyperlinkInfo implements HyperlinkInfo {
    private final PsiFile[] myPsiFiles;

    public MyHyperlinkInfo(final PsiFile[] psiFiles) {
      myPsiFiles = psiFiles;
    }

    public void navigate(final Project project) {
      DefaultPsiElementListCellRenderer renderer = new DefaultPsiElementListCellRenderer();

      final JList list = new JList(myPsiFiles);
      list.setCellRenderer(renderer);

      renderer.installSpeedSearch(list);

      final Runnable runnable = new Runnable() {
        public void run() {
          int[] ids = list.getSelectedIndices();
          if (ids == null || ids.length == 0) return;
          Object[] selectedElements = list.getSelectedValues();
          for (Object element : selectedElements) {
            Navigatable descriptor = EditSourceUtil.getDescriptor((PsiElement) element);
            if (descriptor != null && descriptor.canNavigate()) {
              descriptor.navigate(true);
            }
          }
        }
      };

      final Editor editor = PlatformDataKeys.EDITOR.getData(DataManager.getInstance().getDataContext());

      new PopupChooserBuilder(list).
          setTitle("Choose file").
          setItemChoosenCallback(runnable).
          createPopup().showInBestPositionFor(editor);
    }
  }

  private static class DefaultPsiElementListCellRenderer extends PsiElementListCellRenderer {
    public String getElementText(final PsiElement element) {
      return element.getContainingFile().getName();
    }

    @Nullable
    protected String getContainerText(final PsiElement element, final String name) {
      final PsiDirectory parent = ((PsiFile) element).getParent();
      if (parent == null) return null;
      final PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(parent);
      if (psiPackage == null) return null;
      return "(" + psiPackage.getQualifiedName() + ")";
    }

    protected int getIconFlags() {
      return 0;
    }
  }

}
