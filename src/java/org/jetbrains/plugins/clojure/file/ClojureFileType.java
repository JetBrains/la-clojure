package org.jetbrains.plugins.clojure.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.ClojureIcons;
import org.jetbrains.plugins.clojure.ClojureLanguage;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: merlyn
 * Date: 16-Nov-2008
 * Time: 11:08:03 PM
 * Copyright 2007, 2008 Red Shark Technology
 * <p/>
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ClojureFileType extends LanguageFileType {

  public static final ClojureFileType CLOJURE_FILE_TYPE = new ClojureFileType();
  public static final Language CLOJURE_LANGUAGE = CLOJURE_FILE_TYPE.getLanguage();
  public static final Icon CLOJURE_LOGO = ClojureIcons.CLOJURE_ICON_16x16;
  @NonNls
  public static final String CLOJURE_DEFAULT_EXTENSION = "clj";


  public ClojureFileType() {
    super(new ClojureLanguage());
  }

  @NotNull
  public String getName() {
    return "Clojure";
  }

  @NotNull
  public String getDescription() {
    return "Clojure file";
  }

  @NotNull
  public String getDefaultExtension() {
    return "clj";
  }

  public Icon getIcon() {
    return ClojureIcons.CLOJURE_ICON_16x16;
  }

  public boolean isJVMDebuggingSupported() {
    return true;
  }



}
