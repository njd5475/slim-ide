package com.njd5475.github.slim.view;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;

public class SlimSettings {

  private static SlimSettings             instance;
  private boolean                         centeredDefault   = true;
  private Dimension                       windowSizeDefault = new Dimension(800, 600);
  private Set<SlimSettingsChangeListener> listeners         = new LinkedHashSet<>();

  private SlimSettings() {

  }

  public static SlimSettings addChangeListener(SlimSettingsChangeListener listener) {
    if (instance == null) {
      loadDefaults();
    }
    instance.listeners.add(listener);
    return instance;
  }

  public static boolean removeChangeListener(SlimSettingsChangeListener listener) {
    if (instance == null) {
      throw new NullPointerException("You should not attempt to call remove when settings have not been loaded!");
    }
    return instance.listeners.remove(listener);
  }

  public static SlimSettings loadDefaults() {
    if (instance == null) {
      instance = new SlimSettings();
    }
    return instance;
  }

  public Point getWindowLocation() {
    return new Point(0, 0);
  }

  public boolean isCentered() {
    return centeredDefault;
  }

  public Dimension getWindowDimensions() {
    return windowSizeDefault;
  }
}
