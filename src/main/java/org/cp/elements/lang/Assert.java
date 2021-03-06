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

package org.cp.elements.lang;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * The Assert class is a more capable replacement for Java's assert facility, providing functionality to make assertions
 * about pre-conditions and state in order to ensure that an object's invariants are upheld and enforced.
 * 
 * @author John J. Blum
 * @see java.lang.String#format(String, Object...)
 * @see java.text.MessageFormat#format(String, Object...)
 * @see AssertionException
 * @see org.cp.elements.lang.LangExtensions#assertThat(Object)
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class Assert {

  /**
   * Asserts that an argument is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine the validity of the argument.
   * @throws java.lang.IllegalArgumentException if the argument is invalid.
   * @see #argument(Boolean, String, Object...)
   */
  public static void argument(Boolean valid) {
    argument(valid, "argument is not valid");
  }

  /**
   * Asserts that an argument is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine the validity of the argument.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the argument is invalid.
   * @see #argument(Boolean, RuntimeException)
   */
  public static void argument(Boolean valid, String message, Object... arguments) {
    argument(valid, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that an argument is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine the validity of the argument.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the argument is invalid.
   */
  public static void argument(Boolean valid, RuntimeException e) {
    if (!Boolean.TRUE.equals(valid)) {
      throw e;
    }
  }

  /**
   * Asserts that two objects are comparable.  The assertion holds if and only if the Comparable objects
   * are equal by comparison.
   *
   * @param <T> the Comparable class type of the objects in the comparison.
   * @param obj1 the first Comparable object in the relational comparison.
   * @param obj2 the second Comparable object in the relational comparison.
   * @throws org.cp.elements.lang.ComparisonException if the assertion fails and the two objects are not comparable.
   * @see #comparable(Comparable, Comparable, String, Object...)
   * @see java.lang.Comparable#compareTo(Object)
   */
  public static <T extends Comparable<T>> void comparable(T obj1, T obj2) {
    comparable(obj1, obj2, "[%1$s] is not comparable to [%2$s]", obj1, obj2);
  }

  /**
   * Asserts that two objects are comparable.  The assertion holds if and only if the Comparable objects
   * are equal by comparison.
   *
   * @param <T> the Comparable class type of the objects in the comparison.
   * @param obj1 the first Comparable object in the relational comparison.
   * @param obj2 the second Comparable object in the relational comparison.
   * @param message a String specifying the message for the ComparisonException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws org.cp.elements.lang.ComparisonException if the assertion fails and the two objects are not comparable.
   * @see #comparable(Comparable, Comparable, RuntimeException)
   * @see java.lang.Comparable#compareTo(Object)
   */
  public static <T extends Comparable<T>> void comparable(T obj1, T obj2, String message, Object... arguments) {
    comparable(obj1, obj2, new ComparisonException(format(message, arguments)));
  }

  /**
   * Asserts that two objects are comparable.  The assertion holds if and only if the Comparable objects
   * are equal by comparison.
   *
   * @param <T> the Comparable class type of the objects in the comparison.
   * @param obj1 the first Comparable object in the relational comparison.
   * @param obj2 the second Comparable object in the relational comparison.
   * @param e the RumtimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the assertion fails and the two objects are not comparable.
   * @see java.lang.Comparable#compareTo(Object)
   */
  public static <T extends Comparable<T>> void comparable(T obj1, T obj2, RuntimeException e) {
    if (obj1 == null || obj2 == null || obj1.compareTo(obj2) != 0) {
      throw e;
    }
  }

  /**
   * Asserts that two objects are equal as determined by {@link Object#equals(Object)}.  The assertion holds
   * if and only if both objects are not null and equal in value.
   *
   * @param obj1 the left operand in the equality comparison.
   * @param obj2 the right operand in the equality comparison.
   * @throws org.cp.elements.lang.EqualityException if the assertion fails and the two objects are not equal.
   * @see #equals(Object, Object, String, Object...)
   * @see java.lang.Object#equals(Object)
   */
  public static void equals(Object obj1, Object obj2) {
    equals(obj1, obj2, "[%1$s] is not equal to [%2$s]", obj1, obj2);
  }

  /**
   * Asserts that two objects are equal as determined by {@link Object#equals(Object)}.  The assertion holds
   * if and only if both objects are not null and equal in value.
   *
   * @param obj1 the left operand in the equality comparison.
   * @param obj2 the right operand in the equality comparison.
   * @param message a String specifying the message for the EqualityException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws org.cp.elements.lang.EqualityException if the assertion fails and the two objects are not equal.
   * @see #equals(Object, Object, RuntimeException)
   * @see java.lang.Object#equals(Object)
   */
  public static void equals(Object obj1, Object obj2, String message, Object... arguments) {
    equals(obj1, obj2, new EqualityException(format(message, arguments)));
  }

  /**
   * Asserts that two objects are equal as determined by {@link Object#equals(Object)}.  The assertion holds
   * if and only if both objects are not null and equal in value.
   *
   * @param obj1 the left operand in the equality comparison.
   * @param obj2 the right operand in the equality comparison.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the two objects are not equal.
   * @see java.lang.Object#equals(Object)
   */
  public static void equals(Object obj1, Object obj2, RuntimeException e) {
    if (obj1 == null || !obj1.equals(obj2)) {
      throw e;
    }
  }

  /**
   * Assert that the String is not blank.  The assertion holds if and only if the String is not null, empty
   * or contains only whitespace characters.
   *
   * @param value the String being evaluated for blankness.
   * @throws java.lang.IllegalArgumentException if the String is blank.
   * @see #hasText(String, String, Object...)
   * @see java.lang.String
   */
  public static void hasText(String value) {
    hasText(value, "argument is blank");
  }

  /**
   * Assert that the String is not blank.  The assertion holds if and only if the String is not null, empty
   * or contains only whitespace characters.
   *
   * @param value the String being evaluated for blankness.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the String is blank.
   * @see #hasText(String, RuntimeException)
   * @see java.lang.String
   */
  public static void hasText(String value, String message, Object... arguments) {
    hasText(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Assert that the String is not blank.  The assertion holds if and only if the String is not null, empty
   * or contains only whitespace characters.
   *
   * @param value the String being evaluated for blankness.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the String is blank.
   * @see java.lang.String
   */
  public static void hasText(String value, RuntimeException e) {
    if (value == null || value.trim().isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts that the current Thread holds the specified lock.  The assertion holds if and only if the lock is not null
   * and the current Thread is the holder of the lock.
   *
   * @param lock the Object used as the lock and synchronization mutex/monitor.
   * @throws java.lang.IllegalMonitorStateException if the current Thread does not hold the lock or the lock is null.
   * @see #holdsLock(Object, String, Object...)
   * @see java.lang.Thread#holdsLock(Object)
   */
  public static void holdsLock(Object lock) {
    holdsLock(lock, "current thread [%1$s] does not hold lock [%2$s]", Thread.currentThread().getName(), lock);
  }

  /**
   * Asserts that the current Thread holds the specified lock.  The assertion holds if and only if the lock is not null
   * and the current Thread is the holder of the lock.
   *
   * @param lock the Object used as the lock and synchronization mutex/monitor.
   * @param message a String specifying the message for the IllegalMonitorStateException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalMonitorStateException if the current Thread does not hold the lock or the lock is null.
   * @see #holdsLock(Object, RuntimeException)
   * @see java.lang.Thread#holdsLock(Object)
   */
  public static void holdsLock(Object lock, String message, Object... arguments) {
    holdsLock(lock, new IllegalMonitorStateException(format(message, arguments)));
  }

  /**
   * Asserts that the current Thread holds the specified lock.  The assertion holds if and only if the lock is not null
   * and the current Thread is the holder of the lock.
   *
   * @param lock the Object used as the lock and synchronization mutex/monitor.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the current Thread does not hold the lock or the lock is null.
   * @see java.lang.Thread#holdsLock(Object)
   */
  public static void holdsLock(Object lock, RuntimeException e) {
    if (lock == null || !Thread.holdsLock(lock)) {
      throw e;
    }
  }

  /**
   * Asserts that the 'from' class type is assignable to the 'to' class type.  The assertion holds if and only if
   * the 'from' class type is the same as or a subclass of the 'to' class type.
   *
   * @param from the class type being evaluated for assignment compatibility with the 'to' class type.
   * @param to the class type used to determine if the 'from' class type is assignment compatible.
   * @throws java.lang.ClassCastException if the 'from' class type is not assignment compatible with the 'to' class type.
   * @see #isAssignableTo(Class, Class, String, Object...)
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  public static void isAssignableTo(Class<?> from, Class<?> to) {
    isAssignableTo(from, to, "[%1$s] is not assignable to [%2$s]", from, to);
  }

  /**
   * Asserts that the 'from' class type is assignable to the 'to' class type.  The assertion holds if and only if
   * the 'from' class type is the same as or a subclass of the 'to' class type.
   *
   * @param from the class type being evaluated for assignment compatibility with the 'to' class type.
   * @param to the class type used to determine if the 'from' class type is assignment compatible.
   * @param message a String specifying the message for the ClassCastException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.ClassCastException if the 'from' class type is not assignment compatible with the 'to' class type.
   * @see #isAssignableTo(Class, Class, RuntimeException)
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  public static void isAssignableTo(Class<?> from, Class<?> to, String message, Object... arguments) {
    isAssignableTo(from, to, new ClassCastException(format(message, arguments)));
  }

  /**
   * Asserts that the 'from' class type is assignable to the 'to' class type.  The assertion holds if and only if
   * the 'from' class type is the same as or a subclass of the 'to' class type.
   *
   * @param from the class type being evaluated for assignment compatibility with the 'to' class type.
   * @param to the class type used to determine if the 'from' class type is assignment compatible.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the 'from' class type is not assignment compatible with the 'to' class type.
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  public static void isAssignableTo(Class<?> from, Class<?> to, RuntimeException e) {
    if (to == null || (from != null && !to.isAssignableFrom(from))) {
      throw e;
    }
  }

  /**
   * Asserts that the condition is false.  The assertion holds if and only if the value is equal to false.
   *
   * @param condition the Boolean value being evaluated as a false condition.
   * @throws java.lang.IllegalArgumentException if the value is not false.
   * @see #isFalse(Boolean, String, Object...)
   * @see java.lang.Boolean#FALSE
   */
  public static void isFalse(Boolean condition) {
    isFalse(condition, "condition [%1$s] is not false", condition);
  }

  /**
   * Asserts that the condition is false.  The assertion holds if and only if the value is equal to false.
   *
   * @param condition the Boolean value being evaluated as a false condition.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the value is not false.
   * @see #isFalse(Boolean, RuntimeException)
   * @see java.lang.Boolean#FALSE
   */
  public static void isFalse(Boolean condition, String message, Object... arguments) {
    isFalse(condition, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the condition is false.  The assertion holds if and only if the value is equal to false.
   *
   * @param condition the Boolean value being evaluated as a false condition.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the value is not false.
   * @see java.lang.Boolean#FALSE
   */
  public static void isFalse(Boolean condition, RuntimeException e) {
    if (!Boolean.FALSE.equals(condition)) {
      throw e;
    }
  }

  /**
   * Asserts that the given object is an instance of the specified class type.  The assertion holds if and only if
   * the object is not null and is an instance of the specified class type.  This assertion functions exactly
   * the same as the Java instanceof operator.
   *
   * @param obj the object evaluated as an instance of the class type.
   * @param type the class type used to evaluate the object in the instanceof operator.
   * @throws org.cp.elements.lang.IllegalTypeException if the object is not an instance of the specified class type.
   * @see #isInstanceOf(Object, Class, String, Object...)
   * @see java.lang.Class#isInstance(Object)
   */
  public static void isInstanceOf(Object obj, Class<?> type) {
    isInstanceOf(obj, type, "[%1$s] is not an instance of [%2$s]", obj, type);
  }

  /**
   * Asserts that the given object is an instance of the specified class type.  The assertion holds if and only if
   * the object is not null and is an instance of the specified class type.  This assertion functions exactly
   * the same as the Java instanceof operator.
   *
   * @param obj the object evaluated as an instance of the class type.
   * @param type the class type used to evaluate the object in the instanceof operator.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws org.cp.elements.lang.IllegalTypeException if the object is not an instance of the specified class type.
   * @see #isInstanceOf(Object, Class, RuntimeException)
   * @see java.lang.Class#isInstance(Object)
   */
  public static void isInstanceOf(Object obj, Class<?> type, String message, Object... arguments) {
    isInstanceOf(obj, type, new IllegalTypeException(format(message, arguments)));
  }

  /**
   * Asserts that the given object is an instance of the specified class type.  The assertion holds if and only if
   * the object is not null and is an instance of the specified class type.  This assertion functions exactly
   * the same as the Java instanceof operator.
   *
   * @param obj the object evaluated as an instance of the class type.
   * @param type the class type used to evaluate the object in the instanceof operator.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the object is not an instance of class type.
   * @see java.lang.Class#isInstance(Object)
   */
  public static void isInstanceOf(Object obj, Class<?> type, RuntimeException e) {
    if (!type.isInstance(obj)) {
      throw e;
    }
  }

  /**
   * Asserts that the condition is true.  The assertion holds if and only if the value is equal to true.
   *
   * @param condition the Boolean value being evaluated as a true condition.
   * @throws java.lang.IllegalArgumentException if the value is not true.
   * @see #isTrue(Boolean, String, Object...)
   * @see java.lang.Boolean#TRUE
   */
  public static void isTrue(Boolean condition) {
    isTrue(condition, "condition [%1$s] is not true", condition);
  }

  /**
   * Asserts that the condition is true.  The assertion holds if and only if the value is equal to true.
   *
   * @param condition the Boolean value being evaluated as a true condition.
   * @param message a String specifying the message for the IllegalArgumentException thrown  if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the value is not true.
   * @see #isTrue(Boolean, RuntimeException)
   * @see java.lang.Boolean#TRUE
   */
  public static void isTrue(Boolean condition, String message, Object... arguments) {
    isTrue(condition, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the condition is true.  The assertion holds if and only if the value is equal to true.
   *
   * @param condition the Boolean value being evaluated as a true condition.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the value is not true.
   * @see java.lang.Boolean#TRUE
   */
  public static void isTrue(Boolean condition, RuntimeException e) {
    if (!Boolean.TRUE.equals(condition)) {
      throw e;
    }
  }

  /**
   * Asserts that the String is not empty.  The assertion holds if and only if the String is not the empty String.
   *
   * @param value the String being evaluated for emptiness.
   * @throws java.lang.IllegalArgumentException if the String is empty.
   * @see #notEmpty(String, String, Object...)
   * @see java.lang.String
   */
  public static void notEmpty(String value) {
    notEmpty(value, "argument is empty");
  }

  /**
   * Asserts that the String is not empty.  The assertion holds if and only if the String is not the empty String.
   *
   * @param value the String being evaluated for emptiness.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the String is empty.
   * @see #notEmpty(String, RuntimeException)
   * @see java.lang.String
   */
  public static void notEmpty(String value, String message, Object... arguments) {
    notEmpty(value, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the String is not empty.  The assertion holds if and only if the String is not the empty String.
   *
   * @param value the String being evaluated for emptiness.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the String is empty.
   * @see java.lang.String
   */
  public static void notEmpty(String value, RuntimeException e) {
    if ("".equals(value)) {
      throw e;
    }
  }

  /**
   * Asserts that the Object array is not empty.  The assertion holds if and only if the Object array is not null
   * and contains at least 1 element.
   *
   * @param array the Object array to evaluate.
   * @throws java.lang.IllegalArgumentException if the Object array is null or empty.
   * @see #notEmpty(Object[], String, Object...)
   * @see java.lang.Object[]
   */
  public static void notEmpty(Object[] array) {
    notEmpty(array, "array is empty");
  }

  /**
   * Asserts that the Object array is not empty.  The assertion holds if and only if the Object array is not null
   * and contains at least 1 element.
   *
   * @param array the Object array to evaluate.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the Object array is null or empty.
   * @see #notEmpty(Object[], RuntimeException)
   * @see java.lang.Object[]
   */
  public static void notEmpty(Object[] array, String message, Object... arguments) {
    notEmpty(array, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the Object array is not empty.  The assertion holds if and only if the Object array is not null
   * and contains at least 1 element.
   *
   * @param array the Object array to evaluate.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the Object array is null or empty.
   * @see java.lang.Object[]
   */
  public static void notEmpty(Object[] array, RuntimeException e) {
    if (array == null || array.length == 0) {
      throw e;
    }
  }

  /**
   * Asserts that the Collection is not empty.  The assertion holds if and only if the Collection is not null
   * and contains at least 1 element.
   *
   * @param collection the Collection to evaluate.
   * @throws java.lang.IllegalArgumentException if the Collection is null or empty.
   * @see #notEmpty(Collection, String, Object...)
   * @see java.util.Collection#isEmpty()
   */
  public static void notEmpty(Collection<?> collection) {
    notEmpty(collection, "collection is empty");
  }

  /**
   * Asserts that the Collection is not empty.  The assertion holds if and only if the Collection is not null
   * and contains at least 1 element.
   *
   * @param collection the Collection to evaluate.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the Collection is null or empty.
   * @see #notEmpty(java.util.Collection, RuntimeException)
   * @see java.util.Collection#isEmpty()
   */
  public static void notEmpty(Collection<?> collection, String message, Object... arguments) {
    notEmpty(collection, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the Collection is not empty.  The assertion holds if and only if the Collection is not null
   * and contains at least 1 element.
   *
   * @param collection the Collection to evaluate.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the Collection is null or empty.
   * @see java.util.Collection#isEmpty()
   */
  public static void notEmpty(Collection<?> collection, RuntimeException e) {
    if (collection == null || collection.isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts that the Map is not empty.  The assertion holds if and only if the Map is not null
   * and contains at least 1 key/value mapping.
   *
   * @param map the Map to evaluate.
   * @throws java.lang.IllegalArgumentException if the Map is null or empty.
   * @see #notEmpty(Map, String, Object...)
   * @see java.util.Map#isEmpty()
   */
  public static void notEmpty(Map<?, ?> map) {
    notEmpty(map, "map is empty");
  }

  /**
   * Asserts that the Map is not empty.  The assertion holds if and only if the Map is not null
   * and contains at least 1 key/value mapping.
   *
   * @param map the Map to evaluate.
   * @param message a String specifying the message for the IllegalArgumentException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the Map is null or empty.
   * @see #notEmpty(java.util.Map, RuntimeException)
   * @see java.util.Map#isEmpty()
   */
  public static void notEmpty(Map<?, ?> map, String message, Object... arguments) {
    notEmpty(map, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the Map is not empty.  The assertion holds if and only if the Map is not null
   * and contains at least 1 key/value mapping.
   *
   * @param map the Map to evaluate.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the Map is null or empty.
   * @see java.util.Map#isEmpty()
   */
  public static void notEmpty(Map<?, ?> map, RuntimeException e) {
    if (map == null || map.isEmpty()) {
      throw e;
    }
  }

  /**
   * Asserts that the Object reference is not null.  The assertion holds if and only if the Object reference
   * is not null.
   *
   * @param obj the Object reference being evaluated.
   * @throws java.lang.IllegalArgumentException if the Object reference is null.
   * @see #notNull(Object, String, Object...)
   * @see java.lang.Object
   */
  public static void notNull(Object obj) {
    notNull(obj, "argument is null");
  }

  /**
   * Asserts that the Object reference is not null.  The assertion holds if and only if the Object reference
   * is not null.
   *
   * @param obj the Object reference being evaluated.
   * @param message a String specifying the message for the NullPointerException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalArgumentException if the Object reference is null.
   * @see #notNull(Object, RuntimeException)
   * @see java.lang.Object
   */
  public static void notNull(Object obj, String message, Object... arguments) {
    notNull(obj, new IllegalArgumentException(format(message, arguments)));
  }

  /**
   * Asserts that the Object reference is not null.  The assertion holds if and only if the Object reference
   * is not null.
   *
   * @param obj the Object reference being evaluated.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the Object reference is null.
   * @see java.lang.Object
   */
  public static void notNull(Object obj, RuntimeException e) {
    if (obj == null) {
      throw e;
    }
  }

  /**
   * Asserts that two objects are the same object as determined by the identity comparison.  The assertion holds
   * if and only if the two objects are the same object in memory.
   *
   * @param obj1 the left operand in the identity comparison.
   * @param obj2 the right operand in the identity comparison.
   * @throws org.cp.elements.lang.IdentityException if the two objects are not the same.
   * @see #same(Object, Object, String, Object...)
   * @see java.lang.Object
   */
  public static void same(Object obj1, Object obj2) {
    same(obj1, obj2, "[%1$s] is not the same as [%2$s]", obj1, obj2);
  }

  /**
   * Asserts that two objects are the same object as determined by the identity comparison.  The assertion holds
   * if and only if the two objects are the same object in memory.
   *
   * @param obj1 the left operand in the identity comparison.
   * @param obj2 the right operand in the identity comparison.
   * @param message a String specifying the message for the IdentityException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws org.cp.elements.lang.IdentityException if the two objects are not the same.
   * @see #same(Object, Object, RuntimeException)
   * @see java.lang.Object
   */
  public static void same(Object obj1, Object obj2, String message, Object... arguments) {
    same(obj1, obj2, new IdentityException(format(message, arguments)));
  }

  /**
   * Asserts that two objects are the same object as determined by the identity comparison.  The assertion holds
   * if and only if the two objects are the same object in memory.
   *
   * @param obj1 the left operand in the identity comparison.
   * @param obj2 the right operand in the identity comparison.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the objects are not the same.
   * @see java.lang.Object
   */
  public static void same(Object obj1, Object obj2, RuntimeException e) {
    if (obj1 != obj2) {
      throw e;
    }
  }

  /**
   * Asserts that the state is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value indicating whether the state is valid.
   * @throws java.lang.IllegalStateException if the state is invalid.
   * @see #state(Boolean, String, Object...)
   */
  public static void state(Boolean valid) {
    state(valid, "state is invalid");
  }

  /**
   * Asserts that the state is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value indicating whether the state is valid.
   * @param message a String specifying the message for the IllegalStateException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.IllegalStateException if the state is invalid.
   * @see #state(Boolean, RuntimeException)
   */
  public static void state(Boolean valid, String message, Object... arguments) {
    state(valid, new IllegalStateException(format(message, arguments)));
  }

  /**
   * Asserts that the state is valid.  The assertion holds if and only if valid is true.
   *
   * @param valid a Boolean value indicating whether the state is valid.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the state is invalid.
   */
  public static void state(Boolean valid, RuntimeException e) {
    if (!Boolean.TRUE.equals(valid)) {
      throw e;
    }
  }

  /**
   * Asserts that an operation is supported.  The assertion holds if and only if supported is true.
   *
   * @param supported a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine if the operation is supported.
   * @throws java.lang.UnsupportedOperationException if the operations is unsupported.
   * @see #supported(Boolean, String, Object...)
   */
  public static void supported(Boolean supported) {
    supported(supported, "operation not supported");
  }

  /**
   * Asserts that an operation is supported.  The assertion holds if and only if supported is true.
   *
   * @param supported a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine if the operation is supported.
   * @param message a String specifying the message of the UnsupportedOperationException thrown if the assertion fails.
   * @param arguments an array of Object arguments used as placeholder values when formatting the message.
   * @throws java.lang.UnsupportedOperationException if the operations is unsupported.
   * @see #supported(Boolean, RuntimeException)
   */
  public static void supported(Boolean supported, String message, Object... arguments) {
    supported(supported, new UnsupportedOperationException(format(message, arguments)));
  }

  /**
   * Asserts that an operation is supported.  The assertion holds if and only if supported is true.
   *
   * @param supported a Boolean value resulting from the evaluation of the criteria used by the caller
   * to determine if the operation is supported.
   * @param e the RuntimeException thrown if the assertion fails.
   * @throws java.lang.RuntimeException if the operation is unsupported.
   */
  public static void supported(Boolean supported, RuntimeException e) {
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
  private static String format(String message, Object... arguments) {
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
  private static String messageFormat(String message, Object... arguments) {
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
  private static String stringFormat(String message, Object... arguments) {
    return String.format(message, arguments);
  }
}
