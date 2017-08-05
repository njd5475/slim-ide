package com.njd5475.github.slim.material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

public class MaterialGroup extends Material {

	public Set<IMaterial> children = new HashSet<IMaterial>();
	
	public MaterialGroup(IMaterial parent, IMaterial...children) {
		super(parent);
		this.children.addAll(Arrays.asList(children));
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
		for(IMaterial m : children) {
			m.render(renderer);
		}
	}

}
