package com.njd5475.github.slim.model;

import java.util.Set;

import com.njd5475.github.slim.view.SlimRenderVisitor;
import com.njd5475.github.slim.view.SlimRenderable;

public class SlimLineWrapper implements SlimRenderable, Comparable<SlimLineWrapper> {

	private SlimFileWrapper					file;
	private int											lineNum;
	private Set<SlimSymbolWrapper>	symbols;
	private String									originalLine;

	public SlimLineWrapper(int lineNum, String line, SlimFileWrapper wrapper) {
		this.file = wrapper;
		this.lineNum = lineNum;
		this.originalLine = line;
		this.symbols = SlimSymbolWrapper.build(this);
	}

	public int getHashCode() {
		return (originalLine + file.hashCode() + lineNum).hashCode();
	}

	@Override
	public void render(SlimRenderVisitor visitor) {
		visitor.render(this);
	}

	public Set<SlimSymbolWrapper> getSymbols() {
		return symbols;
	}

	public String getLine() {
		return originalLine;
	}

	@Override
	public int compareTo(SlimLineWrapper arg0) {
		return lineNum - arg0.lineNum;
	}

	public int length() {
		//TODO: does not handle modifications
		return originalLine.length();
	}

	public void addCharacterAt(char keyChar, int cursorColumn) {
		int totalCharsReached = 0, lastCharsTotal = 0;
		for(SlimSymbolWrapper symbol : symbols) {
			lastCharsTotal = totalCharsReached;
			totalCharsReached += symbol.length();
			if(cursorColumn <= totalCharsReached) {
				symbol.addCharacterAt(keyChar, Math.abs(cursorColumn - lastCharsTotal));
				break;
			}
		}
		//rebuild original line
		originalLine = calcLine();
	}

	private String calcLine() {
		StringBuilder builder = new StringBuilder("");
		for(SlimSymbolWrapper sym : symbols) {
			builder.append(sym.toString());
		}
		return builder.toString();
	}

}
