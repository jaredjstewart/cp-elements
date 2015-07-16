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

package org.cp.elements.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cp.elements.test.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The AssertTest class is a test suite of test cases to test the contract and functionality of the Assert class
 * in the org.cp.elements API and Framework.
 * 
 * @author John J. Blum
 * @see org.cp.elements.lang.Assert
 * @see org.cp.elements.test.TestUtils
 * @see org.junit.Test
 * @since 1.0.0
 * @version 1.0.0
 */
public class AssertTest {

  private static final Object LOCK = new Object();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  protected static Object returnsNull() {
    return null;
  }

  @Test
  public void assertArgumentIsValid() {
    Assert.argument(true, "argument is invalid");
  }

  @Test
  public void assertArgumentIsInvalid() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(test) is not a valid argument");
    Assert.argument(false, "({0}) is not a valid argument", "test");
  }

  @Test
  public void assertArgumentWithNullCondition() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(mock) is not a valid argument");
    Assert.argument(null, "(%1$s) is not a valid argument", "mock");
  }

  @Test
  public void assertArgumentThrowsAssertionFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.argument(false, new AssertionFailedException("test"));
  }

  @Test
  public void assertArgumentThrowsIllegalArgumentExceptionWithMixedMessageFormatting() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(spy) is a bad argument, a bad, bad, bad argument; just terrible");
    Assert.argument(false, "argument (%1$s) is a {1} argument, a %2$s, {1}, %2$s argument; just {2}",
      "spy", "bad", "terrible");
  }

  @Test
  public void assertEqualsWithEqualValues() {
    Assert.equals(Boolean.TRUE, true, "the values are unequal");
    Assert.equals(TestUtils.createCalendar(2011, Calendar.OCTOBER, 4),
      TestUtils.createCalendar(2011, Calendar.OCTOBER, 4), "the values are unequal");
    Assert.equals(TestUtils.createCalendar(2013, Calendar.JANUARY, 13),
      TestUtils.createCalendar(2013, Calendar.JANUARY, 13), "the values are unequal");
    Assert.equals(TestUtils.createCalendar(2015, Calendar.JULY, 16),
      TestUtils.createCalendar(2015, Calendar.JULY, 16), "the values are unequal");
    Assert.equals("c".charAt(0), 'c', "the values are unequal");
    Assert.equals(Double.valueOf(String.valueOf(Math.PI)), Math.PI, "the values are unequal");
    Assert.equals(Integer.valueOf("2"), 2, "the values are unequal");
    Assert.equals("test", "test", "the values are unequal");
    Assert.equals(TestEnum.valueOf("ONE"), TestEnum.ONE, "the values are unequal");
  }

  @Test
  public void assertEqualsWithUnequalBooleanValues() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the boolean values are not equal");
    Assert.equals(Boolean.TRUE, false, "the boolean values are not equal");
  }

  @Test
  public void assertEqualsWithUnequalCalendars() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the calendars are not equal");
    Assert.equals(TestUtils.createCalendar(2011, Calendar.OCTOBER, 4), Calendar.getInstance(),
      "the calendars are not equal");
  }

  @Test
  public void assertEqualsWithUnequalCharacters() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the characters are not equal");
    Assert.equals('x', 'X', "the characters are not equal");
  }

  @Test
  public void assertEqualsWithUnequalDoubleValues() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the double values are not equal");
    Assert.equals(3.14159d, Math.PI, "the double values are not equal");
  }

  @Test
  public void assertEqualsWithUnequalIntegerValues() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the integer values are not equal");
    Assert.equals(-1, 1, "the integer values are not equal");
  }

  @Test
  public void assertEqualsWithUnequalStrings() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the strings are not equal");
    Assert.equals("test", "TEST", "the strings are not equal");
  }

  @Test
  public void assertEqualsWithNullValues() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("null values are not equal");
    Assert.equals(null, null, "null values are not equal");
  }

  @Test
  public void assertEqualsWithNullValueAndNullStringLiteral() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("null is not equal to \"null\"");
    Assert.equals(null, "null", "null is not equal to \"null\"");
  }

  @Test
  public void assertEqualsWithNullAndNil() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("null is not equal to nil");
    Assert.equals("null", "nil", "null is not equal to nil");
  }

  @Test
  public void assertEqualsFormatsMessageUsingArguments() {
    expectedException.expect(EqualityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("Expected true; but was false");
    Assert.equals(true, false, "Expected %1$s; but was {1}", true, false);
  }

  @Test
  public void assertEqualsThrowsIllegalArgumentException() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.equals(new Object(), new Object(), new IllegalArgumentException("test"));
  }

  @Test
  public void assertHoldsLock() {
    synchronized (LOCK) {
      Assert.holdsLock(LOCK, "current Thread does not hold the lock");
    }
  }

  @Test
  public void assertHoldsLockWhenCurrentThreadDoesNotHoldTheLock() {
    expectedException.expect(IllegalMonitorStateException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage(String.format("current Thread (%1s) does not hold lock (%2$s)",
      Thread.currentThread().getName(), LOCK));
    Assert.holdsLock(LOCK, "current Thread ({0}) does not hold lock (%2$s)!", Thread.currentThread().getName(), LOCK);
  }

  @Test
  public void assertHoldsLockWithNullLock() {
    expectedException.expect(IllegalMonitorStateException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage(String.format("current Thread (%1s) does not hold lock (%2$s)",
      Thread.currentThread().getName(), null));
    Assert.holdsLock(null, "current Thread (%1$s) does not hold lock ({1})", Thread.currentThread().getName(), null);
  }

  @Test
  public void assertHoldsLockThrowsAssertionFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.holdsLock(LOCK, new AssertionFailedException("test"));
  }

  @Test
  public void testAssertIsAssignableTo() {
    Assert.isAssignableTo(Boolean.class, Boolean.class, "The class type is not assignable to a Boolean!");
    Assert.isAssignableTo(Character.class, Object.class, "The class type is not assignable to Object!");
    Assert.isAssignableTo(java.sql.Date.class, java.util.Date.class, "The class type is not assignable to java.util.Date!");
    Assert.isAssignableTo(Double.class, Number.class, "The class type is not assignable to Number!");
    Assert.isAssignableTo(Integer.class, Number.class, "The class type is not assignable to Number!");
    Assert.isAssignableTo(String.class, Object.class, "The class type is not assignable to Object!");
  }

  @Test(expected = ClassCastException.class)
  public void testAssertIsAssignableToCastingObjectToString() {
    try {
      Assert.isAssignableTo(Object.class, String.class, "{0} is not assignable to a reference of type {1}!",
        "Object", "String");
    }
    catch (ClassCastException e) {
      assertEquals("Object is not assignable to a reference of type String!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = ClassCastException.class)
  public void testAssertIsAssignableToWithIncompatibleClassTypes() {
    try {
      Assert.isAssignableTo(Integer.class, Boolean.class, "{0} is not type compatible with {1}!",
        "Integer", "Boolean");
    }
    catch (ClassCastException e) {
      assertEquals("Integer is not type compatible with Boolean!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertIsAssignableToThrowsAssertionFailedException() {
    Assert.isAssignableTo(Calendar.class, Date.class, new AssertionFailedException());
  }

  @Test
  public void testAssertIsFalse() {
    Assert.isFalse(false, "The value is not false!");
    Assert.isFalse(Boolean.FALSE, "The value is not false!");
    Assert.isFalse(!Boolean.TRUE, "The value is not false!");
    Assert.isFalse(new Object() == new Object(), "An object compared for identity with another object is false!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertIsFalseWithTrue() {
    try {
      Assert.isFalse(true, "{0} is not {1}!", "True", "false");
    }
    catch (IllegalArgumentException e) {
      assertEquals("True is not false!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertIsFalseThrowsAssertionFailedException() {
    Assert.isFalse(Boolean.TRUE, new AssertionFailedException());
  }

  @Test
  public void testAssertIsInstanceOf() throws Exception {
    Assert.isInstanceOf(true, Boolean.class, "The Object is not an instance of the Boolean class!");
    Assert.isInstanceOf('c', Character.class, "The Object is not an instance of the Character class!");
    Assert.isInstanceOf(3.14f, Float.class, "The Object is not an instance of the Float class!");
    Assert.isInstanceOf(Math.PI, Double.class, "The Object is not an instance of the Double class!");
    Assert.isInstanceOf(0, Integer.class, "The Object is not an instance of the Integer class!");
    Assert.isInstanceOf(1l, Long.class, "The Object is not an instance of the Long class!");
    Assert.isInstanceOf("test", CharSequence.class, "The Object is not an instance of the CharSequence class!");
    Assert.isInstanceOf(new Object(), Object.class, "The Object is not an instance of the Object class!");
    Assert.isInstanceOf(Object.class, Class.class, "The Object is not an instance of the Class class!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertIsInstanceOfIsNotAnInstance() {
    try {
      Assert.isInstanceOf("0123456789", Long.class, "A {0} is not an instance of the {1} class!",
        "String", "Long");
    }
    catch (IllegalArgumentException e) {
      assertEquals("A String is not an instance of the Long class!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertIsInstanceOfThrowsAssertionFailedException() {
    Assert.isInstanceOf(new Object(), Class.class, new AssertionFailedException());
  }

  @Test
  public void testAssertIsTrue() {
    Assert.isTrue(true, "The value is not true!");
    Assert.isTrue(Boolean.TRUE, "The value is not true!");
    Assert.isTrue(!Boolean.FALSE, "The value is not true!");
    Assert.isTrue("test".equals("test"), "The value is not true!");
    Assert.isTrue("test".equalsIgnoreCase("TEST"), "The value is not true!");
    Assert.isTrue(LOCK.equals(LOCK), "An Object is equal to itself!");
    Assert.isTrue(LOCK == LOCK, "An identity comparison of an Object with itself is true!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertIsTrueWithFalse() {
    try {
      Assert.isTrue(false, "{0} is not {1}!", "False", "true");
    }
    catch (IllegalArgumentException e) {
      assertEquals("False is not true!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertIsTrueThrowsAssertionFailedException() {
    Assert.isTrue(Boolean.FALSE, new AssertionFailedException());
  }

  @Test
  public void testAssertNotBlank() throws Exception {
    Assert.notBlank("test", "The String is blank!");
    Assert.notBlank("blank", "The String is blank!");
    Assert.notBlank("empty", "The String is blank!");
    Assert.notBlank("null", "The String is blank!");
    Assert.notBlank("space", "The String is blank!");
    Assert.notBlank("_", "The String is blank!");
    Assert.notBlank("--", "The String is blank!");
    Assert.notBlank("0", "The String is blank!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotBlankWithEmptyString() {
    Assert.notBlank("", "The empty String is blank!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotBlankWithNull() {
    Assert.notBlank(null, "Null is blank!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotBlankWithNullCharacter() {
    Assert.notBlank("\0", "The null Character is blank!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotBlankWithSpaces() {
    Assert.notBlank("  ", "Spaces is blank!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotBlankWithTabs() {
    try {
      Assert.notBlank("\t", "The {0} is blank!", "tab character");
    }
    catch (IllegalArgumentException e) {
      assertEquals("The tab character is blank!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertNotBlankWithNewlineThrowsAssertionFailedException() {
    Assert.notBlank("\n", new AssertionFailedException("The newline character is blank!"));
  }

  @Test
  public void testAssertNotEmptyStrings() {
    Assert.notEmpty((String) null, "The String is empty!");
    Assert.notEmpty("blank", "The String is empty!");
    Assert.notEmpty("empty", "The String is empty!");
    Assert.notEmpty("null", "The String 'null' is not empty!");
    Assert.notEmpty(" ", "The String is empty!");
    Assert.notEmpty("   ", "The String is empty!");
    Assert.notEmpty("_", "The String is empty!");
    Assert.notEmpty("___", "The String is empty!");
    Assert.notEmpty("\0", "The String is empty!");
    Assert.notEmpty("\n", "The String is empty!");
    Assert.notEmpty("\t", "The String is empty!");
    Assert.notEmpty("test", "The String is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyWithEmptyString() {
    Assert.notEmpty("", "The String is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyStringUsingMessage() {
    try {
      Assert.notEmpty("", "The {0} String is empty!", "empty");
    }
    catch (IllegalArgumentException e) {
      assertEquals("The empty String is empty!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertNotEmptyStringThrowsAssertionFailedException() {
    Assert.notEmpty("", new AssertionFailedException());
  }

  @Test
  public void testAssertNotEmptyArray() {
    Assert.notEmpty(new Object[] { "assert", "mock", "test" }, "The Object array is empty!");
    Assert.notEmpty(new String[1], "The Object array is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyArrayWithNull() {
    Assert.notEmpty((Object[]) null, "A null Object array reference is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyArrayWithEmptyArray() {
    Assert.notEmpty(new Object[0], "A zero element Object array is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyArrayUsingMessage() {
    try {
      Assert.notEmpty(new Object[0], "The {0} is empty!", "Object array");
    }
    catch (IllegalArgumentException e) {
      assertEquals("The Object array is empty!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertNotEmptyArrayThrowsAssertionFailedException() {
    Assert.notEmpty(new Object[0], new AssertionFailedException());
  }

  @Test
  public void testAssertNotEmptyCollection() {
    final Collection<String> animals = new ArrayList<String>(3);

    animals.add("bat");
    animals.add("cat");
    animals.add("dog");

    org.junit.Assert.assertNotNull(animals);
    assertFalse(animals.isEmpty());

    Assert.notEmpty(animals, "The Collection is empty!");
    Assert.notEmpty(Collections.singleton(1), "The Collection is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyCollectionWithNull() {
    Assert.notEmpty((Collection) null, "A null Collection reference is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyCollectionWithEmptyCollection() {
    Assert.notEmpty(Collections.emptyList(), "The List is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyCollectionUsingMessage() {
    try {
      Assert.notEmpty(Collections.emptySet(), "The {0} is empty!", "Set");
    }
    catch (IllegalArgumentException e) {
      assertEquals("The Set is empty!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertNotEmptyCollectionThrowsAssertionFailedException() {
    Assert.notEmpty(Collections.emptyList(), new AssertionFailedException());
  }

  @Test
  public void testAssertNotEmptyMap() {
    final Map<String, String> map = new HashMap<String, String>(1);

    map.put("key", "value");

    org.junit.Assert.assertNotNull(map);
    assertFalse(map.isEmpty());

    Assert.notEmpty(map, "The Map is empty!");
    Assert.notEmpty(Collections.singletonMap("myKey", "myValue"), "The Map is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyMapWithNull() {
    Assert.notEmpty((Map) null, "A null Map reference is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyMapWithEmptyMap() {
    Assert.notEmpty(Collections.emptyMap(), "The Map is empty!");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAssertNotEmptyMapUsingMessage() {
    try {
      Assert.notEmpty(Collections.emptyMap(), "The {0} is empty!", "Map");
    }
    catch (IllegalArgumentException e) {
      assertEquals("The Map is empty!", e.getMessage());
      throw e;
    }
  }

  @Test(expected = AssertionFailedException.class)
  public void testAssertNotEmptyMapThrowsAssertionFailedException() {
    Assert.notEmpty(Collections.emptyMap(), new AssertionFailedException());
  }

  @Test
  public void assertNotNull() throws Exception {
    Assert.notNull(false, "object reference is null");
    Assert.notNull('\0', "object reference is null");
    Assert.notNull(0, "object reference is null");
    Assert.notNull(0.0d, "object reference is null");
    Assert.notNull("nil", "object reference is null");
    Assert.notNull("null", "object reference is null");
    Assert.notNull(new Object(), "object reference is null");
    Assert.notNull(new Object[0], "object reference is null");
    Assert.notNull(Collections.emptyList(), "object reference is null");
    Assert.notNull(Void.class, "object reference is null");
  }

  @Test
  public void assertNotNullWithNull() {
    expectedException.expect(NullPointerException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("expected non-null Object reference");
    Assert.notNull(null, "expected non-null {0} reference!", "Object reference");
  }

  @Test
  public void assertNotNullWithMethodReturningNull() {
    expectedException.expect(NullPointerException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("the returnsNull() method returned null");
    Assert.notNull(returnsNull(), "the %1$s method returned {1}", "returnsNull()", null);
  }

  @Test
  public void assertNotNullThrowsAssertionFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.notNull(null, new AssertionFailedException("test"));
  }

  @Test
  public void assertSameWithIdenticalObjects() {
    Assert.same(null, null, "objects are not the same");
    Assert.same(true, Boolean.TRUE, "objects are not the same");
    Assert.same('c', 'c', "objects are not the same");
    Assert.same(1, 1, "objects are not the same");
    //Assert.same(Math.PI, Math.PI, "PI should be the same as PI!");
    Assert.same("test", "test", "objects are not the same");
    Assert.same(LOCK, LOCK, "objects are not the same");
  }

  @Test
  public void assertSameWithDifferentObjects() {
    expectedException.expect(IdentityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("character x is not the same as string x");
    Assert.same('x', "x", "character {0} is not the same as string %2$s", "x", "x");
  }

  @Test
  public void assetSameWithNullAndObject() {
    expectedException.expect(IdentityException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("null is not the same as Object");
    Assert.same(null, new Object(), "null is not the same as Object");
  }

  @Test
  public void assertSameThrowsAssertFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.same(new Object(), new Object(), new AssertionFailedException("test"));
  }

  @Test
  public void assertStateIsValid() {
    Assert.state(true, "state is invalid");
  }

  @Test
  public void assertStateIsInvalid() {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(object) state is invalid");
    Assert.state(false, "(%1$s) state is invalid", "object");
  }

  @Test
  public void assertStateWithNullCondition() {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(bean) state is invalid");
    Assert.state(null, "({0}) state is invalid", "bean");
  }

  @Test
  public void assertStateThrowsAssertionFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.state(false, new AssertionFailedException("test"));
  }

  @Test
  public void assertSupportedForSupportedOperation() {
    Assert.supported(true, "operation is unsupported");
  }

  @Test
  public void assertSupportedForUnsupportedOperation() {
    expectedException.expect(UnsupportedOperationException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(read) operation is unsupported");
    Assert.supported(false, "(%1$s) operation is unsupported", "read");
  }

  @Test
  public void assertSupportedWithNullCondition() {
    expectedException.expect(UnsupportedOperationException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("(create) operation is unsupported");
    Assert.supported(null, "({0}) operation is unsupported", "create");
  }

  @Test
  public void assertSupportedThrowsAssertionFailedException() {
    expectedException.expect(AssertionFailedException.class);
    expectedException.expectCause(is(nullValue(Throwable.class)));
    expectedException.expectMessage("test");
    Assert.supported(false, new AssertionFailedException("test"));
  }

  protected static enum TestEnum {
    ONE,
    TWO
  }

}
