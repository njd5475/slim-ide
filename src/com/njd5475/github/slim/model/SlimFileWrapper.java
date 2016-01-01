package com.njd5475.github.slim.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;
import com.njd5475.github.slim.view.SlimRenderVisitor;

public class SlimFileWrapper {

	private File										file;
	private Set<FileChangeListener>	listeners	= new HashSet<FileChangeListener>();
	private Set<SlimLineWrapper>		lines			= new TreeSet<SlimLineWrapper>();
	private boolean									loaded		= false;

	public SlimFileWrapper(File file) {
		this.file = file;
	}

	public void addLsitener(FileChangeListener listener) {
		listeners.add(listener);
	}

	public void render(SlimRenderVisitor visitor) {
		visitor.render(this);
	}

	public String toString() {
		return file.getName();
	}

	public Collection<SlimLineWrapper> getLines() {
		load();
		return lines;
	}

	private void load() {
		if (!loaded) {
			long start = System.currentTimeMillis();
			try {
				BufferedReader buffr = new BufferedReader(new FileReader(file), 4096);
				String line = null;
				int lineNo = 0;
				while ((line = buffr.readLine()) != null) {
					lines.add(new SlimLineWrapper(++lineNo, line, this));
				}
				buffr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File loaded in " + (System.currentTimeMillis() - start) + "ms");
			loaded = true;
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public Set<File> getNext() {
		Set<File> nextFiles = new TreeSet<File>();
		for(File f : file.getParentFile().listFiles()) {
			if(f.isFile() && !f.isHidden()) {
				nextFiles.add(f);
			}
		}
		return nextFiles;
	}

	public int getLineCount() {
		return lines.size();
	}
	
}
