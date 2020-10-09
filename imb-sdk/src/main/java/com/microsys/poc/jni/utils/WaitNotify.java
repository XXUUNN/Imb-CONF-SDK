package com.microsys.poc.jni.utils;

public class WaitNotify {
	public static WaitNotify waitNotifyObj = new WaitNotify();
	public static boolean wasNotified = false;
	
	public static void doWait() {
		synchronized (waitNotifyObj) {
			if (!wasNotified) {
				try {
					waitNotifyObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			wasNotified = false;
		}
	}
	
	public static void doNotify() {
		synchronized (waitNotifyObj) {
			wasNotified = true;
			waitNotifyObj.notify();
		}
	}
}
