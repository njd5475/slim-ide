package com.njd5475.github.slim.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;
import com.njd5475.github.slim.view.SlimRenderContext;
import com.njd5475.github.slim.view.SlimRenderVisitor;

public class SlimFileWrapper implements Comparable<SlimFileWrapper> {

  private static final int              FLUSH_RATE = 1000;
  private File                          file;
  private Set<FileChangeListener>       listeners  = new HashSet<FileChangeListener>();
  private Map<Integer, SlimLineWrapper> lines      = new TreeMap<Integer, SlimLineWrapper>();
  private boolean                       loaded     = false;

  public SlimFileWrapper(File file) {
    this.file = file;
  }

  public void addLsitener(FileChangeListener listener) {
    listeners.add(listener);
  }

  public void render(SlimRenderContext ctx, SlimRenderVisitor visitor) {
    visitor.render(ctx, this);
  }

  public String toString() {
    return file.getName();
  }

  public Collection<SlimLineWrapper> getLines() {
    load();
    return lines.values();
  }

  private void load() {
    if(!loaded) {
      long start = System.currentTimeMillis();
      try {
        if(file.exists()) {
          BufferedReader buffr = new BufferedReader(new FileReader(file), 4096);
          String line = null;
          int lineNo = 0;
          SlimLineWrapper wrapper;
          while((line = buffr.readLine()) != null) {
            wrapper = new SlimLineWrapper(++lineNo, line + "\n", this);
            lines.put(wrapper.getLineNumber(), wrapper);
          }
          wrapper = new SlimLineWrapper(++lineNo, "", this);
          lines.put(wrapper.getLineNumber(), wrapper);
          buffr.close();
        } else {
          lines.put(1, new SlimLineWrapper(1, "\n", this));
        }
      } catch(FileNotFoundException e) {
        e.printStackTrace();
      } catch(IOException e) {
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
    if(file.exists()) {
      for(File f: file.getParentFile().listFiles()) {
        if(f.isFile() && !f.isHidden()) {
          nextFiles.add(f);
        }
      }
    }
    return nextFiles;
  }

  public int getLineCount() {
    return lines.size();
  }

  public void remove(SlimLineWrapper line) {
    int lineNum = line.getLineNumber();
    lines.remove(lineNum);
    int totalLines = getLineCount();

    Set<SlimLineWrapper> theLines = new HashSet<SlimLineWrapper>();
    // resequence the lines
    for(int i = lineNum + 1; i < totalLines; ++i) {
      SlimLineWrapper ln = lines.remove(i);
      ln.lineDeleted(line);
      // remove from map
      theLines.add(ln);
    }
    // rebuild line map
    for(SlimLineWrapper l: theLines) {
      lines.put(l.getLineNumber(), l);
    }
  }

  public SlimLineWrapper getLine(int i) {
    return lines.get(i);
  }

  public void save() throws IOException {
    if(!file.exists()) {
      file.createNewFile();
    }

    PrintWriter pw = new PrintWriter(file);
    int i = 0;
    for(SlimLineWrapper line: lines.values()) {
      pw.print(line.getLine());
      if((++i % FLUSH_RATE) == 0) {
        pw.flush();
      }
    }
    pw.flush();
    pw.close();
  }

  public void addLineAt(SlimLineWrapper line, int cursorColumn) {
    String before = line.getLine().substring(0, cursorColumn);
    String after = line.getLine().substring(cursorColumn, line.getLine().length());
    if(!before.endsWith("\n")) {
      before += "\n";
    }
    SlimLineWrapper lineBefore = new SlimLineWrapper(line.getLineNumber(), before, this);
    SlimLineWrapper lineAfter = new SlimLineWrapper(line.getLineNumber() + 1, after, this);
    lines.put(lineBefore.getLineNumber(), lineBefore);
    insertLine(lineAfter);
  }

  private void insertLine(SlimLineWrapper line) {
    int lineNum = line.getLineNumber();
    int totalLines = getLineCount();
    Set<SlimLineWrapper> toUpdate = new HashSet<SlimLineWrapper>();
    for(int i = lineNum; i <= totalLines; ++i) {
      SlimLineWrapper current = lines.remove(i);
      if(current != null) {
        toUpdate.add(current);
        current.lineInserted(current);
      }
    }
    for(SlimLineWrapper reinsert: toUpdate) {
      lines.put(reinsert.getLineNumber(), reinsert);
    }
    lines.put(lineNum, line);
  }

  @Override
  public int compareTo(SlimFileWrapper o) {
    return this.toString().compareTo(o.toString());
  }

  public String getExtension() {
    int loc = file.getName().lastIndexOf('.');
    if(loc > -1) {
      return file.getName().substring(loc);
    }
    return "";
  }

  public boolean qualifies() {
    try {

      BufferedReader buffr = Files.newBufferedReader(file.toPath());
      char[] tBuf = new char[10];

      int read = buffr.read(tBuf);
      
      int valids = 0;
      for(char c : tBuf) {
        if(c >= 32 && c < 127 || c == 9 || c == 10 || c == 12) {
          ++valids;
        }
      }
      return valids > 8;
    } catch(IOException e) {
    }
    return false;
  }

}
