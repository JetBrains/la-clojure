package org.jetbrains.plugins.clojure.runner;

import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FieldPanel;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.ClojureBundle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 8, 2009
 * Time: 11:15:58 AM
 * Copyright 2007, 2008, 2009 Red Shark Technology
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ClojureRunConfigurationEditor extends SettingsEditor<ClojureScriptRunConfiguration> {

  private DefaultComboBoxModel myModulesModel;
  private JComboBox myModulesBox;
  private JPanel myMainPanel;
  private RawCommandLineEditor myVMParameters;
  private RawCommandLineEditor myParameters;
  private JPanel scriptPathPanel;
  private JPanel workDirPanel;
  private JCheckBox myReplCB;
  private JTextField scriptPathField;
  private JTextField workDirField;

  public ClojureRunConfigurationEditor() {
    scriptPathField = new JTextField();
    final BrowseFilesListener scriptBrowseListener = new BrowseFilesListener(scriptPathField,
        ClojureBundle.message("script.path"),
        ClojureBundle.message("spec.path.to.script"),
        new FileChooserDescriptor(true, false, false, false, false, false) {
          public boolean isFileSelectable(VirtualFile file) {
            return file.getFileType() == ClojureFileType.CLOJURE_FILE_TYPE;
          }
        });

    final FieldPanel scriptFieldPanel = new FieldPanel(scriptPathField, ClojureBundle.message("script.path") + ":", null, scriptBrowseListener, null);
    scriptPathPanel.setLayout(new BorderLayout());
    scriptPathPanel.add(scriptFieldPanel, BorderLayout.CENTER);

    workDirField = new JTextField();
    final BrowseFilesListener workDirBrowseFilesListener = new BrowseFilesListener(workDirField,
        ClojureBundle.message("work.dir"),
        ClojureBundle.message("specify.work.dir"),
        BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);
    final FieldPanel workDirFieldPanel = new FieldPanel(workDirField, ClojureBundle.message("work.dir")+ ":", null, workDirBrowseFilesListener, null);
    workDirPanel.setLayout(new BorderLayout());
    workDirPanel.add(workDirFieldPanel, BorderLayout.CENTER);
  }

  public void resetEditorFrom(ClojureScriptRunConfiguration configuration) {
    myVMParameters.setDialogCaption(ClojureBundle.message("vm.params"));
    myVMParameters.setText(configuration.getVmParams());

    myParameters.setDialogCaption(ClojureBundle.message("script.params"));
    myParameters.setText(configuration.getScriptParams());

    scriptPathField.setText(configuration.getScriptPath());
    workDirField.setText(configuration.getWorkDir());

    myReplCB.setEnabled(true);
    myReplCB.setSelected(configuration.getRunInREPL());

    myModulesModel.removeAllElements();
    for (Module module : configuration.getValidModules()) {
      myModulesModel.addElement(module);
    }
    myModulesModel.setSelectedItem(configuration.getModule());
  }

  public void applyEditorTo(ClojureScriptRunConfiguration configuration) throws ConfigurationException {
    configuration.setModule((Module) myModulesBox.getSelectedItem());
    configuration.setVmParams(myVMParameters.getText());
    configuration.setRunInREPL(myReplCB.isSelected());
    configuration.setScriptParams(myParameters.getText());
    configuration.setScriptPath(scriptPathField.getText());
    configuration.setWorkDir(workDirField.getText());
  }

  @NotNull
  public JComponent createEditor() {
    myModulesModel = new DefaultComboBoxModel();
    myModulesBox.setModel(myModulesModel);
    myReplCB.setEnabled(true);
    myReplCB.setSelected(false);

    myModulesBox.setRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, final Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final Module module = (Module) value;
        if (module != null) {
          setIcon(module.getModuleType().getNodeIcon(false));
          setText(module.getName());
        }
        return this;
      }
    });

    return myMainPanel;
  }

  public void disposeEditor() {
  }
}
