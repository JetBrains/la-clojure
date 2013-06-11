package org.jetbrains.plugins.clojure.compiler;

import com.intellij.compiler.CompilerConfiguration;
import com.intellij.compiler.CompilerException;
import com.intellij.compiler.impl.javaCompiler.BackendCompiler;
import com.intellij.compiler.impl.javaCompiler.BackendCompilerWrapper;
import com.intellij.compiler.impl.resourceCompiler.ResourceCompiler;
import com.intellij.compiler.make.CacheCorruptedException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.compiler.ex.CompileContextEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

import java.util.Arrays;

/**
 * @author ilyas
 */
public class ClojureCompiler implements TranslatingCompiler {
  private static final Logger LOG = Logger.getInstance("#org.jetbrains.plugins.clojure.compiler.ClojureCompiler");
  private Project myProject;
  private static final FileTypeManager FILE_TYPE_MANAGER = FileTypeManager.getInstance();

  public ClojureCompiler(Project project) {
    myProject = project;
  }

  @NotNull
  public String getDescription() {
    return ClojureBundle.message("clojure.compiler.description");
  }

  public boolean isCompilableFile(final VirtualFile file, CompileContext context) {
    final ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(myProject);
    return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
      public Boolean compute() {
        if (!file.isValid()) return false;

        final FileType fileType = FILE_TYPE_MANAGER.getFileTypeByFile(file);
        if (!fileType.equals(ClojureFileType.CLOJURE_FILE_TYPE)) return false;
        
        PsiFile psi = PsiManager.getInstance(myProject).findFile(file);
        if (!(psi instanceof ClojureFile)) return false;

        return (settings.getState().COPY_CLJ_SOURCES || settings.getState().COMPILE_CLOJURE && ((ClojureFile) psi).isClassDefiningFile());
      }
    });

  }


  public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink outputSink) {
    final BackendCompiler backEndCompiler = getBackEndCompiler();
    final BackendCompilerWrapper wrapper = new BackendCompilerWrapper(moduleChunk, myProject, Arrays.asList(files),
        (CompileContextEx) context, backEndCompiler, outputSink);
    final ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(context.getProject());

    if (settings.getState().COMPILE_CLOJURE) {
      // Compile Clojure classes
      try {
        wrapper.compile();
      }
      catch (CompilerException e) {
        context.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
      }
      catch (CacheCorruptedException e) {
        LOG.info(e);
        context.requestRebuildNextTime(e.getMessage());
      }
    }

    // Copy clojure sources to output path
    if (settings.getState().COPY_CLJ_SOURCES) {
      final ResourceCompiler resourceCompiler = new ResourceCompiler(myProject, CompilerConfiguration.getInstance(myProject));
      resourceCompiler.compile(context, moduleChunk, files, outputSink);
    }
  }

  public boolean validateConfiguration(CompileScope scope) {
    return getBackEndCompiler().checkCompiler(scope);
  }

  private BackendCompiler getBackEndCompiler() {
    return new ClojureBackendCompiler(myProject);
  }
}