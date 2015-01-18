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

package org.cp.elements.lang.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import javax.annotation.Resource;

import org.cp.elements.lang.Filter;
import org.cp.elements.lang.NumberUtils;
import org.cp.elements.lang.annotation.Id;
import org.cp.elements.test.AbstractMockingTestSuite;
import org.cp.elements.test.TestUtils;
import org.cp.elements.util.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The ReflectionUtilsTest class is a test suite of test cases testing the contract and functionality
 * of the ReflectionUtils class.
 *
 * @author John J. Blum
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @see org.cp.elements.lang.reflect.ReflectionUtils
 * @see org.cp.elements.test.AbstractMockingTestSuite
 * @see org.cp.elements.test.TestUtils
 * @see org.junit.Test
 * @since 1.0.0
 */
public class ReflectionUtilsTest extends AbstractMockingTestSuite {

  private static final AtomicReference<String> METHOD_NAME = new AtomicReference<String>();

  @Before
  public void setup() {
    setLogLevel(Level.INFO);
  }

  @After
  public void tearDown() {
    METHOD_NAME.set(null);
    SuperType.stringField = null;
  }

  @Test
  public void testGetArgumentTypes() {
    assertNull(ReflectionUtils.getArgumentTypes((Object[]) null));
    TestUtils.assertEquals(new Class[0], ReflectionUtils.getArgumentTypes());
    TestUtils.assertEquals(new Class[] { Boolean.class, Character.class, Integer.class, Double.class, String.class },
      ReflectionUtils.getArgumentTypes(true, 'c', 123, Math.PI, "test"));
    TestUtils.assertEquals(new Class[] { Boolean.class, null, Integer.class, Double.class, null },
      ReflectionUtils.getArgumentTypes(false, null, 1, 3.14159, null));
  }

  @Test
  public void testGetValueOfClassField() {
    assertEquals(42l, ReflectionUtils.getValue(SuperType.class, "serialVersionUID", Long.class).longValue());
    assertEquals(42l, ReflectionUtils.getValue(DerivedType.class, "serialVersionUID", Long.class).longValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetValueOfNonExistingClassField() {
    try {
      ReflectionUtils.getValue(SuperType.class, "DEFAULT_ID", Long.class);
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format("Field with name (DEFAULT_ID) does not exist on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof FieldNotFoundException);
      throw expected;
    }
  }

  @Test
  public void testGetValueOfObjectField() {
    assertEquals(1l, ReflectionUtils.getValue(new DerivedType(), "id", Long.class).longValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetValueOfNonExistingObjectField() {
    try {
      ReflectionUtils.getValue(new DerivedType(), "name", String.class);
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format("Field with name (name) does not exist on object of type (%1$s)!",
        DerivedType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof FieldNotFoundException);
      throw expected;
    }
  }

  @Test(expected = FieldAccessException.class)
  public void testGetValueThrowsFieldAccessException() {
    try {
      ReflectionUtils.getValue(new Object(), ReflectionUtils.getField(DerivedType.class, "id"), Object.class);
    }
    catch (FieldAccessException expected) {
      assertEquals(String.format("Failed to get value of field (id) from object of type (%1$s)!",
          Object.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof IllegalArgumentException);
      throw expected;
    }
  }

  @Test(expected = NullPointerException.class)
  public void testGetValueThrowsNullPointerException() {
    try {
      ReflectionUtils.getValue(new Object(), (Field) null, Object.class);
    }
    catch (NullPointerException expected) {
      // for line "boolean currentAccessible = field.isAccessible();" in getValue(:Object, :Field, :Class<T>):T
      assertEquals(150, expected.getStackTrace()[0].getLineNumber());
      throw expected;
    }
  }

  @Test
  public void testSetClassField() {
    assertNull(SuperType.stringField);
    ReflectionUtils.setField(SuperType.class, "stringField", "test");
    assertEquals("test", SuperType.stringField);
  }

  @Test(expected = FieldAccessException.class)
  public void testSetFinalClassField() {
    try {
      assertEquals(42l, SuperType.serialVersionUID);
      ReflectionUtils.setField(SuperType.class, "serialVersionUID", 24l);
    }
    catch (FieldAccessException expected) {
      assertEquals(String.format("Cannot set the value of a final field (serialVersionUID) on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertNull(expected.getCause());
      throw expected;
    }
    finally {
      assertEquals(42l, SuperType.serialVersionUID);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNonExistingClassField() {
    try {
      ReflectionUtils.setField(SuperType.class, "nonExistingField", "test");
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format("Field with name (nonExistingField) does not exist on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof FieldNotFoundException);
      assertTrue(expected.getCause().getCause() instanceof NoSuchFieldException);
      throw expected;
    }
  }

  @Test
  public void testSetObjectField() {
    DerivedType derivedType = new DerivedType();
    assertNull(derivedType.booleanField);
    ReflectionUtils.setField(derivedType, "booleanField", true);
    assertTrue(derivedType.booleanField);
  }

  @Test(expected = FieldAccessException.class)
  public void testSetFinalObjectField() {
    DerivedType derivedType = new DerivedType();

    try {
      assertEquals(0xCAFEBABE, derivedType.magicNumber.intValue());
      ReflectionUtils.setField(derivedType, "magicNumber", 0x0);
    }
    catch (FieldAccessException expected) {
      assertEquals(String.format("Cannot set the value of a final field (magicNumber) on object of type (%1$s)!",
        derivedType.getClass().getName()), expected.getMessage());
      assertNull(expected.getCause());
      throw expected;
    }
    finally {
      assertEquals(0xCAFEBABE, derivedType.magicNumber.intValue());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNonExistingObjectField() {
    try {
      ReflectionUtils.setField(new DerivedType(), "nonExistingField", "test");
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format("Field with name (nonExistingField) does not exist on object of type (%1$s)!",
        DerivedType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof FieldNotFoundException);
      assertTrue(expected.getCause().getCause() instanceof NoSuchFieldException);
      throw expected;
    }
  }

  @Test(expected = FieldAccessException.class)
  public void testSetObjectFieldWithIllegalValue() {
    DerivedType derivedType = new DerivedType();

    try {
      assertNull(derivedType.booleanField);
      ReflectionUtils.setField(derivedType, "booleanField", 1);
    }
    catch (FieldAccessException expected) {
      assertEquals(String.format("Failed to set field (booleanField) to value (1) on object of type (%1$s)!",
        derivedType.getClass().getName()), expected.getMessage());
      assertNotNull(expected.getCause());
      throw expected;
    }
    finally {
      assertNull(derivedType.booleanField);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testSetFieldThrowsNullPointerException() {
    try {
      ReflectionUtils.setField(new Object(), (Field) null, "test");
    }
    catch (NullPointerException expected) {
      // for line "Assert.isFalse(Modifier.isFinal(field.getModifiers())..." in setField(:Object, :Field, :Object):void
      assertEquals(224, expected.getStackTrace()[0].getLineNumber());
      throw expected;
    }
  }

  @Test
  public void testNullCastToVoid() {
    assertNull(Void.class.cast(null));
  }

  @Test
  public void testInvokeClassMethod() {
    assertNull(METHOD_NAME.get());
    ReflectionUtils.invoke(SuperType.class, "methodOne");
    assertEquals("methodOne", METHOD_NAME.get());
  }

  @Test
  public void testInvokeClassMethodWithArguments() {
    assertNull(METHOD_NAME.get());
    ReflectionUtils.invoke(SuperType.class, "methodTwo", "test");
    assertEquals("methodTwo(test)", METHOD_NAME.get());
  }

  @Test
  public void testInvokeClassMethodWithReturnValue() {
    assertNull(METHOD_NAME.get());
    String returnValue = ReflectionUtils.invoke(SuperType.class, "methodThree", String.class);
    assertEquals("test", returnValue);
    assertEquals("methodThree():test", METHOD_NAME.get());
  }

  @Test
  public void testInvokeClassMethodWithArgumentsAndReturnValue() {
    assertNull(METHOD_NAME.get());
    Integer sum = ReflectionUtils.invoke(SuperType.class, "methodFour",
      ArrayUtils.<Class<?>>asArray(Integer.TYPE, Integer.TYPE), ArrayUtils.asArray(5, 7), Integer.class);
    assertEquals(12, NumberUtils.valueOf(sum));
    assertEquals("methodFour", METHOD_NAME.get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvokeNoSuchClassMethod() {
    try {
      assertNull(METHOD_NAME.get());
      ReflectionUtils.invoke(SuperType.class, "methodSix", "test");
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format("No method with signature (methodSix(:String):void) exists on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof MethodNotFoundException);
      assertTrue(expected.getCause().getCause() instanceof NoSuchMethodException);
      throw expected;
    }
    finally {
      assertNull(METHOD_NAME.get());
    }
  }

  @Test
  public void testInvokeObjectMethod() {
    assertNull(METHOD_NAME.get());
    ReflectionUtils.invoke(new DerivedType(), "methodFive");
    assertEquals("methodFive", METHOD_NAME.get());
  }

  @Test
  public void testInvokeObjectMethodWithArguments() {
    assertNull(METHOD_NAME.get());
    ReflectionUtils.invoke(new DerivedType(), "methodSix", "string");
    assertEquals("methodSix(string)", METHOD_NAME.get());
  }

  @Test
  public void testInvokeObjectMethodWithReturnValue() {
    assertNull(METHOD_NAME.get());
    String returnValue = ReflectionUtils.invoke(new DerivedType(), "methodSeven", String.class);
    assertEquals("string", returnValue);
    assertEquals("methodSeven():string", METHOD_NAME.get());
  }

  @Test
  public void testInvokeObjectMethodWithArgumentsAndReturnValue() {
    assertNull(METHOD_NAME.get());
    String returnValue = ReflectionUtils.invoke(new DerivedType(), "methodEight", ArrayUtils.asArray("test", "ing"),
      String.class);
    assertEquals("testing", returnValue);
    assertEquals("methodEight", METHOD_NAME.get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvokeNoSuchObjectMethod() {
    try {
      assertNull(METHOD_NAME.get());
      ReflectionUtils.invoke(new DerivedType(), "methodEight", ArrayUtils.asArray('A', 'C'), String.class);
    }
    catch (IllegalArgumentException expected) {
      assertEquals(String.format(
        "No method with signature (methodEight(:Character, :Character):String) exists on object of type (%1$s)!",
          DerivedType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof MethodNotFoundException);
      assertTrue(expected.getCause().getCause() instanceof NoSuchMethodException);
      throw expected;
    }
    finally {
      assertNull(METHOD_NAME.get());
    }
  }

  @Test(expected = MethodInvocationException.class)
  public void testInvokeMethodWithWrongNumberOfArguments() {
    try {
      assertNull(METHOD_NAME.get());
      ReflectionUtils.invoke(SuperType.class, "methodFour", ArrayUtils.<Class<?>>asArray(String.class),
        ArrayUtils.asArray("Jon", "Doe"), String.class);
    }
    catch (MethodInvocationException expected) {
      assertEquals(String.format("Failed to invoke method (methodFour(:String):String) on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof IllegalArgumentException);
      assertEquals("wrong number of arguments", expected.getCause().getMessage());
      throw expected;
    }
    finally {
      assertNull(METHOD_NAME.get());
    }
  }

  @Test(expected = MethodInvocationException.class)
  public void testInvokeMethodWithArgumentParameterTypeMismatch() {
    try {
      assertNull(METHOD_NAME.get());
      ReflectionUtils.invoke(SuperType.class, "methodFour", ArrayUtils.<Class<?>>asArray(Integer.TYPE, Integer.TYPE),
        ArrayUtils.asArray(5.4d, 4.5d), Integer.TYPE);
    }
    catch (MethodInvocationException expected) {
      assertEquals(String.format("Failed to invoke method (methodFour(:int, :int):int) on class type (%1$s)!",
        SuperType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof IllegalArgumentException);
      assertEquals("argument type mismatch", expected.getCause().getMessage());
      throw expected;
    }
    finally {
      assertNull(METHOD_NAME.get());
    }
  }

  @Test(expected = MethodInvocationException.class)
  public void testInvokeMethodThrowsMethodInvocationException() {
    try {
      assertNull(METHOD_NAME.get());
      ReflectionUtils.invoke(new DerivedType(), "methodNine", ArrayUtils.asArray("test"), Object.class);
    }
    catch (MethodInvocationException expected) {
      assertEquals(String.format("Failed to invoke method (methodNine(:String):Object) on object of type (%1$s)!",
        DerivedType.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof InvocationTargetException);
      assertTrue(expected.getCause().getCause() instanceof IllegalArgumentException);
      assertEquals("test", expected.getCause().getCause().getMessage());
      throw expected;
    }
    finally {
      assertEquals("methodNine", METHOD_NAME.get());
    }
  }

  @Test(expected = NullPointerException.class)
  public void testInvokeMethodThrowsNullPointerException() {
    try {
      ReflectionUtils.invoke(new Object(), (Method) null, ArrayUtils.emptyArray(), Void.class);
    }
    catch (NullPointerException expected) {
      assertEquals(567, expected.getStackTrace()[0].getLineNumber());
      throw expected;
    }
  }

  @Test
  public void testWithFieldsOnClass() {
    final Set<String> fieldNames = new HashSet<String>(2);

    ReflectionUtils.withFields().on(SuperType.class).call(new ReflectionUtils.FieldCallback() {
      @Override public void with(final Field field) {
        fieldNames.add(field.getName());
      }
    }).throwing(new FieldNotFoundException());

    assertNotNull(fieldNames);
    assertFalse(fieldNames.isEmpty());
    assertEquals(2, fieldNames.size());
    assertTrue(fieldNames.containsAll(Arrays.asList("serialVersionUID", "stringField")));
  }

  @Test
  public void testWithFieldsOnObject() {
    final Set<String> fieldNames = new HashSet<String>(5);

    ReflectionUtils.withFields().on(new DerivedType()).call(new ReflectionUtils.FieldCallback() {
      @Override public void with(final Field field) {
        fieldNames.add(field.getName());
      }
    }).throwing(new FieldNotFoundException());

    assertNotNull(fieldNames);
    assertFalse(fieldNames.isEmpty());
    assertEquals(5, fieldNames.size());
    assertTrue(fieldNames.containsAll(Arrays.asList("serialVersionUID", "stringField",
      "booleanField", "magicNumber", "id")));
  }

  @Test
  public void testWithPublicInstanceFieldsOnly() {
    final Set<String> fieldNames = new HashSet<String>(2);

    ReflectionUtils.withFields().on(DerivedType.class).matching(new Filter<Field>() {
      @Override public boolean accept(final Field field) {
        return Modifier.isPublic(field.getModifiers());
      }
    }).call(new ReflectionUtils.FieldCallback() {
      @Override public void with(final Field field) {
        fieldNames.add(field.getName());
      }
    }).throwing(new FieldNotFoundException());

    assertNotNull(fieldNames);
    assertFalse(fieldNames.isEmpty());
    assertEquals(1, fieldNames.size());
    assertTrue(fieldNames.containsAll(Arrays.asList("magicNumber")));
  }

  @Test(expected = FieldNotFoundException.class)
  public void testWithPublicProtectedNonFinalInstanceFields() {
    final AtomicInteger count = new AtomicInteger(0);

    try {
      ReflectionUtils.withFields().on(new DerivedType()).matching(new Filter<Field>() {
        @Override public boolean accept(final Field field) {
          int fieldModifiers = field.getModifiers();
          return ((Modifier.isPublic(fieldModifiers) || Modifier.isProtected(fieldModifiers))
            && !Modifier.isFinal(fieldModifiers) && !Modifier.isStatic(fieldModifiers));
        }
      }).call(new ReflectionUtils.FieldCallback() {
        @Override public void with(final Field field) {
          logDebug(field.getName());
          count.incrementAndGet();
        }
      }).throwing(new FieldNotFoundException());
    }
    finally {
      assertEquals(0, count.get());
    }
  }

  @Test
  public void testWithNullFields() {
    ReflectionUtils.withFields(null, null, null).call(new ReflectionUtils.FieldCallback() {
      @Override public void with(final Field field) {
        throw new NullPointerException("The Field must not be null!");
      }
    });
  }

  @Test
  public void testWithMethodsOnClass() {
    final Set<String> methods = new HashSet<String>(5);

    ReflectionUtils.withMethods().on(SuperType.class).matching(new Filter<Method>() {
      @Override public boolean accept(final Method method) {
        return SuperType.class.equals(method.getDeclaringClass());
      }
    }).call(new ReflectionUtils.MethodCallback() {
      @Override public void with(final Method method) {
        methods.add(String.format("%1$s(%2$d)", method.getName(), method.getParameterTypes().length));
      }
    }).throwing(new MethodNotFoundException());

    assertNotNull(methods);
    assertFalse(methods.isEmpty());
    assertEquals(5, methods.size());
    assertTrue(methods.containsAll(Arrays.asList("methodOne(0)", "methodTwo(1)", "methodThree(0)",
      "methodFour(1)", "methodFour(2)")));
  }

  @Test
  public void testWithMethodsOnObject() {
    final Set<String> methods = new HashSet<String>(10);

    ReflectionUtils.withMethods().on(new DerivedType()).matching(new Filter<Method>() {
      @Override public boolean accept(final Method method) {
        return (DerivedType.class.equals(method.getDeclaringClass()) && method.getName().startsWith("method"));
      }
    }).call(new ReflectionUtils.MethodCallback() {
      @Override public void with(final Method method) {
        logDebug(method.getName());
        methods.add(String.format("%1$s(%2$d)", method.getName(), method.getParameterTypes().length));
      }
    }).throwing(new MethodNotFoundException());

    assertNotNull(methods);
    assertFalse(methods.isEmpty());
    assertEquals(5, methods.size());
    assertTrue(methods.containsAll(Arrays.asList("methodFive(0)", "methodSix(1)", "methodSeven(0)", "methodEight(2)",
      "methodNine(1)")));
  }

  @Test
  public void testWithMethodTenOnObject() {
    final AtomicInteger count = new AtomicInteger(0);

    ReflectionUtils.withMethods().on(new DerivedType()).matching(new Filter<Method>() {
      @Override public boolean accept(final Method method) {
        return ("methodTen".equals(method.getName()));
      }
    }).call(new ReflectionUtils.MethodCallback() {
      @Override public void with(final Method method) {
        count.incrementAndGet();
      }
    });

    assertEquals(0, count.get());
  }

  @Test(expected = MethodNotFoundException.class)
  public void testWithNonMatchingMethodOnObjectThrowsMethodNotFoundException() {
    final AtomicInteger count = new AtomicInteger(0);

    try {
      ReflectionUtils.withMethods().on(new DerivedType()).matching(new Filter<Method>() {
        @Override public boolean accept(final Method obj) {
          return false;
        }
      }).call(new ReflectionUtils.MethodCallback() {
        @Override public void with(final Method member) {
          count.incrementAndGet();
        }
      }).throwing(new MethodNotFoundException());
    }
    finally {
      assertEquals(0, count.get());
    }
  }

  @Test
  public void testWithNullMethods() {
    ReflectionUtils.withMethods(null, null, null).call(new ReflectionUtils.MethodCallback() {
      @Override public void with(final Method method) {
        throw new NullPointerException("The Method must not be null!");
      }
    });
  }

  @SuppressWarnings("unused")
  protected static class SuperType {

    private static final long serialVersionUID = 42l;

    protected static String stringField;

    public static void methodOne() {
      METHOD_NAME.compareAndSet(null, "methodOne");
    }

    public static void methodTwo(final Object arg) {
      METHOD_NAME.compareAndSet(null, String.format("methodTwo(%1$s)", arg));
    }

    public static Object methodThree() {
      METHOD_NAME.compareAndSet(null, "methodThree():test");
      return "test";
    }

    public static String methodFour(final String name) {
      METHOD_NAME.compareAndSet(null, "methodFour");
      return String.format("Hello %1$s", name);
    }

    public static int methodFour(final int valueOne, final int valueTwo) {
      METHOD_NAME.compareAndSet(null, "methodFour");
      return (valueOne + valueTwo);
    }
  }

  @Resource
  @SuppressWarnings("unused")
  protected static class DerivedType extends SuperType {

    private Boolean booleanField;

    public final Integer magicNumber = 0xCAFEBABE;

    @Id
    private Long id = 1l;

    public void methodFive() {
      METHOD_NAME.compareAndSet(null, "methodFive");
    }

    public void methodSix(final Object arg) {
      METHOD_NAME.compareAndSet(null, String.format("methodSix(%1$s)", arg));
    }

    public Object methodSeven() {
      METHOD_NAME.compareAndSet(null, "methodSeven():string");
      return "string";
    }

    public String methodEight(final String left, final String right) {
      METHOD_NAME.compareAndSet(null, "methodEight");
      return (left + right);
    }

    public Object methodNine(final String message) {
      METHOD_NAME.compareAndSet(null, "methodNine");
      throw new IllegalArgumentException(message);
    }
  }

}
