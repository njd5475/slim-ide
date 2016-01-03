package com.njd5475.github.slim.controller;

import java.util.HashSet;
import java.util.Set;

import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.model.SlimSymbolWrapper;

public class SlimEditContext {

	private Set<SlimSymbolWrapper>	deleteSymbols	= new HashSet<SlimSymbolWrapper>();
	private Set<SlimLineWrapper>		deleteLines		= new HashSet<SlimLineWrapper>();
	private Set<SlimLineWrapper>		joinNextLine	= new HashSet<SlimLineWrapper>();

	public SlimEditContext() {
		// TODO Auto-generated constructor stub
	}

	public void delete(SlimSymbolWrapper sym) {
		this.deleteSymbols.add(sym);
	}

	public void delete(SlimLineWrapper line) {
		this.deleteLines.add(line);
	}

	public void apply(SlimLineWrapper line) {
		for (SlimSymbolWrapper symbol : deleteSymbols) {
			line.removeSymbol(symbol);
		}
	}

	public void joinNextLine(SlimSymbolWrapper sym) {
		this.joinNextLine.add(sym.getLine());
	}

	public void apply(SlimFileWrapper file) {
		for (SlimLineWrapper line : joinNextLine) {
			SlimLineWrapper line2 = file.getLine(line.getLineNumber() + 1);
			line.join(line2);
		}
		for(SlimLineWrapper line : deleteLines) {
			if(line.isEmpty()) {
				file.remove(line);
			}
		}
	}
}
