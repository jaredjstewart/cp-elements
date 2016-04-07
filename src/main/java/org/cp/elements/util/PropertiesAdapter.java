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

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.Filter;
import org.cp.elements.util.convert.ConversionService;
import org.cp.elements.util.convert.provider.DefaultConversionService;

/**
 * The PropertiesAdapter class is a wrapper around a {@link java.util.Properties} object encapsulating functionality
 * to conveniently access properties and convert to a specific value type.
 *
 * @author John J. Blum
 * @see java.lang.Iterable
 * @see java.util.Properties
 * @see org.cp.elements.util.convert.ConversionService
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class PropertiesAdapter implements Iterable<String> {

  private final ConversionService conversionService;

  private final Properties delegate;

  /**
   * Factory method to get an instance of the PropertiesAdapter class initialized with the given {@link Properties}.
   *
   * @param properties the {@link Properties} to wrap.
   * @return an instance of the {@link PropertiesAdapter} initialized with the given {@link Properties}.
   */
  public static PropertiesAdapter from(Properties properties) {
    return new PropertiesAdapter(properties);
  }

  /**
   * Constructs an instance of the PropertiesAdapter class initialized with the given {@link Properties}.
   *
   * @param properties the {@link Properties} to wrap with this wrapper.
   */
  public PropertiesAdapter(Properties properties) {
    Assert.notNull(properties, "The Properties to wrap cannot be null");
    this.delegate = properties;
    this.conversionService = new DefaultConversionService();
  }

  /**
   * Returns a reference to the {@link ConversionService} used to convert the property value
   * into a value of the desired class type.
   *
   * @return a reference to the {@link ConversionService} used ot convert the property value
   * into a value of the desired class type.
   * @see org.cp.elements.util.convert.ConversionService
   */
  protected ConversionService getConversionService() {
    return conversionService;
  }

  /**
   * Returns the {@link Properties} wrapped by this wrapper.
   *
   * @return the {@link Properties} wrapped by this wrapper.
   * @see java.util.Properties
   */
  public Properties getProperties() {
    return delegate;
  }

  /**
   * Determines whether the named property is present in this {@link Properties} adapter.
   *
   * @param propertyName the name of the property to check for presence in this adapter.
   * @return a boolean value indicating whether the named property is present in this set of {@link Properties}.
   * @see java.util.Properties#containsKey(Object)
   * @see #getProperties()
   */
  public boolean contains(String propertyName) {
    return getProperties().containsKey(propertyName);
  }

  /**
   * Converts the value of the named property into an instance of the given {@link Class} type.
   *
   * @param <T> Class type of the return value.
   * @param propertyName the name of the property to get.
   * @param type the desired {@link Class} type to convert the value of the named property to.
   * @return the value of the named property converted to an instance of the given {@link Class} type.
   * @see org.cp.elements.util.convert.ConversionService#convert(Object, Class)
   * @see java.lang.Class
   * @see #getConversionService()
   * @see #get(String) \
   */
  protected <T> T convert(String propertyName, Class<T> type) {
    return getConversionService().convert(get(propertyName), type);
  }

  /**
   * Defaults of the value for the named property if the property does not exist.
   *
   * @param <T> {@link Class} type of the return value.
   * @param propertyName the name of the property to get.
   * @param defaultValue the default value to return if the named property does not exist.
   * @param type the desired {@link Class} type of the named property value.
   * @return the value of the named property as a instance of the specified {@link Class} type
   * or return the default value if the named property does not exist.
   * @see java.lang.Class
   * @see #contains(String)
   * @see #convert(String, Class)
   */
  protected <T> T defaultIfNotExists(String propertyName, T defaultValue, Class<T> type) {
    return (contains(propertyName) ? convert(propertyName, type) : defaultValue);
  }

  /**
   * Filters the properties from this adapter by name.
   *
   * @param filter the {@link Filter} used to filter the properties of this adapter.
   * @return a newly constructed instance of the {@link PropertiesAdapter} containing only the filtered properties.
   * @see org.cp.elements.lang.Filter
   * @see java.util.Properties
   * @see #from(Properties)
   */
  public PropertiesAdapter filter(Filter<String> filter) {
    Properties properties = new Properties();

    for (String propertyName : this) {
      if (filter.accept(propertyName)) {
        properties.setProperty(propertyName, get(propertyName));
      }
    }

    return from(properties);
  }

  /**
   * Gets the assigned value of named property as a {@link String}.
   *
   * @param propertyName the name of the property to get.
   * @return the assigned value of the named property as a {@link String}.
   * @see #get(String, String)
   */
  public String get(String propertyName) {
    return get(propertyName, null);
  }

  /**
   * Gets the assigned value of the named property as a {@link String} or returns the default value
   * if the named property does not exist.
   *
   * @param propertyName the name of the property to get.
   * @param defaultValue the default value to return if the named property does not exist.
   * @return the assigned value of the named property as a {@link String} or default value
   * if the named property does not exist.
   * @see java.util.Properties#getProperty(String)
   * @see #getProperties()
   */
  public String get(String propertyName, String defaultValue) {
    return getProperties().getProperty(propertyName, defaultValue);
  }

  /**
   * Gets the assigned value of the named property as an instance of the specified {@link Class} type.
   *
   * @param <T> {@link Class} type of the return value.
   * @param propertyName the name of the property to get.
   * @return the assigned value of the named property as an instance of the specified {@link Class} type.
   * @see #getAsType(String, Object, Class)
   */
  public <T> T getAsType(String propertyName, Class<T> type) {
    return getAsType(propertyName, null, type);
  }

  /**
   * Gets the assigned value of the named property as an instance of the specified {@link Class} type
   * or the default value if the named property does not exist.
   *
   * @param <T> {@link Class} type of the return value.
   * @param propertyName the name of the property to get.
   * @param defaultValue the default value to return if the named property does not exist.
   * @return the assigned value of the named property as an instance of the specified {@link Class} type
   * or the default value if the named property does not exist.
   * @see #defaultIfNotExists(String, Object, Class)
   * @see java.lang.Class
   */
  public <T> T getAsType(String propertyName, T defaultValue, Class<T> type) {
    return defaultIfNotExists(propertyName, defaultValue, type);
  }

  /**
   * Determines whether this {@link Properties} object contains any properties.
   *
   * @return a boolean value indicating whether this {@link Properties} object contains any properties.
   * @see java.util.Properties#isEmpty()
   * @see #getProperties()
   */
  public boolean isEmpty() {
    return getProperties().isEmpty();
  }

  /**
   * Iterates the property names in this {@link Properties} object.
   *
   * @return an {@link Iterator} to iterate the name of the properties in this {@link Properties} object.
   * @see java.util.Properties#stringPropertyNames()
   * @see java.util.Iterator
   * @see #getProperties()
   */
  @Override
  public Iterator<String> iterator() {
    return Collections.unmodifiableSet(getProperties().stringPropertyNames()).iterator();
  }

  /**
   * Returns the number of properties in this {@link Properties} object.
   *
   * @return an integer value indicating the number of properties in this {@link Properties} object.
   * @see java.util.Properties#size()
   * @see #getProperties()
   */
  public int size() {
    return getProperties().size();
  }

}