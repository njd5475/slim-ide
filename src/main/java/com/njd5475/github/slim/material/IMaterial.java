package com.njd5475.github.slim.material;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public interface IMaterial {

  public int getX();

  public int getY();

  public int getWidth();

  public int getHeight();

  public boolean contains(int x, int y);

  public boolean canHandle(MouseEvent me);

  public boolean canHandle(KeyEvent ke);

  public IMaterial getParent();

  public void render(MaterialRenderer renderer);

  public void doKey(KeyEvent e);

}
