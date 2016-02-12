package com.njd5475.github.slim.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.njd5475.github.slim.controller.SlimEditContext;

public class SlimLineWrapperTest {

	@Mock
	private SlimEditContext edits;
	
	@Mock
	private SlimFileWrapper file;
	
	private SlimLineWrapper line;

	@Before
	public void setup() {
		line = new SlimLineWrapper(2, "This is a test line", file);
	}
	
	@Test
	public void testAddCharacterAt() {
		line.addCharacterAt('7', 5);
		assertEquals("This 7is a test line", line.getLine());
	}

	@Test
	public void testRemoveCharacterAt() {
		line.removeCharacterAt(0, edits);
		assertEquals("his is a test line", line.getLine());
	}
}
