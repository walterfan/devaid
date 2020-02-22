package com.github.walterfan.util.annotation;

/**
 * Created by yafan on 4/7/2017.
 */

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
/*
** Annotation for test case
*/
public @interface TestCase {
    String name() default "";

}