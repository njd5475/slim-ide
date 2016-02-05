package com.njd5475.github.slim.model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;
import com.njd5475.github.slim.controller.SlimDirectory;

public class SlimFileContext implements FileChangeListener {

	private Set<SlimFileWrapper>		files			= new TreeSet<SlimFileWrapper>(new Comparator<SlimFileWrapper>() {

																							@Override
																							public int compare(SlimFileWrapper arg0, SlimFileWrapper arg1) {
																								return arg0.toString().compareTo(arg1.toString());
																							}

																						});
	private Set<FileChangeListener>	listeners	= new HashSet<FileChangeListener>();
	private SlimDirectory directory;

	public SlimFileContext(String[] args) {
		directory = new SlimDirectory(new File("."));
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

	public boolean hasFiles() {
		return files.size() > 0;
	}

	@Override
	public void onNewFileLoaded(SlimFileWrapper newFile) {

	}

	public File getNextFile() {
		try {
      File ten[] = directory.listTen(".*\\w+\\.java.*");
      if(ten != null && ten.length > 0) {
        File next = ten[0];
        if(next != null) {
			    return next.getCanonicalFile();
        }
      }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getFileCount() {
		return files.size();
	}

  public void openNextFile() {
    addNewFile(getNextFile());
  }
}
