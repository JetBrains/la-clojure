package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.lexer.ClojureTokenTypes;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElementImpl;
import org.jetbrains.plugins.clojure.psi.api.ClKeyword;
import org.jetbrains.plugins.clojure.psi.api.ClMap;

import java.util.Arrays;
import java.util.List;

/**
 * @author ilyas
*/
public class ClMapImpl extends ClojurePsiElementImpl implements ClMap {
  public ClMapImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "ClMap";
  }

  @NotNull
  public PsiElement getFirstBrace() {
    PsiElement element = findChildByType(ClojureTokenTypes.LEFT_CURLY);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace() {
    return findChildByType(ClojureTokenTypes.RIGHT_CURLY);
  }


  public List<ClMapEntry> getEntries() {
    return Arrays.asList(findChildrenByClass(ClMapEntry.class));
  }

  public ClojurePsiElement getValue(final String key) {
    final ClMapEntry entry = ContainerUtil.find(getEntries(), new Condition<ClMapEntry>() {
      public boolean value(ClMapEntry clMapEntry) {
        final ClKeyword clKeyword = clMapEntry.getKeywordKey();
        if (clKeyword == null) return false;
        final String text = StringUtil.trimStart(clKeyword.getText(), ":");
        return text.equals(key);
      }
    });
    if (entry == null) return null;
    return entry.getValue();
  }
}
