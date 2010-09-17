package org.jetbrains.plugins.clojure.editor.braceHighlighter;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx;
import com.intellij.openapi.editor.ex.FocusChangeListener;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.Alarm;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author ilyas
 */
public class ClojureBraceHighlighter extends AbstractProjectComponent {

  private final Alarm myAlarm = new Alarm();

  @NotNull
  @Override
  public String getComponentName() {
    return "ClojureBraceHighlighter";
  }

  protected ClojureBraceHighlighter(Project project) {
    super(project);
  }

  public void projectOpened() {
    StartupManager.getInstance(myProject).registerPostStartupActivity(new DumbAwareRunnable() {
      public void run() {
        doInit();
      }
    });
  }

  private void doInit() {
    final EditorEventMulticaster eventMulticaster = EditorFactory.getInstance().getEventMulticaster();

    DocumentListener documentListener = new DocumentAdapter() {
      public void documentChanged(DocumentEvent e) {
        myAlarm.cancelAllRequests();
        Editor[] editors = EditorFactory.getInstance().getEditors(e.getDocument(), myProject);
        for (Editor editor : editors) {
          updateBraces(editor, myAlarm, e);
        }
      }
    };
    eventMulticaster.addDocumentListener(documentListener, myProject);

    final FocusChangeListener myFocusChangeListener = new FocusChangeListener() {
      public void focusGained(Editor editor) {
        updateBraces(editor, myAlarm, null);
      }

      public void focusLost(Editor editor) {
        // do nothing
      }
    };
    ((EditorEventMulticasterEx) eventMulticaster).addFocusChangeListner(myFocusChangeListener);


    myProject.getMessageBus().connect(myProject).subscribe(
        FileEditorManagerListener.FILE_EDITOR_MANAGER,
        new FileEditorManagerAdapter() {
          public void selectionChanged(FileEditorManagerEvent e) {
            myAlarm.cancelAllRequests();
          }
        });
  }

  static void updateBraces(@NotNull final Editor editor, @NotNull final Alarm alarm, final DocumentEvent e) {
    final Document document = editor.getDocument();
    if (document instanceof DocumentEx && ((DocumentEx) document).isInBulkUpdate()) return;
    ClojureBraceHighlightingHandler.lookForInjectedAndHighlightInOtherThread(editor, alarm, new Processor<ClojureBraceHighlightingHandler>() {
      public boolean process(final ClojureBraceHighlightingHandler handler) {
        handler.updateBraces(e);
        return false;
      }
    });
  }


}
