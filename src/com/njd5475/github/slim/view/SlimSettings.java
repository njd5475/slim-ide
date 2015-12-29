package com.njd5475.github.slim.view;

import java.awt.Dimension;
import java.awt.Point;

public class SlimSettings {

	private static SlimSettings	instance;
	private boolean							centeredDefault		= true;
	private Dimension						windowSizeDefault	= new Dimension(800, 600);

	private SlimSettings() {
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
