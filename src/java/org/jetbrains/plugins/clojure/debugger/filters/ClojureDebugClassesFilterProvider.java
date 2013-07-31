package org.jetbrains.plugins.clojure.debugger.filters;

import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.ui.classFilter.DebuggerClassFilterProvider;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ilyas
 */
public class ClojureDebugClassesFilterProvider implements DebuggerClassFilterProvider {

  @NonNls
  private static final String[] PROHIBITED_CLASS_PATTERNS =
      {"clojure.*"};

  private static ClassFilter[] FILTERS = ContainerUtil.map(PROHIBITED_CLASS_PATTERNS, new Function<String, ClassFilter>() {
    public ClassFilter fun(final String s) {
      return new ClassFilter(s);
    }
  }, new ClassFilter[0]);

  public List<ClassFilter> getFilters() {

    final ClojureDebuggerSettings settings = ClojureDebuggerSettings.getInstance();
    final Boolean flag = settings.DEBUG_DISABLE_SPECIFIC_CLOJURE_METHODS;
    final ArrayList<ClassFilter> list = new ArrayList<ClassFilter>();
    if (flag == null || flag.booleanValue()) {
      list.addAll(Arrays.asList(FILTERS));
      return list;
    }
    return list;
  }

}
