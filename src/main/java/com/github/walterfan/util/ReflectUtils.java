package com.github.walterfan.util;

import java.lang.reflect.Method;

/**
 * The Class ReflectUtils.
 * @author Walter
 */
public final class ReflectUtils {
    
    private ReflectUtils() {
        
    }
    
    /**
     * Creates the bean.
     *
     * @param className the class name
     * @param factoryMethod the factory method
     * @return the object
     * @throws Exception the exception
     */
    public static Object createBean(String className, String factoryMethod)
            throws Exception {
        Class<?> cls = Class.forName(className);
        if (null == factoryMethod) {
            return cls.newInstance();
        }
        Method method = cls.getMethod(factoryMethod, new Class[0]);
        return method.invoke(cls, new Object[0]);
    }

    /**
     * Creates the object by name.
     *
     * @param className the class name
     * @return the object
     * @throws Exception the exception
     */
    public static Object createObjectByName(String className) throws Exception {
        return createBean(className, null);
    }

    /**
     * Creates the method by name.
     *
     * @param theObj the the obj
     * @param methodName the method name
     * @param parameters the parameters
     * @return the method
     */
    public static Method createMethodByName(Object theObj, String methodName,
            Object[] parameters) {
        Class[] types = null;
        if (null != parameters && parameters.length > 0) {
            types = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                types[i] = parameters[i].getClass();
            }
        }

        try {
            return theObj.getClass().getDeclaredMethod(methodName, types);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
