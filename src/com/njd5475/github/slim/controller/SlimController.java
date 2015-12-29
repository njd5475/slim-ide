package com.njd5475.github.slim.controller;

import com.njd5475.github.slim.model.SlimFileContext;
import com.njd5475.github.slim.model.SlimFileWrapper;
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

}
