package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;

import java.util.Collection;

/**
 * @author ilyas
 */
public class CompleteSymbol {
  public static Object[] getVariants(ClSymbol symbol) {
    final PsiElement parent = symbol.getParent();
    if (parent instanceof ClList) {
      ClList list = (ClList) parent;
      // Completion only for first symbols
      final boolean isFirst = list.getFirstSymbol() == symbol;
      final Project project = symbol.getProject();
      final Collection<String> keys = StubIndex.getInstance().getAllKeys(ClDefNameIndex.KEY);
      return ContainerUtil.map2Array(keys, new Function<String, Object>() {
        public Object fun(String s) {
          final LookupItem item = new LookupItem(s, s);
          item.setIcon(ClojureIcons.FUNCTION);
          if (isFirst) {
            item.setBold();
          }
          return item;
        }
      });
    }
    return new Object[0];
  }
}
