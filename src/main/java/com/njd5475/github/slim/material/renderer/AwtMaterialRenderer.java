package com.njd5475.github.slim.material.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

import com.njd5475.github.slim.material.BorderMaterial;
import com.njd5475.github.slim.material.Material;
import com.njd5475.github.slim.material.MaterialGroup;
import com.njd5475.github.slim.material.SolidMaterial;

public class AwtMaterialRenderer implements MaterialRenderer {

	private Graphics2D g;

	public AwtMaterialRenderer() {
		// TODO Auto-generated constructor stub
	}
	
	public void setGraphics(Graphics2D g) {
		this.g = g;
	}

	@Override
	public void render(BorderMaterial m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(MaterialGroup m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(SolidMaterial m) {
		Color oldColor = g.getColor();
		g.setColor(m.getColor());
		g.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
		g.setColor(oldColor);
	}

  @Override
  public void fill(Material m, Color clr) {
    Color last = g.getColor();
    g.setColor(clr);
    g.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
    g.setColor(last);
  }

}
