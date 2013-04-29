package org.jetbrains.plugins.clojure.completion;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.psi.statistics.StatisticsManager;
import com.intellij.psi.statistics.impl.StatisticsManagerImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureLightPlatformCodeInsightTestCase;

/**
 * @author Alefas
 * @since 16.01.13
 */
public abstract class ClojureCompletionTestBase extends ClojureLightPlatformCodeInsightTestCase {
  protected void setUp() throws Exception {
    super.setUp();
    ((StatisticsManagerImpl) StatisticsManager.getInstance()).enableStatistics(getTestRootDisposable());
  }

  protected class CompleteResult {
    private final LookupElement[] myElements;
    private final String myName;

    public LookupElement[] getElements() {
      return myElements;
    }

    public String getName() {
      return myName;
    }

    public CompleteResult(LookupElement[] elements, String name) {
      myElements = elements;
      myName = name;
    }
  }

  @Nullable
  protected CompleteResult complete() {
    return complete(1);
  }

  @Nullable
  protected CompleteResult complete(CompletionType completionType) {
    return complete(1, completionType);
  }

  @Nullable
  protected CompleteResult complete(int time) {
    return complete(time, CompletionType.BASIC);
  }

  @Nullable
  protected CompleteResult complete(int time, CompletionType completionType) {
    new CodeCompletionHandlerBase(completionType, false, false, true).
        invokeCompletion(getProject(), getEditor(), time, false, false);
    LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(getEditor());
    if (lookup == null) return null;
    return new CompleteResult(lookup.getItems().toArray(LookupElement.EMPTY_ARRAY),
        lookup.itemPattern(lookup.getItems().get(0)));
  }

  protected void completeLookupItem() {
    completeLookupItem(null, '\t');
  }

  protected void completeLookupItem(char completionChar) {
    completeLookupItem(null, completionChar);
  }

  protected void completeLookupItem(LookupElement item) {
    completeLookupItem(item, '\t');
  }

  protected void completeLookupItem(LookupElement item, char completionChar) {
    LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(getEditor());
    if (item == null) lookup.finishLookup(completionChar);
    else lookup.finishLookup(completionChar, item);
  }
}