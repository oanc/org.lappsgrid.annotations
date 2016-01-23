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
 * Specifies metadata that is common across multiple services that
 * share a common super class.
 * <p>
 * Due to the way annotations work in Java, a child class does not
 * inherit annotations from a super class in the same way it inherits
 * fields and methods.  Instead, if the child class includes the same
 * annotation as its super class the super class annotation is lost
 * completely; that is, values from the two annotations are <b>not</b>
 * merged as one might expect.
 * <p>
 * Therefore there are two annotations defined: one for the super class,
 * <tt>@CommonMetadata</tt>; and one for the child class,
 * <tt>@ServiceMetadata</tt>. These two annotation interfaces are exactly
 * the same.
 * <p>
 * <b>Note:</b> The <tt>@CommonMetadata</tt> annotation must appear on
 * the immediate super class of the service for the annotation processor
 * to find it.
 * <p>
 * Any time a discriminator URI is required the <i>short name</i> (as
 * defined in <a href="http://vocab.lappsgrid.org/discriminators.html">http://vocab.lappsgrid.org/discriminators.html</a>)
 * can be used. That is:
 * <pre>
 *    @CommonMetadata(requires="token")
 * </pre>
 * is equivalent to
 * <pre>
 *    @CommonMetadata(requires="http://vocab.lappsgrid.org/Token")
 * </pre>
 *
 * @author Keith Suderman
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CommonMetadata
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
	 * A brief description of the service.
	 */
	String description() default "";

	/**
	 *	The service's version. If no value is provided for the version
	 *	the annotation processors will first look for a file named VERSION
	 *	in the root directory of the project, then it will try to parse
	 *	the version from the pom.xml file.
	 */
	String version() default "";

	/**
	 * The URI of the organization providing the service.
	 */
	String vendor() default "";

	/**
	 * The allowable usages of the service.
	 */
	String allow() default "any";

	/**
	 * The software license for the service.
	 */
	String license() default "";

	/**
	 * Set <i>encoding</i> when the service requires and produces the
	 * same character encoding.
	 */
	String encoding() default "";

	/**
	 * Allows the required character encoding to be set separately from
	 * the character encoding that is produced.
	 */
	String requires_encoding() default "";

	/**
	 * Allows the character encoding produced by the service to be set
	 * separately from the required character encoding.
	 */
	String produces_encoding() default "";

	/**
	 * Sets the language that is required and produced by the service.
	 * The value(s) should be an ISO language code.
	 */
	String[] language() default {};

	/**
	 * Sets the languages the services requires. The value(s) should be
	 * valid ISO language codes.
	 */
	String[] requires_language() default {};

	/**
	 * Specify the language(s) the service produces. The value(s) should
	 * be valid ISO language codes.
	 */
	String[] produces_language() default {};

	/**
	 * Specifies the document formats required and produced by this
	 * service.
	 */
	String[] format() default {};

	/**
	 * The document format(s) required by this service.
	 */
	String[] requires_format() default {};

	/**
	 * THe document format(s) produced by this service.
	 */
	String[] produces_format() default {};

	/**
	 * The annotation types required by this service.
	 */
	String[] requires() default {};

	/**
	 * The annotation types produces by this service.
	 */
	String[] produces() default {};
}
