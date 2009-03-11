package org.jetbrains.plugins.clojure.debugger;

import com.intellij.debugger.engine.evaluation.CodeFragmentFactory;
import com.intellij.debugger.engine.evaluation.TextWithImports;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.debugger.fragments.ClojureCodeFragment;

/**
 * @author ilyas
 */
public class ClojureCodeFragementFactory implements CodeFragmentFactory{
  public JavaCodeFragment createCodeFragment(TextWithImports item, PsiElement context, Project project) {
    final StringBuffer text = new StringBuffer();
    text.append("clojure.lang.Compiler.eval(clojure.lang.RT.readString(\"");
    text.append(StringUtil.escapeStringCharacters(item.getText()));
    text.append("\"))");
    final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
    return factory.createCodeBlockCodeFragment(text.toString(), null, true);
  }

  public JavaCodeFragment createPresentationCodeFragment(TextWithImports item, PsiElement context, Project project) {
    final ClojureCodeFragment fragment = new ClojureCodeFragment(project, item.getText());
    fragment.setContext(context);
    return fragment;
  }

  public boolean isContextAccepted(PsiElement context) {
    return context != null && context.getLanguage().equals(ClojureFileType.CLOJURE_FILE_TYPE.getLanguage());
  }

  public LanguageFileType getFileType() {
    return ClojureFileType.CLOJURE_FILE_TYPE;
  }
}
