package org.jetbrains.plugins.clojure.documentation;

import com.intellij.codeInsight.javadoc.JavaDocUtil;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.defs.ClDef;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;

import java.util.List;

/**
 * @author ilyas
 */
public class ClojureDocumentationProvider implements DocumentationProvider {

  public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    if (element instanceof ClDef) {
      ClDef def = (ClDef) element;
      return def.getPresentationText();
    }
    if (element instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) element;
      return symbol.getNameString();
    }
    return null;
  }

  public String generateDoc(PsiElement element, PsiElement originalElement) {
    final String str = getDocString(element);
    if (str == null) return null;

/*    final StringBuffer buffer = new StringBuffer();
    final Iterable<String> lines = StringUtil.tokenize(str, "\n\n");
    for (String line : lines) {
      final Iterable<String> words = StringUtil.tokenize(line, " \t\f");
      buffer.append(StringUtil.join(words, new Function<String, String>() {
        public String fun(String s) {
          if (s != null && s.startsWith(":")) return "<b>" + s + "</b>";
          return s;
        }
      }, " ")).append("<br/><br/>");
    }*/
//    return buffer.toString();

    return "<pre>" + str + "</pre>";
  }

  @Nullable
  private static String getDocString(PsiElement element) {
    if (element instanceof ClDef) {
      ClDef def = (ClDef) element;
      return def.getDocString();
    }
    if (element instanceof ClSymbol &&
        element.getParent() instanceof ClDef) {
      final ClDef def = (ClDef) element.getParent();
      if (def.getNameSymbol() == element) {
        return def.getDocString();
      }
    }
    return null;
  }

  public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
    if (object instanceof ClojurePsiElement) {
      return ((ClojurePsiElement) object);
    }
    return null;
  }

  public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
    return JavaDocUtil.findReferenceTarget(psiManager, link, context);
  }
  public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
    return null;
  }
}
