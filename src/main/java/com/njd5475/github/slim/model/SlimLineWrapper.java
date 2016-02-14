package com.njd5475.github.slim.model;

import java.util.Set;

import com.njd5475.github.slim.controller.SlimEditContext;
import com.njd5475.github.slim.view.SlimRenderVisitor;
import com.njd5475.github.slim.view.SlimRenderable;

public class SlimLineWrapper implements SlimRenderable, Comparable<SlimLineWrapper> {

	private SlimFileWrapper file;
	private int lineNum;
	private Set<SlimSymbolWrapper> symbols;
	private String originalLine;

	public SlimLineWrapper(int lineNum, String line, SlimFileWrapper wrapper) {
		this.file = wrapper;
		this.lineNum = lineNum;
		this.originalLine = line;
		this.symbols = SlimSymbolWrapper.build(this);
	}

	public SlimFileWrapper getFile() {
		return file;
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
		// TODO: does not handle modifications
		return originalLine.length();
	}

	public void addCharacterAt(char keyChar, int cursorColumn) {
		int totalCharsReached = 0, lastCharsTotal = 0;
		for (SlimSymbolWrapper symbol : symbols) {
			lastCharsTotal = totalCharsReached;
			totalCharsReached += symbol.length();
			if (cursorColumn <= totalCharsReached) {
				symbol.addCharacterAt(keyChar, Math.abs(cursorColumn - lastCharsTotal));
				break;
			}
		}
		// rebuild original line
		originalLine = calcLine();
		// rebuild symbols
		this.symbols = SlimSymbolWrapper.build(this);
	}

	private String calcLine() {
		StringBuilder builder = new StringBuilder("");
		for (SlimSymbolWrapper sym : symbols) {
			builder.append(sym.toString());
		}
		return builder.toString();
	}

	public SlimSymbolWrapper getSymbolAt(int cursorColumn) {
		int totalCharsReached = 0, lastCharsTotal = 0;
		for (SlimSymbolWrapper symbol : symbols) {
			lastCharsTotal = totalCharsReached;
			totalCharsReached += symbol.length();
			if (cursorColumn < totalCharsReached) {
				return symbol;
			}
		}
		return null;
	}

	public void removeCharacterAt(int cursorColumn, SlimEditContext editContext) {
		int totalCharsReached = 0, lastCharsTotal = 0;
		for (SlimSymbolWrapper symbol : symbols) {
			lastCharsTotal = totalCharsReached;
			totalCharsReached += symbol.length();
			if (cursorColumn < totalCharsReached) {
				try {
					symbol.removeCharacterAt(Math.abs(cursorColumn - lastCharsTotal), editContext);
					if (symbol.length() == 0) {
						editContext.delete(symbol);
					}
				} catch (StringIndexOutOfBoundsException e) {
					System.err.println("String out of bounds");
				}
				break;
			}
		}
		// rebuild original line
		originalLine = calcLine();
	}

	public void removeSymbol(SlimSymbolWrapper symbol) {
		symbols.remove(symbol);
	}

	public int getLineNumber() {
		return lineNum;
	}

	public void join(SlimLineWrapper line2) {
		if (line2 == null) {
			throw new NullPointerException("You cannot join to a non-existant line");
		}

		if (file == line2.file) {
			this.originalLine += line2.originalLine;
			this.originalLine = this.originalLine.replaceAll("\n", "") + "\n";
			this.symbols = SlimSymbolWrapper.build(this); // rebuild the symbols
			// delete line 2
			line2.getFile().remove(line2);
		}
	}

	public boolean isEmpty() {
		return this.symbols.isEmpty();
	}

	public void lineDeleted(SlimLineWrapper line) {
		if (this.file == line.file && this.lineNum > line.lineNum) {
			this.lineNum--;
		}
	}

	public void lineInserted(SlimLineWrapper line) {
		if (this.file == line.file && line.lineNum <= lineNum) {
			this.lineNum++;
		}
	}

}
