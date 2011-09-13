package org.jetbrains.plugins.clojure.psi.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClQuotedForm;
import org.jetbrains.plugins.clojure.psi.api.ns.ClNs;

/**
 * @author ilyas
 */
public abstract class ClojurePsiCheckers {

  public static boolean isImportList(PsiElement elem) {
    return specificHeadText(elem, ClojureKeywords.IMPORT);
  }

  public static boolean isRequireList(PsiElement elem) {
    return specificHeadText(elem, ClojureKeywords.REQUIRE);
  }

  public static boolean isUseList(PsiElement elem) {
    return specificHeadText(elem, ClojureKeywords.USE);
  }

  public static boolean isNs(PsiElement elem) {
    return (elem instanceof ClNs);
  }

  private static boolean specificHeadText(PsiElement elem, String head) {
    return (elem instanceof ClList) &&
        head.equals(((ClList) elem).getHeadText());
  }

  public static boolean isImportingClause(PsiElement elem) {
    return isImportList(elem) ||
        isRequireList(elem) ||
        isUseList(elem);
  }

  public static boolean isImportMember(ClList list) {
    return isImportingClause(list.getParent()) ||
        (list.getParent() instanceof ClQuotedForm &&
        isImportingClause(list.getParent().getParent()));
  }
}
