package com.njd5475.github.slim.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileIndexer {
    
    private static FileIndexer instance;
    private Map<File, FileLineIndex> indexes = new HashMap<File, FileLineIndex>();
    
    public FileLineIndex getIndex(File file) {
        FileLineIndex index = indexes.get(file);
        if(index == null) {
            index = new FileLineIndex(file);
            indexes.put(file, index);
        }
        
        return index;
    }
    
    public static FileLineIndex getLineIndex(File file) {
        return getDefaultFileIndexer().getIndex(file);
    }
    
    public static FileIndexer getDefaultFileIndexer() {
        if(instance == null) {
            instance = new FileIndexer();
        }
        
        return instance;
    }
}
