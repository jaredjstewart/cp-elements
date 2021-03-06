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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import org.cp.elements.lang.reflect.ConstructorNotFoundException;
import org.cp.elements.lang.reflect.FieldNotFoundException;
import org.cp.elements.lang.reflect.MethodNotFoundException;
import org.cp.elements.util.ArrayUtils;

/**
 * The ClassUtils class provides utility methods for working with {@link Class} objects.
 *
 * @author John J. Blum
 * @see java.lang.Class
 * @see java.lang.Object
 * @see java.lang.annotation.Annotation
 * @see java.lang.reflect.AnnotatedElement
 * @see java.lang.reflect.Constructor
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class ClassUtils {

  protected static final boolean DEFAULT_INITIALIZE_LOADED_CLASS = true;

  public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

  public static final String CLASS_FILE_EXTENSION = ".class";

  /**
   * Determines whether a given Class type is assignable to a declared Class type.  A given Class type is assignable to
   * a declared Class type if it is in the same Class type hierarchy, i.e. the given Class type is a subclass,
   * or sub-interface of the declared Class type.  Null is also assignable to the declared (to) Class type.
   * 
   * @param fromType the Class type evaluated for assignment compatibility with the declared Class type.
   * @param toType the declared Class type defining the type hierarchy, or bounds on the given Class type for
   * assignment compatibility.
   * @return a boolean value indicating given Class type is assignable to the declared Class type.
   * @see java.lang.Class#isAssignableFrom(Class)
   */
  @NullSafe
  public static boolean assignableTo(Class<?> fromType, Class<?> toType) {
    return (toType != null && (fromType == null || toType.isAssignableFrom(fromType)));
  }

  /**
   * Get the Class type of the specified Object.  Returns null if the Object reference is null.
   * 
   * @param obj the Object who's Class type is being determined.
   * @return a Class object signifying the type of the specified Object.
   * @see java.lang.Object#getClass()
   */
  @NullSafe
  public static Class<?> getClass(Object obj) {
    return (obj != null ? obj.getClass() : null);
  }

  /**
   * Gets the fully-qualified name of the Class type for the specified Object.  Returns null if the Object reference
   * is null.
   * 
   * @param obj the Object who's class name is determined.
   * @return a String value specifying the fully qualified class name of the Object.
   * @see java.lang.Class#getName()
   * @see java.lang.Object#getClass()
   */
  @NullSafe
  public static String getClassName(Object obj) {
    return (obj != null ? obj.getClass().getName() : null);
  }

  /**
   * Gets the unqualified, simple name of the Class type for the specified Object.  Returns null if the Object reference
   * is null.
   * 
   * @param obj the Object who's simple class name is determined.
   * @return a String value indicating the simple class name of the Object.
   * @see java.lang.Class#getSimpleName()
   * @see java.lang.Object#getClass()
   */
  @NullSafe
  public static String getClassSimpleName(Object obj) {
    return (obj != null ? obj.getClass().getSimpleName() : null);
  }

  /**
   * Gets the resource name of the given {@link Class} type.  The resource name of a given {@link Class} is
   * the pathname of the class file defining the {@link Class} type relative to the CLASSPATH.
   *
   * For instance, if the {@link Class} type were java.lang.Object.class, then the resource name would be...
   *
   * <code>
   *   java/lang/Object.class.
   * </code>
   *
   * @param type the {@link Class} type from which to construct the resource name.
   * @return a String indicating the resource name of the given {@link Class} type.
   * @see java.lang.Class
   */
  @NullSafe
  public static String getResourceName(Class type) {
    return (type != null ? type.getName().replaceAll("\\.", "/").concat(CLASS_FILE_EXTENSION) : null);
  }

  /**
   * Attempts to find a compatible constructor on the given class type with a signature having parameter types
   * satisfying the specified arguments.
   *
   * @param <T> the generic class type to search for the constructor.
   * @param type the Class type to search for the desired constructor.
   * @param arguments an array of Object arguments used to match the constructor's signature.
   * @return a Constructor from the given class type whose signature matches the specified arguments.
   * @see java.lang.Class
   * @see java.lang.Class#getDeclaredConstructors()
   * @see java.lang.reflect.Constructor
   */
  @SuppressWarnings({ "unchecked", "all" })
  public static <T> Constructor<T> findConstructor(Class<T> type, Object... arguments) {
    for (Constructor<?> constructor : type.getDeclaredConstructors()) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();

      if (ArrayUtils.nullSafeLength(arguments) == parameterTypes.length) {
        boolean match = true;

        for (int index = 0; match && index < parameterTypes.length; index++) {
          match &= instanceOf(arguments[index], parameterTypes[index]);
        }

        if (match) {
          return (Constructor<T>) constructor;
        }
      }
    }

    return null;
  }

  /**
   * Gets the constructor with the specified signature from the given class type.
   *
   * @param <T> the generic class type from which to get the constructor.
   * @param type the Class type from which to get the Constructor.
   * @param parameterTypes an array of class types indicating the constructor signature.
   * @return a Constructor from the given class type with a matching signature.
   * @see java.lang.Class
   * @see java.lang.Class#getDeclaredConstructor(Class[])
   * @see java.lang.reflect.Constructor
   */
  public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
    try {
      return type.getDeclaredConstructor(parameterTypes);
    }
    catch (NoSuchMethodException e) {
      throw new ConstructorNotFoundException(e);
    }
  }

  /**
   * Attempts to resolve the constructor from the given class type based on the constructor's exact signature,
   * otherwise finds a constructor who's signature parameter types satisfy the array of Object arguments.
   *
   * @param <T> the generic class type from which to resolve the constructor.
   * @param type the Class type from which to resolve the constructor.
   * @param parameterTypes an array of Class types indicating the desired constructor's signature.
   * @param arguments an array of Object arguments used to match the constructor's signature.
   * @return a Constructor from the given class type who's signature either matches the parameter types
   * or satisfies the array of arguments.
   * @see #getConstructor(Class, Class[])
   * @see #findConstructor(Class, Object...)
   * @see java.lang.Class
   * @see java.lang.reflect.Constructor
   */
  public static <T> Constructor<T> resolveConstructor(Class<T> type, Class<?>[] parameterTypes, Object... arguments) {
    try {
      return getConstructor(type, parameterTypes);
    }
    catch (ConstructorNotFoundException e) {
      Constructor<T> constructor = findConstructor(type, arguments);

      Assert.notNull(constructor, new ConstructorNotFoundException(String.format(
        "Failed to resolve constructor with signature [%1$s] on class type [%2$s]",
          getMethodSignature(getSimpleName(type), parameterTypes, Void.class), getName(type)), e.getCause()));

      return constructor;
    }
  }

  /**
   * Gets a Field object representing the named field on the specified class.  This method will recursively search
   * up the class hierarchy of the specified class until the Object class is reached.  If the named field is found
   * then a Field object representing the class field is returned, otherwise a NoSuchFieldException is thrown.
   * 
   * @param type the Class type to search for the specified field.
   * @param fieldName a String indicating the name of the field on the class.
   * @return a Field object representing the named field on the specified class.
   * @throws FieldNotFoundException if the named field does not exist on the specified class
   * or a superclass of the specified class.
   * @see java.lang.Class
   * @see java.lang.Class#getDeclaredField(String)
   * @see java.lang.reflect.Field
   */
  public static Field getField(Class<?> type, String fieldName) {
    try {
      return type.getDeclaredField(fieldName);
    }
    catch (NoSuchFieldException e) {
      if (type.getSuperclass() != null) {
        return getField(type.getSuperclass(), fieldName);
      }

      throw new FieldNotFoundException(e);
    }
  }

  /**
   * Attempts to find a method with the specified name on the given class type having a signature with parameter types
   * that are compatible with the given arguments.  This method searches recursively up the inherited class hierarchy
   * for the given class type until the desired method is found or the class type hierarchy is exhausted, in which case,
   * null is returned.
   *
   * @param type the Class type to search for the desired method.
   * @param methodName a String value indicating the name of the method to find.
   * @param arguments an array of object values indicating the arguments the method's parameters must accept.
   * @return a Method on the given class type with the specified name having a signature compatible with the arguments,
   * or null if no such Method exists on the given class type or one of it's inherited (parent) class types.
   * @throws NullPointerException if the given class type is null.
   * @see java.lang.Class
   * @see java.lang.Class#getDeclaredMethods()
   * @see java.lang.Class#getSuperclass()
   * @see java.lang.reflect.Method
   */
  @SuppressWarnings("all")
  public static Method findMethod(Class<?> type, String methodName, Object... arguments) {
    for (Method method : type.getDeclaredMethods()) {
      if (method.getName().equals(methodName)) {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (ArrayUtils.nullSafeLength(arguments) == parameterTypes.length) {
          boolean match = true;

          for (int index = 0; match && index < parameterTypes.length; index++) {
            match &= instanceOf(arguments[index], parameterTypes[index]);
          }

          if (match) {
            return method;
          }
        }
      }
    }

    return (type.getSuperclass() != null ? findMethod(type.getSuperclass(), methodName, arguments) : null);
  }

  /**
   * Gets a Method object representing the named method on the specified class.  This method will recursively search
   * up the class hierarchy of the specified class until the Object class is reached.  If the named method is found
   * then a Method object representing the class method is returned, otherwise a NoSuchMethodException is thrown.
   * 
   * @param type the Class type to search for the specified method.
   * @param methodName a String indicating the name of the method on the class.
   * @param parameterTypes an array of Class objects identifying the parameters and their types
   * based on the method's signature.
   * @return a Method object representing the named method on the specified class.
   * @throws MethodNotFoundException if the named method does not exist on the specified class
   * or a superclass of the specified class.
   * @see java.lang.Class
   * @see java.lang.Class#getDeclaredMethod(String, Class[])
   * @see java.lang.reflect.Method
   */
  public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
    try {
      return type.getDeclaredMethod(methodName, parameterTypes);
    }
    catch (NoSuchMethodException e) {
      if (type.getSuperclass() != null) {
        return getMethod(type.getSuperclass(), methodName, parameterTypes);
      }

      throw new MethodNotFoundException(e);
    }
  }

  /**
   * Attempts to resolve the method with the specified name and signature on the given class type.  The named method's
   * resolution is first attempted by using the specified method's name along with the array of parameter types.
   * If unsuccessful, the method proceeds to lookup the named method by searching all "declared" methods
   * of the class type having a signature compatible with the given argument types.  This method operates recursively
   * until the method is resolved or the class type hierarchy is exhausted, in which case,
   * a MethodNotFoundException is thrown.
   *
   * @param type the Class type on which to resolve the method.
   * @param methodName a String indicating the name of the method to resolve.
   * @param parameterTypes an array of Class objects used to resolve the exact signature of the method.
   * @param arguments an array of Objects used in a method invocation serving as a fallback search/lookup strategy
   * if the method cannot be resolved using it's parameter types.  Maybe null.
   * @param returnType the declared class type of the method's return value (used only for Exception message purposes).
   * @return the resolved method from the given class type given the name, parameter types (signature)
   * and calling arguments, if any.
   * @throws MethodNotFoundException if the specified method cannot be resolved on the given class type.
   * @throws NullPointerException if the class type is null.
   * @see #getMethod(Class, String, Class[])
   * @see #findMethod(Class, String, Object...)
   * @see java.lang.Class
   * @see java.lang.reflect.Method
   */
  public static Method resolveMethod(Class<?> type, String methodName, Class<?>[] parameterTypes, Object[] arguments,
      Class<?> returnType) {

    try {
      return getMethod(type, methodName, parameterTypes);
    }
    catch (MethodNotFoundException e) {
      Method method = findMethod(type, methodName, arguments);

      Assert.notNull(method, new MethodNotFoundException(String.format(
        "Failed to resolve method with signature [%1$s] on class type [%2$s]",
          getMethodSignature(methodName, parameterTypes, returnType), getName(type)), e.getCause()));

      return method;
    }
  }

  /**
   * Builds the signature of a method based on a java.lang.reflect.Method object.
   *
   * @param method the Method object to build an method signature of.
   * @return the signature of the Method as a String.
   * @see #getMethodSignature(String, Class[], Class)
   */
  protected static String getMethodSignature(Method method) {
    return getMethodSignature(method.getName(), method.getParameterTypes(), method.getReturnType());
  }

  /**
   * Builds the signature of a method based on the method's name, parameter types and return type.
   *
   * @param methodName a String indicating the name of the method.
   * @param parameterTypes an array of Class objects indicating the type of each method parameter.
   * @param returnType a Class object indicating the methods return type.
   * @return the signature of the method as a String.
   * @see #getSimpleName(Class)
   */
  protected static String getMethodSignature(String methodName, Class<?>[] parameterTypes, Class<?> returnType) {
    StringBuilder buffer = new StringBuilder(methodName);

    buffer.append("(");

    if (parameterTypes != null) {
      int index = 0;

      for (Class<?> parameterType : parameterTypes) {
        buffer.append(index++ > 0 ? ", :" : ":");
        buffer.append(getSimpleName(parameterType));
      }
    }

    buffer.append("):");
    buffer.append(returnType == null || Void.class.equals(returnType) ? "void" : getSimpleName(returnType));

    return buffer.toString();
  }

  /**
   * Gets the fully-qualified name of the Class.
   * 
   * @param type the Class type to return the fully-qualified name of.
   * @return a String value with the fully-qualified name of the Class.
   * @see java.lang.Class#getName()
   */
  @NullSafe
  public static String getName(Class type) {
    return (type != null ? type.getName() : null);
  }

  /**
   * Gets the simple name of the Class.
   * 
   * @param type the Class type to return the simple name of.
   * @return a String value with the simple name of the Class.
   * @see java.lang.Class#getSimpleName()
   */
  @NullSafe
  public static String getSimpleName(Class type) {
    return (type != null ? type.getSimpleName() : null);
  }

  /**
   * Determines whether the given Object is an instance of the specified Class.  Note, an Object cannot be an
   * instance of null, so this method returns false if the Class type is null or the Object is null.
   * 
   * @param obj the Object to test as an instance of the specified Class type.
   * @param type the Class type used in the instanceof operation.
   * @return a boolean value indicating whether the Object is an instance of the Class type.
   * @see java.lang.Class#isInstance(Object)
   */
  @NullSafe
  public static boolean instanceOf(Object obj, Class<?> type) {
    return (type != null && type.isInstance(obj));
  }

  /**
   * Determines whether the specified Class object represents an annotation type.
   *
   * @param type the Class object tested as an annotation type.
   * @return true iff the Class object is not null and represents an annotation type.
   * @see java.lang.Class#isAnnotation()
   */
  @NullSafe
  public static boolean isAnnotation(Class type) {
    return (type != null && type.isAnnotation());
  }

  /**
   * Determines whether the specified Annotation meta-data is present on the given "annotated" members,
   * such as fields and methods.
   *
   * @param annotation the Annotation used in the detection for presence on the given members.
   * @param members the members of a class type or object to inspect for the presence of the specified Annotation.
   * @return a boolean value indicating whether the specified Annotation is present on any of the given members.
   * @see java.lang.annotation.Annotation
   * @see java.lang.reflect.AccessibleObject#isAnnotationPresent(Class)
   */
  @NullSafe
  public static boolean isAnnotationPresent(Class<? extends Annotation> annotation, AnnotatedElement... members) {
    for (AnnotatedElement member : ArrayUtils.nullSafeArray(members, AnnotatedElement.class)) {
      if (member != null && member.isAnnotationPresent(annotation)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Determines whether the specified Class object represents an array type.
   * 
   * @param type the Class object tested as an array type.
   * @return true iff the Class object is not null and represents an array type.
   * @see java.lang.Class#isArray()
   */
  @NullSafe
  public static boolean isArray(Class type) {
    return (type != null && type.isArray());
  }

  /**
   * Determines whether the specified Class object represents an actual class, and not an Annotation, Array, Enum,
   * Interface or Primitive type.
   * 
   * @param type the Class object tested as an actual class.
   * @return true iff the Class object is not null and represents an actual class.
   */
  @NullSafe
  public static boolean isClass(Class type) {
    return (type != null && !(type.isAnnotation() || type.isArray() || type.isEnum() || type.isInterface()
      || type.isPrimitive()));
  }

  /**
   * Determines whether the specified Class object represents an enum type.
   * 
   * @param type the Class object tested as an enum type.
   * @return true iff the Class object is not null and represents an enum type.
   * @see java.lang.Class#isEnum()
   */
  @NullSafe
  public static boolean isEnum(Class type) {
    return (type != null && type.isEnum());
  }

  /**
   * Determines whether the specified Class object represents an interface.
   * 
   * @param type the Class object tested as an interface.
   * @return true iff the Class object is not null and represents an interface.
   * @see java.lang.Class#isInterface()
   */
  @NullSafe
  public static boolean isInterface(Class type) {
    return (type != null && type.isInterface());
  }

  /**
   * Determines whether the specified class identified by name is available and present on the application classpath.
   *
   * @param className the fully qualified name of the class to determine the presence of.
   * @return a boolean value indicating whether the class identified by name is in the classpath.
   * @see #loadClass(String)
   */
  public static boolean isPresent(String className) {
    try {
      return (loadClass(className) != null);
    }
    catch (TypeNotFoundException ignore) {
      return false;
    }
  }

  /**
   * Determines whether the specified Class object represents a primitive type.
   *
   * @param type the Class object tested as a primitive type.
   * @return true iff the Class object is not null and represents a primitive type.
   * @see java.lang.Class#isPrimitive()
   */
  @NullSafe
  public static boolean isPrimitive(Class type) {
    return (type != null && type.isPrimitive());
  }

  /**
   * Loads the Class object for the specified, fully qualified class name using the current Thread's context ClassLoader,
   * following by initializing the class.
   * 
   * @param fullyQualifiedClassName a String value indicating the fully qualified class name of the Class to load.
   * @return a Class object for the specified, fully-qualified class name.
   * @throws TypeNotFoundException if the Class identified by the fully qualified class name could not be found.
   * @see #loadClass(String, boolean, ClassLoader)
   * @see java.lang.Thread#currentThread()
   * @see java.lang.Thread#getContextClassLoader()
   */
  public static Class loadClass(String fullyQualifiedClassName) {
    return loadClass(fullyQualifiedClassName, DEFAULT_INITIALIZE_LOADED_CLASS,
      Thread.currentThread().getContextClassLoader());
  }

  /**
   * Loads the Class object for the specified, fully qualified class name using the provided ClassLoader and the option
   * to initialize the class (calling any static initializers) once loaded.
   * 
   * @param fullyQualifiedClassName a String indicating the fully qualified class name of the Class to load.
   * @param initialize a boolean value indicating whether to initialize the class after loading.
   * @param classLoader the ClassLoader used to load the class.
   * @return a Class object for the specified, fully-qualified class name.
   * @throws TypeNotFoundException if the Class identified by the fully qualified class name could not be found.
   * @see java.lang.Class#forName(String, boolean, ClassLoader)
   */
  @SuppressWarnings("all")
  public static Class loadClass(String fullyQualifiedClassName, boolean initialize, ClassLoader classLoader) {
    try {
      return Class.forName(fullyQualifiedClassName, initialize, classLoader);
    }
    catch (ClassNotFoundException e) {
      throw new TypeNotFoundException(String.format("Class [%s] was not found", fullyQualifiedClassName), e);
    }
    catch (NoClassDefFoundError err) {
      throw new TypeNotFoundException(String.format("Class [%s] was not found", fullyQualifiedClassName), err);
    }
  }

  /**
   * Locates the class file resource given the binary name of the {@link Class}.
   *
   * @param binaryName a String with the binary name of the {@link Class} that's class file resource will be located.
   * @return a {@link URL} with the location of the class file resource containing the {@link Class} definition
   * for the given binary name.
   * @see #locateClass(String, ClassLoader)
   * @see java.net.URL
   */
  public static URL locateClass(String binaryName) {
    return locateClass(binaryName, Thread.currentThread().getContextClassLoader());
  }

  /**
   * Locates the class file resource given the binary name of the {@link Class}.  The {@link ClassLoader} used
   * to search class file resource for the {@link Class} with the given binary name.
   *
   * @param binaryName a String with the binary name of the {@link Class} that's class file resource will be located.
   * @param classLoader the {@link ClassLoader} used to locate the class file resource for the {@link Class}
   * with the given binary name.
   * @return a {@link URL} with the location of the class file resource containing the {@link Class} definition
   * for the given binary name.
   * @see java.net.URL
   */
  public static URL locateClass(String binaryName, ClassLoader classLoader) {
    try {
      Class<?> type = loadClass(binaryName, false, classLoader);
      return type.getClassLoader().getResource(getResourceName(type));
    }
    catch (TypeNotFoundException ignore) {
      return null;
    }
  }

  /**
   * Determines whether the Object is an instance of any of the Class types and returns false if it is.
   * 
   * @param obj the Object of the instanceof comparison.
   * @param types an array of Class types used in the instanceof comparison.
   * @return a true boolean value iff the Object is not an instance of any of the Class types.
   * @see #instanceOf(Object, Class)
   */
  @NullSafe
  @SuppressWarnings("all")
  public static boolean notInstanceOf(Object obj, Class... types) {
    boolean result = true;

    for (int index = 0; result && index < ArrayUtils.nullSafeLength(types); index++) {
      result &= !instanceOf(obj, types[index]);
    }

    return result;
  }
}
