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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureBundle;
import org.jetbrains.plugins.clojure.file.ClojureFileType;
import org.jetbrains.plugins.clojure.psi.api.ClojureFile;

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
    final FileType fileType = FILE_TYPE_MANAGER.getFileTypeByFile(file);
    PsiFile psi = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>() {
      public PsiFile compute() {
        return PsiManager.getInstance(myProject).findFile(file);
      }
    });

    final boolean isClojureFile = fileType.equals(ClojureFileType.CLOJURE_FILE_TYPE) &&
        psi instanceof ClojureFile;

    return isClojureFile && (settings.COPY_CLJ_SOURCES ||
        settings.COMPILE_CLOJURE && ((ClojureFile) psi).isClassDefiningFile());
  }

  public ExitStatus compile(CompileContext context, VirtualFile[] files) {
    final BackendCompiler backEndCompiler = getBackEndCompiler();
    final BackendCompilerWrapper wrapper = new BackendCompilerWrapper(myProject, files, (CompileContextEx) context, backEndCompiler);
    OutputItem[] outputItems = new OutputItem[0];
    final ClojureCompilerSettings settings = ClojureCompilerSettings.getInstance(context.getProject());

    if (settings.COMPILE_CLOJURE) {
      // Compile Clojure classes
      try {
        outputItems = wrapper.compile();
      }
      catch (CompilerException e) {
        outputItems = EMPTY_OUTPUT_ITEM_ARRAY;
        context.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
      }
      catch (CacheCorruptedException e) {
        LOG.info(e);
        context.requestRebuildNextTime(e.getMessage());
        outputItems = EMPTY_OUTPUT_ITEM_ARRAY;
      }
    }

    // Copy clojure sources to output path
    if (settings.COPY_CLJ_SOURCES) {
      final ResourceCompiler resourceCompiler = new ResourceCompiler(myProject, CompilerConfiguration.getInstance(myProject));
      resourceCompiler.compile(context, files);
    }

    return new ExitStatusImpl(outputItems, wrapper.getFilesToRecompile());
  }

  public boolean validateConfiguration(CompileScope scope) {
    return getBackEndCompiler().checkCompiler(scope);
  }

  private BackendCompiler getBackEndCompiler() {
    return new ClojureBackendCompiler(myProject);
  }

  private static class ExitStatusImpl implements ExitStatus {

    private OutputItem[] myOuitputItems;
    private VirtualFile[] myMyFilesToRecompile;

    public ExitStatusImpl(OutputItem[] ouitputItems, VirtualFile[] myFilesToRecompile) {
      myOuitputItems = ouitputItems;
      myMyFilesToRecompile = myFilesToRecompile;
    }

    public OutputItem[] getSuccessfullyCompiled() {
      return myOuitputItems;
    }

    public VirtualFile[] getFilesToRecompile() {
      return myMyFilesToRecompile;
    }
  }
}