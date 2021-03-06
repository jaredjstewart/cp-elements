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

package org.cp.elements.lang.support;

import java.util.Comparator;

import org.cp.elements.lang.Ordered;

/**
 * The OrderedComparator class is an implementation of the Comparator interface for Ordered objects.
 *
 * @author John J. Blum
 * @see java.util.Comparator
 * @see org.cp.elements.lang.Ordered
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class OrderedComparator implements Comparator<Ordered> {

  /**
   * Compares two Ordered objects to determine their relative order by index.
   *
   * @param ordered1 the first Ordered object in the order comparison.
   * @param ordered2 the second Ordered object in the order comparison.
   * @return an integer value indicating one Ordered object's order relative to another Ordered object.
   */
  @Override
  public int compare(final Ordered ordered1, final Ordered ordered2) {
    return (ordered1.getIndex() < ordered2.getIndex() ? -1 : (ordered1.getIndex() > ordered2.getIndex() ? 1 : 0));
  }

}
