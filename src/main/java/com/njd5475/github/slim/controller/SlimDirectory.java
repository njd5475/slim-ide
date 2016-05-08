package com.njd5475.github.slim.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Stack;
import java.util.Arrays;
import java.util.HashSet;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlimDirectory {

    private Map<File, File>      listing        = new HashMap<File, File>();

    private Map<String, Integer> extensionCount = new HashMap<String, Integer>();

    private AtomicBoolean        done           = new AtomicBoolean(false);

    private File                 startFile;

    public SlimDirectory(File file) {
        if (file == null) {
            file = new File("."); // if it's null use current directory
        }

        if (file.exists()) {
            // use absolute path
            try {
                file = file.getCanonicalFile();
            } catch (IOException ioe) {

            }
        }

        startFile = file;
        listAllFiles();
    }

    private void listAllFiles() {
        (new Thread() {
            private int MAX_FILES = 10000;

            public void run() {
                long start = System.currentTimeMillis();
                Stack<File> processStack = new Stack<File>();
                processStack.push(startFile);
                while (!processStack.isEmpty() && listing.size() < MAX_FILES) {
                    File f = processStack.pop();
                    if (f.isDirectory()) {
                        File[] listFiles = f.listFiles();
                        if (listFiles != null) {
                            for (File more : listFiles) {
                                processStack.push(more);
                            }
                        }
                    } else if (!listing.containsKey(f)) {
                        process(f);
                    }
                }
                System.out.println(
                        "Done with listing: " + listing.size() + " in " + (System.currentTimeMillis() - start) + "ms");
                done.set(true);
            }
        }).start();
    }

    protected void process(File f) {
        listing.put(f, f);
        String ext = getExtension(f);
        if (ext != null && f.getName() == ext) {
            Integer count = extensionCount.get(ext);
            if (count == null) {
                count = 0;
            }
            ++count; // explicit
            extensionCount.put(ext, count);
        }
    }

    private String getExtension(File f) {
        if(f.getName().lastIndexOf('.') == -1) {
            return null;
        }
        return f.getName().substring(f.getName().lastIndexOf("."));
    }

    public File[] listTen(String match, File[] exclude) {
        File ten[] = new File[10];
        int found = 0;
        Set<File> excludeSet = new HashSet<File>(Arrays.asList(exclude));
        for (Map.Entry<File, File> entry : listing.entrySet()) {
            try {
                if (entry.getKey().getCanonicalPath().matches(match) && !excludeSet.contains(entry.getKey())) {
                    ten[found++] = entry.getKey();
                    if (found == ten.length) {
                        break;
                    }
                }
            } catch (IOException ioe) {
            }
        }
        return ten;
    }

    public boolean isDone() {
        return done.get();
    }

    public String getCommonExtension() {
        Map.Entry<String, Integer> largest = null;
        for(Map.Entry<String, Integer> entry : extensionCount.entrySet()) {
            if(largest == null || largest.getValue() < entry.getValue()) {
                largest = entry;
            }
        }
        return largest.getKey();
    }
}
