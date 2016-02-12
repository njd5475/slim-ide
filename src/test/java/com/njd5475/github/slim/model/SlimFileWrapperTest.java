package com.njd5475.github.slim.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SlimFileWrapperTest {

	private SlimFileWrapper file;
	
	@Before
	public void setup() {
		file = new SlimFileWrapper(get("testfile.txt"));
		assertEquals(10, file.getLines().size());
	}
	
	@Test
	public void testRemove() {
		int lineCount = file.getLines().size();
		file.remove(file.getLine(2));
		Collection<SlimLineWrapper> lines = file.getLines();
		assertEquals(lineCount-1, lines.size());
	}

	@Test
	public void testInsertLine() {
		int lineCount = file.getLineCount();
		SlimLineWrapper line = mock(SlimLineWrapper.class);
		when(line.getLine()).thenReturn("New Line Three");
		when(line.getLineNumber()).thenReturn(3);
		file.addLineAt(line, 3);
		assertEquals(lineCount+1, file.getLineCount());
		
		Set<SlimLineWrapper> lines = new HashSet<SlimLineWrapper>();
		lines.addAll(file.getLines());
		assertEquals(lines.size(), file.getLineCount());
		
		for(SlimLineWrapper ln : file.getLines()) {
			assertEquals(ln, file.getLine(ln.getLineNumber()));
		}
	}
	
	public File get(String filename) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
		return new File(url.getPath());
	}
}
