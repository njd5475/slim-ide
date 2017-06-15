package com.njd5475.github.slim.material;

public interface PhysicalProperty {

	public void change(double dt);

	public PhysicalState getCurrentState();
	
	public boolean isTransitioning();
	
}
