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
	/**
	 * The value of the $schema element.  The annotation processor
	 * will provide an appropriate default if the value is not
	 * explicitly defined.
	 */
	String schema() default "";

	/**
	 * A brief description of the data source.
	 */
	String description() default "";

	/**
	 *	The data source's version. If no value is provided for the version
	 *	the annotation processors will first look for a file named VERSION
	 *	in the root directory of the project, then it will try to parse
	 *	the version from the pom.xml file.
	 */
	String version() default "";

	/**
	 * The URI of the organization providing the data source.
	 */
	String vendor() default "";

	/**
	 * The allowable usages of the data source.
	 */
	String allow() default "any";

	/**
	 * The software license for the data source.
	 */
	String license() default "";

	/**
	 * The character encoding used by documents returned by the data source.
	 */
	String encoding() default "";

	/**
	 * Sets the language of the documents returned by the data source.
	 */
	String[] language() default {};

	/**
	 * Specifies the document format returned by this data source.
	 */
	String[] format() default {};
}
