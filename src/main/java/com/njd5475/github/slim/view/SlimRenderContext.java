package com.njd5475.github.slim.view;

import java.awt.Graphics2D;

public class SlimRenderContext {

    private Graphics2D g;

    public SlimRenderContext(Graphics2D g) {
        this.g = g;
    }

    public void translate(double x, double y) {
        g.translate(x, y);
    }

    public void dispose() {
        g.dispose();
    }
}
