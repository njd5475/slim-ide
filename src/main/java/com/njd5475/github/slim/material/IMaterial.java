package com.njd5475.github.slim.material;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public interface IMaterial {

	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public IMaterial getParent();
	public void render(MaterialRenderer renderer);
}
