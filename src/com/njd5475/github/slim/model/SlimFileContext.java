package com.njd5475.github.slim.model;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;

public class SlimFileContext implements FileChangeListener {

	private Set<SlimFileWrapper>		files			= new TreeSet<SlimFileWrapper>(new Comparator<SlimFileWrapper>() {

																							@Override
																							public int compare(SlimFileWrapper arg0, SlimFileWrapper arg1) {
																								return arg0.toString().compareTo(arg1.toString());
																							}

																						});
	private Set<FileChangeListener>	listeners	= new HashSet<FileChangeListener>();

	public SlimFileContext(String[] args) {
		// turn this into a list of files to load
		for (String arg : args) {
			File file = new File(arg);
			if (file.getName() != null) {
				addNewFile(file);
			} else {
				System.out.println("Invalid file name '" + arg + "'");
			}
		}
	}

	public void addNewFile(File file) {
		SlimFileWrapper wrapper = new SlimFileWrapper(file);
		wrapper.addLsitener(this);
		files.add(wrapper);
		for (FileChangeListener listener : listeners) {
			listener.onNewFileLoaded(wrapper);
		}
	}

	public void addListener(FileChangeListener listener) {
		listeners.add(listener);
	}

	public Collection<SlimFileWrapper> getFiles() {
		return files;
	}

	@Override
	public void onNewFileLoaded(SlimFileWrapper newFile) {

	}

	public File getNextFile() {
		Set<File> nextFiles = new TreeSet<File>();
		for(SlimFileWrapper file : files) {
			nextFiles.addAll(file.getNext());
		}
		for(SlimFileWrapper file : files) {
			nextFiles.remove(file.getFile());
		}
		if(nextFiles.isEmpty()) {
			File parentDir = files.iterator().next().getFile().getParentFile();
			for(File dirsInParent : parentDir.listFiles()) {
				if(dirsInParent.isDirectory() && dirsInParent.listFiles().length > 0) {
					for(File maybe : dirsInParent.listFiles()) {
						if(maybe.isFile()) {
							nextFiles.add(maybe);
						}
					}
				}
			}
		}
		return nextFiles.iterator().next();
	}
}
