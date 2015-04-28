package com.github.walterfan.service;

/**
 * The Interface IMethodInvoker.
 * @author walter
 */
public interface IMethodInvoker {
    
    /**
     * Equal.
     *
     * @param beanName the bean name
     * @param methodName the method name
     * @return true, if successful
     */
    boolean equal(String beanName, String methodName);
    
    /**
     * Invoke.
     *
     * @param beanName the bean name
     * @param methodName the method name
     * @param parameters the parameters
     * @return the object
     * @throws Exception the exception
     */
    Object invoke(String beanName, String methodName, Object[] parameters) throws Exception;
}
