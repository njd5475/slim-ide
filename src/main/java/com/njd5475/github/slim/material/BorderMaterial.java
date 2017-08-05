package com.njd5475.github.slim.material;

import java.awt.Insets;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public class BorderMaterial extends Material {

	private Insets insets;

	public BorderMaterial(IMaterial parent, Insets insets) {
		super(parent);
		this.insets = insets;
	}

	@Override
	public int getX() {
		return this.getParent().getX() + insets.left;
	}

	@Override
	public int getY() {
		return this.getParent().getY() + insets.top;
	}

	@Override
	public int getWidth() {
		return this.getParent().getWidth() - (insets.left + insets.right);
	}

	@Override
	public int getHeight() {
		return this.getParent().getHeight() - (insets.top + insets.bottom);
	}

	@Override
	public void render(MaterialRenderer renderer) {
		renderer.render(this);
	}

	public Insets getInsets() {
		return insets;
	}
}
