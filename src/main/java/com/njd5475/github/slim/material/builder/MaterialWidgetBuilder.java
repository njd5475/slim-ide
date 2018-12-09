package com.njd5475.github.slim.material.builder;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JFrame;

import com.njd5475.github.slim.material.IMaterial;
import com.njd5475.github.slim.material.Material;
import com.njd5475.github.slim.material.Screen;

public class MaterialWidgetBuilder {

  private JFrame         frame;
  private Material       object;
  private Set<IMaterial> materials = new LinkedHashSet<>();

  public MaterialWidgetBuilder(JFrame frame, Set<IMaterial> collection) {
    this.frame = frame;
    object = new Screen(frame);
    this.materials = collection;
  }

  public void newMaterial() {
    object = new Screen(frame);
  }

  public Material build() {
    materials.add(object);
    return object;
  }

  public void top(int percentage) {
    object = object.top(percentage);
  }

  public void minHeight(int i) {
    object = object.minHeight(i);
  }

  public void fill(Color color) {
    object = object.fill(color);
  }

  public void text(String toShow) {
    object = object.text(toShow);
  }

  public void dynamicText(String toShow) {
    object = object.dynamicText(toShow);
  }

}
