package org.lappsgrid.annotation.processor;

import org.lappsgrid.annotation.processing.MetadataProcessor;
import org.lappsgrid.metadata.DataSourceMetadata;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Serializer;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.*;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Keith Suderman
 */
public class CompilerBase
{
//    protected JavaCompiler compiler

//    @BeforeClass
//    static void testSetup() {
//        File directory = new File("src/main/resources/metadata")
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//    }
//
//    @AfterClass
//    static void testCleanup() {
//        File directory = new File("src/main/resources/metadata")
//        if (directory.exists()) {
//            directory.delete()
//        }
//    }

//    @Before
//    void setup() {
//        compiler = ToolProvider.getSystemJavaCompiler()
//    }
//
//    @After
//    void cleanup() {
//        compiler = null
//    }

	Boolean compile(String src)
	{
		return compile(src, "Empty");
	}

	Boolean compile(String src, String className)
	{
		System.out.println("Compiling " + className);
		List units = Arrays.asList(new MemoryJavaFileObject(className, src));
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector();
		StringWriter stdout = new StringWriter();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaCompiler.CompilationTask task = compiler.getTask(stdout, null, diagnostics, null, null, units);
		List processors = Arrays.asList(new MetadataProcessor());
		task.setProcessors(processors);
		boolean result = task.call();
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
		{
			Diagnostic.Kind kind = diagnostic.getKind();
			System.out.println(diagnostic.getMessage(null));
//            if (kind == Diagnostic.Kind.WARNING || kind == Diagnostic.Kind.ERROR) {
//                println diagnostic.getMessage(Locale.US)
//            }

		}
		if (result)
		{
			System.out.println("Compilation succeeded.");
			System.out.println(stdout.toString());
			return true;
		}
		System.out.println("Compilation failed.");
		System.out.println(stdout.toString());
		return false;
	}


	public ServiceMetadata getMetadata() throws IOException
	{
		File file = new File("src/main/resources/metadata/test.Empty.json");
		if (!file.exists())
		{
			throw new IOException("Unable to locate metadata file at " + file.getPath());
		}
		Map map = Serializer.parse(getText(file), Map.class);
		return new ServiceMetadata(map);
	}

	public DataSourceMetadata getDataSourceMetadata(String className) throws IOException
	{
		File file = new File("src/main/resources/metadata/${className}.json");
		if (!file.exists())
		{
			throw new IOException("Unable to locate metadata file at " + file.getPath());
		}
		Map map = Serializer.parse(getText(file), Map.class);
		return new DataSourceMetadata(map);
	}

	protected String getText(File file)
	{
		final int SIZE = 4096;
		try
		{
			FileInputStream stream = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[SIZE];
			int size = reader.read(buffer, 0, SIZE);
			while (size > 0)
			{
				builder.append(buffer, 0, size);
				size = reader.read(buffer, 0, SIZE);
			}
			reader.close();
			return builder.toString();
		}
		catch (IOException e)
		{
			return null;
		}
	}
}
