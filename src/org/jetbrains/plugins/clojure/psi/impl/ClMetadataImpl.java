package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClMetadata;

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
}
