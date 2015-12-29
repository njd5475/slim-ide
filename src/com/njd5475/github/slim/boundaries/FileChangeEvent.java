package com.njd5475.github.slim.boundaries;

import java.io.File;

import com.lmax.disruptor.EventFactory;

public class FileChangeEvent {

	public static final EventFactory<FileChangeEvent>	EVENT_FACTORY	= new EventFactory<FileChangeEvent>() {
																																		@Override
																																		public FileChangeEvent newInstance() {
																																			return new FileChangeEvent();
																																		}
																																	};
	private File																			sourceFile;

	public FileChangeEvent() {

	}

	public File getChangeEvent() {
		return sourceFile;
	}

	public static void resetFileChangeEvent(File file, FileChangeEvent event) {
		if (file != null) {
			event.sourceFile = file;
		} else {
			throw new NullPointerException("Cannot change event to a null event!");
		}
	}
}
