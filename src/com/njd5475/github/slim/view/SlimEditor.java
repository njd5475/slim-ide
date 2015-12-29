package com.njd5475.github.slim.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.njd5475.github.slim.controller.SlimController;

public class SlimEditor extends JPanel {

	private SlimController									controller;
	private int															margin				= 10;
	private int															lineHeight		= 15;
	private boolean													fixedWidth		= false;
	private boolean													once;
	private double													scrollOffsetY	= 0;
	private Color														background		= Color.white;
	private Map<RenderingHints.Key, Object>	renderingHints;
	private int cursorLine;
	private int cursorColumn;

	public SlimEditor(SlimSettings defaults, SlimController controller) {
		this.setPreferredSize(defaults.getWindowDimensions());
		this.controller = controller;
		Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		Font useMe = null;
		for (Font font : fonts) {
			if (font.getName().contains("Monospaced.plain")) {
				useMe = font;
				break;
			}
		}
		useMe = useMe.deriveFont(12f).deriveFont(Font.PLAIN);
		this.setFont(useMe);
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double rot = e.getWheelRotation();
				scrollOffsetY -= lineHeight * rot;
				scrollOffsetY = Math.min(0, scrollOffsetY);
				if (Math.abs(scrollOffsetY) + getHeight() > getMaxHeight()) {
					scrollOffsetY = -(getMaxHeight() - getHeight());
				}
				repaint();
			}
		});
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					cursorColumn++;
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					cursorColumn--;
				}else if(e.getKeyCode() == KeyEvent.VK_UP) {
					cursorLine--;
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					cursorLine++;
				}
				
				if(cursorColumn > SlimEditor.this.controller.getLineLength(cursorLine)) {
					cursorLine++;
					cursorColumn = 0;
				}
				
				if(cursorColumn < 0) {
					cursorLine--;
					cursorColumn = SlimEditor.this.controller.getLineLength(cursorLine);
				}
				
				if(cursorLine > SlimEditor.this.controller.getTotalLines()) {
					cursorLine = SlimEditor.this.controller.getTotalLines();
				}
				
				if(cursorLine < 0) {
					cursorLine = 0;
				}
				
				SlimEditor.this.repaint();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		this.setBackground(background);
		refreshRenderingHints();
	}

	private void refreshRenderingHints() {
		if (renderingHints == null) {
			renderingHints = new HashMap<Key, Object>();
		}
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	@Override
	protected void paintComponent(Graphics init) {
		super.paintComponent(init);

		SlimRenderVisitor renderer = controller.getRenderer();
		Graphics2D g = (Graphics2D) init;

		g.setRenderingHints(renderingHints);
		g.translate(0, scrollOffsetY);
		renderer.renderEditor(this, g);
		if (!once) {
			System.out.println("Took " + (System.currentTimeMillis() - SlimIDE.start) + "ms to get to render");
			once = true;
		}
		g.dispose();
	}

	public SlimController getController() {
		return controller;
	}
	
	public int getCusorLine() {
		return cursorLine;
	}
	
	public int getCursorColumn() {
		return cursorColumn;
	}

	public int getCurrentMargin() {
		return margin;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getMaxHeight() {
		return getLineHeight() * getTotalNumberOfLines();
	}

	private int getTotalNumberOfLines() {
		return controller.getTotalLines();
	}

	public boolean isFixedWidth() {
		return fixedWidth;
	}

}
