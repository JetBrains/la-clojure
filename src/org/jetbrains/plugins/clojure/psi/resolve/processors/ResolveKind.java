package org.jetbrains.plugins.clojure.psi.resolve.processors;

/**
 * @author Alefas
 * @since 22.01.13
 */
public enum ResolveKind {
  NAMESPACE, JAVA_CLASS, OTHER;

  public static ResolveKind[] allKinds() {
    return new ResolveKind[] {ResolveKind.JAVA_CLASS, ResolveKind.NAMESPACE, ResolveKind.OTHER};
  }

  public static ResolveKind[] javaClassesKinds() {
    return new ResolveKind[] {JAVA_CLASS};
  }

  public static ResolveKind[] namesSpaceKinds() {
    return new ResolveKind[] {NAMESPACE};
  }
}
