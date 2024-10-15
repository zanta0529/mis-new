package com.proco.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorManager {
	ScheduledExecutorService[] serviceSet;
	java.util.concurrent.atomic.AtomicLong[] jobcounts;
	int size;
	long ct = System.currentTimeMillis();
	
	public ExecutorManager(int size) {
		if(size<=0) size=1;
		this.size = size;
		serviceSet = new ScheduledExecutorService[size];
		jobcounts = new java.util.concurrent.atomic.AtomicLong[size];
		for(int inx = 0 ; inx < size ; inx++) {
			serviceSet[inx] = Executors.newSingleThreadScheduledExecutor();	
			jobcounts[inx] = new java.util.concurrent.atomic.AtomicLong(0L);
		}
	}

	public void schedule(Runnable task, long delay, TimeUnit unit, String mark) {
		int inx = mark.hashCode();
		if(inx<0) {
			int a3_1 = inx;
			inx = inx-a3_1-a3_1;
		}
		inx = inx%this.size;
		jobcounts[inx].addAndGet(1);
		serviceSet[inx].schedule(task, delay, unit);
	}
	
	public void submit(Runnable task, int inx) {
		if(inx<0) {
			int a3_1 = inx;
			inx = inx-a3_1-a3_1;
		}
		inx = inx%this.size;
		jobcounts[inx].addAndGet(1);
		serviceSet[inx].submit(task);
	}
	
	public void submit(Runnable task, String mark) {
		int inx = mark.hashCode();
		if(inx<0) {
			int a3_1 = inx;
			inx = inx-a3_1-a3_1;
		}
		inx = inx%this.size;
		jobcounts[inx].addAndGet(1);
		serviceSet[inx].submit(task);
	}
	
	java.util.concurrent.ConcurrentSkipListSet<Integer> markCode = new java.util.concurrent.ConcurrentSkipListSet<Integer>();
	public boolean submitOnMark(Runnable task, String mark) {
		Integer inx = mark.hashCode();
		if(markCode.contains(inx)) return false;
		else markCode.add(inx);
		
		
		if(inx<0) {
			int a3_1 = inx;
			inx = inx-a3_1-a3_1;
		}
		inx = inx%this.size;
		jobcounts[inx].addAndGet(1);
		serviceSet[inx].submit(task);
		return true;
	}
	
	public void submitFinish(String mark) {
		Integer inx = mark.hashCode();
		markCode.remove(inx);
	}
	
	public long getAndResetCount() {
		long s = 0;
		for(int inx = 0 ; inx < size ; inx++) {
			s+= jobcounts[inx].getAndSet(0);
		}		
		long cct0 = System.currentTimeMillis();
		long cct = cct0 - ct;
		ct = cct0;
		if(cct<1000) cct=1000;
		s = s/(cct/1000);
		return s;
	}
	
	public void shutdown() {
		for(int inx = 0 ; inx < size ; inx++) {
			serviceSet[inx].shutdown();
		}		
	}
	
}
