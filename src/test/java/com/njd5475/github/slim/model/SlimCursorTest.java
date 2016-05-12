package com.njd5475.github.slim.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.njd5475.github.slim.view.SlimCursor;

public class SlimCursorTest {

    @Mock
    private SlimFileWrapper mFile;
    @Mock
    private SlimLineWrapper mLine;
    
    private SlimCursor cursor;
    
    @Before
    public void setUp() {
        mFile = mock(SlimFileWrapper.class);
        mLine = mock(SlimLineWrapper.class);
        cursor = new SlimCursor(mFile, 2, 3);
    }
    
    @Test
    public void testSkip() {
        when(mLine.length()).thenReturn(20);
        when(mFile.getLine(2)).thenReturn(mLine);
        cursor.skip(7);
        assertEquals(10, cursor.getColumn());
    }

}
