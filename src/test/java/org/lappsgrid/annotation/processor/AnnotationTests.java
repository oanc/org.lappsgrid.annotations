package org.lappsgrid.annotation.processor;

import org.junit.*;
import static org.junit.Assert.*;

import static org.lappsgrid.discriminator.Discriminators.Uri;
import org.lappsgrid.metadata.ServiceMetadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Keith Suderman
 */
public class AnnotationTests extends CompilerBase
{
	public AnnotationTests()
	{

	}

	@AfterClass
	public static void cleanup() throws IOException
	{
//		delete(new File("src/main/resources/metadata"));
		delete(new File("Empty.class"));
		delete(new File("Base.class"));
		System.out.println("Cleanup complete.");
	}

	private static void delete(File file)
	{
		if (!file.exists())
		{
			return;
		}

		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				delete(f);
			}
		}
		if (!file.delete())
		{
			System.out.println("Unable to delete " + file.getPath());
		}
	}

	@Test
	public void testDefaults() throws IOException
	{
		String source = "package test;\n" +
				  "import org.lappsgrid.annotations.ServiceMetadata;\n" +
				  "@ServiceMetadata\n" +
				  "class Empty { }\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertEquals("test.Empty", metadata.getName());
		assertFalse("0.0.0.UNKNOWN".equals(metadata.getVersion()));
//		assertEquals("0.0.0.UNKNOWN", metadata.getVersion());
		assertEquals(Uri.ALL, metadata.getAllow());
	}

	@Test
	public void testOverrideNameAndVersion() throws IOException
	{
		String source = "package test;\n" +
				  "import org.lappsgrid.annotations.ServiceMetadata;\n" +
				  "@ServiceMetadata(name=\"foo\",version=\"1.0.0\")\n" +
				  "class Empty { }\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertEquals("foo", metadata.getName());
		assertEquals("1.0.0", metadata.getVersion());
	}

	@Test
	public void testFormat() throws IOException
	{
		String source = "package test;\n" +
				  "import org.lappsgrid.annotations.ServiceMetadata;\n" +
				  "@ServiceMetadata(\n" +
				  "    format = {\"text\"}\n" +
				  ")\n" +
				  "class Empty { }\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertNotNull(metadata);
		List<String> formats = metadata.getRequires().getFormat();
		assertEquals(1, formats.size());
		assertEquals(Uri.TEXT, formats.get(0));
	}

	@Test
	public void testInheritance() throws IOException
	{
		String source = "package test;\n" +
			"import org.lappsgrid.annotations.ServiceMetadata;\n" +
			"@ServiceMetadata(\n" +
			"		  license = \"apache2\"\n" +
			")\n" +
			"class Base { }\n" +
			"class Empty extends Base {}\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertNotNull(metadata);
		String license = metadata.getLicense();
		assertNotNull(license);
		assertEquals(Uri.APACHE2, license);
	}

	@Test
	public void testCombinedMetadata() throws IOException
	{
		String source = "package test;\n" +
				  "import org.lappsgrid.annotations.ServiceMetadata;\n" +
				  "import org.lappsgrid.annotations.CommonMetadata;\n" +
				  "@CommonMetadata(\n" +
				  "vendor=\"anc\")\n" +
				  "class Base {}\n" +
				  "@ServiceMetadata(license=\"apache2\")\n" +
				  "class Empty extends Base {}\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertNotNull(metadata);
		assertEquals("anc", metadata.getVendor());
		assertEquals(Uri.APACHE2, metadata.getLicense());
	}

	@Test
	public void testLicenseDesc() throws IOException
	{
		String license = "Apache 2.0";
		String source = "package test;\n" +
				"import org.lappsgrid.annotations.ServiceMetadata;\n" +
				"@ServiceMetadata(license=\"" + license +"\")\n" +
				"class Empty { }\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertNotNull(metadata);
		assertEquals(license, metadata.getLicense());
	}

	@Test
	public void testTagSets() throws IOException
	{
		String desc = "description";
		String source = "package test;\n" +
				"import org.lappsgrid.annotations.ServiceMetadata;\n" +
				"@ServiceMetadata(licenseDesc=\"" + desc +"\")\n" +
				"class Empty { }\n";
		compile(source);
		ServiceMetadata metadata = getMetadata();
		assertNotNull(metadata);
		assertEquals(desc, metadata.get);
	}
}
