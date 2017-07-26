package com.minxing.client.util;

public class CacheManager {

	private static CacheManager uniqueInstance = null;
	private Object holdedShareContent = null;

	private CacheManager() {
	}

	public static CacheManager getInstance() {
		Object obj = new Object();
		synchronized (obj) {
			if (uniqueInstance == null) {
				synchronized (obj) {
					uniqueInstance = new CacheManager();
				}
			}
		}
		return uniqueInstance;
	}

	public Object getHoldedShareContent() {
		return holdedShareContent;
	}

	public void setHoldedShareContent(Object holdedShareContent) {
		this.holdedShareContent = holdedShareContent;
	}

	public void resetHoldedShareContent() {
		this.holdedShareContent = null;
	}
}
