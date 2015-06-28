package com.github.walterfan.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {
	public int id() default -1;
	public String name();
	public String description() default "";
	public String preCondition() default "";
	public String standardFlow()  default "";
	public String alternateFlow() default "";
	public String postCondition() default "";
}
