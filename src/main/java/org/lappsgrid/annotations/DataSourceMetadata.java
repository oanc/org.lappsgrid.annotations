/*-
 * Copyright 2015 The Language Application Grid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.lappsgrid.annotations;

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
	 * A short human readable name for the service.
	 */
	String name() default "";

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
