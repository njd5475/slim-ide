package com.njd5475.github.slim.view;

import java.util.Set;

import com.njd5475.github.slim.model.SlimFileWrapper;

public interface EditorListener {
	
	public void filesShownChanged(Set<SlimFileWrapper> filesShown2);
	
}
