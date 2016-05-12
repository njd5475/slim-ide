package com.njd5475.github.slim.view;

import com.njd5475.github.slim.model.SlimFileWrapper;

/**
 * This class represents a cursor so it can be renderable. The capability is to
 * be able to render and use any number of cursor no matter how many there are
 * but only. So this class should be able to be used as multiple instances that
 * maintain their own internal consistency with the file they were created with.
 * 
 * @author nick
 */
public class SlimCursor {

    private int             line;
    private SlimFileWrapper file;
    private int             column;

    public SlimCursor(SlimFileWrapper file, int line, int column) {
        this.line = line;
        this.file = file;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public void advance() {
        skip(1);
    }

    public void skip(int n) {
        column += n;
        while(column > this.file.getLine(line).length()) {
            column -= this.file.getLine(line).length();
            ++line;
        }
    }
    
}
