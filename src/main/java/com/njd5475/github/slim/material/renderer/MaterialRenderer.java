package com.njd5475.github.slim.material.renderer;

import java.awt.Color;

import com.njd5475.github.slim.material.BorderMaterial;
import com.njd5475.github.slim.material.Material;
import com.njd5475.github.slim.material.MaterialGroup;
import com.njd5475.github.slim.material.SolidMaterial;

public interface MaterialRenderer {

	public void render(BorderMaterial bordered);
	public void render(MaterialGroup group);
	public void render(SolidMaterial solidMaterial);
  public void fill(Material m, Color clr);
	
}
