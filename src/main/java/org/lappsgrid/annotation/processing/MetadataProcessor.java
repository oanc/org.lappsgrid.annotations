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

import org.lappsgrid.discriminator.Discriminator;
import org.lappsgrid.discriminator.DiscriminatorRegistry;
import org.lappsgrid.annotations.CommonMetadata;
import org.lappsgrid.annotations.DataSourceMetadata;
import org.lappsgrid.annotations.ServiceMetadata;
import org.lappsgrid.metadata.IOSpecification;
import org.xml.sax.InputSource;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//import org.lappsgrid.metadata.ContentType;

/**
 * @author Keith Suderman
 */
//@SupportedAnnotationTypes({"org.lappsgrid.experimental.annotations.ServiceMetadata",
//		  "org.lappsgrid.experimental.annotations.DataSourceMetadata"})
@SupportedAnnotationTypes({"org.lappsgrid.annotations.ServiceMetadata",
		  "org.lappsgrid.annotations.DataSourceMetadata"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MetadataProcessor extends AbstractProcessor implements Processor
{
//   private Properties defaults = new Properties();

	public MetadataProcessor()
	{
		System.out.println("Creating MetadataProcessor");
	}

	private void log(String message)
	{
		System.out.println(message);
//		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
	}

	private void debug(String message)
	{
		System.out.println(message);
	}

	private String toString(String[] list)
	{
		if (list == null)
		{
			return "";
		}
		if (list.length == 0)
		{
			return "[]";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("[ ");
		int i = 0;
		if (i < list.length)
		{
			buffer.append(list[i++]);
		}
		while (i < list.length)
		{
			buffer.append(", ");
			buffer.append(list[i++]);
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void log(CombinedMetadata meta)
	{
		debug("Dumping combined metadata.");
		debug("Vendor: " + meta.vendor());
		debug("Version: " + meta.version());
		debug("Description: " + meta.description());
		debug("Encoding: " + meta.inputEncoding() + " -> " + meta.outputEncoding());
		debug("Allow: " + meta.allow());
		debug("Format: " + meta.inputFormat() + " -> " + meta.outputFormat());
		debug("Language: " + meta.inputLanguage() + " -> " + meta.outputLanguage());
		debug("Produces: " + toString(meta.produces()));
		debug("Requires: " + toString(meta.requires()));
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
								  RoundEnvironment roundEnv)
	{
      log("Running the MetaData annotation processor.");
		File root = new File("src/main/resources/metadata");
		if (!root.exists())
		{
			if (!root.mkdirs())
			{
				log("Could not create directory " + root.getPath());
				log("ServiceMetadata files will not be generated.");
				return false;
			}
		}
		processServiceMetadata(root, annotations, roundEnv);
		processDataSourceMetadata(root, annotations, roundEnv);
		return false;
	}

	protected void processServiceMetadata(File root, Set<? extends TypeElement> annotations,
													  RoundEnvironment roundEnv)
	{
		log("processServiceMetadata " + root.getPath());
		for (Element elem : roundEnv.getElementsAnnotatedWith(ServiceMetadata.class))
		{
			if (elem.getKind() != ElementKind.CLASS || elem.getModifiers().contains(Modifier.ABSTRACT))
			{
				// We are only interested in concrete classes. The Metadata annotation
				// can only be applied to Types (classes), but we only generate the
				// metadata file for non-abstract classes.
				debug("Skipping " + elem.getSimpleName());
				continue;
			}
			TypeElement type = (TypeElement) elem;
			String className = type.getQualifiedName().toString();
//         TypeMirror parent = type.getSuperclass();
//         ServiceMetadata parentMetadata = parent.getClass().getAnnotation(ServiceMetadata.class);
			CommonMetadata common = type.getAnnotation(CommonMetadata.class);
			ServiceMetadata metadata = type.getAnnotation(ServiceMetadata.class);
			if (common != null)
			{
				debug("parent class has metadata: " + common.toString());
			}
			if (metadata != null)
			{
				debug("this class has metadata: " + className);
				CombinedMetadata combined = new CombinedMetadata(common, metadata);
				File file = new File(root, className + ".json");
				debug("Generating ServiceMetadata for " + className);
				try
				{
					writeMetadata(file, className, combined);
				}
				catch (IOException e)
				{
					log(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void processDataSourceMetadata(File root, Set<? extends TypeElement> annotations,
													  RoundEnvironment roundEnv)
	{
		log("Running the DataSourceMetadataProcessor");
		for (Element elem : roundEnv.getElementsAnnotatedWith(DataSourceMetadata.class))
		{
			if (elem.getKind() != ElementKind.CLASS || elem.getModifiers().contains(Modifier.ABSTRACT))
			{
				// We are only interested in concrete classes. The Metadata annotation
				// can only be applied to Types (classes), but we only generate the
				// metadata file for non-abstract classes.
				debug("Skipping " + elem.getSimpleName());
				continue;
			}
			TypeElement type = (TypeElement) elem;
			String className = type.getQualifiedName().toString();
			TypeMirror parent = type.getSuperclass();
			DataSourceMetadata metadata = type.getAnnotation(DataSourceMetadata.class);
			File file = new File(root, className + ".json");
			debug("Generating DataSourceMetadata for " + className);
			try
			{
				writeDataSourceMetadata(file, className, metadata);
			}
			catch (IOException e)
			{
				log(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private String getContentType(String name)
	{
		Discriminator d = DiscriminatorRegistry.getByName(name);
		if (d == null)
		{
			return null;
		}
		return d.getUri();
	}

	private String get(String string)
	{
		if (string == null || string.length() == 0)
		{
			return null;
		}
		return string;
	}

	private String getValue(String key)
	{
		if (key == null || key.length() == 0)
		{
			return null;
		}
		if (key.startsWith("http"))
		{
			return key;
		}
		Discriminator discriminator = DiscriminatorRegistry.getByName(key);
		if (discriminator != null)
		{
			return discriminator.getUri();
		}

		return key;
	}

	/**
	 * Attempts to find the version number.  If a version number
	 * was supplied in the annotation that value is used. Otherwise the
	 * processor looks for a file named VERSION in the project root directory.
	 * If a VERSION file can not be found, or can not be parsed, the processor
	 * attempts to parse the version from the pom file.
	 * <p/>
	 * Returns <code>null</code> if the version number can not be
	 * determined.
	 *
	 * @param version The version specified in the annotation or an empty
	 *                string if the version was not specified in the
	 *                annotation.
	 * @return The version number if it can be determined, null otherwise.
	 */
	private String getVersion(String version)
	{
		if (version != null && version.length() > 0)
		{
			debug("Using version specified in the annotation: " + version);
			return version;
		}
		File file = new File("VERSION");
		if (file.exists())
		{
			debug("Attempting to parse VERSION file");
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				reader.close();
				debug("Using version from VERSION file: " + line);
				return line;
			}
			catch (IOException ignore)
			{
				// Fall through and try the pom.xml file.
			}
		}

		File pom = new File("pom.xml");
		if (!pom.exists())
		{
			return "0.0.0.UNKNOWN";
		}
		debug("Attempting to get version from POM.");
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new MavenNamespaceContext());
		String expression = "/maven:project/maven:version/text()";
		try
		{
			XPathExpression xpr = xpath.compile(expression);
			InputSource source = new InputSource(new FileReader(pom));
			String s = xpr.evaluate(source);
			debug("Version is " + s);
			return s;
		}
		catch (IOException e)
		{
			log("IOException: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		catch (XPathExpressionException ignored)
		{
			log("Error parsing version: " + ignored.getMessage());
			ignored.printStackTrace();
			return null;
		}
	}

//   private <T> void addList(List<T> list, IOSpecification spec)
//   {
//      for (T item : list)
//      {
//         if (item instanceof ContentType)
//         {
//            spec.add((ContentType) item);
//         }
//         else if (item instanceof AnnotationType)
//         {
//            spec.add((AnnotationType) item);
//         }
//         else
//         {
//            spec.add((String) item);
//         }
//      }
//   }

	private void writeDataSourceMetadata(File file, String className, DataSourceMetadata annotation) throws IOException
	{
		org.lappsgrid.metadata.DataSourceMetadata metadata = new org.lappsgrid.metadata.DataSourceMetadata();
		metadata.setName(className);
		if (annotation == null)
		{
			//throw new IOException("annotation is null");
			return;
		}
		if (annotation.description() == null)
		{
			throw new IOException("Annotation description is null");
		}
		metadata.setDescription(get(annotation.description()));
		metadata.setVendor(get(annotation.vendor()));
		metadata.setLicense(getValue(annotation.license()));
		metadata.setAllow(getValue(annotation.allow()));
//      log("Attempting to get version");
		metadata.setVersion(getVersion(annotation.version()));
		metadata.setEncoding(annotation.encoding());
		metadata.setLanguage(Arrays.asList(annotation.language()));
		List<String> formats = new ArrayList<String>();
		for (String format : annotation.format())
		{
			formats.add(getValue(format));
		}
		metadata.setFormat(formats);
//		metadata.setFormat(Arrays.asList(annotation.format()));

		UTF8Writer writer = null;
		try
		{
			writer = new UTF8Writer(file);
			writer.write(Serializer.toJson(metadata)); // metadata.toPrettyJson());
			log("Wrote " + file.getPath());
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}

	private void writeMetadata(File file, String className, CombinedMetadata combined) throws IOException
	{
		org.lappsgrid.metadata.ServiceMetadata metadata = new org.lappsgrid.metadata.ServiceMetadata();
		String name = combined.name();
		if (name == null || name.length() == 0) {
			name = className;
		}
		System.out.println("Service name is " + name);
		metadata.setName(name);
		metadata.setDescription(get(combined.description()));
		metadata.setVendor(get(combined.vendor()));
		metadata.setLicense(getValue(combined.license()));
		metadata.setAllow(getValue(combined.allow()));
//      log("Attempting to get version");
		metadata.setVersion(getVersion(combined.version()));

		IOSpecification requires = metadata.getRequires();
		log("Setting format");
//      List<String> formats = makeList(contentTypeFactory, combined.inputFormat());
		List<String> formats = makeList(combined.inputFormat());
		debug("Combined formats.");
		for (String type : formats)
		{
			debug(type.toString());
		}
		requires.getFormat().addAll(formats);

		debug("Required formats.");
		for (String type : requires.getFormat())
		{
			debug(type);
		}

		String encoding = combined.inputEncoding();
		if (encoding != null && encoding.length() > 0)
		{
//         log("Setting encoding to " + encoding);
			requires.setEncoding(encoding);
		}

//      log("Setting languages");
      List<String> languages = makeList(combined.inputLanguage());
		requires.getLanguage().addAll(languages);

//      log("Setting annotation types");
//      List<AnnotationType> types = makeList(annotationTypeFactory, combined.requires());
		List<String> annotations = requires.getAnnotations();
		List<String> types = makeList(combined.requires());
		annotations.addAll(types);
//		for (String type : combined.requires())
//		{
//			annotations.add(new AnnotationType(type));
//		}
//		requires.getAnnotations().addAll(annotations);
//      addList(types, requires);

		// Populate the produces IOSpecification
		IOSpecification produces = metadata.getProduces();
//      log("Setting formats.");
//      formats = makeList(contentTypeFactory, combined.outputFormat());
//      addList(formats, produces);
		formats = makeList(combined.outputFormat());
		produces.getFormat().addAll(formats);

		encoding = combined.outputEncoding();
//      if (combined.outputEncoding().length() > 0)
//      {
////         log("Using outEncoding");
//         encoding = combined.outputEncoding();
//      }
		if (encoding != null && encoding.length() > 0)
		{
//         log("Setting encoding to " + encoding);
			produces.setEncoding(encoding);
		}

//      log("Setting languages");
//      languages = makeList(stringFactory, combined.outputLanguage());
//      addList(languages, produces);
		languages = makeList(combined.outputLanguage());
		produces.getLanguage().addAll(languages);

//      log("Setting types");
//      types = makeList(annotationTypeFactory, combined.produces());
//      addList(types, produces);
		types = makeList(combined.produces());
//		produces.getAnnotations().addAll(types);
		annotations = produces.getAnnotations();
		annotations.addAll(types);

		UTF8Writer writer = null;
		try
		{
			writer = new UTF8Writer(file);
			writer.write(Serializer.toJson(metadata)); // metadata.toPrettyJson());
			log("Wrote " + file.getAbsolutePath());
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
		}
	}

	private interface Factory<T>
	{
		T make(String uri);
	}

//	private static class AnnotationTypeFactory implements Factory<AnnotationType>
//	{
//		public AnnotationType make(String uri)
//		{
////			System.out.println("AnnotationTypeFactory.make");
////			System.out.println("Creating annotation type for " + uri);
//			Discriminator d = DiscriminatorRegistry.getByName(uri);
//			if (d == null)
//			{
//				d = DiscriminatorRegistry.getByUri(uri);
//				if (d == null)
//				{
////					System.out.println("Unknown type. Returning a dummy.");
//					return new AnnotationType();
//				}
//			}
////			System.out.println("Creating annotation type for discriminator " + d.getUri());
//			return new AnnotationType(d);
//		}
//	}

	private List<String> makeList(String[] array)
	{
		List<String> list = new ArrayList<String>();
		if (array != null) {
			//list.addAll(Arrays.asList(array));
			for (String name : array) {
				list.add(getUri(name));
			}
		}
		return list;
	}

	private String getUri(String name)
	{
		Discriminator d = DiscriminatorRegistry.getByName(name);
		if (d != null)
		{
			return d.getUri();
		}
		d = DiscriminatorRegistry.getByUri(name);
		if (d != null)
		{
			return d.getUri();
		}
		return name;
	}

	private <T> List<T> makeList(Factory<T> factory, String[] array)
	{
		List<T> list = new ArrayList<>();
		if (array == null)
		{
			return list;
		}
		for (String string : array)
		{
			list.add(factory.make(string));
		}
		return list;
	}

	class MavenNamespaceContext implements NamespaceContext
	{

		@Override
		public String getNamespaceURI(String prefix)
		{
			debug("Getting namespace for prefix " + prefix);
			if ("maven".equals(prefix))
			{
				debug("Return MAVEN URL.");
				return "http://maven.apache.org/POM/4.0.0";
			}
			return null;
		}

		@Override
		public String getPrefix(String namespaceURI)
		{
			return null;
		}

		@Override
		public Iterator getPrefixes(String namespaceURI)
		{
			return null;
		}
	}
}
