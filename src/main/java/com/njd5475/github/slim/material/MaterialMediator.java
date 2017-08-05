package com.njd5475.github.slim.material;

import java.util.HashSet;
import java.util.Set;

public class MaterialMediator {

	private Set<MaterialMouseListener> mouseListeners = new HashSet<>();
	private Set<MaterialPropertyChangeListener> propertyListeners = new HashSet<>();
	
	public MaterialMediator() {
		// TODO Auto-generated constructor stub
	}
	
	public void mouseMoved(int mouseX, int mouseY, int button) {
		for(MaterialMouseListener listens : mouseListeners) {
			listens.mouseMoved(mouseX, mouseY, button);
		}
	}
	
	public void propertyChanged(Object oldProperty, Object newProperty) {
		for(MaterialPropertyChangeListener l : propertyListeners) {
			l.propertyChanged(oldProperty, newProperty);
		}
	}
	
	public void addMouseListener(MaterialMouseListener l) {
		mouseListeners.add(l);
	}

	public void removeMouseListener(MaterialMouseListener l) {
		mouseListeners.remove(l);
	}
	
	public void addPropertyListener(MaterialPropertyChangeListener l) {
		propertyListeners.add(l);
	}
	
	public void removePropertyListener(MaterialPropertyChangeListener l) {
		propertyListeners.remove(l);
	}
}
