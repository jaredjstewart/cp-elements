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

package org.cp.elements.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;

import org.cp.elements.lang.LogicalOperator;
import org.cp.elements.test.AbstractMockingTestSuite;
import org.jmock.Expectations;
import org.junit.Test;

/**
 * The ComposableFileFilterTest class is a test suite of test case testing the contract and functionality
 * of the ComposableFileFilter class.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see java.io.FileFilter
 * @see org.jmock.Mockery
 * @see org.junit.Test
 * @see org.cp.elements.io.ComposableFileFilter
 * @see org.cp.elements.test.AbstractMockingTestSuite
 * @since 1.0.0
 */
public class ComposableFileFilterTest extends AbstractMockingTestSuite {

  @Test
  public void testComposeAnd() {
    FileFilter leftOperand = mock(FileFilter.class, "testComposeAnd.leftOperand");
    FileFilter rightOperand = mock(FileFilter.class, "testComposeAnd.rightOperand");

    assertNull(ComposableFileFilter.and(null, null));
    assertSame(leftOperand, ComposableFileFilter.and(leftOperand, null));
    assertSame(rightOperand, ComposableFileFilter.and(null, rightOperand));

    FileFilter fileFilter = ComposableFileFilter.and(leftOperand, rightOperand);

    assertTrue(fileFilter instanceof ComposableFileFilter);
    assertEquals(leftOperand, ((ComposableFileFilter) fileFilter).getLeftOperand());
    assertEquals(LogicalOperator.AND, ((ComposableFileFilter) fileFilter).getOperator());
    assertEquals(rightOperand, ((ComposableFileFilter) fileFilter).getRightOperand());
  }

  @Test
  public void testComposeOr() {
    FileFilter leftOperand = mock(FileFilter.class, "testComposeOr.leftOperand");
    FileFilter rightOperand = mock(FileFilter.class, "testComposeOr.rightOperand");

    assertNull(ComposableFileFilter.or(null, null));
    assertSame(leftOperand, ComposableFileFilter.or(leftOperand, null));
    assertSame(rightOperand, ComposableFileFilter.or(null, rightOperand));

    FileFilter fileFilter = ComposableFileFilter.or(leftOperand, rightOperand);

    assertTrue(fileFilter instanceof ComposableFileFilter);
    assertEquals(leftOperand, ((ComposableFileFilter) fileFilter).getLeftOperand());
    assertEquals(LogicalOperator.OR, ((ComposableFileFilter) fileFilter).getOperator());
    assertEquals(rightOperand, ((ComposableFileFilter) fileFilter).getRightOperand());
  }

  @Test
  public void testComposeXor() {
    FileFilter leftOperand = mock(FileFilter.class, "testComposeXor.leftOperand");
    FileFilter rightOperand = mock(FileFilter.class, "testComposeXor.rightOperand");

    assertNull(ComposableFileFilter.xor(null, null));
    assertSame(leftOperand, ComposableFileFilter.xor(leftOperand, null));
    assertSame(rightOperand, ComposableFileFilter.xor(null, rightOperand));

    FileFilter fileFilter = ComposableFileFilter.xor(leftOperand, rightOperand);

    assertTrue(fileFilter instanceof ComposableFileFilter);
    assertEquals(leftOperand, ((ComposableFileFilter) fileFilter).getLeftOperand());
    assertEquals(LogicalOperator.XOR, ((ComposableFileFilter) fileFilter).getOperator());
    assertEquals(rightOperand, ((ComposableFileFilter) fileFilter).getRightOperand());
  }

  @Test
  public void testAcceptAnd() {
    File mockFile = mock(File.class, "testAcceptAnd.mockFile");

    final FileFilter acceptOperand = mock(FileFilter.class, "testAcceptAnd.acceptOperand");
    final FileFilter rejectOperand = mock(FileFilter.class, "testAcceptAnd.rejectOperand");

    mockContext.checking(new Expectations() {{
      allowing(acceptOperand).accept(with(aNonNull(File.class)));
      will(returnValue(true));
      allowing(rejectOperand).accept(with(aNonNull(File.class)));
      will(returnValue(false));
    }});

    assertTrue(ComposableFileFilter.and(acceptOperand, acceptOperand).accept(mockFile));
    assertFalse(ComposableFileFilter.and(acceptOperand, rejectOperand).accept(mockFile));
    assertFalse(ComposableFileFilter.and(rejectOperand, acceptOperand).accept(mockFile));
    assertFalse(ComposableFileFilter.and(rejectOperand, rejectOperand).accept(mockFile));
  }

  @Test
  public void testAcceptOr() {
    File mockFile = mock(File.class, "testAcceptOr.mockFile");

    final FileFilter acceptOperand = mock(FileFilter.class, "testAcceptOr.acceptOperand");
    final FileFilter rejectOperand = mock(FileFilter.class, "testAcceptOr.rejectOperand");

    mockContext.checking(new Expectations() {{
      allowing(acceptOperand).accept(with(aNonNull(File.class)));
      will(returnValue(true));
      allowing(rejectOperand).accept(with(aNonNull(File.class)));
      will(returnValue(false));
    }});

    assertTrue(ComposableFileFilter.or(acceptOperand, acceptOperand).accept(mockFile));
    assertTrue(ComposableFileFilter.or(acceptOperand, rejectOperand).accept(mockFile));
    assertTrue(ComposableFileFilter.or(rejectOperand, acceptOperand).accept(mockFile));
    assertFalse(ComposableFileFilter.or(rejectOperand, rejectOperand).accept(mockFile));
  }

  @Test
  public void testAcceptXor() {
    File mockFile = mock(File.class, "testAcceptXor.mockFile");

    final FileFilter acceptOperand = mock(FileFilter.class, "testAcceptXor.acceptOperand");
    final FileFilter rejectOperand = mock(FileFilter.class, "testAcceptXor.rejectOperand");

    mockContext.checking(new Expectations() {{
      allowing(acceptOperand).accept(with(aNonNull(File.class)));
      will(returnValue(true));
      allowing(rejectOperand).accept(with(aNonNull(File.class)));
      will(returnValue(false));
    }});

    assertFalse(ComposableFileFilter.xor(acceptOperand, acceptOperand).accept(mockFile));
    assertTrue(ComposableFileFilter.xor(acceptOperand, rejectOperand).accept(mockFile));
    assertTrue(ComposableFileFilter.xor(rejectOperand, acceptOperand).accept(mockFile));
    assertFalse(ComposableFileFilter.xor(rejectOperand, rejectOperand).accept(mockFile));
  }

}