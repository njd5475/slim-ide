package com.njd5475.github.slim.model;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class SlimFileWrapperTest {

	private SlimFileWrapper file;
	
	@Before
	public void setup() {
		file = new SlimFileWrapper(mock(File.class));
	}
	
	@Test
	public void testRemove() {
		file.remove(file.getLine(2));
		
	}

}
