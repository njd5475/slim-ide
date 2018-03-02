package com.njd5475.github.slim.material;

import javax.swing.JFrame;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public class Screen extends Material {

  private JFrame frame;

  public Screen(JFrame frame) {
    super(null);
    this.frame = frame;
  }
  
  @Override
  public int getX() {
    return 0;
  }

  @Override
  public int getY() {
    return 0;
  }

  @Override
  public int getWidth() {
    return frame.getContentPane().getWidth();
  }

  @Override
  public int getHeight() {
    return frame.getContentPane().getHeight();
  }

  @Override
  public void render(MaterialRenderer renderer) {
    
  }

}
