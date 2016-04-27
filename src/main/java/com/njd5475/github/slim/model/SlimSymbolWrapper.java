package com.njd5475.github.slim.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.njd5475.github.slim.controller.SlimEditContext;
import com.njd5475.github.slim.view.SlimRenderContext;
import com.njd5475.github.slim.view.SlimRenderVisitor;
import com.njd5475.github.slim.view.SlimRenderable;

public class SlimSymbolWrapper implements SlimRenderable {

    private static SlimSymbolWrapper next;
    private SlimLineWrapper          line;
    private StringBuilder            symbols = new StringBuilder();
    private String                   symbolType;

    private SlimSymbolWrapper(SlimLineWrapper line) {
        this.line = line;
    }

    public String toString() {
        return symbols.toString();
    }

    @Override
    public void render(SlimRenderContext ctx, SlimRenderVisitor visitor) {
        visitor.render(ctx, this);
    }

    private static String getType(char c) {
        if (c == ' ' || c == '\t') {
            return "space";
        } else if (c == '\n' || c == '\r') {
            return "line-ending";
        } else if (c >= '0' || c <= '9') {
            return "number";
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return "letter";
        } else {
            return "symbol";
        }
    }

    private static SlimSymbolWrapper buildNext(SlimLineWrapper wrapper, char c) {
        if (next == null || wrapper != next.line || (next.symbolType != null && getType(c) != next.symbolType)) {
            next = new SlimSymbolWrapper(wrapper);
            next.symbolType = getType(c);
        }

        next.symbols.append(c);

        return next;
    }

    public static Set<SlimSymbolWrapper> build(SlimLineWrapper line) {
        next = null;
        Set<SlimSymbolWrapper> symbols = new LinkedHashSet<SlimSymbolWrapper>();
        for (char c : line.getLine().toCharArray()) {
            symbols.add(SlimSymbolWrapper.buildNext(line, c));
        }
        next = null;
        return symbols;
    }

    public SlimLineWrapper getLine() {
        return line;
    }

    public int length() {
        return symbols.length();
    }

    public void addCharacterAt(char keyChar, int i) {
        symbols.insert(i, keyChar);
    }

    public void removeCharacterAt(int abs, SlimEditContext editContext) {
        if ("line-ending".equals(symbolType)) {
            editContext.joinNextLine(this);
        } else {
            if (symbols.length() > 0) {
                symbols.deleteCharAt(abs);
                if (symbols.length() == 0) {
                    editContext.delete(this);
                }
            } else {
                System.err.println("Symbol is empty");
            }
        }
    }
}
