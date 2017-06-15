package com.njd5475.github.slim.material;

/**
 * A Material is the base for the widget library. A material has physically 
 * correct properties that determine how it can be arranged and used and what
 * property changes can be made to it. And how those property changes are shown
 * to the user. Every physically property change must be shown to the user 
 * visually in some way.
 * 
 * @author nick
 */
public class Material {

	private PhysicalState state;
	
	public Material(PhysicalState state) {
		this.state = state;
	}
	
	public Material() {
	  this(new PhysicalState());
	}

}
