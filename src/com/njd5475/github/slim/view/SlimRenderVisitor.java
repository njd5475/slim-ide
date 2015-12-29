package com.njd5475.github.slim.view;

import java.awt.Graphics;

import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimLineWrapper;
import com.njd5475.github.slim.model.SlimSymbolWrapper;

public interface SlimRenderVisitor {

	public void render(SlimFileWrapper slimFileWrapper);

	public void refresh();

	public void renderEditor(SlimEditor slimEditor, Graphics g);

	public void render(SlimLineWrapper slimLineWrapper);

	public void render(SlimSymbolWrapper slimSymbolWrapper);

}
