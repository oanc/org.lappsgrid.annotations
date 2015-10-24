package org.lappsgrid.annotation.processor;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A JavaFileObject held in memory. This is almost exactly the same as
 * the JavaSourceFromString class in the {@link javax.tools.JavaCompiler}
 * documentation.
 *
 * @author Keith Suderman
 */

public class MemoryJavaFileObject implements JavaFileObject
{
	protected String name;
	protected String contents;

	public MemoryJavaFileObject(String name, String contents)
	{
		this.name = name;
		this.contents = contents;
	}


	@Override
	public JavaFileObject.Kind getKind()
	{
		return JavaFileObject.Kind.SOURCE;
	}

	@Override
	public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind)
	{
		return false;
	}

	@Override
	public NestingKind getNestingKind()
	{
		return NestingKind.TOP_LEVEL;
	}

	@Override
	public Modifier getAccessLevel()
	{
		return Modifier.PUBLIC;
	}

	@Override
	public URI toUri()
	{
		return URI.create("string://text/Empty");
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public InputStream openInputStream() throws IOException
	{
		return new ByteArrayInputStream(contents.getBytes());
	}

	@Override
	public OutputStream openOutputStream() throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) throws IOException
	{
		return new StringReader(contents);
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
	{
		return contents;
	}

	@Override
	public Writer openWriter() throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLastModified()
	{
		return 0;
	}

	@Override
	public boolean delete()
	{
		throw new UnsupportedOperationException();
	}
}