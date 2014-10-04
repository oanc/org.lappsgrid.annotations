package org.lappsgrid.experimental.annotations;

import java.lang.annotation.*;

/**
 * @author Keith Suderman
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DataSourceMetadata
{
	String schema() default "";
	String description() default "";
	String version() default "";
	String vendor() default "";
	String allow() default "any";
	String license() default "";
	String encoding() default "";
	String[] language() default {};
	String[] format() default {};
}
