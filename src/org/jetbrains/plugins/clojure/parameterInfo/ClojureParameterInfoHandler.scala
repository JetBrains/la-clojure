package org.jetbrains.plugins.clojure.parameterInfo

import com.intellij.lang.parameterInfo.ParameterInfoHandlerWithTabActionSupport
import org.jetbrains.plugins.clojure.psi.api.ClList
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol

/**
 * @author ilyas
 */

abstract class ClojureParameterInfoHandler extends ParameterInfoHandlerWithTabActionSupport[ClList, Any, ClSymbol] {

}