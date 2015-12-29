package com.njd5475.github.slim.controller;

import com.njd5475.github.slim.model.SlimFileWrapper;

public interface FileChangeListener {

	public void onNewFileLoaded(SlimFileWrapper newFile);

}
