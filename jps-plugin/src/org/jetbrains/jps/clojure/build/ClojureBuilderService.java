package org.jetbrains.jps.clojure.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.BuilderCategory;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author nik
 * @since 02.11.12
 */
public class ClojureBuilderService extends BuilderService {
  @NotNull
  @Override
  public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
    List<ModuleLevelBuilder> list =  new ArrayList<ModuleLevelBuilder>();
    list.add(new ClojureBuilder(false));
    list.add(new ClojureBuilder(true));
    return list;
  }
}
