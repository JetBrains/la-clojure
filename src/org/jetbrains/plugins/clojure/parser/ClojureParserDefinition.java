package org.jetbrains.plugins.clojure.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.plugins.clojure.file.ClojureFile;
import org.jetbrains.plugins.clojure.lexer.ClojureFlexLexer;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.annotations.NotNull;


/**
 * User: peter
 * Date: Nov 20, 2008
 * Time: 11:10:44 AM
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
public class ClojureParserDefinition implements ParserDefinition {

  @NotNull
  public Lexer createLexer(Project project) {
    return new ClojureFlexLexer();
  }

  public PsiParser createParser(Project project) {
    return new ClojureParser();
  }

  public IFileElementType getFileNodeType() {
    return ClojureElementTypes.FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return TokenSet.create(ClojureTokenTypes.WHITESPACE, ClojureTokenTypes.EOL, ClojureTokenTypes.EOF);
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return ClojureTokenTypes.COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return ClojureTokenTypes.STRINGS;
  }

  @NotNull
  public PsiElement createElement(ASTNode node) {
    final IElementType type = node.getElementType();

    if (type == ClojureElementTypes.FILE) {
      return new ClojureElement.File(node);
    } else if (type == ClojureElementTypes.VARIABLE) {
      return new ClojureElement.Symbol(node);
    } else if (type == ClojureElementTypes.DEFN) {
      return new ClojureElement.Defn(node);
    } else if (type == ClojureElementTypes.DEFNDASH) {
      return new ClojureElement.DefnDash(node);
    } else if (type == ClojureElementTypes.DEF) {
      return new ClojureElement.Def(node);
    } else if (type == ClojureElementTypes.BINDINGS) {
      return new ClojureElement.Bindings(node);
    } else if (type == ClojureElementTypes.KEY) {
      return new ClojureElement.Key(node);
    } else if (type == ClojureElementTypes.LITERAL) {
      return new ClojureElement.Literal(node);
    } else if (type == ClojureElementTypes.TOPLIST) {
      return new ClojureElement.TopList(node);
    } else if (type == ClojureElementTypes.LIST) {
      return new ClojureElement.List(node);
    } else if (type == ClojureElementTypes.VECTOR) {
      return new ClojureElement.Vector(node);
    } else if (type == ClojureElementTypes.MAP) {
      return new ClojureElement.Map(node);
    } else if (type == ClojureElementTypes.QUOTED_EXPRESSION) {
      return new ClojureElement.QuotedExpression(node);
    } else if (type == ClojureElementTypes.BACKQUOTED_EXPRESSION) {
      return new ClojureElement.BackQuotedExpression(node);
    } else if (type == ClojureElementTypes.POUND_EXPRESSION) {
      return new ClojureElement.Pound(node);
    } else if (type == ClojureElementTypes.UP_EXPRESSION) {
      return new ClojureElement.Up(node);
    } else if (type == ClojureElementTypes.POUNDUP_EXPRESSION) {
      return new ClojureElement.Metadata(node);
    } else if (type == ClojureElementTypes.TILDA_EXPRESSION) {
      return new ClojureElement.Tilda(node);
    } else if (type == ClojureElementTypes.AT_EXPRESSION) {
      return new ClojureElement.At(node);
    } else if (type == ClojureElementTypes.TILDAAT_EXPRESSION) {
      return new ClojureElement.TildaAt(node);
    }

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {

    if (
        left.getElementType() == ClojureTokenTypes.QUOTE
            || left.getElementType() == ClojureTokenTypes.POUND
            || left.getElementType() == ClojureTokenTypes.POUNDUP
        ) {

      return SpaceRequirements.MUST_NOT;

    } else if (
        left.getElementType() == ClojureTokenTypes.LEFT_PAREN
            || right.getElementType() == ClojureTokenTypes.RIGHT_PAREN
            || left.getElementType() == ClojureTokenTypes.RIGHT_PAREN
            || right.getElementType() == ClojureTokenTypes.LEFT_PAREN

            || left.getElementType() == ClojureTokenTypes.LEFT_CURLY
            || right.getElementType() == ClojureTokenTypes.RIGHT_CURLY
            || left.getElementType() == ClojureTokenTypes.RIGHT_CURLY
            || right.getElementType() == ClojureTokenTypes.LEFT_CURLY

            || left.getElementType() == ClojureTokenTypes.LEFT_SQUARE
            || right.getElementType() == ClojureTokenTypes.RIGHT_SQUARE
            || left.getElementType() == ClojureTokenTypes.RIGHT_SQUARE
            || right.getElementType() == ClojureTokenTypes.LEFT_SQUARE) {

      return SpaceRequirements.MAY;
    }
    return SpaceRequirements.MUST;
  }

  public PsiFile createFile(FileViewProvider viewProvider) {
    return new ClojureFile(viewProvider);
  }
}

