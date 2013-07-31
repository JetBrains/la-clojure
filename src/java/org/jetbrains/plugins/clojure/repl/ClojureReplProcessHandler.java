/*
 * Copyright 2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.clojure.repl;

import clojure.lang.AFn;
import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.util.Alarm;
import com.intellij.util.PathUtil;
import org.jetbrains.plugins.clojure.config.ClojureConfigUtil;
import org.jetbrains.plugins.clojure.utils.ClojureUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Kurt Christensen, ilyas
 * @author <a href="mailto:ianp@ianp.org">Ian Phillips</a>
 */
public class ClojureReplProcessHandler extends ProcessHandler {

  private static final Logger LOG = Logger.getInstance(ClojureReplProcessHandler.class.getName());
  private static ExecutorService ourThreadExecutorsService = null;

  private final Process myProcess;
  private final ProcessWaitFor myWaitFor;
  private final String myExecPath;
  private final Module myModule;

  private static final String CLOJURE_SDK = PathUtil.getJarPathForClass(AFn.class);

  public static Future<?> executeOnPooledThread(Runnable task) {
    final Application application = ApplicationManager.getApplication();

    if (application != null) {
      return application.executeOnPooledThread(task);
    } else {
      if (ourThreadExecutorsService == null) {
        ourThreadExecutorsService = new ThreadPoolExecutor(
            10,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
              @SuppressWarnings({"HardCodedStringLiteral"})
              public Thread newThread(Runnable r) {
                return new Thread(r, "OSProcessHandler pooled thread");
              }
            }
        );
      }

      return ourThreadExecutorsService.submit(task);
    }
  }

  public ClojureReplProcessHandler(String path, Module module)
      throws IOException, ConfigurationException, CantRunException {

    myExecPath = path;
    myModule = module;

    if (notConfigured()) {
      throw new ConfigurationException("Can't create Clojure REPL process");
    } else {
      // Only a single command line per-project is supported. We may need more flexibility
      //  in the future (e.g., different Clojure paths with different args)

      final JavaParameters params = new JavaParameters();
      params.configureByModule(myModule, JavaParameters.JDK_AND_CLASSES_AND_TESTS);
      // To avoid NCDFE while starting REPL

      final boolean sdkConfigured = ClojureConfigUtil.isClojureConfigured(myModule);
      if (!sdkConfigured) {
        final String jarPath = ClojureConfigUtil.CLOJURE_SDK;
        assert jarPath != null;
        params.getClassPath().add(jarPath);
      }

      Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
      ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(myModule);
      OrderEntry[] entries = moduleRootManager.getOrderEntries();
      for (OrderEntry orderEntry : entries) {
        // Add module sources to classpath
        if (orderEntry instanceof ModuleSourceOrderEntry) {
          cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
        }
      }

      for (VirtualFile file : cpVFiles) {
        params.getClassPath().add(file.getPath());
      }


      params.setMainClass(ClojureUtils.CLOJURE_MAIN);
      params.setWorkingDirectory(path);

      final GeneralCommandLine line = CommandLineBuilder.createFromJavaParameters(params, PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()), true);

      final Sdk sdk = params.getJdk();
      assert sdk != null;
      final SdkType type = (SdkType) sdk.getSdkType();
      final String executablePath = ((JavaSdkType) type).getVMExecutablePath(sdk);

      final ArrayList<String> env = new ArrayList<String>();
      final ArrayList<String> cmd = new ArrayList<String>();
      cmd.add(executablePath);
      cmd.addAll(line.getParametersList().getList());

      if (!sdkConfigured) {
        ClojureConfigUtil.warningDefaultClojureJar(myModule);
      }

      myProcess = Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]),
          env.toArray(new String[env.size()]), new File(myExecPath));
      myWaitFor = new ProcessWaitFor(myProcess);
    }
  }

  private boolean notConfigured() {
    return false;
  }

  private static class ProcessWaitFor {
    private final com.intellij.util.concurrency.Semaphore myWaitSemaphore = new com.intellij.util.concurrency.Semaphore();

    private final Future<?> myWaitForThreadFuture;
    private int myExitCode;

    public void detach() {
      myWaitForThreadFuture.cancel(true);
      myWaitSemaphore.up();
    }

    public ProcessWaitFor(final Process process) {
      myWaitSemaphore.down();
      final Runnable action = new Runnable() {
        public void run() {
          try {
            myExitCode = process.waitFor();
          }
          catch (InterruptedException e) {
            // Do nothing, by design
          }
          myWaitSemaphore.up();
        }
      };

      myWaitForThreadFuture = executeOnPooledThread(action);
    }

    public int waitFor() {
      myWaitSemaphore.waitFor();
      return myExitCode;
    }
  }

  public void startNotify() {
    final ReadProcessThread stdoutThread = new ReadProcessThread(createProcessOutReader()) {
      protected void textAvailable(String s) {
        notifyTextAvailable(s, ProcessOutputTypes.STDOUT);
      }
    };

    final ReadProcessThread stderrThread = new ReadProcessThread(createProcessErrReader()) {
      protected void textAvailable(String s) {
        notifyTextAvailable(s, ProcessOutputTypes.STDERR);
      }
    };

    //notifyTextAvailable(myCommandLine + '\n', ProcessOutputTypes.SYSTEM);

    addProcessListener(new ProcessAdapter() {
      public void startNotified(final ProcessEvent event) {
        try {
          final Future<?> stdOutReadingFuture = executeOnPooledThread(stdoutThread);
          final Future<?> stdErrReadingFuture = executeOnPooledThread(stderrThread);

          final Runnable action = new Runnable() {
            public void run() {
              int exitCode = 0;

              try {
                exitCode = myWaitFor.waitFor();

                // tell threads that no more attempts to read process' output should be made
                stderrThread.setProcessTerminated(true);
                stdoutThread.setProcessTerminated(true);

                stdErrReadingFuture.get();
                stdOutReadingFuture.get();
              }
              catch (InterruptedException e) {
                // Do nothing
              }
              catch (ExecutionException e) {
                // Do nothing
              }

              onOSProcessTerminated(exitCode);
            }
          };

          executeOnPooledThread(action);
        }
        finally {
          removeProcessListener(this);
        }
      }
    });

    super.startNotify();
  }

  protected void onOSProcessTerminated(final int exitCode) {
    notifyProcessTerminated(exitCode);
  }

  protected Reader createProcessOutReader() {
    return new BufferedReader(new InputStreamReader(myProcess.getInputStream(), getCharset()));
  }

  protected Reader createProcessErrReader() {
    return new BufferedReader(new InputStreamReader(myProcess.getErrorStream(), getCharset()));
  }

  protected void destroyProcessImpl() {
    try {
      closeStreams();
    }
    finally {
      myProcess.destroy();
    }
  }

  protected void detachProcessImpl() {
    final Runnable runnable = new Runnable() {
      public void run() {
        closeStreams();

        myWaitFor.detach();
        notifyProcessDetached();
      }
    };

    executeOnPooledThread(runnable);
  }

  private void closeStreams() {
    try {
      myProcess.getOutputStream().close();
    }
    catch (IOException e) {
      LOG.error(e);
    }
  }

  public boolean detachIsDefault() {
    return false;
  }

  public OutputStream getProcessInput() {
    return myProcess.getOutputStream();
  }

  public Charset getCharset() {
    return EncodingManager.getInstance().getDefaultCharset();
  }

  private static abstract class ReadProcessThread implements Runnable {
    private static final int NOTIFY_TEXT_DELAY = 300;

    private final Reader myReader;

    private final StringBuffer myBuffer = new StringBuffer();
    private final Alarm myAlarm;

    private boolean myIsClosed = false;
    private boolean myIsProcessTerminated = false;

    public ReadProcessThread(final Reader reader) {
      myReader = reader;
      myAlarm = new Alarm(Alarm.ThreadToUse.SHARED_THREAD);
    }

    public synchronized boolean isProcessTerminated() {
      return myIsProcessTerminated;
    }

    public synchronized void setProcessTerminated(boolean isProcessTerminated) {
      myIsProcessTerminated = isProcessTerminated;
    }

    public void run() {
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      try {
        myAlarm.addRequest(new Runnable() {
          public void run() {
            if (!isClosed()) {
              myAlarm.addRequest(this, NOTIFY_TEXT_DELAY);
              checkTextAvailable();
            }
          }
        }, NOTIFY_TEXT_DELAY);

        try {
          while (!isClosed()) {
            final int c = readNextByte();
            if (c == -1) {
              break;
            }
            synchronized (myBuffer) {
              myBuffer.append((char) c);
            }
            if (c == '\n') { // not by '\r' because of possible '\n'
              checkTextAvailable();
            }
          }
        }
        catch (Exception e) {
          LOG.error(e);
          e.printStackTrace();
        }

        close();
      }
      finally {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
      }
    }

    private int readNextByte() {
      try {
        while (!myReader.ready()) {
          if (isProcessTerminated()) {
            return -1;
          }
          try {
            Thread.sleep(1L);
          }
          catch (InterruptedException ignore) {
          }
        }
        return myReader.read();
      }
      catch (IOException e) {
        return -1; // When process terminated Process.getInputStream()'s underlaying stream becomes closed on Linux.
      }
    }

    private void checkTextAvailable() {
      synchronized (myBuffer) {
        if (myBuffer.length() == 0) return;
        // warning! Since myBuffer is reused, do not use myBuffer.toString() to fetch the string
        // because the created string will get StringBuffer's internal char array as a buffer which is possibly too large.
        final String s = myBuffer.substring(0, myBuffer.length());
        myBuffer.setLength(0);
        textAvailable(s);
      }
    }

    private void close() {
      synchronized (this) {
        if (isClosed()) {
          return;
        }
        myIsClosed = true;
      }
      try {
        myReader.close();
      }
      catch (IOException e1) {
        // supressed
      }
      checkTextAvailable();
    }

    protected abstract void textAvailable(final String s);

    private synchronized boolean isClosed() {
      return myIsClosed;
    }

  }
}
