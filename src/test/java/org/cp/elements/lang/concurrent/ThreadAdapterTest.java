/*
 * Copyright (c) 2011-Present. Codeprimate, LLC and authors.  All Rights Reserved.
 *
 * This software is licensed under the Codeprimate End User License Agreement (EULA).
 * This software is proprietary and confidential in addition to an intellectual asset
 * of the aforementioned authors.
 *
 * By using the software, the end-user implicitly consents to and agrees to be in compliance
 * with all terms and conditions of the EULA.  Failure to comply with the EULA will result in
 * the maximum penalties permissible by law.
 *
 * In short, this software may not be reverse engineered, reproduced, copied, modified
 * or distributed without prior authorization of the aforementioned authors, permissible
 * and expressed only in writing.  The authors grant the end-user non-exclusive, non-negotiable
 * and non-transferable use of the software "as is" without expressed or implied WARRANTIES,
 * EXTENSIONS or CONDITIONS of any kind.
 *
 * For further information on the software license, the end user is encouraged to read
 * the EULA @ ...
 */

package org.cp.elements.lang.concurrent;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cp.elements.lang.Constants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * The ThreadAdapterTest class is a test suite of test cases testing the contract and functionality
 * of the {@link ThreadAdapter} class.
 *
 * @author John J. Blum
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.runners.MockitoJUnitRunner
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ThreadAdapterTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public Timeout globalTimeout = Timeout.seconds(5);

  @Mock
  private Thread mockThread;

  @Test
  public void constructThreadAdapterWithCurrentThread() {
    ThreadAdapter threadAdapter = new ThreadAdapter();

    assertThat(threadAdapter, is(notNullValue()));
    assertThat(threadAdapter.getDelegate(), is(equalTo(Thread.currentThread())));
  }

  @Test
  public void constructThreadAdapterWithRunnable() {
    Runnable mockRunnable = mock(Runnable.class);

    ThreadAdapter threadAdapter = new ThreadAdapter(mockRunnable);

    assertThat(threadAdapter, is(notNullValue()));
    assertThat(threadAdapter.getDelegate(), is(notNullValue()));
    assertThat(threadAdapter.getDelegate(), is(not(equalTo(Thread.currentThread()))));

    threadAdapter.getDelegate().run();

    verify(mockRunnable, times(1)).run();
  }

  @Test
  public void constructThreadAdapterWithThread() {
    ThreadAdapter threadAdapter = new ThreadAdapter(mockThread);

    assertThat(threadAdapter, is(notNullValue()));
    assertThat(threadAdapter.getDelegate(), is(sameInstance(mockThread)));
  }

  @Test
  public void constructThreadAdapterWithNull() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("The delegate Thread must not be null");

    new ThreadAdapter(null);
  }

  @Test
  public void isAliveReturnsFalseForNonStartedThread() {
    assertThat(new ThreadAdapter(mockThread).isAlive(), is(false));
  }

  @Test
  public void isBlockedReturnsTrueForBlockedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.BLOCKED);
    assertThat(new ThreadAdapter(mockThread).isBlocked(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isBlockedReturnsFalseForNonBlockedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(new ThreadAdapter(mockThread).isBlocked(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isDaemonReturnsTrueForDaemonThread() {
    Thread testThread = new Thread("test");
    testThread.setDaemon(true);
    Thread spyThread = spy(testThread);
    assertThat(new ThreadAdapter(spyThread).isDaemon(), is(true));
    verify(spyThread, times(1)).isDaemon();
  }

  @Test
  public void isDaemonReturnsFalseForNonDaemonThread() {
    Thread testThread = new Thread("test");
    testThread.setDaemon(false);
    Thread spyThread = spy(testThread);
    assertThat(new ThreadAdapter(spyThread).isDaemon(), is(false));
    verify(spyThread, times(1)).isDaemon();
  }

  @Test
  public void isNonDaemonReturnsTrueForNonDaemonThread() {
    Thread testThread = new Thread("test");
    testThread.setDaemon(false);
    Thread spyThread = spy(testThread);
    assertThat(new ThreadAdapter(spyThread).isNonDaemon(), is(true));
    verify(spyThread, times(1)).isDaemon();
  }

  @Test
  public void isNonDaemonReturnsFalseForDaemonThread() {
    Thread testThread = new Thread("test");
    testThread.setDaemon(true);
    Thread spyThread = spy(testThread);
    assertThat(new ThreadAdapter(spyThread).isNonDaemon(), is(false));
    verify(spyThread, times(1)).isDaemon();
  }

  @Test
  public void isInterruptedReturnsTrueForInterruptedThread() {
    when(mockThread.isInterrupted()).thenReturn(true);
    assertThat(new ThreadAdapter(mockThread).isInterrupted(), is(true));
    verify(mockThread, times(1)).isInterrupted();
  }

  @Test
  public void isInterruptedReturnsFalseForUninterruptedThread() {
    when(mockThread.isInterrupted()).thenReturn(false);
    assertThat(new ThreadAdapter(mockThread).isInterrupted(), is(false));
    verify(mockThread, times(1)).isInterrupted();
  }

  @Test
  public void isNewReturnsTrueForNewThread() {
    when(mockThread.getState()).thenReturn(Thread.State.NEW);
    assertThat(new ThreadAdapter(mockThread).isNew(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isNewReturnsFalseForOldThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TERMINATED);
    assertThat(new ThreadAdapter(mockThread).isNew(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isRunnableReturnsTrueForRunnableThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(new ThreadAdapter(mockThread).isRunnable(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isRunnableReturnsFalseForNonRunnableThread() {
    when(mockThread.getState()).thenReturn(Thread.State.WAITING);
    assertThat(new ThreadAdapter(mockThread).isRunnable(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTerminatedReturnsTrueForTerminatedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TERMINATED);
    assertThat(new ThreadAdapter(mockThread).isTerminated(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTerminatedReturnsFalseForNonTerminatedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.NEW);
    assertThat(new ThreadAdapter(mockThread).isTerminated(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTimedWaitingReturnsTrueForTimedWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TIMED_WAITING);
    assertThat(new ThreadAdapter(mockThread).isTimedWaiting(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTimedWaitingReturnsFalseForNonTimedWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(new ThreadAdapter(mockThread).isTimedWaiting(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isWaitingReturnsTrueForWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.WAITING);
    assertThat(new ThreadAdapter(mockThread).isWaiting(), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isWaitingReturnsFalseForNonWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(new ThreadAdapter(mockThread).isWaiting(), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void setContextClassLoader() {
    ClassLoader expectedClassLoader = Thread.currentThread().getContextClassLoader();

    ThreadAdapter expectedThreadAdapter = new ThreadAdapter(mockThread);
    ThreadAdapter actualThreadAdatper = expectedThreadAdapter.setContextClassLoader(expectedClassLoader);

    assertThat(expectedClassLoader, is(notNullValue()));
    assertThat(actualThreadAdatper, is(sameInstance(expectedThreadAdapter)));

    verify(mockThread, times(1)).setContextClassLoader(same(expectedClassLoader));
  }

  @Test
  public void getContextClassLoader() {
    ClassLoader expectedClassLoader = Thread.currentThread().getContextClassLoader();

    when(mockThread.getContextClassLoader()).thenReturn(expectedClassLoader);

    assertThat(new ThreadAdapter(mockThread).getContextClassLoader(), is(sameInstance(expectedClassLoader)));

    verify(mockThread, times(1)).getContextClassLoader();
  }

  @Test
  public void setDaemon() {
    ThreadAdapter expectedThreadAdapter = new ThreadAdapter(mockThread);
    ThreadAdapter actualThreadAdapter = expectedThreadAdapter.setDaemon(true);

    assertThat(expectedThreadAdapter, is(notNullValue()));
    assertThat(actualThreadAdapter, is(sameInstance(expectedThreadAdapter)));

    verify(mockThread, times(1)).setDaemon(eq(true));
  }

  @Test
  public void getId() {
    when(mockThread.getId()).thenReturn(1l);
    assertThat(new ThreadAdapter(mockThread).getId(), is(equalTo(1l)));
    verify(mockThread, times(1)).getId();
  }

  @Test
  public void setName() {
    Thread spyThread = spy(new Thread("test"));

    ThreadAdapter expectedThreadAdapter = new ThreadAdapter(spyThread);
    ThreadAdapter actualThreadAdapter = expectedThreadAdapter.setName("test");

    assertThat(expectedThreadAdapter, is(notNullValue()));
    assertThat(actualThreadAdapter, is(sameInstance(expectedThreadAdapter)));

    verify(spyThread, times(1)).setName("test");
  }

  @Test
  public void getName() {
    Thread spyThread = spy(new Thread("test"));
    assertThat(new ThreadAdapter(spyThread).getName(), is(equalTo("test")));
    verify(spyThread, times(1)).getName();
  }

  @Test
  public void setPriority() {
    Thread spyThread = spy(new Thread("test"));

    ThreadAdapter expectedThreadAdapter = new ThreadAdapter(spyThread);
    ThreadAdapter actualThreadAdapter = expectedThreadAdapter.setPriority(1);

    assertThat(expectedThreadAdapter, is(notNullValue()));
    assertThat(actualThreadAdapter, is(sameInstance(expectedThreadAdapter)));

    verify(spyThread, times(1)).setPriority(1);
  }

  @Test
  public void getPriority() {
    Thread testThread = new Thread("test");
    testThread.setPriority(10);
    Thread spyThread = spy(testThread);
    assertThat(new ThreadAdapter(spyThread).getPriority(), is(equalTo(10)));
    verify(spyThread, times(1)).getPriority();
  }

  @Test
  public void getStackTrace() {
    StackTraceElement[] expectedStackTrace = Thread.currentThread().getStackTrace();
    when(mockThread.getStackTrace()).thenReturn(expectedStackTrace);
    assertThat(new ThreadAdapter(mockThread).getStackTrace(), is(equalTo(expectedStackTrace)));
    verify(mockThread, times(1)).getStackTrace();
  }

  @Test
  public void getState() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(new ThreadAdapter(mockThread).getState(), is(equalTo(Thread.State.RUNNABLE)));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void getThreadGroup() {
    ThreadGroup testThreadGroup = new ThreadGroup("test");
    Thread spyThread = spy(new Thread(testThreadGroup, "test"));

    assertThat(new ThreadAdapter(spyThread).getThreadGroup(), is(equalTo(testThreadGroup)));

    verify(spyThread, times(1)).getThreadGroup();
  }

  @Test
  public void setUncaughtExceptionHandler() {
    Thread.UncaughtExceptionHandler mockUncaughtExceptionHandler = mock(Thread.UncaughtExceptionHandler.class);

    ThreadAdapter expectedThreadAdapter = new ThreadAdapter(mockThread);
    ThreadAdapter actualThreadAdapter = expectedThreadAdapter.setUncaughtExceptionHandler(mockUncaughtExceptionHandler);

    assertThat(expectedThreadAdapter, is(notNullValue()));
    assertThat(actualThreadAdapter, is(sameInstance(expectedThreadAdapter)));

    verify(mockThread, times(1)).setUncaughtExceptionHandler(eq(mockUncaughtExceptionHandler));
  }

  @Test
  public void getUncaughtExceptionHandler() {
    Thread.UncaughtExceptionHandler mockUncaughtExceptionHandler = mock(Thread.UncaughtExceptionHandler.class);
    when(mockThread.getUncaughtExceptionHandler()).thenReturn(mockUncaughtExceptionHandler);
    assertThat(new ThreadAdapter(mockThread).getUncaughtExceptionHandler(), is(equalTo(mockUncaughtExceptionHandler)));
    verify(mockThread, times(1)).getUncaughtExceptionHandler();
  }

  @Test
  public void checkAccessSucceeds() {
    Thread spyThread = spy(new Thread("test"));
    new ThreadAdapter(spyThread).checkAccess();
    verify(spyThread, times(1)).checkAccess();
  }

  // no test for dumpStack possible

  @Test
  public void interrupt() {
    new ThreadAdapter(mockThread).interrupt();
    verify(mockThread, times(1)).interrupt();
  }

  @Test
  public void join() {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void joinThrowsInterruptedException() throws InterruptedException {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void joinWithMilliseconds() {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void joinWithMillisecondsThrowsInterruptedException() throws InterruptedException {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void joinWithMillisecondsAndNanoseconds() {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void joinWithMillisecondsAndNanosecondsThrowsInterruptedException() throws InterruptedException {
    fail(Constants.NOT_IMPLEMENTED);
  }

  @Test
  public void run() {
    new ThreadAdapter(mockThread).run();
    verify(mockThread, times(1)).run();
  }

  @Test
  public void start() {
    new ThreadAdapter(mockThread).start();
    verify(mockThread, times(1)).start();
  }

}
