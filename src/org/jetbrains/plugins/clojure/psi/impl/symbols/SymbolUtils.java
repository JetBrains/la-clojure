package org.jetbrains.plugins.clojure.psi.impl.symbols;

import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.api.ClList;

import javax.swing.*;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import static org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations.*;
import org.jetbrains.plugins.clojure.ClojureIcons;

/**
 * @author ilyas
 */
public class SymbolUtils {


  public static Icon getIcon(ClSymbol symbol, int flags) {
    final PsiElement parent = PsiTreeUtil.getParentOfType(symbol, ClList.class);
    if (parent instanceof ClList) {
      ClList list = (ClList) parent;

      // Functions and defs
      if (symbol == list.getSecondNonLeafElement()) {
        final PsiElement fst = list.getFirstNonLeafElement();
        if (fst instanceof ClSymbol) {
          ClSymbol lstSym = (ClSymbol) fst;
          final String nameString = lstSym.getNameString();

          if (FN.equals(nameString)) return ClojureIcons.FUNCTION;
          if (DEFN.equals(nameString)) return ClojureIcons.FUNCTION;
        }
      }


    }

    return null;
  }
}
