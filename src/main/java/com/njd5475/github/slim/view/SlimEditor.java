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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.model.SlimFileWrapper;

public class SlimEditor extends JPanel {

	private Set<EditorListener>							listeners			= new HashSet<EditorListener>();
	private SlimController									controller;
	private int															margin				= 10;
	private int															lineHeight		= 15;
	private boolean													fixedWidth		= false;
	private boolean													once;
	private double													scrollOffsetY	= 0;
	private Color														background		= Color.white;
	private Map<RenderingHints.Key, Object>	renderingHints;
	private int															cursorLine;
	private int															cursorColumn;
	private Set<SlimFileWrapper>						filesShown		= new HashSet<SlimFileWrapper>();
	private int															minLine;
	private int															maxLine;

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
		if(useMe == null) {
			useMe = fonts[0];
		}
		useMe = useMe.deriveFont(12f).deriveFont(Font.PLAIN);
		this.setFont(useMe);
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double rot = e.getWheelRotation();
				scrollOffsetY -= lineHeight * rot * 2;
				scrollOffsetY = Math.min(0, scrollOffsetY);
				if (Math.abs(scrollOffsetY) + getHeight() > getMaxHeight() + getLineHeight()) {
					scrollOffsetY = -(getMaxHeight() - getHeight() + getLineHeight());
				}
				calcLinesShown();
				repaint();
			}
		});
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() >= ' ' && e.getKeyChar() <= '~') {
					SlimEditor.this.controller.addCharacterAt(e.getKeyChar(), cursorLine, cursorColumn);
					cursorColumn++;
					clampCursor();
					SlimEditor.this.repaint();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					cursorColumn++;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					cursorColumn--;
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					cursorLine--;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					cursorLine++;
				} else if (e.getKeyCode() == KeyEvent.VK_T && e.isAltDown()) {
					if (SlimIDE.DEVELOPMENT) {
						SlimIDE.takeScreenshot();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					SlimEditor.this.controller.removeCharacterAt(cursorLine, cursorColumn);
				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (cursorColumn > 0) {
						SlimEditor.this.controller.removeCharacterAt(cursorLine, --cursorColumn);
					}
				}

				clampCursor();

				SlimEditor.this.repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.setBackground(background);
		refreshRenderingHints();
	}

	protected void calcLinesShown() {
		double scrollY = Math.abs(scrollOffsetY);
		minLine = (int) Math.floor(scrollY / getLineHeight());
		maxLine = (int) Math.ceil((scrollY + getHeight()) / getLineHeight());
		Set<SlimFileWrapper> nowShown = controller.getFilesForLines(minLine, maxLine);
		if (!nowShown.containsAll(filesShown) || nowShown.size() != filesShown.size()) {
			// it's changed so update and notify
			filesShown = nowShown;
			notifyListenersFilesShownChanged(filesShown);
		}
	}

	private void notifyListenersFilesShownChanged(Set<SlimFileWrapper> filesShown2) {
		for (EditorListener l : listeners) {
			l.filesShownChanged(filesShown2);
		}
	}

	public void addEditorListener(EditorListener listener) {
		listeners.add(listener);
	}

	protected void clampCursor() {
		if (cursorLine > SlimEditor.this.controller.getTotalLines()) {
			cursorLine = SlimEditor.this.controller.getTotalLines();
		}

		if (cursorLine < 0) {
			cursorLine = 0;
		}

		if (cursorColumn > SlimEditor.this.controller.getLineLength(cursorLine)) {
			cursorLine++;
			if (cursorLine > SlimEditor.this.controller.getTotalLines()) {
				cursorLine = SlimEditor.this.controller.getTotalLines();
			}
			cursorColumn = 0;
		}

		if (cursorColumn < 0) {
			cursorLine--;
			if (cursorLine < 0) {
				cursorLine = 0;
			}
			cursorColumn = SlimEditor.this.controller.getLineLength(cursorLine);
		}
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
		return getLineHeight() * getTotalNumberOfLines() + getLineHeight() * controller.getTotalFileCount();
	}

	private int getTotalNumberOfLines() {
		return controller.getTotalLines();
	}

	public boolean isFixedWidth() {
		return fixedWidth;
	}

}
