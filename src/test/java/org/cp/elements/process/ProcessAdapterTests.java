/*
 * Copyright 2016 Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cp.elements.process;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cp.elements.process.ProcessAdapter.DEFAULT_TIMEOUT_MILLISECONDS;
import static org.cp.elements.process.ProcessAdapter.newProcessAdapter;
import static org.cp.elements.process.ProcessContext.newProcessContext;
import static org.cp.elements.util.ArrayUtils.asArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

import org.cp.elements.io.FileExtensionFilter;
import org.cp.elements.io.FileSystemUtils;
import org.cp.elements.lang.Constants;
import org.cp.elements.lang.SystemUtils;
import org.cp.elements.process.event.ProcessStreamListener;
import org.cp.elements.util.Environment;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link ProcessAdapter}.
 *
 * @author John Blum
 * @see java.lang.Process
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.runners.MockitoJUnitRunner
 * @see edu.umd.cs.mtc.MultithreadedTestCase
 * @see edu.umd.cs.mtc.TestFramework
 * @see org.cp.elements.process.ProcessAdapter
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessAdapterTests {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private Process mockProcess;

  private ProcessContext processContext;

  @AfterClass
  public static void tearDown() {
    FileSystemUtils.deleteRecursive(FileSystemUtils.WORKING_DIRECTORY, new FileExtensionFilter(".pid"));
  }

  @Before
  public void setup() {
    this.processContext = newProcessContext(this.mockProcess).ranBy(SystemUtils.USERNAME)
      .ranIn(FileSystemUtils.WORKING_DIRECTORY);
  }

  @Test
  public void newProcessAdapterWithProcess() {
    ProcessAdapter processAdapter = newProcessAdapter(mockProcess);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);

    ProcessContext processContext = processAdapter.getProcessContext();

    assertThat(processContext).isInstanceOf(ProcessContext.class);
    assertThat(processContext).isNotSameAs(this.processContext);
    assertThat(processContext.getCommandLine()).isEmpty();
    assertThat(processContext.getDirectory()).isEqualTo(FileSystemUtils.WORKING_DIRECTORY);
    assertThat(processContext.getEnvironment()).isEqualTo(Environment.fromEnvironmentVariables());
    assertThat(processContext.getProcess()).isSameAs(this.mockProcess);
    assertThat(processContext.getUsername()).isEqualTo(SystemUtils.USERNAME);
    assertThat(processContext.isRedirectingErrorStream()).isFalse();
  }

  @Test
  public void newProcessAdapterWithProcessAndProcessContext() {
    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess, this.processContext);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.getProcessContext()).isSameAs(this.processContext);
  }

  @Test
  public void newProcessAdapterWithNullProcess() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Process cannot be null");

    newProcessAdapter(null, this.processContext);
  }

  @Test
  public void newProcessAdapterWithNullProcessContext() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("ProcessContext cannot be null");

    newProcessAdapter(this.mockProcess, null);
  }

  // test init()

  @Test
  public void newProcessStreamListenerIsInitializedCorrectly() {
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);

    StringBuilder buffer = new StringBuilder();

    assertThat(processAdapter.register(buffer::append)).isSameAs(processAdapter);

    ByteArrayInputStream in = new ByteArrayInputStream("This is the end of the line!\n".getBytes());

    Runnable processStreamReaderRunnable = processAdapter.newProcessStreamReader(in);

    assertThat(processStreamReaderRunnable).isNotNull();

    processStreamReaderRunnable.run();

    assertThat(buffer.toString()).isEqualTo("This is the end of the line!");

    verify(this.mockProcess, times(1)).exitValue();
  }

  @Test
  public void newReaderIsInitializedCorrectly() {
    InputStream mockInputStream = mock(InputStream.class);
    Reader reader = newProcessAdapter(this.mockProcess).newReader(mockInputStream);

    assertThat(reader).isInstanceOf(BufferedReader.class);

    verifyZeroInteractions(mockInputStream);
  }

  @Test
  public void newThreadIsInitializedCorrectly() {
    Thread testThread = newProcessAdapter(this.mockProcess).newThread("TestThread", () -> {});

    assertThat(testThread).isNotNull();
    assertThat(testThread.isAlive()).isFalse();
    assertThat(testThread.isDaemon()).isTrue();
    assertThat(testThread.isInterrupted()).isFalse();
    assertThat(testThread.getName()).isEqualTo("TestThread");
    assertThat(testThread.getPriority()).isEqualTo(ProcessAdapter.THREAD_PRIORITY);
    assertThat(testThread.getState()).isEqualTo(Thread.State.NEW);
  }

  @Test
  public void isAliveForRunningProcessIsTrue() {
    when(this.mockProcess.isAlive()).thenReturn(true);

    assertThat(newProcessAdapter(this.mockProcess).isAlive()).isTrue();

    verify(this.mockProcess, times(1)).isAlive();
  }

  @Test
  public void isAliveForTerminatedProcessIsFalse() {
    when(this.mockProcess.isAlive()).thenReturn(false);

    assertThat(newProcessAdapter(this.mockProcess).isAlive()).isFalse();

    verify(this.mockProcess, times(1)).isAlive();
  }

  @Test
  public void isInitializedBeforeInitIsFalse() {
    assertThat(newProcessAdapter(this.mockProcess).isInitialized()).isFalse();
  }

  @Test
  public void isInitializedAfterInitIsTrue() {
    this.processContext.inheritIO(true);

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess, this.processContext);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.isInitialized()).isFalse();
    assertThat(this.processContext.inheritsIO()).isTrue();

    processAdapter.init();

    assertThat(processAdapter.isInitialized()).isTrue();
  }

  @Test
  public void isRunningForRunningProcessIsTrue() {
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.isRunning()).isTrue();
    assertThat(processAdapter.isNotRunning()).isFalse();

    verify(this.mockProcess, times(2)).exitValue();
  }

  @Test
  public void isRunningForTerminatedProcessIsFalse() {
    when(this.mockProcess.exitValue()).thenReturn(0);

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.isRunning()).isFalse();
    assertThat(processAdapter.isNotRunning()).isTrue();

    verify(this.mockProcess, times(2)).exitValue();
  }

  @Test
  public void getCommandLineReturnsProcessCommand() {
    String[] commandLine = asArray("java", "-server", "-ea", "--classpath", "/path/to/application.jar",
      "example.Application");

    this.processContext.ranWith(commandLine);

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).getCommandLine())
      .isEqualTo(asList(commandLine));
  }

  @Test
  public void getDirectoryReturnsProcessWorkingDirectory() {
    this.processContext.ranIn(FileSystemUtils.USER_HOME_DIRECTORY);

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).getDirectory())
      .isEqualTo(FileSystemUtils.USER_HOME_DIRECTORY);
  }

  @Test
  public void getEnvironmentReturnsProcessEnvironment() {
    Environment environment = Environment.from(Collections.singletonMap("testVariable", "testValue"));

    this.processContext.using(environment);

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).getEnvironment()).isEqualTo(environment);
  }

  @Test
  public void getIdReturnProcessId() throws IOException {
    File testPid = File.createTempFile("test", ".pid", FileSystemUtils.WORKING_DIRECTORY);

    testPid.deleteOnExit();
    FileSystemUtils.write(new ByteArrayInputStream("112358".getBytes()), testPid);
    this.processContext.ranIn(testPid.getParentFile());

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess, this.processContext);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getDirectory()).isEqualTo(testPid.getParentFile());
    assertThat(processAdapter.getId()).isEqualTo(112358);
    assertThat(processAdapter.safeGetId()).isEqualTo(112358);
  }

  @Test
  public void safeGetIdForNonExistingPidFileHandlesPidUnknownExceptionAndReturnsMinusOne() {
    this.processContext.ranIn(FileSystemUtils.WORKING_DIRECTORY);

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).safeGetId()).isEqualTo(-1);
  }

  @Test
  public void getIdThrowsPidUnknownException() {
    exception.expect(PidUnknownException.class);
    exception.expectCause(is(notNullValue(Throwable.class)));
    exception.expectMessage("Failed to read Process ID (PID) from file [null]");

    newProcessAdapter(this.mockProcess).getId();
  }

  @Test
  public void setIdThrowsUnsupportedSupportedException() {
    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess);

    try {
      exception.expect(UnsupportedOperationException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage(Constants.OPERATION_NOT_SUPPORTED);

      assertThat(processAdapter).isNotNull();

      processAdapter.setId(123);
    }
    finally {
      assertThat(processAdapter.safeGetId()).isEqualTo(-1);
    }
  }

  @Test
  public void getStandardErrorStream() {
    InputStream mockErrorStream = mock(InputStream.class, "Standard Error Stream");

    when(this.mockProcess.getErrorStream()).thenReturn(mockErrorStream);

    assertThat(newProcessAdapter(this.mockProcess).getStandardErrorStream()).isSameAs(mockErrorStream);

    verify(this.mockProcess, times(1)).getErrorStream();
    verify(this.mockProcess, never()).getInputStream();
    verify(this.mockProcess, never()).getOutputStream();
  }

  @Test
  public void getStandardInStream() {
    OutputStream mockOutputStream = mock(OutputStream.class, "Standard In Stream");

    when(this.mockProcess.getOutputStream()).thenReturn(mockOutputStream);

    assertThat(newProcessAdapter(this.mockProcess).getStandardInStream()).isSameAs(mockOutputStream);

    verify(this.mockProcess, times(1)).getOutputStream();
    verify(this.mockProcess, never()).getErrorStream();
    verify(this.mockProcess, never()).getInputStream();
  }

  @Test
  public void getStandardOutStream() {
    InputStream mockInputStream = mock(InputStream.class, "Standard Out Stream");

    when(this.mockProcess.getInputStream()).thenReturn(mockInputStream);

    assertThat(newProcessAdapter(this.mockProcess).getStandardOutStream()).isSameAs(mockInputStream);

    verify(this.mockProcess, times(1)).getInputStream();
    verify(this.mockProcess, never()).getErrorStream();
    verify(this.mockProcess, never()).getOutputStream();
  }

  @Test
  public void getUsernameReturnsNameOfUserUsedToRunProcess() {
    this.processContext.ranBy("jblum");

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).getUsername()).isEqualTo("jblum");
  }

  @Test
  public void exitValueFoRunningProcessThrowsIllegalThreadStateException() {
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

    try {
      exception.expect(IllegalThreadStateException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage("running");

      newProcessAdapter(this.mockProcess).exitValue();
    }
    finally {
      verify(this.mockProcess, times(1)).exitValue();
    }
  }

  @Test
  public void exitValueForTerminatedProcessReturnsExitValue() {
    when(this.mockProcess.exitValue()).thenReturn(1);

    assertThat(newProcessAdapter(this.mockProcess).exitValue()).isEqualTo(1);

    verify(this.mockProcess, times(1)).exitValue();
  }

  @Test
  public void safeExitValueForRunningProcessReturnsMinusOne() {
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

    assertThat(newProcessAdapter(this.mockProcess).safeExitValue()).isEqualTo(-1);

    verify(this.mockProcess, times(1)).exitValue();
  }

  @Test
  public void safeExitValueForTerminatedProcessReturnsExitValue() {
    when(this.mockProcess.exitValue()).thenReturn(1);

    assertThat(newProcessAdapter(this.mockProcess).safeExitValue()).isEqualTo(1);

    verify(this.mockProcess, times(1)).exitValue();
  }

  @Test
  public void killRunningProcessIsSuccessful() throws InterruptedException {
    when(this.mockProcess.destroyForcibly()).thenReturn(this.mockProcess);
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));
    when(this.mockProcess.waitFor()).thenReturn(1);

    assertThat(newProcessAdapter(this.mockProcess).kill()).isEqualTo(1);

    verify(this.mockProcess, times(1)).destroyForcibly();
    verify(this.mockProcess, times(1)).exitValue();
    verify(this.mockProcess, times(1)).waitFor();
  }

  @Test
  public void killTerminatedProcessIsSuccessful() throws InterruptedException {
    when(this.mockProcess.exitValue()).thenReturn(0);

    assertThat(newProcessAdapter(this.mockProcess).kill()).isEqualTo(0);

    verify(this.mockProcess, times(2)).exitValue();
    verify(this.mockProcess, never()).destroyForcibly();
    verify(this.mockProcess, never()).waitFor();
  }

  @Test
  public void restartRunningProcessIsSuccessful() throws InterruptedException {
    ProcessExecutor mockProcessExecutor = mock(ProcessExecutor.class);
    Process mockRestartedProcess = mock(Process.class);

    List<String> expectedCommandLine = asList("java", "-server", "-ea",
      "-classpath", "/class/path/to/application.jar", "mock.Application");

    when(mockProcessExecutor.execute(eq(FileSystemUtils.USER_HOME_DIRECTORY), eq(expectedCommandLine)))
      .thenReturn(mockRestartedProcess);

    doNothing().when(this.mockProcess).destroy();
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running")).thenReturn(0);
    when(this.mockProcess.waitFor()).thenReturn(0);

    this.processContext.ranIn(FileSystemUtils.USER_HOME_DIRECTORY).ranWith(expectedCommandLine);

    ProcessAdapter processAdapter = spy(new ProcessAdapter(this.mockProcess, this.processContext) {
      @Override
      protected ProcessExecutor newProcessExecutor() {
        return mockProcessExecutor;
      }

      @Override
      public Integer safeGetId() {
        return 123;
      }

      @Override
      public synchronized int stop(long timeout, TimeUnit unit) {
        getProcess().destroy();
        return 0;
      }
    });

    ProcessAdapter restartedProcessAdapter = processAdapter.restart();

    assertThat(restartedProcessAdapter).isNotNull();
    assertThat(restartedProcessAdapter.getProcess()).isSameAs(mockRestartedProcess);
    assertThat(restartedProcessAdapter.getProcessContext()).isSameAs(this.processContext);

    verify(this.mockProcess, times(2)).exitValue();
    verify(this.mockProcess, times(1)).destroy();
    verify(this.mockProcess, times(1)).waitFor();
    verify(processAdapter, times(1)).stop(
      eq(DEFAULT_TIMEOUT_MILLISECONDS), eq(TimeUnit.MILLISECONDS));
    verify(mockProcessExecutor, times(1)).execute(
      eq(FileSystemUtils.USER_HOME_DIRECTORY), eq(expectedCommandLine));
    verifyZeroInteractions(mockRestartedProcess);
  }

  @Test
  public void restartTerminatedProcessIsSuccessful() throws InterruptedException {
    ProcessExecutor mockProcessExecutor = mock(ProcessExecutor.class);
    Process mockRestartedProcess = mock(Process.class);

    List<String> expectedCommandLine = asList("java", "-server", "-ea",
      "-classpath", "/class/path/to/application.jar", "mock.Application");

    when(mockProcessExecutor.execute(eq(FileSystemUtils.USER_HOME_DIRECTORY), eq(expectedCommandLine)))
      .thenReturn(mockRestartedProcess);

    this.processContext.ranIn(FileSystemUtils.USER_HOME_DIRECTORY).ranWith(expectedCommandLine);

    doNothing().when(this.mockProcess).destroy();
    when(this.mockProcess.exitValue()).thenReturn(0);

    ProcessAdapter processAdapter = spy(new ProcessAdapter(this.mockProcess, this.processContext) {
      @Override
      protected ProcessExecutor newProcessExecutor() {
        return mockProcessExecutor;
      }

      @Override
      public Integer safeGetId() {
        return 123;
      }

      @Override
      public synchronized int stop(long timeout, TimeUnit unit) {
        getProcess().destroy();
        return 0;
      }
    });

    ProcessAdapter restartedProcessAdapter = processAdapter.restart();

    assertThat(restartedProcessAdapter).isNotNull();
    assertThat(restartedProcessAdapter.getProcess()).isSameAs(mockRestartedProcess);
    assertThat(restartedProcessAdapter.getProcessContext()).isSameAs(this.processContext);

    verify(this.mockProcess, times(2)).exitValue();
    verify(this.mockProcess, never()).destroy();
    verify(this.mockProcess, never()).waitFor();
    verify(processAdapter, never()).stop(eq(DEFAULT_TIMEOUT_MILLISECONDS), eq(TimeUnit.MILLISECONDS));
    verify(mockProcessExecutor, times(1)).execute(
      eq(FileSystemUtils.USER_HOME_DIRECTORY), eq(expectedCommandLine));
    verifyZeroInteractions(mockRestartedProcess);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void restartUnstoppableProcessThrowsIllegalStateException() throws InterruptedException {
    ProcessExecutor mockProcessExecutor = mock(ProcessExecutor.class);

    doNothing().when(this.mockProcess).destroy();

    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"))
      .thenThrow(new IllegalThreadStateException("running"));

    ProcessAdapter processAdapter = spy(new ProcessAdapter(this.mockProcess, this.processContext) {
      @Override
      protected ProcessExecutor newProcessExecutor() {
        return mockProcessExecutor;
      }

      @Override
      public Integer safeGetId() {
        return 123;
      }

      @Override
      public synchronized int stop(long timeout, TimeUnit unit) {
        getProcess().destroy();
        return 0;
      }
    });

    try {
      exception.expect(IllegalStateException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage("Process [123] failed to stop");

      processAdapter.restart();
    }
    finally {
      verify(this.mockProcess, times(2)).exitValue();
      verify(this.mockProcess, times(1)).destroy();
      verify(this.mockProcess, times(1)).waitFor();
      verify(processAdapter, times(1)).stop(
        eq(DEFAULT_TIMEOUT_MILLISECONDS), eq(TimeUnit.MILLISECONDS));
      verify(mockProcessExecutor, never()).execute(any(File.class), any(List.class));
    }
  }

  @Test
  public void stopHandlesProcessDestroyRuntimeException() throws InterruptedException {
    doThrow(new RuntimeException("test")).when(this.mockProcess).destroy();
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running")).thenReturn(1);
    assertThat(newProcessAdapter(this.mockProcess).stop()).isEqualTo(1);
    verify(this.mockProcess, times(1)).destroy();
    verify(this.mockProcess, times(2)).exitValue();
    verify(this.mockProcess, never()).waitFor();
  }

  @Test
  public void stopIsInterruptedWhileWaitingIsSuccessful() throws Throwable {
    TestFramework.runOnce(new StopInterruptedTestCase());
  }

  @Test
  public void stopNonRunningProcessIsSuccessful() {
    when(this.mockProcess.exitValue()).thenReturn(1);
    assertThat(newProcessAdapter(this.mockProcess).stop()).isEqualTo(1);
    verify(this.mockProcess, times(2)).exitValue();
  }

  @Test
  public void stopRunningProcessIsSuccessful() throws InterruptedException {
    doNothing().when(this.mockProcess).destroy();
    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));
    when(this.mockProcess.waitFor()).thenReturn(0);

    assertThat(newProcessAdapter(this.mockProcess, this.processContext).stop()).isEqualTo(0);

    verify(this.mockProcess, times(1)).destroy();
    verify(this.mockProcess, times(1)).exitValue();
    verify(this.mockProcess, times(1)).waitFor();
  }

  @Test
  public void stopAndWaitCallsProcessAdapterStopAndWait() throws InterruptedException {
    doNothing().when(this.mockProcess).destroy();
    when(this.mockProcess.waitFor()).thenReturn(0);

    ProcessAdapter processAdapter = spy(new ProcessAdapter(this.mockProcess, this.processContext) {
      @Override
      public synchronized int stop() {
        getProcess().destroy();
        return 0;
      }

      @Override
      public int waitFor() {
        try {
          return getProcess().waitFor();
        }
        catch (InterruptedException ignore) {
          return -1;
        }
      }
    });

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.stopAndWait()).isEqualTo(0);

    verify(this.mockProcess, times(1)).destroy();
    verify(this.mockProcess, times(1)).waitFor();
    verifyNoMoreInteractions(this.mockProcess);
    verify(processAdapter, times(1)).stop();
    verify(processAdapter, times(1)).waitFor();
  }

  @Test
  public void stopAndWaitWithTimeoutCallsProcessAdapterStopAndWaitWithTimeout() throws InterruptedException {
    doNothing().when(this.mockProcess).destroy();
    when(this.mockProcess.exitValue()).thenReturn(1);
    when(this.mockProcess.waitFor(anyLong(), any(TimeUnit.class))).thenReturn(true);

    ProcessAdapter processAdapter = spy(new ProcessAdapter(this.mockProcess, this.processContext) {
      @Override
      public synchronized int stop(long timeout, TimeUnit unit) {
        getProcess().destroy();
        return 0;
      }

      @Override
      public boolean waitFor(long timeout, TimeUnit unit) {
        try {
          return getProcess().waitFor(timeout, unit);
        }
        catch (InterruptedException ignore) {
          return false;
        }
      }
    });

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.stopAndWait(15L, TimeUnit.SECONDS)).isEqualTo(1);

    verify(this.mockProcess, times(1)).destroy();
    verify(this.mockProcess, times(1)).exitValue();
    verify(this.mockProcess, times(1)).waitFor(eq(15L), eq(TimeUnit.SECONDS));
    verifyNoMoreInteractions(this.mockProcess);
    verify(processAdapter, times(1)).stop(eq(15L), eq(TimeUnit.SECONDS));
    verify(processAdapter, times(1)).waitFor(eq(15L), eq(TimeUnit.SECONDS));
  }

  // test registerShutdownHook()

  @Test
  public void registerAndUnregisterProcessStreamListenerIsCorrect() {
    ProcessStreamListener mockProcessStreamListenerOne = mock(ProcessStreamListener.class,
      "MockProcessStreamListenerOne");

    ProcessStreamListener mockProcessStreamListenerTwo = mock(ProcessStreamListener.class,
      "MockProcessStreamListenerTwo");

    when(this.mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

    ProcessAdapter processAdapter = newProcessAdapter(this.mockProcess);

    assertThat(processAdapter).isNotNull();
    assertThat(processAdapter.getProcess()).isSameAs(this.mockProcess);
    assertThat(processAdapter.register(mockProcessStreamListenerOne)).isSameAs(processAdapter);
    assertThat(processAdapter.register(mockProcessStreamListenerTwo)).isSameAs(processAdapter);

    ByteArrayInputStream in = new ByteArrayInputStream("Line one.\n".getBytes());

    Runnable runnable = processAdapter.newProcessStreamReader(in);

    assertThat(runnable).isNotNull();

    runnable.run();

    assertThat(processAdapter.unregister(mockProcessStreamListenerTwo)).isSameAs(processAdapter);

    in = new ByteArrayInputStream("Line two.\n".getBytes());
    runnable = processAdapter.newProcessStreamReader(in);

    assertThat(runnable).isNotNull();

    runnable.run();

    verify(mockProcessStreamListenerOne, times(1)).onInput(eq("Line one."));
    verify(mockProcessStreamListenerOne, times(1)).onInput(eq("Line two."));
    verify(mockProcessStreamListenerTwo, times(1)).onInput(eq("Line one."));
    verify(mockProcessStreamListenerTwo, never()).onInput(eq("Line two."));
  }

  @Test
  public void waitForCallsProcessWaitFor() throws InterruptedException {
    when(this.mockProcess.waitFor()).thenReturn(1);

    assertThat(newProcessAdapter(this.mockProcess).waitFor()).isEqualTo(1);

    verify(this.mockProcess, times(1)).waitFor();
  }

  @Test
  public void waitForCallsProcessWaitForAndIsInterrupted() throws Throwable {
    TestFramework.runOnce(new WaitForInterruptedTestCase());
  }

  @Test
  public void waitForWithTimeoutCallsProcessWaitForWithTimeout() throws InterruptedException {
    when(this.mockProcess.waitFor(anyLong(), any(TimeUnit.class))).thenReturn(true);

    assertThat(newProcessAdapter(this.mockProcess).waitFor(30, TimeUnit.SECONDS)).isTrue();

    verify(this.mockProcess, times(1)).waitFor(eq(30L), eq(TimeUnit.SECONDS));
  }

  @Test
  public void waitForWithTimeoutCallsProcessWaitForWithTimeoutAndIsInterrupted() throws Throwable {
    TestFramework.runOnce(new WaitForWithTimeoutInterruptedTestCase());
  }

  @SuppressWarnings("unused")
  protected abstract class AbstractWaitInterruptingTestCase extends MultithreadedTestCase {

    protected final Object mutex = new Object();

    protected Thread waitingThread;

    public void thread1() {
      waitingThread = Thread.currentThread();
      waitingThread.setName("Waiting Thread");

      performWait(newProcessAdapter(mockProcess));

      assertThat(waitingThread.isInterrupted()).isTrue();
    }

    protected abstract void performWait(ProcessAdapter processAdapter);

    public void thread2() {
      Thread.currentThread().setName("Interrupting Thread");

      waitForTick(1);

      assertThat(waitingThread).isNotNull();

      waitingThread.interrupt();
    }
  }

  @SuppressWarnings("unused")
  protected class StopInterruptedTestCase extends AbstractWaitInterruptingTestCase {

    @Override
    public void initialize() {
      super.initialize();

      try {
        doNothing().when(mockProcess).destroy();

        when(mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

        when(mockProcess.waitFor()).thenAnswer(invocationOnMock -> {
          synchronized (mutex) {
            mutex.wait();
          }

          return 1;
        });
      }
      catch (InterruptedException ignore) {
      }
    }

    @Override
    public void thread1() {
      super.thread1();
    }

    @Override
    protected void performWait(ProcessAdapter processAdapter) {
      assertThat(processAdapter.stop(30, TimeUnit.SECONDS)).isEqualTo(-1);
    }

    @Override
    public void thread2() {
      super.thread2();
    }

    @Override
    public void finish() {
      super.finish();

      try {
        verify(mockProcess, times(1)).destroy();
        verify(mockProcess, times(2)).exitValue();
        verify(mockProcess, times(1)).waitFor();
        verify(mockProcess, never()).waitFor(anyLong(), any(TimeUnit.class));
      }
      catch (InterruptedException ignore) {
      }
    }
  }

  @SuppressWarnings("unused")
  protected class WaitForInterruptedTestCase extends AbstractWaitInterruptingTestCase {

    @Override
    public void initialize() {
      super.initialize();

      try {
        when(mockProcess.exitValue()).thenReturn(1);

        when(mockProcess.waitFor()).thenAnswer(invocationOnMock -> {
          synchronized (mutex) {
            mutex.wait();
          }

          return 1;
        });
      }
      catch (InterruptedException ignore) {
      }
    }

    @Override
    public void thread1() {
      super.thread1();
    }

    @Override
    protected void performWait(ProcessAdapter processAdapter) {
      assertThat(processAdapter.waitFor()).isEqualTo(1);
    }

    @Override
    public void thread2() {
      super.thread2();
    }

    @Override
    public void finish() {
      super.finish();

      try {
        verify(mockProcess, times(1)).exitValue();
        verify(mockProcess, times(1)).waitFor();
        verify(mockProcess, never()).waitFor(anyLong(), any(TimeUnit.class));
      }
      catch (InterruptedException ignore) {
      }
    }
  }

  @SuppressWarnings("unused")
  protected class WaitForWithTimeoutInterruptedTestCase extends AbstractWaitInterruptingTestCase {

    @Override
    public void initialize() {
      super.initialize();

      try {
        when(mockProcess.exitValue()).thenThrow(new IllegalThreadStateException("running"));

        when(mockProcess.waitFor(anyLong(), any(TimeUnit.class))).thenAnswer(invocationOnMock -> {
          assertThat(invocationOnMock.getArgumentAt(0, Long.class)).isEqualTo(15L);
          assertThat(invocationOnMock.getArgumentAt(1, TimeUnit.class)).isEqualTo(TimeUnit.SECONDS);

          synchronized (mutex) {
            mutex.wait();
          }

          return false;
        });
      }
      catch (InterruptedException ignore) {
      }
    }

    @Override
    public void thread1() {
      super.thread1();
    }

    @Override
    protected void performWait(ProcessAdapter processAdapter) {
      assertThat(processAdapter.waitFor(15L, TimeUnit.SECONDS)).isFalse();
    }

    @Override
    public void thread2() {
      super.thread2();
    }

    @Override
    public void finish() {
      super.finish();

      try {
        verify(mockProcess, times(1)).exitValue();
        verify(mockProcess, times(1)).waitFor(eq(15L), eq(TimeUnit.SECONDS));
        verify(mockProcess, never()).waitFor();
      }
      catch (InterruptedException ignore) {
      }
    }
  }
}
