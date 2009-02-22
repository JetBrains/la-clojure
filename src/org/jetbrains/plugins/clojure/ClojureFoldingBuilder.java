package org.jetbrains.plugins.clojure;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.clojure.parser.ClojureElementTypes;
import static org.jetbrains.plugins.clojure.parser.ClojureElementTypes.*;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Dec 31, 2008
 * Time: 10:31:02 AM
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
public class ClojureFoldingBuilder implements FoldingBuilder {

  public String getPlaceholderText(ASTNode node) {

    final IElementType type = node.getElementType();
    final PsiElement psi = node.getPsi();
    if (psi instanceof ClList) {
      final String text = ((ClList) psi).getPresentableText();
      return "(" + (text != null ? text + " " : "") + "...)";
    }
    throw new Error("Unexpected node: " + type + "-->" + node.getText());
  }

  public boolean isCollapsedByDefault(ASTNode node) {
    return false;
  }

  public FoldingDescriptor[] buildFoldRegions(ASTNode node, Document document) {
    touchTree(node);
    List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
    appendDescriptors(node, descriptors);
    return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
  }

  /**
   * We have to touch the PSI tree to get the folding to show up when we first open a file
   *
   * @param node given node
   */
  private void touchTree(ASTNode node) {
    if (node.getElementType() == ClojureElementTypes.FILE) {
      node.getPsi().getFirstChild();
    }
  }

  private void appendDescriptors(final ASTNode node, final List<FoldingDescriptor> descriptors) {
    if (isFoldableNode(node)) {
      descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
    }

    ASTNode child = node.getFirstChildNode();
    while (child != null) {
      appendDescriptors(child, descriptors);
      child = child.getTreeNext();
    }
  }

  private boolean isFoldableNode(ASTNode node) {

    final PsiElement element = node.getPsi();
    final IElementType type = node.getElementType();
    if (type == LIST && element.getParent() instanceof ClojureFile &&
        node.getText().contains("\n") &&
        element instanceof ClList) {
      return true;
    }

    return (type == DEF
        || type == DEFMETHOD) &&
        node.getText().contains("\n")
        ;
  }
}
