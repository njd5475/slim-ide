package com.njd5475.github.slim.controller;

import java.util.Collection;

import com.njd5475.github.slim.model.SlimFileContext;
import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.view.SlimRenderVisitor;

public class SlimController implements FileChangeListener {

	private SlimFileContext		fileContext;
	private SlimRenderVisitor	renderer;
	private int								numberOfLines;

	public SlimController(SlimRenderVisitor renderer, SlimFileContext fileContext) {
		this.fileContext = fileContext;
		this.fileContext.addListener(this);
		for (SlimFileWrapper file : this.fileContext.getFiles()) {
			numberOfLines += file.getLines().size();
		}
		this.renderer = renderer;
	}

	@Override
	public void onNewFileLoaded(SlimFileWrapper newFile) {
		numberOfLines += newFile.getLines().size();
		renderer.refresh();
	}

	public SlimRenderVisitor getRenderer() {
		return renderer;
	}

	public SlimFileContext getFileContext() {
		return fileContext;
	}

	public int getTotalLines() {
		return numberOfLines;
	}

	public int getLineLength(int cursorLine) {
		SlimLineWrapper line = getLine(cursorLine);
		if(line != null) {
			return line.length();
		}
		return 0;
	}

	public void addCharacterAt(char keyChar, int cursorLine, int cursorColumn) {
		SlimLineWrapper line = getLine(cursorLine);
		line.addCharacterAt(keyChar, cursorColumn);
	}

	public SlimLineWrapper getLine(int linenum) {
		int totalLinesReached = 0, lastTotal= 0;
		for (SlimFileWrapper file : this.fileContext.getFiles()) {
			Collection<SlimLineWrapper> lines = file.getLines();
			lastTotal = totalLinesReached;
			totalLinesReached += lines.size();
			if(linenum <= totalLinesReached) {
				SlimLineWrapper[] array = lines.toArray(new SlimLineWrapper[lines.size()]);
				return array[linenum-lastTotal];
			}
		}
		return null;
	}
}
