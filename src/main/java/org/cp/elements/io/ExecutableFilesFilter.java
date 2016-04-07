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

import java.io.File;
import java.io.FileFilter;

import org.cp.elements.lang.Filter;

/**
 * The ExecutableFilesFilter class is a {@link FileFilter} and {@link Filter} implementation that filters {@link File}s
 * based on whether they are executable.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see java.io.FileFilter
 * @see org.cp.elements.lang.Filter
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ExecutableFilesFilter implements FileFilter, Filter<File> {

  public static final ExecutableFilesFilter EXECUTABLE_FILES = new ExecutableFilesFilter(true);
  public static final ExecutableFilesFilter NON_EXECUTABLE_FILES = new ExecutableFilesFilter(false);

  private final boolean executable;

  /**
   * Constructs an instance of the ExecutableFilesFilter class initialized with a boolean value indicating whether
   * executable {@link File}s are accepted or rejected by this {@link FileFilter}.
   *
   * @param executable a boolean value indicating whether executable {@link File}s are accepted or rejected
   * by this {@link FileFilter}.
   */
  protected ExecutableFilesFilter(final boolean executable) {
    this.executable = executable;
  }

  /**
   * Determines whether the given {@link File} is accepted by this {@link FileFilter}, which evaluates whether
   * the {@link File} is executable or not.
   *
   * @param pathname the {@link File} to evaluate.
   * @return a boolean value indicating whether executable {@link File}s are accepted or rejected.
   * @see java.io.FileFilter#accept(File)
   * @see java.io.File#canExecute()
   */
  @Override
  public boolean accept(final File pathname) {
    return (pathname.canExecute() == executable);
  }

}