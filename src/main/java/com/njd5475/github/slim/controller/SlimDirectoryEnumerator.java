package com.njd5475.github.slim.controller;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.io.File;

public class SlimDirectoryEnumerator {

	private static FileProcessingEventFactory factory;
	private static Disruptor<FileProcessingEvent> disruptor;
  private static FileProcessingEventProducer producer; 

	public static void main(String...args) {
		System.out.println("Started Directory Enumerator");
		// Executor that will be used to construct new threads for consumers
    Executor executor = Executors.newCachedThreadPool();

    // The factory for the event
    factory = new FileProcessingEventFactory();

    // Specify the size of the ring buffer, must be power of 2.
    int bufferSize = 1024;

    // Construct the Disruptor
    disruptor = new Disruptor<FileProcessingEvent>(factory, bufferSize, executor);

    // Connect the handler
    //disruptor.handleEventsWith(new FileProcessingEventHandler());
    producer = new FileProcessingEventProducer();
    disruptor.handleEventsWith(producer);

    // Start the Disruptor, starts all threads running
    disruptor.start();

    // Get the ring buffer from the Disruptor to be used for publishing.
    RingBuffer<FileProcessingEvent> ringBuffer = disruptor.getRingBuffer();

		disruptor.publishEvent(producer);
	}

	static class FileProcessingEventHandler implements EventHandler<FileProcessingEvent> {
		public void onEvent(FileProcessingEvent event, long time, boolean duplicate) {

		}
	}

  static class FileProcessingEventProducer implements EventTranslator<FileProcessingEvent>, EventHandler<FileProcessingEvent> {
		File currentFile = null;
		public FileProcessingEventProducer() {
		}

		private void processFile(File fileToProcess) {
			if(fileToProcess != null) {
				if(fileToProcess.isFile()) {
					System.out.println(fileToProcess);
				}else{
					for(File f : fileToProcess.listFiles()) {
						currentFile = f;
						disruptor.publishEvent(this);
					}
				}
			}
		}

		public void onEvent(FileProcessingEvent event, long time, boolean duplicate) {
			System.out.println("event.file " + event.file);
			processFile(event.file);
		}

		public void translateTo(FileProcessingEvent event, long sequence) {
			event.file = currentFile;
		}
  }

	static class FileProcessingEvent {
		File file;
	}

	static class FileProcessingEventFactory implements EventFactory<FileProcessingEvent> {
		public FileProcessingEvent newInstance() {
			return new FileProcessingEvent();
		}
	}
}
