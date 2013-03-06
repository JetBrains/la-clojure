package org.jetbrains.plugins.clojure.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.plugins.clojure.psi.ClojurePsiElement;
import org.jetbrains.plugins.clojure.psi.api.*;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.impl.list.ListDeclarations;
import org.jetbrains.plugins.clojure.psi.impl.ns.NamespaceUtil;
import org.jetbrains.plugins.clojure.psi.resolve.ResolveUtil;
import org.jetbrains.plugins.clojure.psi.util.ClojureKeywords;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;

import java.util.*;

/**
 * @author ilya
 */
public abstract class ImportOwner {
  public static boolean processDeclarations(PsiElement self, PsiScopeProcessor processor, PsiElement place) {
    for (PsiElement element : self.getChildren()) {
      if (element instanceof ClList || element instanceof ClVector) {
        ClListLike directive = (ClListLike) element;
        final PsiElement first = directive.getFirstNonLeafElement();
        if (first == null) return true;
        final String headText = first.getText();
        if (!processImports(processor, place, directive, headText)) return false;
        if (!processUses(processor, place, directive, headText)) return false;
        if (!processRequires(processor, place, directive, headText)) return false;
        if (!processRefer(processor, place, directive, headText)) return false;
      }
    }
    return true;
  }

  /**
   * Require directive loads clojure namespaces
   * Syntax in namespace:
   * {Require in namespace} ::= '(' ':require' {Require Directive}* ')'
   * {Require Directive}    ::= '(' {Prefix} {Name Directive}* ')' |
   *                            '[' {Prefix} {Name Directive}* ']' |
   *                            '[' {Namespace Qualified Name} ':as' {Identifier} ']'
   *                            {Namespace Qualified Name}
   * {Name Directive}       ::= '[' {Namespace Name} ':as' {Identifier} ']' |
   *                            {Namespace Name}
   *
   * Examples:
   * (:require clojure.string)
   * (:require [clojure.string :as str])
   * (:require (clojure.string))
   * (:require (clojure string reflect))
   * (:require (clojure [string :as str] reflect))
   *
   * Syntax for function call is the same, you just can quote it (unquoted will not work in this case).
   *
   * Examples:
   * (require 'clojure.string)
   * (require '[clojure.string :as str])
   * (require '(clojure.string))
   * (require '(clojure string reflect))
   * (require '(clojure [string :as str] reflect))
   */
  public static boolean processRequires(PsiScopeProcessor processor, PsiElement place, ClListLike child, String headText) {
    final boolean isRequireKeyword = ClojureKeywords.REQUIRE.equals(headText);
    final boolean isRequireFunction = ListDeclarations.REQUIRE.equals(headText);
    if (isRequireKeyword || isRequireFunction) {
      if (processRequireInner(processor, place, child, isRequireKeyword, isRequireFunction)) return false;
    }
    return true;
  }

  private static boolean processRequireInner(PsiScopeProcessor processor, PsiElement place, ClListLike child, boolean requireKeyword, boolean requireFunction) {
    for (PsiElement stmt : child.getChildren()) {
      if (requireKeyword && !checkRequireStatement(processor, place, child, stmt)) return true;
      if (requireFunction && stmt instanceof ClQuotedForm) {
        final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
        if (!checkRequireStatement(processor, place, child, quotedElement)) return true;
      }
    }
    return false;
  }

  private static boolean processReferInner(PsiScopeProcessor processor, PsiElement place, ClListLike child, boolean referKeyword, boolean referFunction) {
    for (PsiElement stmt : child.getChildren()) {
      if (referKeyword && !checkReferStatement(processor, place, child, stmt)) return true;
      if (referFunction && stmt instanceof ClQuotedForm) {
        final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
        if (!checkReferStatement(processor, place, child, quotedElement)) return true;
      }
    }
    return false;
  }

  /**
   * (refer ns-symbol & filters)
   * filters: :only list of symbols
   *          :exclude list of symbols
   *          :rename map of from symbol to symbol
   * (refer '[clojure.string :exclude [replace reverse]])
   * (refer '[clojure.string :rename {replace str-replace, reverse str-reverse}])
   * (refer '[clojure.string :only [join split]])
   */
  public static boolean processRefer(PsiScopeProcessor processor, PsiElement place, ClListLike directive, String headText) {
    final boolean isReferKeyword = ClojureKeywords.REFER.equals(headText);
    final boolean isReferFunction = ListDeclarations.REFER.equals(headText);
    if (isReferKeyword || isReferFunction) {
      if (processReferInner(processor, place, directive, isReferKeyword, isReferFunction)) return false;
    }
    return true;
  }

  /**
   * Use = refer + require
   */
  public static boolean processUses(PsiScopeProcessor processor, PsiElement place, ClListLike directive, String headText) {
    final boolean isUseKeyword = ClojureKeywords.USE.equals(headText);
    final boolean isUseFunction = ListDeclarations.USE.equals(headText);
    if (isUseKeyword || isUseFunction) {
      if (processRequireInner(processor, place, directive, isUseKeyword, isUseFunction)) return false;
      if (processReferInner(processor, place, directive, isUseKeyword, isUseFunction)) return false;
    }
    return true;
  }

  /**
   * Import directive imports Java classes.
   * Syntax in namespace:
   * {Import in namespace} ::= '(' ':import' {Import Directive}* ')'
   * {Import Directive}    ::= '(' {Package} {Class Name}* ')' |
   *                           '[' {Package} {Class Name}* ']' |
   *                           {Class Qualified Name}
   *
   * Examples:
   * (:import java.util.Date)
   * (:import (java.util Date ArrayList))
   * (:import [java.util Date ArrayList])
   *
   * Syntax for function call is the same, you just can quote it.
   *
   * Examples:
   * (import 'java.util.Date)
   * (import java.util.Date)
   * (import '(java.util Date))
   * (import '[java.util Date])
   * (import (java.util Date ArrayList))
   */
  public static boolean processImports(PsiScopeProcessor processor, PsiElement place, ClListLike child, String headText) {
    final boolean isImportKeyword = ClojureKeywords.IMPORT.equals(headText);
    final boolean isImportFunction = ListDeclarations.IMPORT.equals(headText);
    if (isImportKeyword || isImportFunction) {
      for (PsiElement stmt : child.getChildren()) {
        if (!checkImportStatement(processor, place, child, stmt)) return false;
        if (isImportFunction && stmt instanceof ClQuotedForm) {
          final ClojurePsiElement quotedElement = ((ClQuotedForm) stmt).getQuotedElement();
          if (!checkImportStatement(processor, place, child, quotedElement)) return false;
        }
      }
    }

    return true;
  }

  private static boolean checkImportStatement(PsiScopeProcessor processor, PsiElement place, ClListLike child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkImportQualifier(processor, place, child, ((ClSymbol) stmt).getNameString())) return false;
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      final List<String> qualifiedNames = extractImportQualifiedNames(listLike);
      for (String qualifiedName : qualifiedNames) {
        if (!checkImportQualifier(processor, place, child, qualifiedName)) return false;
      }
    }
    return true;
  }

  private static boolean checkRequireStatement(PsiScopeProcessor processor, PsiElement place, ClListLike child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkRequireQualifier(processor, place, child, ((ClSymbol) stmt).getNameString())) return false;
    } else if (stmt instanceof ClVector && isSpecialVector((ClVector) stmt, ClojureKeywords.AS)) {
      ClVector vector = (ClVector) stmt;
      final ClSymbol[] symbols = vector.getAllSymbols();
      if (symbols.length > 0) {
        final ClSymbol symbol = symbols[0];
        if (!processVectorAliasSymbols(processor, vector, symbol)) return false;
        if (!checkRequireQualifier(processor, place, child, symbol.getNameString())) {
          return false;
        }
      }
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      if (!processRequireQualifiedNames(processor, place, child, listLike)) return false;
    }
    return true;
  }



  private static boolean checkReferStatement(PsiScopeProcessor processor, PsiElement place, ClListLike child, PsiElement stmt) {
    if (stmt instanceof ClSymbol) {
      if (!checkReferQualifier(processor, place, child, ((ClSymbol) stmt).getNameString(), new ReferFilter())) return false;
    } else if (stmt instanceof ClVector && isSpecialVector((ClVector) stmt)) {
      ClVector vector = (ClVector) stmt;
      final ClSymbol[] symbols = vector.getAllSymbols();
      if (symbols.length > 0) {
        final ClSymbol symbol = symbols[0];
        final ReferFilter referFilter = collectReferFilter(vector, symbol);
        if (!checkReferQualifier(processor, place, child, symbol.getNameString(), referFilter)) {
          return false;
        }
      }
    } else if (stmt instanceof ClVector || stmt instanceof ClList) {
      final ClListLike listLike = (ClListLike) stmt;
      if (!processReferQualifiedNames(processor, place, child, listLike)) return false;
    }
    return true;
  }

  private static class ReferFilter {
    private List<String> excludes = new ArrayList<String>();
    private List<String> only = new ArrayList<String>();
    private Map<String, ClSymbol> renames = new HashMap<String, ClSymbol>();

    private void setHasOnly(boolean hasOnly) {
      this.hasOnly = hasOnly;
    }

    private boolean hasOnly = false;

    public void addExclude(String s) {
      excludes.add(s);
    }

    public void addOnly(String s) {
      only.add(s);
    }

    public void addRename(String from, ClSymbol to) {
      renames.put(from, to);
    }

    public List<String> getExcludes() {
      return excludes;
    }

    private List<String> getOnly() {
      return only;
    }

    private Map<String, ClSymbol> getRenames() {
      return renames;
    }

    public String accept(String name) {
      if (excludes.contains(name)) return null;
      if (!only.isEmpty() && !only.contains(name)) return null;
      final ClSymbol symbol = renames.get(name);
      return symbol == null ? name : symbol.getNameString();
    }
  }

  private static ReferFilter collectReferFilter(ClVector vector, ClSymbol firstSymbol) {
    final ReferFilter result = new ReferFilter();
    for (PsiElement child : vector.getChildren()) {
      if (child instanceof ClKeyword) {
        final String keywordName = ((ClKeyword) child).getName();
        final boolean isOnly = keywordName.equals(ClojureKeywords.ONLY);
        final boolean isExclude = keywordName.equals(ClojureKeywords.EXCLUDE);
        final boolean isRename = keywordName.equals(ClojureKeywords.RENAME);
        if (isOnly || isExclude) {
          final PsiElement list = ClojurePsiUtil.getNextNonWhiteSpace(child);
          if (list instanceof ClVector || list instanceof ClList) {
            ClListLike listLike = (ClListLike) list;
            final ClSymbol[] symbols = listLike.getAllSymbols();
            for (ClSymbol symbol : symbols) {
              if (isOnly) {
                result.addOnly(symbol.getNameString());
              } else if (isExclude) {
                result.addExclude(symbol.getNameString());
              }
            }
          }
        } else if (isRename) {
          final PsiElement map = ClojurePsiUtil.getNextNonWhiteSpace(child);
          if (map instanceof ClMap) {
            for (ClMapEntry entry : ((ClMap) map).getEntries()) {
              final ClojurePsiElement key = entry.getKey();
              final ClojurePsiElement value = entry.getValue();
              if (key instanceof ClSymbol && value instanceof ClSymbol) {
                result.addRename(((ClSymbol) key).getNameString(), (ClSymbol) value);
              }
            }
          }
        }
      }
    }
    return result;
  }

  private static boolean processVectorAliasSymbols(PsiScopeProcessor processor, ClVector vector, ClSymbol firstSymbol) {
    for (PsiElement child : vector.getChildren()) {
      if (child instanceof ClKeyword && ((ClKeyword) child).getName().equals(ClojureKeywords.AS)) {
        NameHint nameHint = processor.getHint(NameHint.KEY);
        final PsiElement symbol = ClojurePsiUtil.getNextNonWhiteSpace(child);
        if (symbol instanceof ClSymbol) {
          String alias = nameHint == null ? null : nameHint.getName(ResolveState.initial());
          final String aliasName = ((ClSymbol) symbol).getName();
          if (alias != null && alias.equals(aliasName)) {
            for (ResolveResult result : firstSymbol.multiResolve(false)) {
              final PsiElement element = result.getElement();
              if (element instanceof PsiNamedElement) {
                PsiNamedElement namedElement = (PsiNamedElement) element;
                return processor.execute(namedElement, ResolveState.initial());
              }
            }
          } else if (nameHint == null) {
            if (!processor.execute(symbol, ResolveState.initial())) return false;
          }
        }
        break;
      }
    }

    return true;
  }

  public static boolean isSpecialVector(ClVector vector) {
    return isSpecialVector(vector, ClojureKeywords.AS) ||
           isSpecialVector(vector, ClojureKeywords.ONLY) ||
           isSpecialVector(vector, ClojureKeywords.RENAME) ||
           isSpecialVector(vector, ClojureKeywords.EXCLUDE);
  }

  public static boolean isSpecialVector(ClVector vector, String keyword) {
    for (PsiElement child : vector.getChildren()) {
      if (child instanceof ClKeyword && ((ClKeyword) child).getName().equals(keyword)) {
        return true;
      }
    }
    return false;
  }

  private static boolean checkImportQualifier(PsiScopeProcessor processor, PsiElement place, ClListLike child, String qualifiedName) {
    final PsiClass clazz = JavaPsiFacade.getInstance(child.getProject()).
        findClass(qualifiedName, GlobalSearchScope.allScope(place.getProject()));
    return !(clazz != null && !ResolveUtil.processElement(processor, clazz));
  }

  private static boolean checkRequireQualifier(PsiScopeProcessor processor, PsiElement place, ClListLike child, String qualifiedName) {
    //todo: See http://youtrack.jetbrains.com/issue/CLJ-169 for more details
    //In current state we don't need to do any work here
    return true;
  }

  private static boolean checkReferQualifier(PsiScopeProcessor processor, PsiElement place, ClListLike child,
                                             String qualifiedName, ReferFilter filter) {
    NameHint nameHint = processor.getHint(NameHint.KEY);
    String expectedName = null;
    if (nameHint != null) expectedName = nameHint.getName(ResolveState.initial());
    for (PsiNamedElement element : NamespaceUtil.getDeclaredElements(qualifiedName, place.getProject())) {
      if (element != null) {
        final String name = element.getName();
        final String newName = filter.accept(name);
        if (newName != null && (expectedName == null || expectedName.equals(newName))) {
          if (newName.equals(name)) {
            if (!ResolveUtil.processElement(processor, element)) return false;
          } else {
            if (!ResolveUtil.processElement(processor, element,
                ResolveState.initial().put(ResolveUtil.RENAMED_KEY, newName))) return false;
          }
        }
      }
    }
    return true;
  }

  private static List<String> extractImportQualifiedNames(ClListLike listLike) {
    final List<String> qualifiedNames = new ArrayList<String>();
    final PsiElement fst = listLike.getFirstNonLeafElement();
    if (fst instanceof ClSymbol) {
      PsiElement next = fst.getNextSibling();
      while (next != null) {
        if (next instanceof ClSymbol) {
          ClSymbol clazzSym = (ClSymbol) next;
          qualifiedNames.add(((ClSymbol) fst).getNameString() + "." + clazzSym.getNameString());
        }
        next = next.getNextSibling();
      }
    }
    return qualifiedNames;
  }

  private static boolean processRequireQualifiedNames(PsiScopeProcessor processor, PsiElement place, ClListLike child,
                                                      ClListLike listLike) {
    final PsiElement fst = listLike.getFirstNonLeafElement();
    if (fst instanceof ClSymbol) {
      PsiElement next = fst.getNextSibling();
      while (next != null) {
        if (next instanceof ClSymbol) {
          ClSymbol clazzSym = (ClSymbol) next;
          if (!checkRequireQualifier(processor, place, child, ((ClSymbol) fst).getNameString() + "." + clazzSym.getNameString())) {
            return false;
          }
        } else if (next instanceof ClVector) {
          ClVector vector = (ClVector) next;
          final ClSymbol[] symbols = vector.getAllSymbols();
          if (symbols.length > 0) {
            final ClSymbol symbol = symbols[0];
            if (isSpecialVector((ClVector) next, ClojureKeywords.AS) &&
                !processVectorAliasSymbols(processor, vector, symbol)) return false;
            if (!checkRequireQualifier(processor, place, child,
                ((ClSymbol) fst).getNameString() + "." + symbol.getNameString())) {
              return false;
            }
          }
        }
        next = next.getNextSibling();
      }
    }
    return true;
  }

  private static boolean processReferQualifiedNames(PsiScopeProcessor processor, PsiElement place, ClListLike child,
                                                    ClListLike listLike) {
    final PsiElement fst = listLike.getFirstNonLeafElement();
    if (fst instanceof ClSymbol) {
      PsiElement next = fst.getNextSibling();
      boolean isSimple = true;
      while (next != null) {
        if (next instanceof ClSymbol) {
          isSimple = false;
          ClSymbol clazzSym = (ClSymbol) next;
          if (!checkReferQualifier(processor, place, child, ((ClSymbol) fst).getNameString() + "." + clazzSym.getNameString(), new ReferFilter())) {
            return false;
          }
        } else if (next instanceof ClVector) {
          isSimple = false;
          ClVector vector = (ClVector) next;
          final ClSymbol[] symbols = vector.getAllSymbols();
          if (symbols.length > 0) {
            final ClSymbol symbol = symbols[0];
            ReferFilter filter = collectReferFilter(vector, symbol);
            if (!checkReferQualifier(processor, place, child,
                ((ClSymbol) fst).getNameString() + "." + symbol.getNameString(), filter)) {
              return false;
            }
          }
        }

        if (isSimple && listLike instanceof ClVector) {
          if (!checkReferQualifier(processor, place, child, ((ClSymbol) fst).getNameString(), new ReferFilter())) return false;
        }
        next = next.getNextSibling();
      }
    }
    return true;
  }
}
