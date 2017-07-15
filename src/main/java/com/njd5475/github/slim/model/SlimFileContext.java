package com.njd5475.github.slim.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;

public class SlimFileContext implements FileChangeListener {

	private Set<SlimFileWrapper>							files			= new TreeSet<SlimFileWrapper>();
	private Set<FileChangeListener>						listeners	= new HashSet<FileChangeListener>();
	private Map<String, Set<SlimFileWrapper>>	byExt;

	public SlimFileContext(String[] args) {
		LinkedList<String> argStack = new LinkedList<>();
		argStack.addAll(Arrays.asList(args));
		// turn this into a list of files to load
		while (!argStack.isEmpty()) {
			File file = new File(argStack.pop());
			if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (File f : files) {
					argStack.push(f.getAbsolutePath());
				}
			} else if (file.isFile() && file.getName() != null) {
				addNewFile(file);
			} else {
				System.out
						.println("Invalid file name '" + file.getAbsolutePath() + "'");
			}
		}
	}

	public void addNewFile(File file) {
		SlimFileWrapper wrapper = new SlimFileWrapper(file);
		if (!files.contains(wrapper)) {
			wrapper.addLsitener(this);
			files.add(wrapper);
			for (FileChangeListener listener : listeners) {
				listener.onNewFileLoaded(wrapper);
			}
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

	public File[] getOpenFiles() {
		Set<File> filePaths = new HashSet<File>();
		for (SlimFileWrapper wrapper : files) {
			filePaths.add(wrapper.getFile());
		}
		return filePaths.toArray(new File[filePaths.size()]);
	}

	@Override
	public void onNewFileLoaded(SlimFileWrapper newFile) {

	}

	public File getNextFile() {
		try {
			File openFiles[] = getOpenFiles();
			File ten[] = listTen(".*\\w+\\." + recognizedCodeExtension() + ".*",
					openFiles);
			if (ten != null && ten.length > 0) {
				File next = ten[0];
				if (next != null) {
					return next.getCanonicalFile();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private File[] listTen(String string, File[] openFiles) {
		File[] ten = new File[10];
		Iterator<SlimFileWrapper> iter = this.files.iterator();
		int i = 0;
		for (; i < ten.length; ++i) {
			if (!iter.hasNext()) {
				break;
			}
			ten[i] = iter.next().getFile();
		}

		if (i < ten.length) {
			ten = Arrays.copyOf(ten, i + 1);
		}
		return ten;
	}

	private String recognizedCodeExtension() {
		String largestExtension = getCommonExtension();

		return largestExtension;
	}

	private String getCommonExtension() {
		if (byExt == null) {
			byExt = buildExtensionMap();
		}

		Set<Map.Entry<String, Set<SlimFileWrapper>>> orderByLargest;
		orderByLargest = new TreeSet<Map.Entry<String, Set<SlimFileWrapper>>>(
				(Map.Entry<String, Set<SlimFileWrapper>> o1,
						Map.Entry<String, Set<SlimFileWrapper>> o2) -> {
					return o1.getValue().size() - o2.getValue().size();
				});
		orderByLargest.addAll(byExt.entrySet());
		if (!orderByLargest.isEmpty()) {
			return orderByLargest.iterator().next().getKey();
		}
		return null;
	}

	private Map<String, Set<SlimFileWrapper>> buildExtensionMap() {
		Map<String, Set<SlimFileWrapper>> ext = new HashMap<>();
		Set<SlimFileWrapper> forExt;
		String extension;
		for (SlimFileWrapper sf : files) {
			forExt = ext.get(extension = sf.getExtension());
			if (forExt == null) {
				forExt = new HashSet<>();
				ext.put(extension, forExt);
			}
			forExt.add(sf);
		}
		return ext;
	}

	public int getFileCount() {
		return files.size();
	}

	public void openNextFile() {
		addNewFile(getNextFile());
	}
}
