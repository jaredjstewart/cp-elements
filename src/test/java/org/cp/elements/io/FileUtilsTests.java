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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.cp.elements.lang.StringUtils;
import org.cp.elements.test.AbstractBaseTestSuite;
import org.cp.elements.test.annotation.IntegrationTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * The FileUtilsTests class is a test suite of test cases testing the contract and functionality
 * of the {@link FileUtils} class.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.junit.rules.ExpectedException
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.runners.MockitoJUnitRunner
 * @see org.cp.elements.io.FileUtils
 * @see org.cp.elements.test.AbstractBaseTestSuite
 * @see org.cp.elements.test.annotation.IntegrationTest
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class FileUtilsTests extends AbstractBaseTestSuite {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Mock
  private File mockFile;

  protected File newFile(String pathname) {
    return new File(pathname);
  }

  protected File newFile(File parent, String pathname) {
    return new File(parent, pathname);
  }

  @Test
  public void assertExistsWithExistingDirectory() throws FileNotFoundException {
    assertThat(FileUtils.assertExists(WORKING_DIRECTORY), is(equalTo(WORKING_DIRECTORY)));
  }

  @Test
  @SuppressWarnings("all")
  public void assertExistsWithExistingFile() throws FileNotFoundException {
    when(mockFile.exists()).thenReturn(true);
    assertThat(FileUtils.assertExists(mockFile), is(equalTo(mockFile)));
    verify(mockFile, times(1)).exists();
  }

  @Test
  public void assertExistsWithNonExistingDirectory() throws FileNotFoundException {
    exception.expect(FileNotFoundException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[/absolute/path/to/non/existing/directory] was not found");

    FileUtils.assertExists(newFile("/absolute/path/to/non/existing/directory"));
  }

  @Test
  public void assertExistsWithNonExistingFile() throws FileNotFoundException {
    exception.expect(FileNotFoundException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[relative/path/to/non/existing/file.ext] was not found");

    FileUtils.assertExists(newFile("relative/path/to/non/existing/file.ext"));
  }

  @Test
  public void assertExistsWithNull() throws FileNotFoundException {
    exception.expect(FileNotFoundException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[null] was not found");

    FileUtils.assertExists(null);
  }

  @Test
  @SuppressWarnings("all")
  public void createDirectoryWithNonExistingDirectory() {
    when(mockFile.isDirectory()).thenReturn(false);
    when(mockFile.isFile()).thenReturn(false);
    when(mockFile.mkdirs()).thenReturn(true);

    assertThat(FileUtils.createDirectory(mockFile), is(true));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, times(1)).mkdirs();
  }

  @Test
  @SuppressWarnings("all")
  public void createDirectoryWithExistingDirectory() {
    when(mockFile.isDirectory()).thenReturn(true);
    when(mockFile.isFile()).thenReturn(false);
    when(mockFile.mkdir()).thenReturn(false);

    assertThat(FileUtils.createDirectory(mockFile), is(true));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).mkdirs();
  }

  @Test
  @SuppressWarnings("all")
  public void createDirectoryWithExistingFile() {
    when(mockFile.isFile()).thenReturn(true);

    assertThat(FileUtils.createDirectory(mockFile), is(false));

    verify(mockFile, never()).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).mkdirs();
  }

  @Test
  public void createDirectoryWithNull() {
    assertThat(FileUtils.createDirectory(null), is(false));
  }

  @Test
  @SuppressWarnings("all")
  public void createFileWithNonExistingFile() throws IOException {
    when(mockFile.isDirectory()).thenReturn(false);
    when(mockFile.isFile()).thenReturn(false);
    when(mockFile.createNewFile()).thenReturn(true);

    assertThat(FileUtils.createFile(mockFile), is(true));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, times(1)).createNewFile();
  }

  @Test
  @SuppressWarnings("all")
  public void createFileWithExistingDirectory() throws IOException {
    when(mockFile.isDirectory()).thenReturn(true);
    when(mockFile.isFile()).thenReturn(false);
    when(mockFile.createNewFile()).thenReturn(true);

    assertThat(FileUtils.createFile(mockFile), is(false));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, never()).isFile();
    verify(mockFile, never()).createNewFile();
  }

  @Test
  @SuppressWarnings("all")
  public void createFileWithExistingFile() throws IOException {
    when(mockFile.isDirectory()).thenReturn(false);
    when(mockFile.isFile()).thenReturn(true);
    when(mockFile.createNewFile()).thenReturn(false);

    assertThat(FileUtils.createFile(mockFile), is(true));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).createNewFile();
  }

  @Test
  public void createFileWithNull() {
    assertThat(FileUtils.createFile(null), is(false));
  }

  @Test
  @SuppressWarnings("all")
  public void createFileThrowsIOExceptionOnFileCreateNewFile() throws IOException {
    when(mockFile.isDirectory()).thenReturn(false);
    when(mockFile.isFile()).thenReturn(false);
    when(mockFile.createNewFile()).thenThrow(new IOException("test"));

    assertThat(FileUtils.createFile(mockFile), is(false));

    verify(mockFile, times(1)).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, times(1)).createNewFile();
  }

  @Test
  @SuppressWarnings("all")
  public void deleteExistingFileIsSuccessful() {
    when(mockFile.exists()).thenReturn(true);
    when(mockFile.delete()).thenReturn(true);

    assertThat(FileUtils.delete(mockFile), is(true));

    verify(mockFile, times(1)).exists();
    verify(mockFile, times(1)).delete();
  }

  @Test
  @SuppressWarnings("all")
  public void deleteExistingFileIsUnsuccessful() {
    when(mockFile.exists()).thenReturn(true);
    when(mockFile.delete()).thenReturn(false);

    assertThat(FileUtils.delete(mockFile), is(false));

    verify(mockFile, times(1)).exists();
    verify(mockFile, times(1)).delete();
  }

  @Test
  @SuppressWarnings("all")
  public void deleteNonExistingFileIsUnsuccessful() {
    when(mockFile.exists()).thenReturn(false);

    assertThat(FileUtils.delete(mockFile), is(false));

    verify(mockFile, times(1)).exists();
    verify(mockFile, never()).delete();
  }

  @Test
  public void deleteNullIsUnsuccessful() {
    assertThat(FileUtils.delete(null), is(false));
  }

  @Test
  public void getExtensionOfFilesWithExtension() {
    assertThat(FileUtils.getExtension(newFile("/absolute/path/to/file.ext")), is(equalTo("ext")));
    assertThat(FileUtils.getExtension(newFile("relative/path/to/file.ext")), is(equalTo("ext")));
    assertThat(FileUtils.getExtension(newFile("FileUtils.java")), is(equalTo("java")));
    assertThat(FileUtils.getExtension(newFile("FileUtilsTests.class")), is(equalTo("class")));
    assertThat(FileUtils.getExtension(newFile("search.c")), is(equalTo("c")));
    assertThat(FileUtils.getExtension(newFile("sort.cpp")), is(equalTo("cpp")));
    assertThat(FileUtils.getExtension(newFile("/path/to/file/with/two/extensions/test.java.class")),
      is(equalTo("java.class")));
  }

  @Test
  public void getExtensionOfFilesWithNoExtension() {
    assertThat(FileUtils.getExtension(newFile("file")), is(equalTo(StringUtils.EMPTY_STRING)));
    assertThat(FileUtils.getExtension(newFile("file.")), is(equalTo(StringUtils.EMPTY_STRING)));
    assertThat(FileUtils.getExtension(newFile("exe")), is(equalTo(StringUtils.EMPTY_STRING)));
  }

  @Test
  public void getExtensionWithNull() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("File cannot be null");

    FileUtils.getExtension(null);
  }

  @Test
  public void getLocationOfFilesWithLocation() {
    assertThat(FileUtils.getLocation(newFile("/absolute/path/to/file.ext")), is(equalTo("/absolute/path/to")));
    assertThat(FileUtils.getLocation(newFile("relative/path/to/file.ext")), is(equalTo(String.format(
      "%1$s%2$srelative/path/to", WORKING_DIRECTORY.getAbsolutePath(), File.separator))));
    assertThat(FileUtils.getLocation(newFile("/location/to/a/file/system/directory")),
      is(equalTo("/location/to/a/file/system")));
    assertThat(FileUtils.getLocation(WORKING_DIRECTORY), is(equalTo(WORKING_DIRECTORY.getParent())));
  }

  @Test
  public void getLocationOfFileWithNoLocation() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Unable to determine the location of file [file.ext]");

    FileUtils.getLocation(newFile("file.ext"));
  }

  @Test
  public void getLocationWithNull() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("File cannot be null");

    FileUtils.getLocation(null);
  }

  @Test
  public void getNameOfFilesWithName() {
    assertThat(FileUtils.getName(newFile("/absolute/path/to/file.ext")), is(equalTo("file")));
    assertThat(FileUtils.getName(newFile("relative/path/to/file.ext")), is(equalTo("file")));
    assertThat(FileUtils.getName(newFile("FileUtilsTests.java")), is(equalTo("FileUtilsTests")));
    assertThat(FileUtils.getName(newFile("FileUtils.class")), is(equalTo("FileUtils")));
    assertThat(FileUtils.getName(newFile("search.c")), is(equalTo("search")));
    assertThat(FileUtils.getName(newFile("sort.cpp")), is(equalTo("sort")));
    assertThat(FileUtils.getName(newFile("/path/to/file/with/two/extensions/test.java.class")),
      is(equalTo("test")));
  }

  @Test
  public void getNameOfFilesWithNoName() {
    assertThat(FileUtils.getName(newFile(".exe")), is(equalTo(StringUtils.EMPTY_STRING)));
    assertThat(FileUtils.getName(newFile(".")), is(equalTo(StringUtils.EMPTY_STRING)));
  }

  @Test
  public void getNameWithNull() {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("File cannot be null");

    FileUtils.getName(null);
  }

  @Test
  public void isDirectoryWithDirectories() {
    assertThat(FileUtils.isDirectory(TEMPORARY_DIRECTORY), is(true));
    assertThat(FileUtils.isDirectory(USER_HOME), is(true));
    assertThat(FileUtils.isDirectory(WORKING_DIRECTORY), is(true));
  }

  @Test
  public void isDirectoryWithNonDirectories() {
    assertThat(FileUtils.isDirectory(newFile(TEMPORARY_DIRECTORY, "non_existing_directory/")), is(false));
    assertThat(FileUtils.isDirectory(newFile(USER_HOME, "nonExistingFile.ext")), is(false));
    assertThat(FileUtils.isDirectory(newFile(WORKING_DIRECTORY, "cp-elements-1.0.0.SNAPSHOT.jar")), is(false));
  }

  @Test
  public void isDirectoryWithNull() {
    assertThat(FileUtils.isDirectory(null), is(false));
  }

  @Test
  public void isEmptyWithExistingNonEmptyDirectoryIsTrue() {
    assertThat(FileUtils.isEmpty(WORKING_DIRECTORY), is(true));
  }

  @Test
  @SuppressWarnings("all")
  public void isEmptyWithExistingNonEmptyFileIsFalse() {
    when(mockFile.isFile()).thenReturn(true);
    when(mockFile.length()).thenReturn(1l);

    assertThat(FileUtils.isEmpty(mockFile), is(false));

    verify(mockFile, times(1)).isFile();
    verify(mockFile, times(1)).length();
  }

  @Test
  @SuppressWarnings("all")
  public void isEmptyWithNonExistingFileIsTrue() {
    when(mockFile.isFile()).thenReturn(false);

    assertThat(FileUtils.isEmpty(mockFile), is(true));

    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).length();
  }

  @Test
  public void isEmptyWithNullIsTrue() {
    assertThat(FileUtils.isEmpty(null), is(true));
  }

  @Test
  public void isExistingWithExistingDirectory() {
    assertThat(FileUtils.isExisting(WORKING_DIRECTORY), is(true));
  }

  @Test
  @SuppressWarnings("all")
  public void isExistingWithExistingFile() {
    when(mockFile.exists()).thenReturn(true);

    assertThat(FileUtils.isExisting(mockFile), is(true));

    verify(mockFile, never()).isDirectory();
    verify(mockFile, never()).isFile();
    verify(mockFile, times(1)).exists();
  }

  @Test
  public void isExistingWithNonExistingDirectory() {
    assertThat(FileUtils.isExisting(newFile("/absolute/path/to/non/existing/directory")), is(false));
  }

  @Test
  public void isExistingWithNonExistingFile() {
    assertThat(FileUtils.isExisting(newFile("relative/path/to/non/existing/file.ext")), is(false));
  }

  @Test
  public void isExistingWithNull() {
    assertThat(FileUtils.isExisting(null), is(false));
  }

  @Test
  @SuppressWarnings("all")
  public void isFileWithFile() {
    when(mockFile.isFile()).thenReturn(true);

    assertThat(FileUtils.isFile(mockFile), is(true));

    verify(mockFile, never()).isDirectory();
    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).exists();
  }

  @Test
  public void isFileWithNonFiles() {
    assertThat(FileUtils.isFile(WORKING_DIRECTORY), is(false));
    assertThat(FileUtils.isFile(newFile("/absolute/path/to/non/existing/directory/")), is(false));
    assertThat(FileUtils.isFile(newFile("relative/path/to/non/existing/file.ext")), is(false));
  }

  @Test
  public void isFileWithNull() {
    assertThat(FileUtils.isFile(null), is(false));
  }

  @Test
  public void newFileWithNonNullNonExistingPathnameIsSuccessful() {
    File file = FileUtils.newFile("/absolute/path/to/file.ext");

    assertThat(file, is(notNullValue()));
    assertThat(file.exists(), is(false));
    assertThat(file.getAbsolutePath(), is(equalTo("/absolute/path/to/file.ext")));
  }

  @Test
  @IntegrationTest
  public void newFileWithNonNullExistingDirectoryIsSuccessful() {
    assertThat(WORKING_DIRECTORY.isDirectory(), is(true));
    assertThat(FileUtils.newFile(WORKING_DIRECTORY.getAbsolutePath()), is(equalTo(WORKING_DIRECTORY)));
  }

  @Test
  @IntegrationTest
  public void newFileWithNonNullExistingFileIsSuccessful() {
    File fileUtilsClass = getLocation(FileUtils.class);

    assertThat(fileUtilsClass, is(notNullValue()));
    assertThat(fileUtilsClass.isFile(), is(true));
    assertThat(FileUtils.newFile(fileUtilsClass.getAbsolutePath()), is(equalTo(fileUtilsClass)));
  }

  @Test
  public void newFileWithNullPathnameIsUnsuccessful() {
    exception.expect(NullPointerException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage(is(nullValue(String.class)));

    FileUtils.newFile(null);
  }

  @Test
  public void readFromNonFile() throws IOException {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[null] must be a valid file");

    FileUtils.read(null);
  }

  @Test
  @SuppressWarnings("all")
  public void readFromUnreadableFile() throws IOException {
    when(mockFile.isFile()).thenReturn(true);
    when(mockFile.canRead()).thenReturn(false);
    when(mockFile.getCanonicalPath()).thenReturn("/path/to/file.ext");

    try {
      exception.expect(IllegalStateException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage("[/path/to/file.ext] is unreadable");

      FileUtils.read(mockFile);
    }
    finally {
      verify(mockFile, times(1)).isFile();
      verify(mockFile, times(1)).canRead();
      verify(mockFile, times(1)).getCanonicalPath();
    }
  }

  @Test
  @SuppressWarnings("all")
  public void sizeOfExistingDirectory() {
    assertThat(FileUtils.size(WORKING_DIRECTORY), is(equalTo(0l)));
  }

  @Test
  @SuppressWarnings("all")
  public void sizeOfExistingFile() {
    when(mockFile.isFile()).thenReturn(true);
    when(mockFile.length()).thenReturn(1l);

    assertThat(FileUtils.size(mockFile), is(equalTo(1l)));

    verify(mockFile, times(1)).isFile();
    verify(mockFile, times(1)).length();
  }

  @Test
  @SuppressWarnings("all")
  public void sizeOfNonExistingFile() {
    when(mockFile.isFile()).thenReturn(false);

    assertThat(FileUtils.size(mockFile), is(equalTo(0l)));

    verify(mockFile, times(1)).isFile();
    verify(mockFile, never()).length();
  }

  @Test
  public void sizeOfNull() {
    assertThat(FileUtils.size(null), is(equalTo(0l)));
  }

  @Test
  @SuppressWarnings("all")
  public void tryGetCanonicalFileElseGetAbsoluteFile() throws IOException {
    when(mockFile.getAbsoluteFile()).thenReturn(null);
    when(mockFile.getCanonicalFile()).thenReturn(mockFile);

    assertThat(FileUtils.tryGetCanonicalFileElseGetAbsoluteFile(mockFile), is(equalTo(mockFile)));

    verify(mockFile, never()).getAbsoluteFile();
    verify(mockFile, times(1)).getCanonicalFile();
  }

  @Test
  @SuppressWarnings("all")
  public void tryGetCanonicalFileElseGetAbsoluteFileWhenGetCanonicalFileThrowsIOException() throws IOException {
    File expectedMockAbsoluteFile = mock(File.class, "expectedMockAbsoluteFile");

    when(mockFile.getAbsoluteFile()).thenReturn(expectedMockAbsoluteFile);
    when(mockFile.getCanonicalFile()).thenThrow(new IOException("test"));

    assertThat(FileUtils.tryGetCanonicalFileElseGetAbsoluteFile(mockFile), is(equalTo(expectedMockAbsoluteFile)));

    verify(mockFile, times(1)).getAbsoluteFile();
    verify(mockFile, times(1)).getCanonicalFile();
  }

  @Test
  @SuppressWarnings("all")
  public void tryGetCanonicalPathElseGetAbsolutePath() throws IOException {
    when(mockFile.getCanonicalPath()).thenReturn("/path/to/file.ext");

    assertThat(FileUtils.tryGetCanonicalPathElseGetAbsolutePath(mockFile), is(equalTo("/path/to/file.ext")));

    verify(mockFile, never()).getAbsolutePath();
    verify(mockFile, times(1)).getCanonicalPath();
  }

  @Test
  @SuppressWarnings("all")
  public void tryGetCanonicalPathElseGetAbsolutePathWhenGetCanonicalPathThrowsIOException() throws IOException {
    when(mockFile.getAbsolutePath()).thenReturn("/path/to/file.ext");
    when(mockFile.getCanonicalPath()).thenThrow(new IOException("test"));

    assertThat(FileUtils.tryGetCanonicalPathElseGetAbsolutePath(mockFile), is(equalTo("/path/to/file.ext")));

    verify(mockFile, times(1)).getAbsolutePath();
    verify(mockFile, times(1)).getCanonicalPath();
  }

  @Test
  public void writeNullInputStreamToFile() throws IOException {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("InputStream cannot be null");

    try {
      FileUtils.write(null, mockFile);
    }
    finally {
      verifyZeroInteractions(mockFile);
    }
  }

  @Test
  public void writeInputStreamToNullFile() throws IOException {
    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("File cannot be null");

    InputStream mockInputStream = mock(InputStream.class);

    try {
      FileUtils.write(mockInputStream, null);
    }
    finally {
      verifyZeroInteractions(mockInputStream);
    }
  }

  @Test
  @SuppressWarnings("all")
  public void writeToExistingUnwritableFile() throws IOException {
    when(mockFile.exists()).thenReturn(true);
    when(mockFile.canWrite()).thenReturn(false);
    when(mockFile.getCanonicalPath()).thenReturn("/path/to/file.ext");

    InputStream mockInputStream = mock(InputStream.class);

    try {
      exception.expect(IllegalStateException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage("[/path/to/file.ext] is not writable");

      FileUtils.write(mockInputStream, mockFile);
    }
    finally {
      verify(mockFile, times(1)).exists();
      verify(mockFile, times(1)).canWrite();
      verify(mockFile, times(1)).getCanonicalPath();
      verifyZeroInteractions(mockInputStream);
    }
  }

  @Test
  @IntegrationTest
  public void writeToFileThenReadFromFileIsSuccessful() throws IOException {
    String expectedContent = "This is a test of the read/write File operations in FileUtils!";

    InputStream in = new ByteArrayInputStream(expectedContent.getBytes());

    File testFile = newFile(TEMPORARY_DIRECTORY, "writeToFileThenReadFromFileIsSuccessful.txt");

    assertThat(testFile, is(notNullValue()));
    assertThat(testFile.exists(), is(false));

    testFile.deleteOnExit();

    assertThat(FileUtils.write(in, testFile), is(sameInstance(testFile)));
    assertThat(testFile.isFile(), is(true));
    assertThat(testFile.length(), is(greaterThan(0l)));

    String actualContent = FileUtils.read(testFile);

    assertThat(actualContent, is(equalTo(expectedContent)));
  }
}
