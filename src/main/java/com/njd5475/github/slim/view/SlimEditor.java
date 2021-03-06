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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.material.IMaterial;
import com.njd5475.github.slim.material.renderer.AwtMaterialRenderer;
import com.njd5475.github.slim.model.SlimFileWrapper;

public class SlimEditor extends JPanel {

  private Set<EditorListener>             listeners     = new HashSet<EditorListener>();
  private SlimController                  controller;
  private int                             margin        = 30;
  private int                             lineHeight    = 15;
  private boolean                         fixedWidth    = false;
  private boolean                         once;
  private double                          scrollOffsetY = 0;
  private Color                           background    = Color.white;
  private Map<RenderingHints.Key, Object> renderingHints;
  private int                             cursorLine    = 1;
  private int                             cursorColumn;
  private Set<SlimFileWrapper>            filesShown    = new HashSet<SlimFileWrapper>();
  private int                             minLine;
  private int                             maxLine;
  private AwtMaterialRenderer             awtRenderer;
  private boolean                         choosenFont;
  private Set<IMaterial>                  materials     = new LinkedHashSet<>();

  public SlimEditor(SlimSettings defaults, SlimController controller) {
    this.setPreferredSize(defaults.getWindowDimensions());
    this.controller = controller;
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
        for(IMaterial m : materials) {
          if(m.canHandle(e)) {
            m.doKey(e);
          }
        }
        if (!e.isConsumed() && e.getKeyChar() >= ' ' && e.getKeyChar() <= '~') {
          SlimEditor.this.controller.addCharacterAt(e.getKeyChar(), cursorLine, cursorColumn);
          cursorColumn++;
          clampCursor();
          SlimEditor.this.repaint();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        for (IMaterial m : materials) {
          if (m.canHandle(e)) {
            m.doKey(e);
          }
        }
        if (!e.isConsumed()) {
          if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            cursorColumn++;
          } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            cursorColumn--;
          } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            cursorLine--;
          } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            cursorLine++;
          } else if (e.getKeyCode() == KeyEvent.VK_END) {
            cursorColumn = SlimEditor.this.controller.getLineLength(cursorLine) - 1;
          } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
            cursorColumn = 0;
          } else if (e.getKeyCode() == KeyEvent.VK_T && e.isAltDown()) {
            if (SlimIDE.DEVELOPMENT) {
              SlimIDE.takeScreenshot();
            }
          } else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
            SlimEditor.this.controller.saveCurrentFile(cursorLine);
          } else if (e.getKeyCode() == KeyEvent.VK_O && e.isControlDown()) {
            SlimEditor.this.controller.openNextFile();
          } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            SlimEditor.this.controller.addLineAt(cursorLine, cursorColumn);
            cursorLine++;
            cursorColumn = 0;
          } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            SlimEditor.this.controller.removeCharacterAt(cursorLine, cursorColumn);
          } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (cursorColumn > 0) {
              SlimEditor.this.controller.removeCharacterAt(cursorLine, --cursorColumn);
            } else {
              if (cursorLine > 0) {
                // fake it
                --cursorLine;
                cursorColumn = SlimEditor.this.controller.getLine(cursorLine).length() - 1;
                SlimEditor.this.controller.removeCharacterAt(cursorLine, cursorColumn);
              }
            }
          }

          clampCursor();
          // only if the cursor goes outside the view area
          if (cursorOutsideView()) {
            scrollToCursor();
          }
        }else {
          System.out.println("Key was consumed");
        }

        SlimEditor.this.repaint();
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

      }
    });
    this.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        // TODO find line clicked on

      }

      @Override
      public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

      }

    });
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        chooseFont();
        SlimEditor.this.repaint();
      }
    });
    this.setBackground(background);
    refreshRenderingHints();
  }

  protected boolean cursorOutsideView() {
    int currentLineY = getLineHeight() * cursorLine + getLineHeight() * controller.getFileCountAt(cursorLine);

    return currentLineY < Math.abs(scrollOffsetY) || currentLineY > Math.abs(scrollOffsetY) + this.getHeight();
  }

  protected void scrollToCursor() {
    scrollOffsetY = -this.cursorLine * this.getLineHeight();
    scrollOffsetY -= (this.getLineHeight()) * (this.controller.getFileCountAt(this.cursorLine) - 1);
    scrollOffsetY = Math.min(0, scrollOffsetY);
    if (Math.abs(scrollOffsetY) + getHeight() > getMaxHeight() + getLineHeight()) {
      scrollOffsetY = -(getMaxHeight() - getHeight() + getLineHeight());
    }
  }

  protected void chooseFont() {
    if (!hasChoosenFont()) {
      Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
      Font useMe = null;
      for (Font font : fonts) {
        if (font.getName().contains("Monospaced") || font.getName().contains("Monac")) {
          System.out.println("Using: " + font.getName());
          useMe = font;
          break;
        }
      }
      if (useMe == null) {
        useMe = fonts[0];
      }
      useMe = useMe.deriveFont(12f).deriveFont(Font.PLAIN);
      this.setFont(useMe);
      this.choosenFont = true;
    }
  }

  private boolean hasChoosenFont() {
    return choosenFont;
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
    SlimController ctrl = SlimEditor.this.controller;

    if (cursorLine < 1) {
      cursorLine = 1;
    }

    if (cursorColumn >= ctrl.getLineLength(cursorLine) && cursorLine >= ctrl.getTotalLines()) {
      cursorLine = ctrl.getTotalLines();
      cursorColumn = ctrl.getLineLength(cursorLine);
    }

    if (cursorColumn < 0) {
      cursorLine--;
      if (cursorLine < 1) {
        cursorLine = 1;
      } else {
        cursorColumn = ctrl.getLineLength(cursorLine) - 1;
      }
    }

    if (cursorLine >= ctrl.getTotalLines()) {
      cursorLine = ctrl.getTotalLines();
    }
  }

  private void refreshRenderingHints() {
    if (renderingHints == null) {
      renderingHints = new HashMap<Key, Object>();
    }
    renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    // renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS,
    // RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    // renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
    // RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }

  @Override
  protected void paintComponent(Graphics init) {
    super.paintComponent(init);

    SlimRenderVisitor renderer = controller.getRenderer();
    Graphics2D g = (Graphics2D) init.create();

    g.setRenderingHints(renderingHints);
    g.translate(0, scrollOffsetY);
    renderer.renderEditor(this, g);
    if (!once) {
      System.out.println("Took " + (System.currentTimeMillis() - SlimIDE.start) + "ms to get to render");
      once = true;
    }
    g.dispose();

    g = (Graphics2D) init.create();
    if (awtRenderer == null) {
      awtRenderer = new AwtMaterialRenderer();
    }
    awtRenderer.setGraphics(g);
    for (IMaterial m : this.materials) {
      m.render(awtRenderer);
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

  public int getCurrentWindowLineStart() {
    return (int) Math.abs(this.scrollOffsetY / getLineHeight());
  }

  public int getMaxHeight() {
    return getLineHeight() * getTotalNumberOfLines() + (2 * getLineHeight()) * controller.getTotalFileCount();
  }

  private int getTotalNumberOfLines() {
    return controller.getTotalLines();
  }

  public boolean isFixedWidth() {
    return fixedWidth;
  }

  public void addMaterial(IMaterial widget) {
    this.materials.add(widget);
  }

  public void addWidgets(Collection<IMaterial> widgets) {
    this.materials.addAll(widgets);
  }

}
