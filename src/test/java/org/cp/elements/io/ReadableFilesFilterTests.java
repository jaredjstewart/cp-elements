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

package org.cp.elements.io;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test suite of test cases testing the contract and functionality of the {@link ReadableFilesFilter} class.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see java.io.FileFilter
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.runners.MockitoJUnitRunner
 * @see org.cp.elements.io.ReadableFilesFilter
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadableFilesFilterTests {

  @Mock
  private File mockFile;

  @Test
  @SuppressWarnings("all")
  public void readableFilesFilterAcceptsReadableFile() {
    when(mockFile.canRead()).thenReturn(true);
    assertThat(ReadableFilesFilter.READABLE_FILES.accept(mockFile), is(true));
    verify(mockFile, times(1)).canRead();
  }

  @Test
  @SuppressWarnings("all")
  public void readableFilesFilterRejectsNonReadableFile() {
    when(mockFile.canRead()).thenReturn(false);
    assertThat(ReadableFilesFilter.READABLE_FILES.accept(mockFile), is(false));
    verify(mockFile, times(1)).canRead();
  }

  @Test
  @SuppressWarnings("all")
  public void nonReadableFilesFilterAcceptsNonReadableFile() {
    when(mockFile.canRead()).thenReturn(false);
    assertThat(ReadableFilesFilter.NON_READABLE_FILES.accept(mockFile), is(true));
    verify(mockFile, times(1)).canRead();
  }

  @Test
  @SuppressWarnings("all")
  public void nonReadableFilesFilterRejectsReadableFile() {
    when(mockFile.canRead()).thenReturn(true);
    assertThat(ReadableFilesFilter.NON_READABLE_FILES.accept(mockFile), is(false));
    verify(mockFile, times(1)).canRead();
  }

  @Test
  public void readableFilesFilterRejectsNull() {
    assertThat(ReadableFilesFilter.NON_READABLE_FILES.accept(null), is(false));
    assertThat(ReadableFilesFilter.READABLE_FILES.accept(null), is(false));
  }
}
