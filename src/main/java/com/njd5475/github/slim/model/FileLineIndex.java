package com.njd5475.github.slim.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileLineIndex {

    private File file;
    private File indexfile;
    private long loaded = -1;
    
    private Map<Integer, Integer> lineEnds = new HashMap<Integer, Integer>();
    
    public FileLineIndex(File file) {
        this.file = file;
        this.indexfile = new File(System.getProperty("user.home"), file.getName() + ".index");
        try {
            if(!this.indexfile.createNewFile()) {
               loadExisting();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            load();
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method will return the byte position of the beginning of a 
     * particular line number designated by lineBegin.
     * 
     * @param lineBegin
     * @return
     */
    public long bytePos(int lineBegin) {
    	checkLoaded();
    	long pos = -1;
    	// add all line endings from 0 to lineBegin
    	for(int i = 0; i < lineBegin; ++i) {
    		Integer count = lineEnds.get(i);
    		if(count != null) {
    			pos += count;
    		}else{
    			pos = -1;
    			break;
    		}
    	}
    	return pos;
    }

    private int lineNm = 0;
    private int lastPos = 0;
    private String indexFiles;
    
    private void load() throws IOException {
        lineNm = 0;
        lastPos = 0;
        Files.lines(file.toPath()).forEach( (line) -> {
            ++lineNm;
            lineEnds.put(lineNm, (lastPos += line.getBytes().length));
        });
        loaded = System.currentTimeMillis();
    }
    
    private void checkLoaded() {
    	if(loaded == -1 && file.lastModified() > loaded) {
    		try {
					load();
					save();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}
    }
    
    @SuppressWarnings("resource")
		private void loadExisting() throws ClassNotFoundException, FileNotFoundException, IOException {
        lineEnds = (Map<Integer,Integer>) (new ObjectInputStream(new FileInputStream(indexFiles))).readObject();
    }
    
    @SuppressWarnings("resource")
		private void save() throws FileNotFoundException, IOException {
        (new ObjectOutputStream(new FileOutputStream(indexFiles))).writeObject(lineEnds);
    }

}
