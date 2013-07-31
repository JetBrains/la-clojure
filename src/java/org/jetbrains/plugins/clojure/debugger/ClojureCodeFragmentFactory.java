package org.jetbrains.plugins.clojure.debugger;

import com.intellij.debugger.engine.evaluation.CodeFragmentFactory;
import com.intellij.debugger.engine.evaluation.TextWithImports;
import com.intellij.debugger.engine.evaluation.expression.EvaluatorBuilder;
import com.intellij.debugger.engine.evaluation.expression.EvaluatorBuilderImpl;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import org.jetbrains.plugins.clojure.debugger.fragments.ClojureCodeFragment;
import org.jetbrains.plugins.clojure.file.ClojureFileType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author ilyas
 */
public class ClojureCodeFragmentFactory extends CodeFragmentFactory{
  public JavaCodeFragment createCodeFragment(TextWithImports item, PsiElement context, Project project) {
    final StringBuffer text = new StringBuffer();

    final String query = "(do " + StringUtil.escapeStringCharacters(item.getText()) + ")";

    ArrayList<String> localNames = getLocalsFromContext(context);
    Collections.sort(localNames);
    text.append("java.lang.String str = \"(intern 'clojure.core 'tmp-debug (fn [" + StringUtil.join(localNames, " ") + "] " + query + "))\";\n");
    text.append("clojure.lang.Compiler.load(new java.io.StringReader(str));\n");
    text.append("clojure.lang.Var tmp = clojure.lang.RT.var(\"clojure.core\", \"tmp-debug\");\n");

    final String argList = StringUtil.join(localNames, ", ");
    text.append("tmp.fn().invoke(" + argList + ");\n");

    final JavaCodeFragmentFactory factory = JavaCodeFragmentFactory.getInstance(project);
    return factory.createCodeBlockCodeFragment(text.toString(), null, true);
  }

  private static ArrayList<String> getLocalsFromContext(PsiElement context) {
    final ArrayList<String> result = new ArrayList<String>();
    if (context instanceof PsiCodeBlock) {
      PsiCodeBlock block = (PsiCodeBlock) context;
      for (PsiStatement stmt : block.getStatements()) {
        if (stmt instanceof PsiDeclarationStatement) {
          PsiDeclarationStatement decl = (PsiDeclarationStatement) stmt;
          for (PsiElement element : decl.getDeclaredElements()) {
            if (element instanceof PsiLocalVariable) {
              PsiLocalVariable var = (PsiLocalVariable) element;
              result.add(var.getName());
            }
          }
        }
      }
    }
    return result;
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

  public EvaluatorBuilder getEvaluatorBuilder() {
    return EvaluatorBuilderImpl.getInstance();
  }
}
