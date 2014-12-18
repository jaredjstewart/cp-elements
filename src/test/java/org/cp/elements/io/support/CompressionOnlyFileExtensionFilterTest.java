/*
 * Copyright (c) 2011-Present. Codeprimate, LLC and authors.  All Rights Reserved.
 * <p/>
 * This software is licensed under the Codeprimate End User License Agreement (EULA).
 * This software is proprietary and confidential in addition to an intellectual asset
 * of the aforementioned authors.
 * <p/>
 * By using the software, the end-user implicitly consents to and agrees to be in compliance
 * with all terms and conditions of the EULA.  Failure to comply with the EULA will result in
 * the maximum penalties permissible by law.
 * <p/>
 * In short, this software may not be reverse engineered, reproduced, copied, modified
 * or distributed without prior authorization of the aforementioned authors, permissible
 * and expressed only in writing.  The authors grant the end-user non-exclusive, non-negotiable
 * and non-transferable use of the software "as is" without expressed or implied WARRANTIES,
 * EXTENSIONS or CONDITIONS of any kind.
 * <p/>
 * For further information on the software license, the end user is encouraged to read
 * the EULA @ ...
 */

package org.cp.elements.io.support;

import static org.junit.Assert.*;

import java.io.File;

import org.cp.elements.test.TestUtils;
import org.junit.Test;

/**
 * The CompressionOnlyFileExtensionFilterTest class is a test suite of test cases testing the contract and functionality
 * of the CompressionOnlyFileExtensionFilter class.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see org.cp.elements.io.support.CompressionOnlyFileExtensionFilterTest
 * @see org.junit.Test
 * @since 1.0.0
 */
public class CompressionOnlyFileExtensionFilterTest {

  @Test
  public void testAccept() {
    CompressionOnlyFileExtensionFilter fileExtensionFilter = new CompressionOnlyFileExtensionFilter();

    TestUtils.assertEquals(CompressionOnlyFileExtensionFilter.COMPRESSION_ONLY_FILE_EXTENSIONS,
      fileExtensionFilter.getFileExtensions());

    for (String fileExtension : CompressionOnlyFileExtensionFilter.COMPRESSION_ONLY_FILE_EXTENSIONS) {
      assertTrue(fileExtensionFilter.accept(new File(String.format("file.%1$s", fileExtension))));
    }
  }

  @Test
  public void testReject() {
    CompressionOnlyFileExtensionFilter fileExtensionFilter = new CompressionOnlyFileExtensionFilter();

    assertFalse(fileExtensionFilter.accept(new File("/path/to/a/file/archive.tar")));
    assertFalse(fileExtensionFilter.accept(new File("absolute/path/to/a/file.jar")));
    assertFalse(fileExtensionFilter.accept(new File("/path/to/a/source.java")));
    assertFalse(fileExtensionFilter.accept(new File("absolute/path/to/a/binary.class")));
    assertFalse(fileExtensionFilter.accept(new File("/path/to/a/file.ext")));
    assertFalse(fileExtensionFilter.accept(new File("absolute/path/to/a/file")));
  }

}