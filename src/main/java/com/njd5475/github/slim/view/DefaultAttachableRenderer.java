package com.njd5475.github.slim.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.model.SlimFilesContext;
import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.model.SlimSymbolWrapper;

public class DefaultAttachableRenderer implements SlimRenderVisitor {

  private JPanel     component;
  private Graphics   g;
  private Graphics2D currentLineG;
  private int        lineHeight;
  private int        margin;
  private Graphics2D lineOnly;
  private int        tabWidth    = 2;
  private String     tabSpaces   = null;
  private int        lineY       = 0;
  private int        currentLine = 0;
  private int        cursorColumn;
  private int        cursorLine;
  private int[]      widths;
  private int        currentChar;
  private int        drawWidth;
  private int        maxDescent;

  public DefaultAttachableRenderer() {
    resetTabWidth(tabWidth);
  }

  private void resetTabWidth(int tabWidth2) {
    StringBuilder tabs = new StringBuilder();
    for(int i = tabWidth2; i > 0; --i) {
      tabs.append(' ');
    }
    tabSpaces = tabs.toString();
  }

  public void attachToPanel(JPanel panel) {
    this.component = panel;
  }

  @Override
  public void render(SlimRenderContext ctx, SlimFileWrapper slimFileWrapper) {
    currentLineG = (Graphics2D) g.create();
    lineY = 0;
    Rectangle bb = g.getClipBounds();

    Graphics2D cursor = (Graphics2D) currentLineG.create();
    renderFileSeparator(cursor);
    cursor.drawString("Current File: " + slimFileWrapper.getFile().getName() + " Total lines " + slimFileWrapper.getLines().size(), 0, lineHeight);
    cursor.dispose();
    
    currentLineG.translate(margin, 0);
    for(SlimLineWrapper line: slimFileWrapper.getLines()) {
      currentLineG.translate(0, lineHeight);
      lineY += lineHeight;
      if(lineY > bb.getY() + bb.getHeight()) {
        break;
      }
      line.render(ctx, this);
    }
    currentLineG.dispose();
  }
  
  private void renderFileSeparator(Graphics2D cursor) {
    cursor.setColor(new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), 100));
    cursor.fillRect(0, 0, cursor.getClipBounds().width, lineHeight + maxDescent);
    cursor.setColor(Color.black);
    cursor.drawLine(0, 0, cursor.getClipBounds().width, 0);
  }

  @Override
  public void refresh() {
    if(this.component != null) {
      this.component.repaint();
    }
  }

  @Override
  public void renderEditor(SlimEditor slimEditor, Graphics g) {
    SlimRenderContext ctx = new SlimRenderContext((Graphics2D) g.create());
    long start = System.currentTimeMillis();
    this.g = g;
    this.widths = g.getFontMetrics().getWidths();
    this.maxDescent = g.getFontMetrics().getMaxDescent();
    SlimController controller = slimEditor.getController();
    SlimFilesContext context = controller.getFileContext();
    margin = slimEditor.getCurrentMargin();
    lineHeight = slimEditor.getLineHeight();
    drawWidth = slimEditor.getWidth();
    currentLine = 1;
    cursorColumn = slimEditor.getCursorColumn();
    cursorLine = slimEditor.getCusorLine();
    if(context.getNextFile() != null) {
      String nextFile = String.format("Next File: %s", context.getNextFile());
      int totalWidth = g.getClipBounds().width - margin;
      int strWidth = g.getFontMetrics().stringWidth(nextFile);
      String nextStr = nextFile.toString();
      try {
        nextStr = nextStr.replace((new File(".")).getCanonicalPath(), "");
      } catch(IOException ioe) {
      }
      if(context.hasFiles()) {
        for(SlimFileWrapper wrapper: context.getFiles()) {
          wrapper.render(ctx, this);
          g.translate(0, (wrapper.getLineCount() * lineHeight) + lineHeight);
        }
      } else {
        g.drawString(nextStr, totalWidth / 2 - strWidth / 2, g.getClipBounds().height / 2 - lineHeight - maxDescent);
      }
      g.drawString(nextStr, totalWidth - strWidth, lineHeight - maxDescent);
    } else {
      int y = 20;
      for(SlimFileWrapper wrapper: context.getFiles()) {
        g.drawString(wrapper.getFile().getAbsolutePath(), 0, y);
        y += 20;
      }
    }
    long end = System.currentTimeMillis() - start;
    //System.out.println("Render took " + end + "ms");
    ctx.dispose();
  }

  @Override
  public void render(SlimRenderContext ctx, SlimLineWrapper slimLineWrapper) {
    lineOnly = (Graphics2D) currentLineG.create();
    currentChar = 0;
    lineOnly.translate(0, lineHeight);

    Graphics2D marg = (Graphics2D) lineOnly.create();
    marg.translate(-margin, 0);
    marg.drawString(String.valueOf(slimLineWrapper.getLineNumber()), 0, 0);
    marg.dispose();
    if(currentLine == cursorLine) {
      Graphics2D cursor = (Graphics2D) lineOnly.create();
      cursor.setColor(new Color(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 100));
      cursor.fillRect(0, -lineHeight, cursor.getClipBounds().width, lineHeight + maxDescent);
      cursor.dispose();
    }
    for(SlimSymbolWrapper sym: slimLineWrapper.getSymbols()) {
      sym.render(ctx, this);
    }
    ++currentLine; // increment the current line count
    lineOnly.dispose();
  }

  @Override
  public void render(SlimRenderContext ctx, SlimSymbolWrapper symbol) {

    char[] symChars = symbol.toString().toCharArray();
    for(char c: symChars) {
      if(currentChar == cursorColumn && currentLine == cursorLine) {
        Graphics2D block = (Graphics2D) lineOnly.create();
        block.setColor(Color.BLACK);
        block.setStroke(new BasicStroke(2));
        block.drawLine(0, -lineHeight + 1, 0, block.getFontMetrics().getMaxDescent());
        block.dispose();
      }
      if(c != '\t') {
        lineOnly.drawString(String.valueOf(c), 0, 0);
        try {
          lineOnly.translate(widths[c], 0);
        } catch(ArrayIndexOutOfBoundsException e) {
          lineOnly.translate(25, 0);
        }
      } else {
        lineOnly.translate(tabWidth * widths[' '], 0);
      }
      currentChar++;
    }
    // lineOnly.drawString(symbol.toString(), 0, 0);
    // lineOnly.translate(lineOnly.getFontMetrics().stringWidth(symbol.toString().replaceAll("\t",
    // tabSpaces)), 0);
  }

  @Override
  public void render(SlimRenderContext ctx, SlimWidget slimWidget) {

  }

  @Override
  public void render(SlimRenderContext ctx, SlimInputWidget slimInputWidget) {

  }

}
