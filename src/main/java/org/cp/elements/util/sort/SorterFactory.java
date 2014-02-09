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

package org.cp.elements.util.sort;

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.util.sort.support.BubbleSort;
import org.cp.elements.util.sort.support.HeapSort;
import org.cp.elements.util.sort.support.InsertionSort;
import org.cp.elements.util.sort.support.MergeSort;
import org.cp.elements.util.sort.support.QuickSort;
import org.cp.elements.util.sort.support.SelectionSort;
import org.cp.elements.util.sort.support.ShellSort;

/**
 * The SorterFactory class is a factory for creating instances of different Sorter implementations that implement
 * different sorting algorithms.
 * <p/>
 * @author John J. Blum
 * @see org.cp.elements.util.sort.SortType
 * @see org.cp.elements.util.sort.support.BubbleSort
 * @see org.cp.elements.util.sort.support.HeapSort
 * @see org.cp.elements.util.sort.support.InsertionSort
 * @see org.cp.elements.util.sort.support.MergeSort
 * @see org.cp.elements.util.sort.support.QuickSort
 * @see org.cp.elements.util.sort.support.SelectionSort
 * @see org.cp.elements.util.sort.support.ShellSort
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SorterFactory {

  /**
   * Creates an instance of the Sorter interface implementing the sorting algorithm based on the SortType.
   * <p/>
   * @param <T> the Class type of the actual Sorter implementation based on the SortType.
   * @param type the type of sorting algorithm Sorter implementation to create.
   * @return a Sorter implementation subclass that implements the sorting algorithm based on the SortType.
   * @see org.cp.elements.util.sort.Sorter
   * @see org.cp.elements.util.sort.SortType
   */
  @SuppressWarnings("unchecked")
  public static <T extends Sorter> T createSorter(final SortType type) {
    switch (ObjectUtils.defaultIfNull(type, SortType.UNKONWN)) {
      case BUBBLE_SORT:
        return (T) new BubbleSort();
      case HEAP_SORT:
        return (T) new HeapSort();
      case INSERTION_SORT:
        return (T) new InsertionSort();
      case MERGE_SORT:
        return (T) new MergeSort();
      case QUICK_SORT:
        return (T) new QuickSort();
      case SELECTION_SORT:
        return (T) new SelectionSort();
      case SHELL_SORT:
        return (T) new ShellSort();
      default:
        throw new IllegalArgumentException(String.format("The SortType (%1$s) is not supported by the %2$s!", type,
          SorterFactory.class.getSimpleName()));
    }
  }

}