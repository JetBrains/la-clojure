package org.jetbrains.plugins.clojure.compiler;

import com.intellij.compiler.OutputParser;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * @author ilyas
 */
public class ClojureOutputParser extends OutputParser {

  @NonNls
  private static final String ourErrorMarker = "comp_err:";
  @NonNls
  private static final String ourCompilingMarker = "compiling:";
  @NonNls
  private static final String ourCompiledMarker = "compiled:";


  @Override
  public boolean processMessageLine(Callback callback) {
    final String line = callback.getNextLine();
    if (line == null) {
      return false;
    }

    final String text = line.trim();
    if (text.startsWith(ourCompiledMarker)) {
      final String clazz = StringUtil.trimStart(text, ourCompiledMarker);
      callback.setProgressText("Compiled " + clazz);
      callback.fileProcessed(clazz);
      return true;
    }

    if (text.startsWith(ourCompilingMarker)) {
      String clazz = StringUtil.trimStart(text, ourCompilingMarker);
      callback.setProgressText("Compiling " + clazz);
      callback.fileProcessed(clazz);
      return true;
    }

    if (text.startsWith(ourErrorMarker)) {
      final String info = StringUtil.trimStart(text, ourErrorMarker);
      final int i = info.indexOf(":");
      final int at = info.indexOf("@");

      final String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, info.substring(0, i).replace(File.separatorChar, '/'));
      final String ns = info.substring(i + 1, at);

      final String msg = StringUtil.trimEnd(info.substring(at + 1), ":");


      int lineNum = 0;
      if (msg.matches(".*:\\d+\\)")) {
        try {
          final int j = msg.lastIndexOf(":");
          final String cand = msg.substring(j + 1, msg.length() - 1);
          lineNum = Integer.parseInt(cand);
        } catch (NumberFormatException e) {
          lineNum = 0;
        }
      }
//      final String trace = info.substring(at);
//      final List<String> list = StringUtil.split(trace, "^^");
//      final String newTrace = StringUtil.join(list.toArray(new String[0]), "\n");

      callback.message(CompilerMessageCategory.ERROR, "Error compiling lib \'" + ns + "\'\n" + msg, url, lineNum, 0);
      return true;
    } else {
      // exception tail
//      callback.message(CompilerMessageCategory.ERROR, text, "", 0, 0);
    }

    return true;
  }


}
