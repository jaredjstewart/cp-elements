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

package org.cp.elements.util;

import static org.cp.elements.lang.LangExtensions.assertThat;
import static org.cp.elements.lang.ObjectUtils.defaultIfNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.Filter;
import org.cp.elements.lang.FilteringTransformer;
import org.cp.elements.lang.NullSafe;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.Transformer;

/**
 * The ArrayUtils class encapsulates utility methods and functionality for interacting with Object arrays.
 * 
 * @author John J. Blum
 * @see java.lang.Iterable
 * @see java.lang.reflect.Array
 * @see java.util.ArrayList
 * @see java.util.Enumeration
 * @see java.util.Iterator
 * @see java.util.List
 * @see org.cp.elements.lang.Filter
 * @see org.cp.elements.lang.FilteringTransformer
 * @see org.cp.elements.lang.Transformer
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class ArrayUtils {

  @SuppressWarnings("all")
  private static final Object[] EMPTY_ARRAY = new Object[0];

  /**
   * Adds (inserts) the element to the end of the array.
   * 
   * @param <T> Class type of the elements stored in the array.
   * @param element element to insert at the end of the array.
   * @param array array in which to insert the element.
   * @return a new array with the element inserted at the end.
   * @see #insert(Object, Object[], int)
   * @see #count(Object[])
   */
  public static <T> T[] append(T element, T[] array) {
    return insert(element, array, count(array));
  }

  /**
   * Convenience method for specifying an array.
   *
   * @param <T> Class type of the elements stored in the array.
   * @param array the array.
   * @return the given array.
   */
  @SafeVarargs
  public static <T> T[] asArray(T... array) {
    return array;
  }

  /**
   * Converts the {@link Enumeration} into an array.
   *
   * @param <T> Class type of the elements in the {@link Enumeration}.
   * @param enumeration {@link Enumeration} to convert into an array.
   * @param componentType {@link Class} type of the elements in the {@link Enumeration}.
   * @return an array containing all the elements from the {@link Enumeration}.  Returns an empty array
   * if the {@link Enumeration} is null.
   * @see java.util.Enumeration
   * @see #asArray(Iterable, Class)
   */
  @NullSafe
  public static <T> T[] asArray(Enumeration<T> enumeration, Class<T> componentType) {
    return asArray(CollectionUtils.iterable(enumeration), componentType);
  }

  /**
   * Converts the {@link Iterable} into an array.
   *
   * @param <T> Class type of the elements in the {@link Iterable} collection.
   * @param iterable {@link Iterable} collection to convert into an array.
   * @param componentType {@link Class} type of the elements in the {@link Iterable} collection.
   * @return an array containing all the elements from the {@link Iterable} collection.  Returns an empty array
   * if the {@link Iterable} collection is null.
   * @see java.lang.Class
   * @see java.lang.Iterable
   */
  @NullSafe
  @SuppressWarnings("unchecked")
  public static <T> T[] asArray(Iterable<T> iterable, Class<T> componentType) {
    List<T> arrayList = new ArrayList<>();

    for (T element : CollectionUtils.nullSafeIterable(iterable)) {
      arrayList.add(element);
    }

    return arrayList.toArray((T[]) Array.newInstance(defaultIfNull(componentType, Object.class), arrayList.size()));
  }

  /**
   * Converts the {@link Iterator} into an array.
   *
   * @param <T> Class type of the elements in the {@link Iterator}.
   * @param iterator {@link Iterator} to convert into an array.
   * @param componentType {@link Class} type of the elements in the {@link Iterator}.
   * @return an array containing all the elements from the {@link Iterator}.  Returns an empty array
   * if the {@link Iterator} is null.
   * @see java.util.Iterator
   * @see #asArray(Iterable, Class)
   */
  @NullSafe
  public static <T> T[] asArray(Iterator<T> iterator, Class<T> componentType) {
    return asArray(CollectionUtils.iterable(iterator), componentType);
  }

  /**
   * Counts the number of elements in the array.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to evaluate.
   * @return an integer value indicating the number of elements in the array (a.k.a. size).
   */
  @NullSafe
  public static <T> int count(T[] array) {
    return (array == null ? 0 : array.length);
  }

  /**
   * Counts the number of elements in the array matching the criteria (rules) defined by the {@link Filter}.
   * 
   * @param <T> Class type of the elements in the array.
   * @param array array to search.
   * @param filter {@link Filter} used to match elements in the array and tally the count.
   * @return an integer value indicating the number of elements in the array matching the criteria
   * defined by the {@link Filter}.
   * @see org.cp.elements.lang.Filter
   */
  @NullSafe
  public static <T> int count(T[] array, Filter<T> filter) {
    int count = 0;

    for (T element : nullSafeArray(array)) {
      if (filter.accept(element)) {
        count++;
      }
    }

    return count;
  }

  /**
   * Returns an empty array.
   * 
   * @return an empty array with no capacity for elements.
   * @see #EMPTY_ARRAY
   */
  public static Object[] emptyArray() {
    return EMPTY_ARRAY.clone();
  }

  /**
   * Returns an {@link Enumeration} to enumerate over the elements in the array.
   * 
   * @param <T> Class type of the elements in the array.
   * @param array array to enumerate.
   * @return an {@link Enumeration} over the elements in the array or an empty {@link Enumeration} if the array is null.
   * @see java.util.Enumeration
   */
  @NullSafe
  @SafeVarargs
  public static <T> Enumeration<T> enumeration(T... array) {
    return (array == null ? Collections.emptyEnumeration() : new Enumeration<T>() {

      private int index = 0;

      @Override
      public boolean hasMoreElements() {
        return (index < array.length);
      }

      @Override
      public T nextElement() {
        Assert.isTrue(hasMoreElements(), new NoSuchElementException("No more elements"));
        return array[index++];
      }
    });
  }

  /**
   * Filters the elements in the given array.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to filter.
   * @param filter {@link Filter} used to filter the array elements.
   * @return a new array of the array class component type containing only elements accepted by the {@link Filter}.
   * @throws IllegalArgumentException if either the array or {@link Filter} are null.
   * @see #filterAndTransform(Object[], FilteringTransformer)
   * @see org.cp.elements.lang.Filter
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] filter(T[] array, Filter<T> filter) {
    Assert.notNull(array, "Array cannot be null");
    Assert.notNull(filter, "Filter cannot be null");

    List<T> arrayList = new ArrayList<>();

    for (T element : array) {
      if (filter.accept(element)) {
        arrayList.add(element);
      }
    }

    return arrayList.toArray((T[]) Array.newInstance(array.getClass().getComponentType(), arrayList.size()));
  }

  /**
   * Filters and transforms the elements in the array.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to filter and transform.
   * @param filteringTransformer {@link FilteringTransformer} used to filter and transform the array elements.
   * @return a new array of the array class component type containing transformed elements accepted
   * by the {@link FilteringTransformer}.
   * @throws IllegalArgumentException if either the array or {@link FilteringTransformer} are null.
   * @see org.cp.elements.lang.FilteringTransformer
   * @see #filter(Object[], Filter)
   * @see #transform(Object[], Transformer)
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] filterAndTransform(T[] array, FilteringTransformer<T> filteringTransformer) {
    return transform(filter(array, filteringTransformer), filteringTransformer);
  }

  /**
   * Searches the array for the first element matching the criteria (rules) defined by the {@link Filter}.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to search.
   * @param filter {@link Filter} used to find the first element in the array matching the criteria (rules)
   * defined by the {@link Filter}.
   * @return the first element from the array matching the criteria (rules) defined by the {@link Filter}
   * or null if no such element is found.
   * @throws IllegalArgumentException if {@link Filter} is null.
   * @see org.cp.elements.lang.Filter
   * @see #findAll(Object[], Filter)
   */
  public static <T> T find(T[] array, Filter<T> filter) {
    Assert.notNull(filter, "Filter cannot be null");

    for (T element : nullSafeArray(array)) {
      if (filter.accept(element)) {
        return element;
      }
    }

    return null;
  }

  /**
   * Searches the array for all elements matching the criteria (rules) defined by the {@link Filter}.
   * 
   * @param <T> Class type of the elements in the array.
   * @param array array to search.
   * @param filter {@link Filter} used to find all elements in the array matching the criteria (rules) defined
   * by the {@link Filter}.
   * @return a {@link List} containing all the matching elements from the array or an empty list if not elements
   * matching the criteria defined by the {@link Filter} were found.
   * @throws IllegalArgumentException if {@link Filter} is null.
   * @see org.cp.elements.lang.Filter
   * @see #find(Object[], Filter)
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> findAll(T[] array, Filter<T> filter) {
    Assert.notNull(filter, "Filter cannot be null");

    List<T> arrayList = new ArrayList<>();

    for (T element : nullSafeArray(array)) {
      if (filter.accept(element)) {
        arrayList.add(element);
      }
    }

    return arrayList;
  }

  /**
   * Returns the first element in the array (at index 0).
   *
   * @param <T> Class type of the elements in the array.
   * @param array array from which to extract the first element.
   * @return the first element in the array or null if the array is null or empty.
   * @see #isNotEmpty(Object[])
   */
  @NullSafe
  @SafeVarargs
  public static <T> T getFirst(T... array) {
    return (isNotEmpty(array) ? array[0] : null);
  }

  /**
   * Inserts element into the array at index.
   * 
   * @param <T> Class type of the elements stored in the array.
   * @param element element to insert into the array.
   * @param array array in which the element is inserted.
   * @param index an integer indicating the index into which the element will be inserted into the array.
   * @return a new array with element inserted at index.
   * @throws IllegalArgumentException if array is null.
   * @throws ArrayIndexOutOfBoundsException if given index is not a valid index in the array.
   * @see #append(Object, Object[])
   * @see #prepend(Object, Object[])
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] insert(T element, T[] array, int index) {
    Assert.notNull(array, "Array cannot be null");

    assertThat(index).throwing(new ArrayIndexOutOfBoundsException(
      String.format("[%1$d] is not a valid index [0, %2$d] in the array", index, array.length)))
        .isGreaterThanEqualToAndLessThanEqualTo(0, array.length);

    Class<?> componentType = array.getClass().getComponentType();

    componentType = defaultIfNull(componentType, ObjectUtils.getClass(element));
    componentType = defaultIfNull(componentType, Object.class);

    T[] newArray = (T[]) Array.newInstance(componentType, array.length + 1);

    if (index > 0) {
      System.arraycopy(array, 0, newArray, 0, index);
    }

    newArray[index] = element;

    if (index < array.length) {
      System.arraycopy(array, index, newArray, index + 1, array.length - index);
    }

    return newArray;
  }

  /**
   * Determines whether the array is empty.  An array is empty if it is null or contains no elements.
   *
   * @param array array to evaluate.
   * @return a boolean value indicating whether the array is empty.
   * @see #isNotEmpty(Object[])
   * @see #count(Object[])
   */
  @NullSafe
  public static boolean isEmpty(Object[] array) {
    return (count(array) == 0);
  }

  /**
   * Determines whether the array is non-empty.  An array is non-empty if it is not null and contains
   * at least 1 element.
   *
   * @param array array to evaluate.
   * @return a boolean value indicating whether the array is non-empty.
   * @see #isEmpty(Object[])
   */
  @NullSafe
  public static boolean isNotEmpty(Object[] array) {
    return !isEmpty(array);
  }

  /**
   * Returns an {@link Iterable} over the elements in the array.
   * 
   * @param <T> Class type of the elements in the array.
   * @param array array to iterate.
   * @return an {@link Iterable) over the elements in the array
   * or an empty {@link Iterable} if the array is null.
   * @see #iterator(Object[])
   * @see java.lang.Iterable
   */
  @SafeVarargs
  public static <T> Iterable<T> iterable(T... array) {
    return () -> iterator(array);
  }

  /**
   * Returns an {@link Iterator} to iterate over the elements in the array.
   * 
   * @param <T> Class type of the elements in the array.
   * @param array array to iterate.
   * @return an {@link Iterator} to iterate over the elements in the array.
   * @see java.util.Iterator
   */
  @SafeVarargs
  public static <T> Iterator<T> iterator(T... array) {
    return (array == null ? Collections.emptyIterator() : new Iterator<T>() {

      private int index = 0;

      @Override
      public boolean hasNext() {
        return (index < array.length);
      }

      @Override
      public T next() {
        Assert.isTrue(hasNext(), new NoSuchElementException("No more elements"));
        return array[index++];
      }
    });
  }

  /**
   * Null-safe method returning the array if not null otherwise returns an empty array.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to evalute.
   * @return the array if not null otherwise return an empty array.
   * @see #nullSafeArray(Object[], Class)
   */
  @NullSafe
  public static <T> T[] nullSafeArray(T[] array) {
    return nullSafeArray(array, componentType(array));
  }

  /**
   * Null-safe method returning the array if not null otherwise returns an empty array.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to evaluate.
   * @param componentType {@link Class} type of the elements in the array.  Defaults to {@link Object} if null.
   * @return the array if not null otherwise return an empty array.
   * @see #nullSafeArray(Object[])
   */
  @NullSafe
  @SuppressWarnings("unchecked")
  public static <T> T[] nullSafeArray(T[] array, Class<?> componentType) {
    return (array != null ? array : (T[]) Array.newInstance(defaultIfNull(componentType, Object.class), 0));
  }

  /* non-Javadoc */
  @SuppressWarnings("unchecked")
  static <T> Class<?> componentType(T[] array) {
    return defaultIfNull((array != null ? array.getClass().getComponentType() : null), Object.class);
  }

  /**
   * Prepends (inserts) the element to the beginning of the array.
   * 
   * @param <T> Class type of the elements in the array.
   * @param element element to insert at the beginning of the array.
   * @param array array in which to insert the element.
   * @return a new array with the element inserted at the beginning.
   * @see #insert(Object, Object[], int)
   */
  public static <T> T[] prepend(T element, T[] array) {
    return insert(element, array, 0);
  }

  /**
   * Shuffles the elements in the array.  This method guarantees a random, uniform shuffling of the elements
   * in the array with an operational efficiency of O(n).
   *
   * @param <T> Class type of the elements in the array.
   * @param array array to shuffle.
   * @return the array of elements shuffled.
   */
  @NullSafe
  public static <T> T[] shuffle(T[] array) {
    if (isNotEmpty(array)) {
      Random random = new Random(System.currentTimeMillis());

      for (int index = 0, adjustedLength = count(array) - 1; index < adjustedLength; index++) {
        int randomIndex = (random.nextInt(adjustedLength - index) + 1);
        swap(array, index, randomIndex);
      }
    }

    return array;
  }

  /**
   * Creates a sub-array from the given array with the values at the specified indices in the given array.
   *
   * @param <T> the Class type of the array elements.
   * @param array the original Object array from which to create the sub-array.
   * @param indices the indices of values from the original array to include in the sub-array.
   * @return a sub-array from the given array with the values at the specified indices in the given array.
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] subArray(T[] array, int... indices) {
    List<T> subArrayList = new ArrayList<>(indices.length);

    for (int index : indices) {
      subArrayList.add(array[index]);
    }

    return subArrayList.toArray((T[]) Array.newInstance(array.getClass().getComponentType(), subArrayList.size()));
  }

  /**
   * Swaps elements in the array at indexOne and indexTwo.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array with elements to swap.
   * @param indexOne index of the first element to swap.
   * @param indexTwo index of the second element to swap.
   * @return the array with the specified elements at indexOne and indexTwo swapped.
   * @throws NullPointerException if the array is null.
   */
  public static <T> T[] swap(T[] array, int indexOne, int indexTwo) {
    T elementAtIndexOne = array[indexOne];
    array[indexOne] = array[indexTwo];
    array[indexTwo] = elementAtIndexOne;
    return array;
  }

  /**
   * Transforms the elements in the array using the {@link Transformer}.
   *
   * @param <T> Class type of the elements in the array.
   * @param array array of elements to transform.
   * @param transformer {@link Transformer} used to transform the elements in the array.
   * @return the array elements transformed.
   * @throws IllegalArgumentException if either array or {@link Transformer} are null.
   * @see org.cp.elements.lang.Transformer
   */
  public static <T> T[] transform(T[] array, Transformer<T> transformer) {
    Assert.notNull(array, "Array cannot be null");
    Assert.notNull(transformer, "Transformer cannot be null");

    int index = 0;

    for (T element : array) {
      array[index++] = transformer.transform(element);
    }

    return array;
  }
}
