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

package org.cp.elements.lang.concurrent;

import static org.cp.elements.lang.concurrent.ThreadUtils.waitFor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.cp.elements.lang.Condition;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

/**
 * The ThreadUtilsTest class is a test suite of test cases testing the contract and functionality of the 
 * ThreadUtils class.
 *
 * @author John J. Blum
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.runners.MockitoJUnitRunner
 * @see edu.umd.cs.mtc.MultithreadedTestCase
 * @see edu.umd.cs.mtc.TestFramework
 * @see org.cp.elements.lang.concurrent.ThreadUtils
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ThreadUtilsTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private Thread mockThread;

  protected static void sleep(final long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException ignore) {
    }
  }

  @Before
  public void setup() {
    // clear the interrupt status of the current Thread
    Thread.interrupted();
  }

  @Test
  public void isAliveWithNull() {
    assertThat(ThreadUtils.isAlive(null), is(false));
  }

  @Test
  public void isBlockedWithBlockedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.BLOCKED);
    assertThat(ThreadUtils.isBlocked(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isBlockedWithRunnableThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(ThreadUtils.isBlocked(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isBlockedWithNull() {
    assertThat(ThreadUtils.isBlocked(null), is(false));
  }

  @Test
  public void isDaemonWithDaemonThread() {
    Thread thread = new Thread("testDaemonThread");
    thread.setDaemon(true);

    assertThat(ThreadUtils.isDaemon(thread), is(true));
  }

  @Test
  public void isDaemonWithNonDaemonThread() {
    Thread thread = new Thread("testNonDaemonThread");
    thread.setDaemon(false);

    assertThat(ThreadUtils.isDaemon(thread), is(false));
  }

  @Test
  public void isDaemonWithNull() {
    assertThat(ThreadUtils.isDaemon(null), is(false));
  }

  @Test
  public void isNonDaemonWithNonDaemonThread() {
    Thread thread = new Thread("testNonDaemonThread");
    thread.setDaemon(false);

    assertThat(ThreadUtils.isNonDaemon(thread), is(true));
  }

  @Test
  public void isNonDaemonWithDaemonThread() {
    Thread thread = new Thread("testDaemonThread");
    thread.setDaemon(true);

    assertThat(ThreadUtils.isNonDaemon(thread), is(false));
  }

  @Test
  public void isNonDaemonWithNull() {
    assertThat(ThreadUtils.isNonDaemon(null), is(false));
  }

  @Test
  public void isInterruptedWithInterruptedThread() {
    when(mockThread.isInterrupted()).thenReturn(true);
    assertThat(ThreadUtils.isInterrupted(mockThread), is(true));
    verify(mockThread, times(1)).isInterrupted();
  }

  @Test
  public void isInterruptedWithNonInterruptedThread() {
    when(mockThread.isInterrupted()).thenReturn(false);
    assertThat(ThreadUtils.isInterrupted(mockThread), is(false));
    verify(mockThread, times(1)).isInterrupted();
  }

  @Test
  public void isInterruptedWithNull() {
    assertThat(ThreadUtils.isInterrupted(null), is(false));
  }

  @Test
  public void isNewWithNewThread() {
    when(mockThread.getState()).thenReturn(Thread.State.NEW);
    assertThat(ThreadUtils.isNew(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isNewWithTerminatedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TERMINATED);
    assertThat(ThreadUtils.isNew(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isNewWithNull() {
    assertThat(ThreadUtils.isNew(null), is(false));
  }

  @Test
  public void isRunnableWithRunnableThread() {
    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);
    assertThat(ThreadUtils.isRunnable(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isRunnableWithBlockedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.BLOCKED);
    assertThat(ThreadUtils.isRunnable(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isRunnableWithNull() {
    assertThat(ThreadUtils.isRunnable(null), is(false));
  }

  @Test
  public void isTerminatedWithTerminatedThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TERMINATED);
    assertThat(ThreadUtils.isTerminated(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTerminatedWithNewThread() {
    when(mockThread.getState()).thenReturn(Thread.State.NEW);
    assertThat(ThreadUtils.isTerminated(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTerminatedWithNull() {
    assertThat(ThreadUtils.isTerminated(null), is(false));
  }

  @Test
  public void isTimedWaitingWithTimedWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TIMED_WAITING);
    assertThat(ThreadUtils.isTimedWaiting(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTimedWaitingWithWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.WAITING);
    assertThat(ThreadUtils.isTimedWaiting(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isTimeWaitingWithNull() {
    assertThat(ThreadUtils.isTimedWaiting(null), is(false));
  }

  @Test
  public void isWaitingWithWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.WAITING);
    assertThat(ThreadUtils.isWaiting(mockThread), is(true));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isWaitingWithTimedWaitingThread() {
    when(mockThread.getState()).thenReturn(Thread.State.TIMED_WAITING);
    assertThat(ThreadUtils.isWaiting(mockThread), is(false));
    verify(mockThread, times(1)).getState();
  }

  @Test
  public void isWaitingWithNull() {
    assertThat(ThreadUtils.isWaiting(null), is(false));
  }

  @Test
  public void getContextClassLoaderOfNonNullThread() {
    ClassLoader mockClassLoader = mock(ClassLoader.class);
    Thread mockThread = mock(Thread.class);

    when(mockThread.getContextClassLoader()).thenReturn(mockClassLoader);

    assertThat(ThreadUtils.getContextClassLoader(mockThread), is(equalTo(mockClassLoader)));

    verify(mockThread, times(1)).getContextClassLoader();
  }

  @Test
  public void getContextClassLoaderWithNull() {
    assertThat(ThreadUtils.getContextClassLoader(null), is(ThreadUtils.class.getClassLoader()));
  }

  @Test
  public void getIdOfNonNullThread() {
    Thread mockThread = mock(Thread.class);

    when(mockThread.getId()).thenReturn(1l);

    assertThat(ThreadUtils.getId(mockThread), is(equalTo(1l)));

    verify(mockThread, times(1)).getId();
  }

  @Test
  public void getIdWithNull() {
    assertThat(ThreadUtils.getId(null), is(equalTo(0l)));
  }

  @Test
  public void getNameOfNonNullThread() {
    assertThat(ThreadUtils.getName(new Thread("test")), is(equalTo("test")));
  }

  @Test
  public void getNameWithNull() {
    assertThat(ThreadUtils.getName(null), is(nullValue()));
  }

  @Test
  public void getPriorityOfNonNullThread() {
    Thread thread = new Thread("test");
    thread.setPriority(1);

    assertThat(ThreadUtils.getPriority(thread), is(equalTo(1)));
  }

  @Test
  public void getPriorityWithNull() {
    assertThat(ThreadUtils.getPriority(null), is(equalTo(0)));
  }

  @Test
  public void getStackTraceOfNonNullThread() {
    StackTraceElement[] expectedStackTrace = Thread.currentThread().getStackTrace();
    Thread mockThread = mock(Thread.class);

    when(mockThread.getStackTrace()).thenReturn(expectedStackTrace);

    assertThat(ThreadUtils.getStackTrace(mockThread), is(equalTo(expectedStackTrace)));

    verify(mockThread, times(1)).getStackTrace();
  }

  @Test
  public void getStackTraceWithNull() {
    StackTraceElement[] stackTrace = ThreadUtils.getStackTrace(null);

    assertThat(stackTrace, is(notNullValue()));
    assertThat(stackTrace.length, is(equalTo(0)));
  }

  @Test
  public void getStateOfNonNullThread() {
    Thread mockThread = mock(Thread.class);

    when(mockThread.getState()).thenReturn(Thread.State.RUNNABLE);

    assertThat(ThreadUtils.getState(mockThread), is(equalTo(Thread.State.RUNNABLE)));

    verify(mockThread, times(1)).getState();
  }

  @Test
  public void getStateWithNull() {
    assertThat(ThreadUtils.getState(null), is(nullValue()));
  }

  @Test
  public void getThreadGroupOfNonNullThread() {
    ThreadGroup expectedThreadGroup = new ThreadGroup("test");
    Thread thread = new Thread(expectedThreadGroup, "test");

    assertThat(ThreadUtils.getThreadGroup(thread), is(equalTo(expectedThreadGroup)));
  }

  @Test
  public void getThreadGroupWithNull() {
    assertThat(ThreadUtils.getThreadGroup(null), is(nullValue()));
  }

  @Test
  public void interruptInterruptsThread() {
    ThreadUtils.interrupt(mockThread);
    verify(mockThread, times(1)).interrupt();
  }

  @Test
  public void interruptWithNull() {
    ThreadUtils.interrupt(null);
  }

  @Test
  public void join() {
    final long expectedWait = 500l;

    AtomicBoolean condition = new AtomicBoolean(false);

    Runnable testRunnable = () -> {
      condition.set(true);
      sleep(expectedWait);
    };

    assertThat(condition.get(), is(false));

    Thread testThread = new Thread(testRunnable, "Test Thread");

    testThread.setDaemon(false);
    testThread.setPriority(Thread.MAX_PRIORITY);
    testThread.start();

    final long t0 = System.currentTimeMillis();

    assertThat(ThreadUtils.join(testThread, expectedWait, 0), is(true));

    final long t1 = System.currentTimeMillis();

    assertThat(Thread.interrupted(), is(false));
    assertThat(condition.get(), is(true));
    assertThat((t1 - t0) >= expectedWait, is(true));
  }

  @Test
  public void joinInterrupted() throws Throwable {
    TestFramework.runOnce(new JoinInterruptedMultithreadedTestCase());
  }

  @Test
  public void joinNullThread() {
    assertThat(ThreadUtils.join(null, 1000, 1000), is(false));
  }

  @Test
  @Ignore
  @Deprecated
  public void joinInterruptedDeprecated() {
    Thread mainThread = Thread.currentThread();

    Runnable testRunnable = mainThread::interrupt;

    Thread testThread = new Thread(testRunnable, "Test Thread");

    testThread.setDaemon(false);
    testThread.start();

    assertFalse(ThreadUtils.join(testThread, 500, 0));
    assertTrue(mainThread.isInterrupted());
  }

  @Test
  public void sleep() {
    final long expectedWait = 500l;
    final long t0 = System.currentTimeMillis();

    assertThat(ThreadUtils.sleep(expectedWait, 0), is(true));

    final long t1 = System.currentTimeMillis();

    assertThat((t1 - t0) >= expectedWait, is(true));
  }

  @Test
  public void sleepInterrupted() throws Throwable {
    TestFramework.runOnce(new SleepInterruptedMultithreadedTestCase());
  }

  @Test
  public void waitForDuration() {
    final long timeout = (System.currentTimeMillis() + 500);

    assertThat(waitFor(500, TimeUnit.MILLISECONDS).checkEvery(100, TimeUnit.MILLISECONDS).run(), is(true));
    assertThat(System.currentTimeMillis(), is(greaterThanOrEqualTo(timeout)));
  }

  @Test
  public void waitForPassesConditionImmediately() {
    AtomicInteger count = new AtomicInteger(0);

    Condition countCondition = () ->  (count.incrementAndGet() > 0);

    assertThat(count.get(), is(equalTo(0)));
    assertThat(waitFor(5, TimeUnit.SECONDS).checkEvery(1, TimeUnit.SECONDS).on(countCondition), is(true));
    assertThat(count.get(), is(equalTo(2)));
  }

  @Test
  public void waitForInterrupted() throws Throwable {
    TestFramework.runOnce(new WaitForInterruptedMultithreadedTestCase());
  }

  @Test
  public void waitForWithInvalidDuration() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("duration (-500) must be greater than 0");

    waitFor(-500);
  }

  @Test
  public void waitForWithIntervalGreaterThanDuration() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("Interval [2 SECONDS] must be greater than 0 and less than equal to duration [500 MILLISECONDS]");

    waitFor(500).checkEvery(2, TimeUnit.SECONDS);
  }

  @Test
  public void waitForWithNegativeInterval() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("Interval [-1 SECONDS] must be greater than 0 and less than equal to duration [500 MILLISECONDS]");

    waitFor(500).checkEvery(-1, TimeUnit.SECONDS);
  }

  @SuppressWarnings("unused")
  protected static final class JoinInterruptedMultithreadedTestCase extends MultithreadedTestCase {

    private Thread interruptingThread;
    private Thread joiningThread;

    public void thread1() throws InterruptedException {
      assertTick(0);

      interruptingThread = Thread.currentThread();
      interruptingThread.setName("Interrupting Thread");

      waitForTick(2);

      assertThat(joiningThread, is(notNullValue()));

      joiningThread.interrupt();
      joiningThread.join();
    }

    public void thread2() {
      waitForTick(1);

      joiningThread = Thread.currentThread();
      joiningThread.setName("Joining Thread");

      assertThat(interruptingThread, is(notNullValue()));
      assertThat(ThreadUtils.join(interruptingThread, TimeUnit.SECONDS.toMillis(5), 0), is(false));
      assertThat(joiningThread.isInterrupted(), is(true));
    }

    @Override
    public void finish() {
      assertThat(interruptingThread.isAlive(), is(false));
      assertThat(joiningThread.isAlive(), is(false));
    }
  }

  @SuppressWarnings("unused")
  protected static final class SleepInterruptedMultithreadedTestCase extends MultithreadedTestCase {

    private Thread interruptingThread;
    private Thread sleepingThread;

    public void thread1() {
      assertTick(0);

      interruptingThread = Thread.currentThread();
      interruptingThread.setName("Interrupting Thread");

      waitForTick(2);

      assertThat(sleepingThread, is(notNullValue()));

      sleepingThread.interrupt();
    }

    public void thread2() {
      waitForTick(1);

      sleepingThread = Thread.currentThread();
      sleepingThread.setName("Sleeping Thread");

      assertThat(interruptingThread, is(notNullValue()));
      assertThat(ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(5), 0), is(false));
      assertThat(sleepingThread.isInterrupted(), is(true));
    }

    @Override
    public void finish() {
      assertThat(interruptingThread.isAlive(), is(false));
      assertThat(sleepingThread.isAlive(), is(false));
    }
  }

  @SuppressWarnings("unused")
  protected static final class WaitForInterruptedMultithreadedTestCase extends MultithreadedTestCase {

    private Thread interruptingThread;
    private Thread waitingThread;

    public void thread1() {
      assertTick(0);

      interruptingThread = Thread.currentThread();
      interruptingThread.setName("Interrupting Thread");

      waitForTick(2);

      assertThat(waitingThread, is(notNullValue()));

      waitingThread.interrupt();
    }

    public void thread2() {
      waitForTick(1);

      waitingThread = Thread.currentThread();
      waitingThread.setName("Waiting Thread");

      AtomicInteger counter = new AtomicInteger(0);

      Condition countCondition = () -> counter.incrementAndGet() > 2;

      assertThat(interruptingThread, is(notNullValue()));
      assertThat(waitFor(500, TimeUnit.MILLISECONDS).checkEvery(100, TimeUnit.MILLISECONDS).on(countCondition), is(false));
      assertThat(waitingThread.isInterrupted(), is(true));
      assertThat(counter.get(), is(equalTo(2)));
    }

    @Override
    public void finish() {
      assertThat(interruptingThread.isAlive(), is(false));
      assertThat(waitingThread.isAlive(), is(false));
    }
  }
}
