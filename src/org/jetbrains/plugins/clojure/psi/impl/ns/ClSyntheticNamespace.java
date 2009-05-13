package org.jetbrains.plugins.clojure.psi.impl.ns;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.impl.ClojurePsiManager;

import javax.swing.*;

/**
 * @author ilyas
 */
public class ClSyntheticNamespace extends LightElement implements PsiPackage {
  @NotNull private final String myName;
  @NotNull private final String myQualifiedName;

  protected ClSyntheticNamespace(PsiManager manager, String name, String fqn) {
    super(manager, ClojureFileType.CLOJURE_LANGUAGE);
    myName = name;
    myQualifiedName = fqn;
  }

  public String getText() {
    return "";
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    throw new IncorrectOperationException("Don't ever call it!");
  }

  public PsiElement copy() {
    throw new IncorrectOperationException("cannot copy: nonphysical element");
  }

  @Override
  public PsiFile getContainingFile() {
    return ClojurePsiManager.getInstance(getProject()).getDummyFile();
  }

  @NotNull
  public String getQualifiedName() {
    return myQualifiedName;
  }

  public PsiPackage getParentPackage() {
    return null;
  }

  @NotNull
  public PsiPackage[] getSubPackages() {
    return new PsiPackage[0];
  }

  @NotNull
  public PsiPackage[] getSubPackages(@NotNull GlobalSearchScope scope) {
    return new PsiPackage[0];
  }

  @NotNull
  public PsiClass[] getClasses() {
    return new PsiClass[0];
  }

  @NotNull
  public PsiClass[] getClasses(@NotNull GlobalSearchScope scope) {
    return new PsiClass[0];
  }

  public PsiModifierList getAnnotationList() {
    return null;
  }

  public void handleQualifiedNameChange(@NotNull String newQualifiedName) {
  }

  public VirtualFile[] occursInPackagePrefixes() {
    return new VirtualFile[0];
  }

  public String getName() {
    return myName;
  }

  public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
    throw new IncorrectOperationException("cannot set name: nonphysical element");
  }

  public void checkSetName(String name) throws IncorrectOperationException {
    throw new IncorrectOperationException("cannot set name: nonphysical element");
  }

  public PsiModifierList getModifierList() {
    return null;
  }

  public boolean hasModifierProperty(@Modifier String name) {
    return false;
  }

  @NotNull
  public PsiDirectory[] getDirectories() {
    return new PsiDirectory[0];
  }

  @NotNull
  public PsiDirectory[] getDirectories(@NotNull GlobalSearchScope scope) {
    return new PsiDirectory[0];
  }

  @Override
  public String toString() {
    return "ClojureSyntheticNamespace:"+ getQualifiedName();
  }

  @Override
  public Icon getIcon(int flags) {
    return ClojureIcons.NAMESPACE;
  }

  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
      public String getPresentableText() {
        final String name = getName();
        return name == null ? "<undefined>" : name;
      }

      @Nullable
      public String getLocationString() {
        String name = getContainingFile().getName();
        //todo show namespace
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open) {
        return ClSyntheticNamespace.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey() {
        return null;
      }
    };
  }

}
