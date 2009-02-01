package org.jetbrains.plugins.clojure.parser;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: peter
 * Date: Jan 1, 2009
 * Time: 5:44:13 PM
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
public class ClojureElement extends ASTWrapperPsiElement {

    public ClojureElement(@NotNull ASTNode astNode, String name) {
        super(astNode);
    }

    public ClojureElement(@NotNull ASTNode astNode) {
        this(astNode, null);
    }

    protected ClojureElement getDefinition(String symbol) {
        for (PsiElement prev = getPrevSibling(); prev != null; prev = prev.getPrevSibling()) {
            if (prev instanceof ClojureElement) {
                System.out.println(symbol + " " + prev);
                ClojureElement def = ((ClojureElement) prev).getDefinition(symbol);
                if (def != null)
                    return def;
            }
        }
        PsiElement parent = getParent();
        if (parent instanceof ClojureElement) {
            return ((ClojureElement) parent).getDefinition(symbol);
        }
        return null;
    }

    public static class File extends ClojureElement {
        public File(ASTNode node) {
            super(node);
        }
    }

    public static class Symbol extends ClojureElement {
        public Symbol(ASTNode node) {
            super(node);
        }

        public PsiElement getDefinition() {
            return getDefinition(getNode().getText());
        }

        class Reference implements PsiReference {

            Symbol element;

            Reference(Symbol sym) {
                element = sym;
            }

            /**
             * Returns the underlying (referencing) element of the reference.
             *
             * @return the underlying element of the reference.
             */
            public PsiElement getElement() {
                return element;
            }

            /**
             * Returns the part of the underlying element which serves as a reference, or the complete
             * text range of the element if the entire element is a reference.
             *
             * @return Relative range in element
             */
            public TextRange getRangeInElement() {
                return element.getTextRange();
            }

            /**
             * Returns the element which is the target of the reference.
             *
             * @return the target element, or null if it was not possible to resolve the reference to a valid target.
             */
            @Nullable
            public PsiElement resolve() {
                return element.getDefinition();
            }

            /**
             * Returns the name of the reference target element which does not depend on import statements
             * and other context (for example, the full-qualified name of the class if the reference targets
             * a Java class).
             *
             * @return the canonical text of the reference.
             */
            public String getCanonicalText() {
                return element.getText();
            }

            /**
             * Called when the reference target element has been renamed, in order to change the reference
             * text according to the new name.
             *
             * @param newElementName the new name of the target element.
             * @return the new underlying element of the reference.
             * @throws com.intellij.util.IncorrectOperationException
             *          if the rename cannot be handled for some reason.
             */
            public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
                return null;
            }

            /**
             * Changes the reference so that it starts to point to the specified element. This is called,
             * for example, by the "Create Class from New" quickfix, to bind the (invalid) reference on
             * which the quickfix was called to the newly created class.
             *
             * @param element the element which should become the target of the reference.
             * @return the new underlying element of the reference.
             * @throws IncorrectOperationException if the rebind cannot be handled for some reason.
             */
            public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
                return null;
            }

            /**
             * Checks if the reference targets the specified element.
             *
             * @param target the element to check target for.
             * @return true if the reference targets that element, false otherwise.
             */
            public boolean isReferenceTo(PsiElement target) {
                return element instanceof Symbol && target.getText().equals(element.getText()) && ((Symbol) element).getDefinition() == this.element;
            }

            /**
             * Returns the array of String, {@link PsiElement} and/or {@link com.intellij.codeInsight.lookup.LookupElement}
             * instances representing all identifiers that are visible at the location of the reference. The contents
             * of the returned array is used to build the lookup list for basic code completion. (The list
             * of visible identifiers may not be filtered by the completion prefix string - the
             * filtering is performed later by IDEA core.)
             *
             * @return the array of available identifiers.
             */
            public Object[] getVariants() {
                return EMPTY_ARRAY;
            }

            /**
             * Returns false if the underlying element is guaranteed to be a reference, or true
             * if the underlying element is a possible reference which should not be reported as
             * an error if it fails to resolve. For example, a text in an XML file which looks
             * like a full-qualified Java class name is a soft reference.
             *
             * @return true if the refence is soft, false otherwise.
             */
            public boolean isSoft() {
                return true;
            }

        }

        /**
         * Returns the reference associated with this PSI element. If the element has multiple
         * associated references (see {@link #getReferences()} for an example), returns the first
         * associated reference.
         *
         * @return the reference instance, or null if the PSI element does not have any
         *         associated references.
         */
        @Nullable
        public PsiReference getReference() {
            return new Reference(this);
        }

        /**
         * Returns all references associated with this PSI element. An element can be associated
         * with multiple references when, for example, the element is a string literal containing
         * multiple sub-strings which are valid full-qualified class names. If an element
         * contains only one text fragment which acts as a reference but the reference has
         * multiple possible targets, {@link com.intellij.psi.PsiPolyVariantReference} should be used instead
         * of returning multiple references.
         *
         * @return the array of references, or an empty array if the element has no associated
         *         references.
         */
        @NotNull
        public PsiReference[] getReferences() {
            return new PsiReference[]{getReference()};
        }
    }

    public static class Key extends ClojureElement {
        public Key(ASTNode node) {
            super(node);
        }
    }

    public static class Defn extends Def {
        public Defn(ASTNode node) {
            super(node);
        }
    }

    public static class DefnDash extends Def {
        public DefnDash(ASTNode node) {
            super(node);
        }
    }

    public static class Def extends ClojureElement {

        public Def(ASTNode node) {
            super(node);
        }

        protected ClojureElement getDefinition(String symbol) {
            Symbol sym = getNameSymbol();
            if( sym != null ) {
                String name = sym.getText();
                assert name != null;
                //System.out.println(symbol + " " + this + " " + name);
                if (name.equals(symbol)) {
                    return this;
                }
            }

            return super.getDefinition(symbol);
        }

        protected Symbol getNameSymbol() {
            PsiElement[] children = getChildren();
            if( children.length > 0 && children[0] instanceof Symbol )
                return (Symbol) children[0];
            return null;
        }

        public String getName() {
            Symbol sym = getNameSymbol();
            if( sym != null ) {
                String name = sym.getText();
                assert name != null;
                return name;
            }
            return "";
        }
    }

    public static class Fn extends ClojureElement {
        public Fn(ASTNode node) {
            super(node);
        }
    }

    public static class Var extends ClojureElement {
        public Var(ASTNode node) {
            super(node);
        }
    }

    public static class Bindings extends ClojureElement {
        public Bindings(ASTNode node) {
            super(node);
        }
    }

    public static class Literal extends ClojureElement {
        public Literal(ASTNode node) {
            super(node);
        }
    }

    public static class List extends ClojureElement {
        public List(ASTNode node) {
            super(node);
        }
    }

    public static class TopList extends ClojureElement {
        public TopList(ASTNode node) {
            super(node);
        }
    }

    public static class Vector extends ClojureElement {
        public Vector(ASTNode node) {
            super(node);
        }
    }

    public static class Map extends ClojureElement {
        public Map(ASTNode node) {
            super(node);
        }
    }

    public static class QuotedExpression extends ClojureElement {
        public QuotedExpression(ASTNode node) {
            super(node);
        }
    }

    public static class BackQuotedExpression extends ClojureElement {
        public BackQuotedExpression(ASTNode node) {
            super(node);
        }
    }

    public static class Pound extends ClojureElement {
        public Pound(ASTNode node) {
            super(node);
        }
    }

    public static class Up extends ClojureElement {
        public Up(ASTNode node) {
            super(node);
        }
    }

    public static class Metadata extends ClojureElement {
        public Metadata(ASTNode node) {
            super(node);
        }
    }

    public static class Tilda extends ClojureElement {
        public Tilda(ASTNode node) {
            super(node);
        }
    }

    public static class At extends ClojureElement {
        public At(ASTNode node) {
            super(node);
        }
    }

    public static class TildaAt extends ClojureElement {
        public TildaAt(ASTNode node) {
            super(node);
        }
    }
}
