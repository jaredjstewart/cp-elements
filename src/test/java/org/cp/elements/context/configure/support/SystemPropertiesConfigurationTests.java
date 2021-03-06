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

package org.cp.elements.context.configure.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.cp.elements.context.configure.Configuration;
import org.junit.Test;

/**
 * Test suite of test cases testing the contract and functionality of the {@link SystemPropertiesConfiguration} class.
 *
 * @author John J. Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.cp.elements.context.configure.Configuration
 * @see org.cp.elements.context.configure.support.SystemPropertiesConfiguration
 * @since 1.0.0
 */
public class SystemPropertiesConfigurationTests {

  private final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();

  @Test
  public void isPresent() {
    assertTrue(configuration.isPresent("java.class.path"));
    assertTrue(configuration.isPresent("java.home"));
    assertTrue(configuration.isPresent("java.version"));
    assertTrue(configuration.isPresent("user.dir"));
    assertTrue(configuration.isPresent("user.home"));
    assertTrue(configuration.isPresent("user.name"));
    assertFalse(configuration.isPresent("unset.system.property"));
  }

  @Test
  public void doGetPropertyValue() {
    assertEquals(System.getProperty("java.class.path"), configuration.doGetPropertyValue("java.class.path"));
    assertEquals(System.getProperty("java.home"), configuration.doGetPropertyValue("java.home"));
    assertEquals(System.getProperty("java.version"), configuration.doGetPropertyValue("java.version"));
    assertEquals(System.getProperty("user.dir"), configuration.doGetPropertyValue("user.dir"));
    assertEquals(System.getProperty("user.home"), configuration.doGetPropertyValue("user.home"));
    assertEquals(System.getProperty("user.name"), configuration.doGetPropertyValue("user.name"));
  }

  @Test
  public void getParentPropertyValue() {
    Configuration mockParentConfiguration = mock(Configuration.class);

    when(mockParentConfiguration.getPropertyValue(eq("custom.system.property"), anyBoolean())).thenReturn("test");

    SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration(mockParentConfiguration);

    assertThat(configuration.getPropertyValue("java.version"), is(equalTo(System.getProperty("java.version"))));
    assertThat(configuration.getPropertyValue("custom.system.property"), is(equalTo("test")));
    assertThat(configuration.getPropertyValue("unset.system.property", false), is(nullValue()));

    verify(mockParentConfiguration, times(1)).getPropertyValue(eq("custom.system.property"), eq(true));
    verify(mockParentConfiguration, times(1)).getPropertyValue(eq("unset.system.property"), anyBoolean());
  }

  @Test
  public void iterator() {
    Set<String> expectedSystemPropertyNames = new HashSet<>(System.getProperties().stringPropertyNames());

    assertFalse(expectedSystemPropertyNames.isEmpty());

    for (String actualSystemPropertyName : configuration) {
      assertTrue(expectedSystemPropertyNames.remove(actualSystemPropertyName));
    }

    assertTrue(expectedSystemPropertyNames.isEmpty());
  }
}
