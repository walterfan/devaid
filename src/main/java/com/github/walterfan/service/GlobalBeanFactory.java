package com.github.walterfan.service;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.walterfan.util.ReflectUtils;


/**
 * A factory for creating GlobalBean objects.
 *
 * @author walter
 */
public final class GlobalBeanFactory {

    /** The logger. */
    private static Log logger = LogFactory.getLog(GlobalBeanFactory.class);

    /** The bean factory. */
    private BeanFactory beanFactory;

    /** The xml config files. */
    private static String[] xmlConfigFiles = null;

    /** The instance. */
    private static GlobalBeanFactory instance;

    private IMethodInvoker methodInvoker;
    /** The lock. */
    private static Object lock = new Object();

    /**
     * Instantiates a new global bean factory.
     */
    private GlobalBeanFactory() {
        beanFactory = new ClassPathXmlApplicationContext(xmlConfigFiles);
    }

    /**
     * Gets the single instance of GlobalBeanFactory.
     *
     * @return single instance of GlobalBeanFactory
     */
    public static synchronized GlobalBeanFactory getInstance() {
        if (null == getXmlConfigFiles()) {
            throw new RuntimeException("Please setXmlConfigFiles firstly");
        }

        if (null == instance) {
            instance = new GlobalBeanFactory();
        }

        return instance;
    }

    /**
     * Sets the xml config files.
     *
     * @param arrXml the new xml config files
     */
    public static void setXmlConfigFiles(String[] arrXml) {
        synchronized (lock) {
            xmlConfigFiles = arrXml;
        }
    }

    /**
     * Gets the xml config files.
     *
     * @return the xml config files
     */
    public static String[] getXmlConfigFiles() {
        synchronized (lock) {
            return xmlConfigFiles;
        }
    }

    /**
     * Sets the bean factory.
     *
     * @param aBeanFactory the new bean factory
     */
    public void setBeanFactory(BeanFactory aBeanFactory) {
        beanFactory = aBeanFactory;
    }

    /**
     * Gets the bean.
     *
     * @param name the name
     * @return the bean
     */
    public Object getBean(String name) {
        if (null == beanFactory) {
            return null;
        }
        return beanFactory.getBean(name);
    }

    /**
     * Invoke.
     *
     * @param beanName the bean name
     * @param methodName the method name
     * @param parameters the parameters
     * @return the object
     * @throws Exception the exception
     */
    public Object invoke(String beanName, String methodName, Object[] parameters) throws Exception {
        Object bean = getBean(beanName);
        if (null == bean) {
            return null;
        }
        Method method = ReflectUtils.createMethodByName(bean, methodName, parameters);
        return method.invoke(bean, parameters);
    }

    /**
     * Dispatch.
     *
     * @param beanName the bean name
     * @param methodName the method name
     * @param parameters the parameters
     * @return the object
     * @throws Exception the exception
     */
    public Object dispatch(String beanName, String methodName, Object[] parameters) throws Exception {
        Object bean = getBean(beanName);
        if (null == bean) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("call " + beanName + "." + methodName + "-->" + bean);
        }
        
        
        if (null != this.methodInvoker) {
            if (this.methodInvoker.equal(beanName, methodName)) {
                return this.methodInvoker.invoke(beanName, methodName, parameters);
            }
            
        }
        // throw new RuntimeException("Cannot find " + beanName + "."+
        // methodName);
        Method method = ReflectUtils.createMethodByName(bean, methodName, parameters);
        return method.invoke(bean, parameters);
    }
}
