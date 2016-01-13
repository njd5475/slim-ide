package com.njd5475.github.slim.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlimDirectory {

	private Map<File, File>	listing	= new HashMap<File, File>();

	private AtomicBoolean			done		= new AtomicBoolean(false);

	private File							startFile;

	public SlimDirectory(File file) {
		if (file == null) {
			file = new File("."); // if it's null use current directory
		}

		if (file.exists()) {
			// use absolute path
			file = file.getAbsoluteFile();
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
						listing.put(f, f);
					}
				}
				System.out
						.println("Done with listing: " + listing.size() + " in " + (System.currentTimeMillis() - start) + "ms");
				done.set(true);
			}
		}).start();
	}

	public File[] listTen(String match) {
		File ten[] = new File[10];
		int found = 0;
		for(Map.Entry<File, File> entry : listing.entrySet()) {
			if(entry.getKey().getAbsolutePath().matches(match)) {
				ten[found++] = entry.getKey();
				if(found == ten.length) {
					break;
				}
			}
		}
		return ten;
	}
	
	public boolean isDone() {
		return done.get();
	}
}
