package org.jetbrains.plugins.clojure.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.stubs.index.ClDefNameIndex;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ilyas
 */
public class CompleteSymbol {
  public static Object[] getVariants(ClSymbol symbol) {
    final PsiElement parent = symbol.getParent();
    Collection<Object> variants = new ArrayList<Object>();

    if (parent instanceof ClList) {

      ClList list = (ClList) parent;
      final boolean isFirst = list.getFirstSymbol() == symbol;

      if (symbol.findFirstChildByClass(ClSymbol.class) == null) {
        variants.addAll(getDefVariants(isFirst));
      }
      variants.addAll(getJavaCompletionVariants(symbol, isFirst));
    }
    return variants.toArray(new Object[variants.size()]);
  }

  private static Collection<LookupItem> getDefVariants(final boolean first) {
    // Completion only for first symbols
    final Collection<String> keys = StubIndex.getInstance().getAllKeys(ClDefNameIndex.KEY);
    return ContainerUtil.map(keys, new Function<String, LookupItem>() {
      public LookupItem fun(String s) {
        final LookupItem item = new LookupItem(s, s);
        item.setIcon(ClojureIcons.FUNCTION);
        if (first) {
          item.setBold();
        }
        return item;
      }
    });
  }

  private static List<Object> getJavaCompletionVariants(ClSymbol symbol, final boolean isFirst) {
    //Processing Java methods
    List<Object> list = new ArrayList<Object>();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(symbol.getProject());

    if (symbol.getChildren().length == 0 && symbol.getText().startsWith(".")) {
      final PsiShortNamesCache shortNamesCache = facade.getShortNamesCache();
      final List<Object> methods = ContainerUtil.map(shortNamesCache.getAllMethodNames(), new Function<String, Object>() {
        public LookupItem fun(String s) {
          final LookupItem item = new LookupItem(s, "." + s);
          item.setIcon(ClojureIcons.JAVA_METHOD);
          if (isFirst) {
            item.setBold();
          }
          return item;
        }
      });

      list.addAll(methods);


      final List<Object> fields = ContainerUtil.map(shortNamesCache.getAllFieldNames(), new Function<String, Object>() {
        public LookupItem fun(String s) {
          final LookupItem item = new LookupItem(s, "." + s);
          item.setIcon(ClojureIcons.JAVA_FIELD);
          if (isFirst) {
            item.setBold();
          }
          return item;
        }
      });

      list.addAll(fields);
    } else {

      //todo remove me!
      final ClSymbol first = symbol.findFirstChildByClass(ClSymbol.class);
      if (first != null && "MathUtils".equals(first.getNameString())) {
        final PsiClass clazz = facade.findClass("org.jetbrains.benchmark.math.java.MathUtils", GlobalSearchScope.allScope(symbol.getProject()));
        if (clazz != null) {
          list.addAll(Arrays.asList(clazz.getAllMethods()));
          return list;
        }
      }

      // add default classes
      final PsiPackage javaLang = facade.findPackage("java.lang");
      if (javaLang != null) {
        list.addAll(Arrays.asList(javaLang.getClasses()));
      }

      final PsiPackage clojureCore = facade.findPackage("clojure.lang");
      if (clojureCore != null) {
        list.addAll(Arrays.asList(clojureCore.getClasses()));
      }

      //todo remove me!
      final PsiPackage util = facade.findPackage("org.jetbrains.benchmark.math.java");
      if (util != null) {
        list.addAll(Arrays.asList(util.getClasses()));
      }


    }
    return list;
  }
}
