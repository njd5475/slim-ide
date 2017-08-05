package com.njd5475.github.slim.material;

import java.awt.Color;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public class SolidMaterial extends Material {

	private Color color;

	public SolidMaterial(IMaterial parent, Color color) {
		super(parent);
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

	@Override
	public int getX() {
		return this.getParent().getX();
	}

	@Override
	public int getY() {
		return this.getParent().getY();
	}

	@Override
	public int getWidth() {
		return this.getParent().getWidth();
	}

	@Override
	public int getHeight() {
		return this.getParent().getHeight();
	}

	@Override
	public void render(MaterialRenderer renderer) {
		renderer.render(this);
	}

}
