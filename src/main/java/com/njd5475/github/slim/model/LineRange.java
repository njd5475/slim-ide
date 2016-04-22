package com.njd5475.github.slim.model;

public class LineRange {

    private int end;
    private int start;

    public LineRange(int start, int num) {
        if(num < 1) {
            throw new IllegalArgumentException("Num must be positive non-zero integer!");
        }
        if(start < 1) {
            throw new IllegalArgumentException("Start must be positive non-zero integer!"); 
        }
        this.start = start;
        this.end = start + num;
    }
    
    public int getStartLine() {
        return start;
    }
    
    public int getEndLine() {
        return end;
    }
    
    public int numberOfLines() {
        return end - start;
    }
}
