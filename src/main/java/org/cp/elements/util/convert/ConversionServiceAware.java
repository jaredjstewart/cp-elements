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

package org.cp.elements.util.convert;

/**
 * The ConversionServiceAware interface is a contract for implementing objects that require an instance of
 * the ConversionService.
 *
 * @author John J. Blum
 * @see org.cp.elements.util.convert.ConversionService
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface ConversionServiceAware {

  /**
   * Sets a reference to the specified ConversionService.
   *
   * @param conversionService the reference to the ConversionService.
   */
  void setConversionService(ConversionService conversionService);

}
