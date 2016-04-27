package com.njd5475.github.slim.view;

import java.awt.Graphics;

import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.model.SlimSymbolWrapper;

public interface SlimRenderVisitor {


	public void refresh();

	public void renderEditor(SlimEditor slimEditor, Graphics g);

	public void render(SlimRenderContext ctx, SlimFileWrapper slimFileWrapper);
	
	public void render(SlimRenderContext ctx, SlimLineWrapper slimLineWrapper);

	public void render(SlimRenderContext ctx, SlimSymbolWrapper slimSymbolWrapper);

	public void render(SlimRenderContext ctx, SlimWidget slimWidget);

    public void render(SlimRenderContext ctx, SlimInputWidget slimInputWidget);

}
