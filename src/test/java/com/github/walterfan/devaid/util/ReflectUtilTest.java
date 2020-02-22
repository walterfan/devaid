package com.github.walterfan.devaid.util;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;

import static org.testng.Assert.assertNotNull;


class JavaBeanReflectionExample {
    public JavaBeanReflectionExample() {
        System.out.println("Default Constructor Called");
    }

    public JavaBeanReflectionExample(int i) {
        System.out.println("One Parameter Constructor Called");
    }


}

@Slf4j
public class ReflectUtilTest {
    @Test
    public void testConstruct() throws Exception {
        JavaBeanReflectionExample reflectionExample = new JavaBeanReflectionExample();
        // Get all the constructors
        Constructor cons[] = reflectionExample.getClass().getDeclaredConstructors();
        for (Constructor constructor : cons) {
            log.info("constructor: {}", constructor.getParameterCount());
        }


        JavaBeanReflectionExample exam1 = (JavaBeanReflectionExample) cons[0].newInstance();
        assertNotNull(exam1);

        JavaBeanReflectionExample exam2 = (JavaBeanReflectionExample) cons[1].newInstance(9);
        assertNotNull(exam2);
    }
}
