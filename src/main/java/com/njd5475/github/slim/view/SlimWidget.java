package com.njd5475.github.slim.view;

public class SlimWidget implements SlimRenderable {

	public SlimWidget() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(SlimRenderVisitor visitor) {
		visitor.render(this);
	}

}
