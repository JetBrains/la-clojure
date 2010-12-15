package org.jetbrains.plugins.clojure.psi.impl.symbols

/**
 * @author ilyas
 */

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.plugins.clojure.ClojureIcons
import org.jetbrains.plugins.clojure.psi.api.ClList
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations

import javax.swing._

/**
 * @author ilyas
 */

object SymbolUtils {

  def getIcon(symbol: ClSymbol, flags: Int): Icon =
    PsiTreeUtil.getParentOfType(symbol, classOf[ClList]) match {
      case list: ClList if symbol == list.getSecondNonLeafElement() =>
        list.getFirstNonLeafElement() match {
          case lstSym: ClSymbol => {
            lstSym.getNameString() match {
              case ListDeclarations.FN => ClojureIcons.FUNCTION
              case ListDeclarations.DEFN | ListDeclarations.DEFN_ => ClojureIcons.FUNCTION
              case _ => null
            }
          }
          case _ => null
        }
      case _ => null
    }

}
