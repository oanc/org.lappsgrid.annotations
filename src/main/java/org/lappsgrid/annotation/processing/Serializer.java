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
package org.lappsgrid.annotation.processing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * @author Keith Suderman
 */
class Serializer {
	private static ObjectMapper mapper;
//	private static ObjectMapper prettyPrinter;

	static {
//		mapper = new ObjectMapper();
//		mapper.disable(SerializationFeature.INDENT_OUTPUT);
//		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	private Serializer() {}

	public static <T> T parse(String json, Class<T> theClass) throws IOException
	{
		return (T) mapper.readValue(json, theClass);
	}

	public static String toJson(Object object) throws IOException
	{
		return mapper.writeValueAsString(object);
	}

//	public static String toPrettyJson(Object object) throws IOException
//	{
//		return prettyPrinter.writeValueAsString(object);
//	}
}
