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

package org.cp.elements.util.convert.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.cp.elements.lang.StringUtils;
import org.cp.elements.util.convert.ConversionException;
import org.cp.elements.util.convert.ConverterAdapter;

/**
 * The NumberConverter class converts an Object value into a Number of a qualified numerical Class type.
 *
 * @author John J. Blum
 * @see java.lang.Number
 * @see java.lang.Object
 * @see java.lang.String
 * @see java.math.BigDecimal
 * @see java.math.BigInteger
 * @see java.util.concurrent.atomic.AtomicInteger
 * @see java.util.concurrent.atomic.AtomicLong
 * @see org.cp.elements.util.convert.ConverterAdapter
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class NumberConverter extends ConverterAdapter<Object, Number> {

  protected static final String CONVERSION_EXCEPTION_MESSAGE = "The Object value (%1$s) is not a valid number of the qualifying type (%2$s)!";

  protected <QT extends Number> QT parseNumber(final String number, final Class<QT> numberType) {
    if (AtomicInteger.class.isAssignableFrom(numberType)) {
      return numberType.cast(new AtomicInteger(Integer.parseInt(number)));
    }
    else if (AtomicLong.class.isAssignableFrom(numberType)) {
      return numberType.cast(new AtomicLong(Long.parseLong(number)));
    }
    else if (BigDecimal.class.isAssignableFrom(numberType)) {
      return numberType.cast(new BigDecimal(number));
    }
    else if (BigInteger.class.isAssignableFrom(numberType)) {
      return numberType.cast(new BigInteger(number));
    }
    else if (Byte.class.isAssignableFrom(numberType)) {
      return numberType.cast(Byte.parseByte(number));
    }
    else if (Short.class.isAssignableFrom(numberType)) {
      return numberType.cast(Short.parseShort(number));
    }
    else if (Integer.class.isAssignableFrom(numberType)) {
      return numberType.cast(Integer.parseInt(number));
    }
    else if (Long.class.isAssignableFrom(numberType)) {
      return numberType.cast(Long.parseLong(number));
    }
    else if (Float.class.isAssignableFrom(numberType)) {
      return numberType.cast(Float.parseFloat(number));
    }
    else if (Double.class.isAssignableFrom(numberType)) {
      return numberType.cast(Double.parseDouble(number));
    }

    throw new ConversionException(String.format("The Class type (%1$s) is not a valid Number type!", numberType));
  }

  protected <QT extends Number> QT toQualifyingNumber(final Number number, final Class<QT> numberType) {
    if (AtomicInteger.class.isAssignableFrom(numberType)) {
      return numberType.cast(new AtomicInteger(number.intValue()));
    }
    else if (AtomicLong.class.isAssignableFrom(numberType)) {
      return numberType.cast(new AtomicLong(number.longValue()));
    }
    else if (BigDecimal.class.isAssignableFrom(numberType)) {
      return numberType.cast(new BigDecimal(number.toString()));
    }
    else if (BigInteger.class.isAssignableFrom(numberType)) {
      return numberType.cast(new BigInteger(number.toString()));
    }
    else if (Byte.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.byteValue());
    }
    else if (Short.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.shortValue());
    }
    else if (Integer.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.intValue());
    }
    else if (Long.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.longValue());
    }
    else if (Float.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.floatValue());
    }
    else if (Double.class.isAssignableFrom(numberType)) {
      return numberType.cast(number.doubleValue());
    }

    throw new ConversionException(String.format("The Class type (%1$s) is not a valid Number type!", numberType));
  }

  @Override
  public boolean canConvert(final Class<?> fromType, final Class<?> toType) {
    return (isAssignableTo(fromType, Number.class, String.class) && isAssignableTo(toType, Number.class));
  }

  @Override
  public <QT extends Number> QT convert(final Object value, final Class<QT> qualifyingType) {
    try {
      if (qualifyingType.isInstance(value)) {
        return qualifyingType.cast(value);
      }
      else if (value instanceof Number) {
        return toQualifyingNumber((Number) value, qualifyingType);
      }
      else if (value instanceof String && StringUtils.containsDigits(value.toString().trim())) {
        return parseNumber(value.toString().trim(), qualifyingType);
      }
      else {
        throw new ConversionException(String.format(CONVERSION_EXCEPTION_MESSAGE, value, qualifyingType.getName()));
      }
    }
    catch (Exception e) {
      if (e instanceof ConversionException) {
        throw (ConversionException) e;
      }
      throw new ConversionException(String.format(CONVERSION_EXCEPTION_MESSAGE, value, qualifyingType.getName()), e);
    }
  }

}
