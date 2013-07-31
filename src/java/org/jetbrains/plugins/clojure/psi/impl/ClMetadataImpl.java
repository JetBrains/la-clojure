package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClMap;
import org.jetbrains.plugins.clojure.psi.api.ClMetadata;

import java.util.Collections;
import java.util.List;

/**
 * @author ilyas
*/
public class ClMetadataImpl extends ClojurePsiElementImpl implements ClMetadata {
  public ClMetadataImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClMetadata";
  }

  @NotNull
  public List<ClKeyword> getKeys() {
    ClMap map = getUnderlyingMap();
    if (map == null) return Collections.emptyList();
    return ContainerUtil.map(map.getEntries(), new Function<ClMapEntry, ClKeyword>() {
      public ClKeyword fun(ClMapEntry clMapEntry) {
        return clMapEntry.getKeywordKey();
      }
    });
  }

  private ClMap getUnderlyingMap() {
    final ClMap map = findChildByClass(ClMap.class);
    if (map == null) return null;
    return map;
  }

  public ClojurePsiElement getValue(String key) {
    final ClMap map = getUnderlyingMap();
    if (map == null) return null;
    return map.getValue(key);
  }
}
