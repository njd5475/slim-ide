package com.njd5475.github.slim.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.model.SlimFileContext;
import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.model.SlimSymbolWrapper;

public class DefaultAttachableRenderer implements SlimRenderVisitor {

	private JPanel			component;
	private Graphics		g;
	private Graphics2D	currentLineG;
	private int					lineHeight;
	private int					margin;
	private Graphics2D	lineOnly;
	private int					tabWidth		= 2;
	private String			tabSpaces		= null;
	private int					lineY				= 0;
	private int					currentLine	= 0;
	private int					cursorColumn;
	private int					cursorLine;
	private int[]				widths;
	private int					currentChar;

	public DefaultAttachableRenderer() {
		resetTabWidth(tabWidth);
	}

	private void resetTabWidth(int tabWidth2) {
		StringBuilder tabs = new StringBuilder();
		for (int i = tabWidth2; i > 0; --i) {
			tabs.append(' ');
		}
		tabSpaces = tabs.toString();
	}

	public void attachToPanel(JPanel panel) {
		this.component = panel;
	}

	@Override
	public void render(SlimFileWrapper slimFileWrapper) {
		currentLineG = (Graphics2D) g.create();
		currentLineG.translate(margin, 0);
		lineY = 0;
		Rectangle bb = g.getClipBounds();

		for (SlimLineWrapper line : slimFileWrapper.getLines()) {
			currentLineG.translate(0, lineHeight);
			lineY += lineHeight;
			if (lineY > bb.getY() + bb.getHeight()) {
				break;
			}
			line.render(this);
		}
		currentLineG.drawString("Total lines " + slimFileWrapper.getLines().size(), 0, 0);
		currentLineG.dispose();
	}

	@Override
	public void refresh() {
		this.component.repaint();
	}

	@Override
	public void renderEditor(SlimEditor slimEditor, Graphics g) {
		long start = System.currentTimeMillis();
		this.g = g;
		this.widths = g.getFontMetrics().getWidths();
		SlimController controller = slimEditor.getController();
		SlimFileContext context = controller.getFileContext();
		margin = slimEditor.getCurrentMargin();
		lineHeight = slimEditor.getLineHeight();
		currentLine = 0;
		cursorColumn = slimEditor.getCursorColumn();
		cursorLine = slimEditor.getCusorLine();
		for (SlimFileWrapper wrapper : context.getFiles()) {
			wrapper.render(this);
		}
		long end = System.currentTimeMillis() - start;
		System.out.println("Render took " + end + "ms");
	}

	@Override
	public void render(SlimLineWrapper slimLineWrapper) {
		lineOnly = (Graphics2D) currentLineG.create();
		currentChar = 0;
		if (currentLine == cursorLine) {
			Graphics2D cursor = (Graphics2D) lineOnly.create();
			cursor.setColor(new Color(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 100));
			cursor.fillRect(0, -lineHeight, cursor.getClipBounds().width, lineHeight+cursor.getFontMetrics().getMaxDescent());
			cursor.dispose();
		}
		for (SlimSymbolWrapper sym : slimLineWrapper.getSymbols()) {
			sym.render(this);
		}		
		++currentLine; // increment the current line count
		lineOnly.dispose();
	}

	@Override
	public void render(SlimSymbolWrapper symbol) {

		char[] symChars = symbol.toString().toCharArray();
		Color originalBackground = lineOnly.getBackground();
		for (char c : symChars) {
			if (currentChar == cursorColumn && currentLine == cursorLine) {
				Graphics2D block = (Graphics2D) lineOnly.create();
				block.setColor(Color.BLACK);
				block.setStroke(new BasicStroke(2));
				block.drawLine(0, -lineHeight+1, 0, block.getFontMetrics().getMaxDescent());
				block.dispose();
			}
			if (c != '\t') {
				lineOnly.drawString(String.valueOf(c), 0, 0);
				lineOnly.translate(widths[c], 0);
			} else {
				lineOnly.translate(tabWidth * widths[' '], 0);
			}
			currentChar++;

		}
		// lineOnly.drawString(symbol.toString(), 0, 0);
		// lineOnly.translate(lineOnly.getFontMetrics().stringWidth(symbol.toString().replaceAll("\t",
		// tabSpaces)), 0);
	}

}
