package com.njd5475.github.slim.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SlimBuffer {

	private StringBuilder builder;

	protected SlimBuffer(byte[] buf) {
		this.builder = new StringBuilder(new String(buf));
	}

	public String toString() {
		return builder.toString();
	}

	public static SlimBuffer buildForLines(File file, FileIndexer index,
			LineRange range) {
		try {
			FileLineIndex fileIndex = index.getIndex(file);
			RandomAccessFile raFile = new RandomAccessFile(file, "r");
			long begin = fileIndex.bytePos(range.getStartLine());
			long end = fileIndex.bytePos(range.getEndLine() + 1);
			byte buf[] = new byte[(int) (end - begin)];
			
			raFile.seek(begin);
			raFile.readFully(buf);
			raFile.close();
			
			return new SlimBuffer(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
