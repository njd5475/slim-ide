package com.njd5475.github.slim.view;

public class SlimInputWidget implements SlimRenderable {

    private String text;

    public SlimInputWidget(String text) {
        this.text = text;
    }
    
    public String getText() {
    	return text;
    }

    @Override
    public void render(SlimRenderContext ctx, SlimRenderVisitor visitor) {
        visitor.render(ctx, this);
    }
    
}
