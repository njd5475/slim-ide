package com.njd5475.github.slim.material;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.njd5475.github.slim.material.renderer.MaterialRenderer;

/**
 * A Material is the base for the widget library. A material has physically 
 * correct properties that determine how it can be arranged and used and what
 * property changes can be made to it. And how those property changes are shown
 * to the user. Every physically property change must be shown to the user 
 * visually in some way.
 * 
 * @author nick
 */
public abstract class Material implements IMaterial {

	private IMaterial parent;
	
	public Material(IMaterial parent) {
		this.parent = parent;
	}
	
	public IMaterial getParent() {
		return parent;
	}
	
	@Override
	public boolean contains(int x, int y) {
	  return x >= getX() && y >= getY() && x <= (getX()+getWidth()) && y <= (getY()+getHeight());
	}
	
	@Override
	public boolean canHandle(MouseEvent me) {
	  return false;
	}
	
	@Override
	public boolean canHandle(KeyEvent ke) {
	  return false;
	}

  @Override
  public int getX() {
    return parent.getX();
  }

  @Override
  public int getY() {
    return parent.getY();
  }

  @Override
  public int getWidth() {
    return parent.getWidth();
  }

  @Override
  public int getHeight() {
    return parent.getHeight();
  }

  @Override
  public void render(MaterialRenderer renderer) {
    if(parent != null) {
      parent.render(renderer);
    }
  }
	
	public Material topLeftCorner(double percentage) {
	  Material m = new Material(this) {
	    private double percent = percentage/100d;

      @Override
      public int getWidth() {
        return (int)(super.getWidth() * percent);
      }

      @Override
      public int getHeight() {
        return (int)(super.getHeight() * percent);
      }
	  };
	  
	  return m;
	}
	
	public Material fill(Color color) {
	  Material m = new Material(this) {
	    private Color clr = color;
	    
	    @Override
	    public void render(MaterialRenderer renderer) {
	      renderer.fill(this, clr);
	      super.render(renderer);
	    }
	  };
	  
	  return m;
	}
	
	public Material top(double percentage) {
	  Material m = new Material(this) {
	    private double percent = percentage/100d;

      @Override
      public int getHeight() {
        return (int)(super.getHeight() * percent);
      }
	  };
	  
	  return m;
	}
	
	public Material minHeight(int height) {
	  Material m = new Material(this) {
	    private int minHeight = height;

      @Override
      public int getHeight() {
        return Math.max(minHeight, super.getHeight());
      }
	  };
	  
	  return m;
	}
	
	public Material text(String toShow) {
	  Material m = new Material(this) {
	    private String text = toShow;
	    
	    @Override
	    public void render(MaterialRenderer r) {
	      super.render(r);
	      r.renderText(text, this);
	    }
	  };
	  
	  return m;
	}
}
