package com.github.walterfan.util.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Bug {
	String id() default "";
	String description() default "";
	String openDate() default "";
	String closeDate() default "";
	String reporter() default "";
	String solver() default "";
	Severity severity() default Severity.Moderate; 
}
