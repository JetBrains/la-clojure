package org.jetbrains.plugins.clojure.debugger.filters;

import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.ui.classFilter.DebuggerClassFilterProvider;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NonNls;

import java.util.List;

/**
 * @author ilyas
 */
public class ClojureDebugClassesFilterProvider implements DebuggerClassFilterProvider {

  @NonNls
  private static final String[] PROHIBITED_CLASS_PATTERNS =
    {"clojure.*"};

  private static List<ClassFilter> FILTERS = ContainerUtil.map(PROHIBITED_CLASS_PATTERNS, new Function<String, ClassFilter>() {
    public ClassFilter fun(final String s) {
      return new ClassFilter(s);
    }
  });

  public List<ClassFilter> getFilters() {
    return FILTERS;
  }

}
