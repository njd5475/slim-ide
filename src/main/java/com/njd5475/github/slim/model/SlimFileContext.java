package com.njd5475.github.slim.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.njd5475.github.slim.controller.FileChangeListener;

public class SlimFileContext implements Runnable, FileChangeListener {

  private Set<SlimFileWrapper>              files     = new TreeSet<SlimFileWrapper>();
  private Set<FileChangeListener>           listeners = new HashSet<FileChangeListener>();
  private Map<String, Set<SlimFileWrapper>> byExt;
  private Queue<File>                       toLoad    = new ConcurrentLinkedQueue<>();

  public SlimFileContext(String[] args) {
    for(String arg: args) {
      File file = new File(arg);
      toLoad.add(file);
    }
  }

  public void run() {
    while(!toLoad.isEmpty()) {
      File poll = toLoad.poll();
      if(poll.isDirectory()) {
        File[] listFiles = poll.listFiles();
        for(File file: listFiles) {
          toLoad.add(file);
        }
      } else if(poll.isFile()) {
        addNewFile(poll);
      } else {
        System.err.println("Error: Unknown file type!");
      }
    }
  }

  public void addNewFile(File file) {
    SlimFileWrapper wrapper = new SlimFileWrapper(file);
    if(!wrapper.qualifies()) {
      return;
    }
    synchronized(files) {
      if(!files.contains(wrapper)) {
        wrapper.addLsitener(this);
        files.add(wrapper);
        for(FileChangeListener listener: listeners) {
          listener.onNewFileLoaded(wrapper);
        }
      }
      files.notifyAll();
    }
  }

  public void addListener(FileChangeListener listener) {
    listeners.add(listener);
  }

  public Collection<SlimFileWrapper> getFiles() {
    List<SlimFileWrapper> newList;
    synchronized(files) {
      newList = new LinkedList<>(files);
      files.notifyAll();
    }
    return newList;
  }

  public boolean hasFiles() {
    return files.size() > 0;
  }

  public File[] getOpenFiles() {
    Set<File> filePaths = new HashSet<File>();
    synchronized(files) {
      for(SlimFileWrapper wrapper: files) {
        filePaths.add(wrapper.getFile());
      }
      files.notifyAll();
    }
    return filePaths.toArray(new File[filePaths.size()]);
  }

  @Override
  public void onNewFileLoaded(SlimFileWrapper newFile) {

  }

  public File getNextFile() {
    try {
      File openFiles[] = getOpenFiles();
      File ten[] = listTen(".*\\w+\\." + recognizedCodeExtension() + ".*", openFiles);
      if(ten != null && ten.length > 0) {
        File next = ten[0];
        if(next != null) {
          return next.getCanonicalFile();
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private File[] listTen(String string, File[] openFiles) {
    File[] ten = new File[10];
    Iterator<SlimFileWrapper> iter = this.files.iterator();
    int i = 0;
    for(; i < ten.length; ++i) {
      if(!iter.hasNext()) {
        break;
      }
      ten[i] = iter.next().getFile();
    }

    if(i < ten.length) {
      ten = Arrays.copyOf(ten, i + 1);
    }
    return ten;
  }

  private String recognizedCodeExtension() {
    String largestExtension = getCommonExtension();

    return largestExtension;
  }

  private String getCommonExtension() {
    if(byExt == null) {
      byExt = buildExtensionMap();
    }

    Set<Map.Entry<String, Set<SlimFileWrapper>>> orderByLargest;
    orderByLargest = new TreeSet<Map.Entry<String, Set<SlimFileWrapper>>>(
        (Map.Entry<String, Set<SlimFileWrapper>> o1, Map.Entry<String, Set<SlimFileWrapper>> o2) -> {
          return o1.getValue().size() - o2.getValue().size();
        });
    orderByLargest.addAll(byExt.entrySet());
    if(!orderByLargest.isEmpty()) {
      return orderByLargest.iterator().next().getKey();
    }
    return null;
  }

  private Map<String, Set<SlimFileWrapper>> buildExtensionMap() {
    Map<String, Set<SlimFileWrapper>> ext = new HashMap<>();
    Set<SlimFileWrapper> forExt;
    String extension;
    for(SlimFileWrapper sf: files) {
      forExt = ext.get(extension = sf.getExtension());
      if(forExt == null) {
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
