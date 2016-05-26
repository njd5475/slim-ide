package com.njd5475.github.slim.view.states;

import java.util.LinkedList;
import java.util.List;

public class HelpState {

    private List<String> helpersText = new LinkedList<String>();
    
    public HelpState() {
        
    }
    
    public void add(String text) {
        if(text == null) {
            throw new NullPointerException("You cannot have null helper text!");
        }
        helpersText.add(text);
    }

}
