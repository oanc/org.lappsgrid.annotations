package org.lappsgrid.experimental.annotations;

import java.lang.annotation.*;

/**
 * @author Keith Suderman
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceMetadata
{
	String schema() default "";
   String description() default "";
   String version() default "";
   String vendor() default "";
   String allow() default "any";
   String license() default "";
   String encoding() default "";
   String requires_encoding() default "";
   String produces_encoding() default "";
   String[] language() default {};
   String[] requires_language() default {};
   String[] produces_language() default {};
   String[] format() default {};
   String[] requires_format() default {};
   String[] produces_format() default {};

   String[] requires() default {};
   String[] produces() default {};
}
