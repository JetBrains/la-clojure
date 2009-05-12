package org.jetbrains.plugins.clojure.psi.impl.synthetic;

import org.jetbrains.plugins.clojure.psi.api.synthetic.ClSyntheticClass;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.ClVector;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.psi.util.ClojurePsiUtil;
import org.jetbrains.plugins.clojure.psi.impl.ClKeyImpl;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.impl.InheritanceImplUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.navigation.ItemPresentation;

import javax.swing.*;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ilyas
 *         <p/>
 *         Class to represent bytecode-compiled clojure files
 */
public class ClSyntheticClassImpl extends LightElement implements ClSyntheticClass {

  @Override
  public PsiElement getNavigationElement() {
    return myFile.getNamespaceElement();
  }

  @NotNull
  private final ClojureFile myFile;
  private String myQualifiedName;
  private String myName;

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return super.processDeclarations(processor, state, lastParent, place);
  }

  public ClSyntheticClassImpl(@NotNull ClojureFile file) {
    super(file.getManager(), ClojureFileType.CLOJURE_LANGUAGE);
    myFile = file;
    assert myFile.isClassDefiningFile();
    assert myFile.getNamespaceElement() != null;
    cachesNames();
  }

  private void cachesNames() {
    String name = myFile.getName();
    int i = name.indexOf('.');
    myName = i > 0 ? name.substring(0, i) : name;
    String packageName = myFile.getPackageName();
    myQualifiedName = packageName.length() > 0 ? packageName + "." + myName : myName;
  }


  public String getText() {
    return "class " + myName + " {}";
  }

  @Override
  public String toString() {
    return "ClojureSynteticClass[" + getQualifiedName() + "]";
  }

  public void accept(@NotNull PsiElementVisitor psiElementVisitor) {
  }


  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      public String getPresentableText() {
        return getName();
      }

      public String getLocationString() {
        String pn = myFile.getPackageName();
        return "(" + (pn.equals("") ? "<default package>" : pn) + ") in " + myFile.getName();
      }

      public TextAttributesKey getTextAttributesKey() {
        return null;
      }

      public Icon getIcon(boolean open) {
        return myFile.getIcon(0);
      }
    };
  }

  public PsiElement copy() {
    return new ClSyntheticClassImpl(myFile);
  }

  public String getQualifiedName() {
    return myQualifiedName;
  }

  public boolean isInterface() {
    return false;
  }

  public boolean isAnnotationType() {
    return false;
  }

  public boolean isEnum() {
    return false;
  }

  public PsiReferenceList getExtendsList() {
    //todo implement me!
    return null;
  }

  public PsiReferenceList getImplementsList() {
    return null;
  }

  @NotNull
  public PsiClassType[] getExtendsListTypes() {
    final PsiClass superClass = getSuperClass();
    final PsiElementFactory factory = JavaPsiFacade.getInstance(getProject()).getElementFactory();
    final GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    if (superClass != null && superClass.getQualifiedName() != null) {
      return new PsiClassType[]{factory.createTypeByFQClassName(superClass.getQualifiedName(), scope)};
    }

    return PsiClassType.EMPTY_ARRAY;
  }

  @Override
  public void delete() throws IncorrectOperationException {
    myFile.delete();
  }

  @NotNull
  public PsiClassType[] getImplementsListTypes() {
    return new PsiClassType[0];
  }

  public PsiClass getSuperClass() {
    final ClList ns = getNsElement();
    final ClKeyImpl key = ClojurePsiUtil.findNamespaceKeyByName(ns, ClojurePsiUtil.EXTENDS);
    if (key != null) {
      final PsiElement next = ClojurePsiUtil.getNextNonWhiteSpace(key);
      if (next instanceof ClSymbol) {
        ClSymbol symbol = (ClSymbol) next;
        return JavaPsiFacade.getInstance(getProject()).findClass(symbol.getText(), GlobalSearchScope.allScope(getProject()));
      }
    }
    return null;
  }

  @NotNull
  private ClList getNsElement() {
    return myFile.getNamespaceElement();
  }

  public PsiClass[] getInterfaces() {
    final ClList ns = getNsElement();
    final ClKeyImpl key = ClojurePsiUtil.findNamespaceKeyByName(ns, ClojurePsiUtil.IMPLEMENTS);
    final ArrayList<PsiClass> classes = new ArrayList<PsiClass>();
    final GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(getProject());
    if (key != null) {
      final PsiElement next = ClojurePsiUtil.getNextNonWhiteSpace(key);
      if (next instanceof ClVector) {
        ClVector vector = (ClVector) next;
        for (PsiElement element : vector.getChildren()) {
          if (element instanceof ClSymbol) {
            ClSymbol symbol = (ClSymbol) element;
            final PsiClass clazz = facade.findClass(symbol.getText(), scope);
            if (clazz != null)  classes.add(clazz);
          }
        }
        return classes.toArray(PsiClass.EMPTY_ARRAY);
      }
    }
    return new PsiClass[0];
  }

  @NotNull
  public PsiClass[] getSupers() {
    final ArrayList<PsiClass> list = new ArrayList<PsiClass>();
    final PsiClass psiClass = getSuperClass();
    if (psiClass != null) {
      list.add(psiClass);
    }
    list.addAll(Arrays.asList(getInterfaces()));
    return list.toArray(PsiClass.EMPTY_ARRAY);
  }

  @NotNull
  public PsiClassType[] getSuperTypes() {
    final ArrayList<PsiClassType> types = new ArrayList<PsiClassType>();
    final PsiClass superClass = getSuperClass();
    final PsiElementFactory factory = JavaPsiFacade.getInstance(getProject()).getElementFactory();
    final GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    if (superClass != null && superClass.getQualifiedName() != null) {
      types.add(factory.createTypeByFQClassName(superClass.getQualifiedName(), scope));
    }

    for (PsiClass clazz : getInterfaces()) {
      final String qName = clazz.getQualifiedName();
      if (qName != null) {
        types.add(factory.createTypeByFQClassName(qName, scope));
      }
    }
    return types.toArray(PsiClassType.EMPTY_ARRAY);
  }

  @NotNull
  public PsiField[] getFields() {
    return new PsiField[0];
  }

  @NotNull
  public PsiMethod[] getMethods() {
    //todo implement me!
    return PsiMethod.EMPTY_ARRAY;
  }

  @NotNull
  public PsiMethod[] getConstructors() {
    return new PsiMethod[0];
  }

  @NotNull
  public PsiClass[] getInnerClasses() {
    return new PsiClass[0];
  }

  @NotNull
  public PsiClassInitializer[] getInitializers() {
    return new PsiClassInitializer[0];
  }

  @NotNull
  public PsiField[] getAllFields() {
    return new PsiField[0];
  }

  @NotNull
  public PsiMethod[] getAllMethods() {
    final ArrayList<PsiMethod> list = new ArrayList<PsiMethod>();
    for (PsiClass clazz : getInterfaces()) {
      list.addAll(Arrays.asList(clazz.getAllMethods()));
    }
    final PsiClass aClass = getSuperClass();
    if (aClass != null) {
      list.addAll(Arrays.asList(aClass.getAllMethods()));
    }
    return list.toArray(PsiMethod.EMPTY_ARRAY);
  }

  @NotNull
  public PsiClass[] getAllInnerClasses() {
    return new PsiClass[0];
  }

  public PsiField findFieldByName(@NonNls String s, boolean b) {
    return null;
  }

  public PsiMethod findMethodBySignature(PsiMethod psiMethod, boolean b) {
    return null;
  }

  @NotNull
  public PsiMethod[] findMethodsBySignature(PsiMethod psiMethod, boolean b) {
    return new PsiMethod[0];
  }

  @NotNull
  public PsiMethod[] findMethodsByName(@NonNls String s, boolean b) {
    return new PsiMethod[0];
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(@NonNls String s, boolean b) {
    return new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
    return new ArrayList<Pair<PsiMethod, PsiSubstitutor>>();
  }

  public PsiClass findInnerClassByName(@NonNls String s, boolean b) {
    return null;
  }

  public PsiJavaToken getLBrace() {
    return null;
  }

  public PsiJavaToken getRBrace() {
    return null;
  }

  public PsiIdentifier getNameIdentifier() {
    return null;
  }

  public PsiElement getScope() {
    return null;
  }

  public boolean isInheritor(@NotNull PsiClass psiClass, boolean b) {
    return InheritanceImplUtil.isInheritor(this, psiClass, b);
  }

  public boolean isInheritorDeep(PsiClass baseClass, @Nullable PsiClass classToByPass) {
    return InheritanceImplUtil.isInheritorDeep(this, baseClass, classToByPass);
  }

  public PsiClass getContainingClass() {
    return null;
  }

  @NotNull
  public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
    return new ArrayList<HierarchicalMethodSignature>();
  }

  public String getName() {
    return myName;
  }

  public PsiElement setName(@NonNls String s) throws IncorrectOperationException {
    return myFile.setClassName(s);
  }

  public PsiModifierList getModifierList() {
    //todo implement me!
    return null;
  }

  public boolean hasModifierProperty(@Modifier String s) {
    //todo implement me!
    return false;
  }

  public PsiDocComment getDocComment() {
    return null;
  }

  public boolean isDeprecated() {
    return false;
  }

  public boolean hasTypeParameters() {
    return false;
  }

  public PsiTypeParameterList getTypeParameterList() {
    return null;
  }

  @NotNull
  public PsiTypeParameter[] getTypeParameters() {
    return new PsiTypeParameter[0];
  }

  @Nullable
  public Icon getIcon(int flags) {
    return myFile.getIcon(flags);
  }

}
