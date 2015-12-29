package com.njd5475.github.slim.model;

import java.util.Set;

import com.njd5475.github.slim.view.SlimRenderVisitor;
import com.njd5475.github.slim.view.SlimRenderable;

public class SlimLineWrapper implements SlimRenderable, Comparable<SlimLineWrapper> {

	private SlimFileWrapper					file;
	private int											lineNum;
	private Set<SlimSymbolWrapper>	line;
	private String									originalLine;

	public SlimLineWrapper(int lineNum, String line, SlimFileWrapper wrapper) {
		this.file = wrapper;
		this.lineNum = lineNum;
		this.originalLine = line;
		this.line = SlimSymbolWrapper.build(this);
	}

	public int getHashCode() {
		return (originalLine + file.hashCode() + lineNum).hashCode();
	}

	@Override
	public void render(SlimRenderVisitor visitor) {
		visitor.render(this);
	}

	public Set<SlimSymbolWrapper> getSymbols() {
		return line;
	}

	public String getLine() {
		return originalLine;
	}

	@Override
	public int compareTo(SlimLineWrapper arg0) {
		return lineNum - arg0.lineNum;
	}

}
