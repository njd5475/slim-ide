package com.njd5475.github.slim.boundaries;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorBoundary {

	private static final Executor EXECUTOR = null;

	private DisruptorBoundary() {

	}

	public static void loadDisruptorCallback() {
		Disruptor<FileChangeEvent> disruptor = new Disruptor<FileChangeEvent>(FileChangeEvent.EVENT_FACTORY, 10, EXECUTOR);
		EventHandler<? super FileChangeEvent> handler1 = null;
		EventHandler<? super FileChangeEvent> handler2 = null;
		EventHandler<? super FileChangeEvent> handler3 = null;
		EventHandler<? super FileChangeEvent> handler4 = null;
		disruptor.handleEventsWith(handler1, handler2, handler3, handler4);
		RingBuffer<FileChangeEvent> ringBuffer = disruptor.start();
	}

}
