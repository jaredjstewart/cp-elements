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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * The Assert class is a more capable replacement to Java's assert facility, providing functionality to make assertions
 * about pre-conditions and current state to ensure that invariants are upheld and enforced.
 * 
 * @author John J. Blum
 * @see org.cp.elements.lang.AssertionFailedException
 * @since 1.0.0
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public final class Assert {

  /**
   * Asserts that an argument is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine the validity of the argument.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown
   * if the assertion fails.
   * @param arguments an array of Object arguments used to populate the placeholders in the message.
   * @throws java.lang.IllegalArgumentException if the assertion fails and the argument is invalid.
   * @see #argument(Boolean, RuntimeException)
   */
  public static void argument(final Boolean valid, final String message, final Object... arguments) {
    argument(valid, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that an argument is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine the validity of the argument.
   * @param e the RuntimeException thrown if the assertion fails and the argument is invalid.
   * @throws java.lang.RuntimeException if the assertion fails and the argument is invalid.
   * @see #argument(Boolean, String, Object...)
   */
  public static void argument(final Boolean valid, final RuntimeException e) {
    if (!Boolean.TRUE.equals(valid)) {
      throw e;
    }
  }

  /**
   * Asserts that two object are equal as determined by Object.equals.  The assertion holds if and only if
   * both objects are not null and equal in value.
   * 
   * @param obj1 the left operand in the equality comparison.
   * @param obj2 the right operand in the equality comparison.
   * @param message a String specifying the message for the EqualityException that is thrown if the assertion fails.
   * @param arguments an array of Object values used as placeholder values when formatting the message.
   * @throws org.cp.elements.lang.EqualityException if the assertion fails and the two objects are not equal.
   * @see #equals(Object, Object, RuntimeException)
   * @see java.lang.Object#equals(Object)
   */
  public static void equals(final Object obj1, final Object obj2, final String message, final Object... arguments) {
    equals(obj1, obj2, new EqualityException(format(message, arguments)));
  }

  /**
   * Asserts that two object are equal as determined by Object.equals.  The assertion holds if and only if
   * both objects are not null and equal in value.
   *
   * @param obj1 the left operand in the equality comparison.
   * @param obj2 the right operand in the equality comparison.
   * @param e the RuntimeException thrown if the assertion fails and the two objects are not equal.
   * @throws java.lang.RuntimeException if the assertion fails and the two objects are not equal.
   * @see #equals(Object, Object, String, Object...)
   * @see java.lang.Object#equals(Object)
   */
  public static void equals(final Object obj1, final Object obj2, final RuntimeException e) {
    if (obj1 == null || !obj1.equals(obj2)) {
      throw e;
    }
  }

  /**
   * Asserts that the current Thread holds the specified lock.  The assertion holds if and only if the lock is not null
   * and the current Thread is the holder of the lock, otherwise the assertion fails.
   * 
   * @param lock an Object used as the lock and synchronization mutex/monitor.
   * @param message a String specifying the message for the IllegalMonitorStateException that is thrown
   * if the assertion fails.
   * @param arguments an array of Object values used as placeholder value when formatting the message.
   * @throws java.lang.IllegalMonitorStateException if the assertion fails and the current Thread does not hold the lock
   * or the lock is null.
   * @see #holdsLock(Object, String, Object...)
   * @see java.lang.Thread#holdsLock(Object)
   */
  public static void holdsLock(final Object lock, final String message, final Object... arguments) {
    holdsLock(lock, new IllegalMonitorStateException(format(message, arguments)));
  }

  /**
   * Asserts that the current Thread holds the specified lock.  The assertion holds if and only if the lock is not null
   * and the current Thread is the holder of the lock, otherwise the assertion fails.
   *
   * @param lock an Object used as the lock and synchronization mutex/monitor.
   * @param e the RuntimeException thrown if the assertion fails and the current Thread does not hold the lock
   * or the lock is null.
   * @throws java.lang.RuntimeException if the the assertion fails and the current Thread does not hold the lock
   * or the lock is null.
   * @see #holdsLock(Object, String, Object...)
   * @see java.lang.Thread#holdsLock(Object)
   */
  public static void holdsLock(final Object lock, final RuntimeException e) {
    if (lock == null || !Thread.holdsLock(lock)) {
      throw e;
    }
  }

  /**
   * Asserts the 'from' Class is assignable to the 'to' Class.
   * 
   * @param from the Class evaluated for assignment compatibility with the 'to' Class.
   * @param to the Class type used to determine if the 'from' Class is assignment compatible.
   * @param message a String specifying the message for the ClassCastException that is thrown if the assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws ClassCastException if the 'from' Class is not type or assignment compatible with the 'to' Class.
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  public static void isAssignableTo(final Class<?> from, final Class<?> to, final String message, final Object... arguments) {
    isAssignableTo(from, to, new ClassCastException(format(message, arguments)));
  }

  /**
   * Asserts the 'from' Class is assignable to the 'to' Class.
   * 
   * @param from the Class evaluated for assignment compatibility with the 'to' Class.
   * @param to the Class type used to determine if the 'from' Class is assignment compatible.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the 'from' Class is not type or assignment compatible with the 'to' Class.
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  public static void isAssignableTo(final Class<?> from, final Class<?> to, final RuntimeException e) {
    if (from == null || to == null || !to.isAssignableFrom(from)) {
      throw e;
    }
  }

  /**
   * Asserts the value is false.  The assertion holds if and only if the value is equal to false.
   * 
   * @param value the Boolean value being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is throw if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the value is not false.
   * @see java.lang.Boolean#FALSE
   */
  public static void isFalse(final Boolean value, final String message, final Object... arguments) {
    isFalse(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the value is false.  The assertion holds if and only if the value is equal to false.
   * 
   * @param value the Boolean value being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the value is not false.
   * @see java.lang.Boolean#FALSE
   */
  public static void isFalse(final Boolean value, final RuntimeException e) {
    if (!Boolean.FALSE.equals(value)) {
      throw e;
    }
  }

  /**
   * Asserts an Object is an instance of Class.  This method functions the same as the Java instanceof operator.
   * If the Object is null or not type compatible with the specified Class, then the assertion fails.
   * 
   * @param obj the Object evaluated for being an instance of Class.
   * @param type the Class type used to evaluate the Object in the instance of operator.
   * @param message a String specifying the message for the IllegalArgumentException that is throw if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the Object is not an instance of Class.
   * @see java.lang.Class#isInstance(Object)
   */
  public static void isInstanceOf(final Object obj, final Class<?> type, final String message, final Object... arguments) {
    isInstanceOf(obj, type, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts an Object is an instance of Class.  This method functions the same as the Java instanceof operator.
   * If the Object is null or not type compatible with the specified Class, then the assertion fails.
   * 
   * @param obj the Object evaluated for being an instance of Class.
   * @param type the Class type used to evaluate the Object in the instance of operator.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the Object is not an instance of Class.
   * @see java.lang.Class#isInstance(Object)
   */
  public static void isInstanceOf(final Object obj, final Class<?> type, final RuntimeException e) {
    if (!type.isInstance(obj)) {
      throw e;
    }
  }

  /**
   * Asserts the value is true.  The assertion holds if and only if the value is equal to true.
   * 
   * @param value the Boolean value being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the value is not true.
   * @see java.lang.Boolean#TRUE
   */
  public static void isTrue(final Boolean value, final String message, final Object... arguments) {
    isTrue(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the value is true.  The assertion holds if and only if the value is equal to true.
   * 
   * @param value the Boolean value being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the value is not true.
   * @see java.lang.Boolean#TRUE
   */
  public static void isTrue(final Boolean value, final RuntimeException e) {
    if (!Boolean.TRUE.equals(value)) {
      throw e;
    }
  }

  /**
   * Assert the String is not blank.  A String is blank if it is null, empty or contains only whitespace characters.
   * 
   * @param value the String being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the String is blank.
   * @see java.lang.String
   */
  public static void notBlank(final String value, final String message, final Object... arguments) {
    notBlank(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Assert the String is not blank.  A String is blank if it is null, empty or contains only whitespace characters.
   * 
   * @param value the String being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the String is blank.
   * @see java.lang.String
   */
  public static void notBlank(final String value, final RuntimeException e) {
    if (value == null || value.trim().isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts the String is not empty.  A String is empty if and only if it is the empty String.
   * 
   * @param value the String being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the String is empty.
   * @see java.lang.String
   */
  public static void notEmpty(final String value, final String message, final Object... arguments) {
    notEmpty(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the String is not empty.  A String is empty if and only if it is the empty String.
   * 
   * @param value the String being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the String is empty.
   * @see java.lang.String
   */
  public static void notEmpty(final String value, final RuntimeException e) {
    if ("".equals(value)) {
      throw e;
    }
  }

  /**
   * Asserts the Object array is not empty. An Object array is empty if it is null or does not contain any elements;
   * has zero length.
   * 
   * @param array the Object array being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the Object array is empty.
   * @see java.lang.Object[]
   */
  public static void notEmpty(final Object[] array, final String message, final Object... arguments) {
    notEmpty(array, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the Object array is not empty. An Object array is empty if it is null or does not contain any elements;
   * has zero length.
   * 
   * @param array the Object array being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the Object array is empty.
   * @see java.lang.Object[]
   */
  public static void notEmpty(final Object[] array, final RuntimeException e) {
    if (array == null || array.length == 0) {
      throw e;
    }
  }

  /**
   * Asserts the Collection is not empty.  A Collection is empty if it is null or does not contain any elements.
   * 
   * @param collection the Collection being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the Collection is empty.
   * @see java.util.Collection#isEmpty()
   */
  public static void notEmpty(final Collection<?> collection, final String message, final Object... arguments) {
    notEmpty(collection, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the Collection is not empty.  A Collection is empty if it is null or does not contain any elements.
   * 
   * @param collection the Collection being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the Collection is empty.
   * @see java.util.Collection#isEmpty()
   */
  public static void notEmpty(final Collection<?> collection, final RuntimeException e) {
    if (collection == null || collection.isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts the Map is not empty.  A Map is empty if it is null or does not contain any key-value pairs.
   * 
   * @param map the Map being evaluated.
   * @param message a String specifying the message for the IllegalArgumentException that is thrown if the
   * assertion fails.
   * @param arguments an array of Object values used to substitute for the placeholders in the message.
   * @throws IllegalArgumentException if the Map is empty.
   * @see java.util.Map#isEmpty()
   */
  public static void notEmpty(final Map<?, ?> map, final String message, final Object... arguments) {
    notEmpty(map, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts the Map is not empty.  A Map is empty if it is null or does not contain any key-value pairs.
   * 
   * @param map the Map being evaluated.
   * @param e the RuntimeException to throw if the assertion fails.
   * @throws RuntimeException if the Map is empty.
   * @see java.util.Map#isEmpty()
   */
  public static void notEmpty(final Map<?, ?> map, final RuntimeException e) {
    if (map == null || map.isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts that the Object reference is not null.  The assertion holds if and only if the Object reference
   * is not null.
   * 
   * @param obj the Object reference being evaluated for null.
   * @param message a String specifying the message for the NullPointerException that is thrown if the assertion fails.
   * @param arguments an array of Object values used as placeholder values when formatting the message.
   * @throws java.lang.NullPointerException if the assertion fails and the Object reference is null.
   * @see #notNull(Object, RuntimeException)
   * @see java.lang.Object
   */
  public static void notNull(final Object obj, final String message, final Object... arguments) {
    notNull(obj, new NullPointerException(format(message, arguments)));
  }

  /**
   * Asserts that the Object reference is not null.  The assertion holds if and only if the Object reference
   * is not null.
   *
   * @param obj the Object reference being evaluated for null.
   * @param e the RuntimeException thrown if the assertion fails and the Object reference is null.
   * @throws java.lang.RuntimeException if the assertion fails and the Object reference is null.
   * @see #notNull(Object, String, Object...)
   * @see java.lang.Object
   */
  public static void notNull(final Object obj, final RuntimeException e) {
    if (obj == null) {
      throw e;
    }
  }

  /**
   * Asserts that two objects are the same object as determined by the identity comparison.  Two objects are the same
   * if and only if they refer to the same object in memory.
   * 
   * @param obj1 the left operand in the identity comparison.
   * @param obj2 the right operand in the identity comparison.
   * @param message a String specifying the message for the IdentityException that is thrown if the assertion fails.
   * @param arguments an array of Object arguments used to populate the placeholders in the message.
   * @throws org.cp.elements.lang.IdentityException if the two objects are not the same.
   * @see #same(Object, Object, RuntimeException)
   * @see java.lang.Object
   */
  public static void same(final Object obj1, final Object obj2, final String message, final Object... arguments) {
    same(obj1, obj2, new IdentityException(format(message, arguments)));
  }

  /**
   * Asserts that two objects are the same object as determined by the identity comparison.  Two objects are the same
   * if and only if they refer to the same object in memory.
   *
   * @param obj1 the left operand in the identity comparison.
   * @param obj2 the right operand in the identity comparison.
   * @param e the RuntimeException thrown if the assertion fails and the objects are not the same.
   * @throws java.lang.RuntimeException if the assertion fails and the objects are not the same.
   * @see #same(Object, Object, String, Object...)
   * @see java.lang.Object
   */
  public static void same(final Object obj1, final Object obj2, final RuntimeException e) {
    if (obj1 != obj2) {
      throw e;
    }
  }

  /**
   * Asserts that the state is valid.  The assertion holds if and only if valid is true.
   * 
   * @param valid a Boolean value indicating whether the state is valid.
   * @param message a String specifying the message for the IllegalStateException that is thrown if the assertion fails.
   * @param arguments an array of Object arguments used to populate the placeholders in the message.
   * @throws java.lang.IllegalStateException if the assertion fails and the state is invalid.
   * @see #state(Boolean, RuntimeException)
   */
  public static void state(final Boolean valid, final String message, final Object... arguments) {
    state(valid, new IllegalStateException(format(message, arguments)));
  }

  /**
   * Asserts that the state is valid.  The assertion holds if and only if valid is true.
   * 
   * @param valid a Boolean value indicating whether the state is valid.
   * @param e the RuntimeException thrown if the assertion fails and the state is invalid.
   * @throws java.lang.RuntimeException if the assertion fails and the state is invalid.
   * @see #state(Boolean, String, Object...)
   */
  public static void state(final Boolean valid, final RuntimeException e) {
    if (!Boolean.TRUE.equals(valid)) {
      throw e;
    }
  }

  /**
   * Asserts that an operation is supported.  The assertion holds if and only if supported is true.
   *
   * @param supported a Boolean value resulting from the evaluation of the criteria used to determine
   * if the operation is supported.
   * @param message a String specifying the message of the UnsupportedOperationException that is thrown
   * if the assertion fails.
   * @param arguments an array of Object arguments used to populate the placeholders in the message.
   * @throws java.lang.UnsupportedOperationException if the assertion fails and the operations is unsupported.
   * @see #supported(Boolean, RuntimeException)
   */
  public static void supported(final Boolean supported, final String message, final Object... arguments) {
    supported(supported, new UnsupportedOperationException(format(message, arguments)));
  }

  /**
   * Asserts that an operation is supported.  The assertion holds if and only if supported is true.
   *
   * @param supported a Boolean value resulting from the evaluation of the criteria used to determine
   * if the operation is supported.
   * @param e the RuntimeException thrown if the assertion fails and the operation is unsupported.
   * @see #supported(Boolean, String, Object...)
   */
  public static void supported(final Boolean supported, final RuntimeException e) {
    if (!Boolean.TRUE.equals(supported)) {
      throw e;
    }
  }

  /**
   * Formats the specified message with the given arguments.
   *
   * @param message the String message to format.
   * @param arguments an array of Object values used when formatting the message.
   * @return the String message formatted with the arguments.
   * @see #messageFormat(String, Object...)
   * @see #stringFormat(String, Object...)
   */
  private static String format(final String message, final Object... arguments) {
    return stringFormat(messageFormat(message, arguments), arguments);
  }

  /**
   * Formats the specified message containing possible placeholders as defined by the java.text.MessageFormat class
   * in the Java API.
   *
   * @param message a String containing the message to format.
   * @param arguments an array of Object values used as placeholder values when formatting the message.
   * @return a String formatted with the arguments.
   * @see java.text.MessageFormat#format(String, Object...)
   */
  private static String messageFormat(final String message, final Object... arguments) {
    return (arguments == null ? message : MessageFormat.format(message, arguments));
  }

  /**
   * Formats the specified message containing possible placeholders as defined by the java.lang.String class
   * in the Java API.
   *
   * @param message a String containing the message to format.
   * @param arguments an array of Object values used as placeholder values when formatting the message.
   * @return a String formatted with the arguments.
   * @see java.lang.String#format(String, Object...)
   */
  private static String stringFormat(final String message, final Object... arguments) {
    return String.format(message, arguments);
  }

}
