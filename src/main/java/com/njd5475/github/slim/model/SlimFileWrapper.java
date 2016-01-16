package com.njd5475.github.slim.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.njd5475.github.slim.controller.FileChangeListener;
import com.njd5475.github.slim.view.SlimRenderVisitor;

public class SlimFileWrapper {


	private static final int FLUSH_RATE = 1000;
	private File													file;
	private Set<FileChangeListener>				listeners	= new HashSet<FileChangeListener>();
	private Map<Integer, SlimLineWrapper>	lines			= new TreeMap<Integer, SlimLineWrapper>();
	private boolean												loaded		= false;

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
		return lines.values();
	}

	private void load() {
		if (!loaded) {
			long start = System.currentTimeMillis();
			try {
				if (file.exists()) {
					BufferedReader buffr = new BufferedReader(new FileReader(file), 4096);
					String line = null;
					int lineNo = 0;
					SlimLineWrapper wrapper;
					while ((line = buffr.readLine()) != null) {
						wrapper = new SlimLineWrapper(++lineNo, line + "\n", this);
						lines.put(wrapper.getLineNumber(), wrapper);
					}
					buffr.close();
				}
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
		if (file.exists()) {
			for (File f : file.getParentFile().listFiles()) {
				if (f.isFile() && !f.isHidden()) {
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
		lines.remove(line.getLineNumber());
		for (int i = lineNum + 1; i < getLineCount(); ++i) {
			SlimLineWrapper lineToMove = lines.get(i);
			lineToMove.lineDeleted(line);
			lines.put(i - 1, lineToMove);
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
		for(SlimLineWrapper line : lines.values()) {
			pw.print(line.getLine());
			if( (++i % FLUSH_RATE) == 0) {
				pw.flush();
			}		
		}
		pw.flush();
		pw.close();
	}

}
