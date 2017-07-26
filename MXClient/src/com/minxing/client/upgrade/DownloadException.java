package com.minxing.client.upgrade;

public class DownloadException extends Exception {
	private static final long serialVersionUID = 7985340174166864851L;
	private String mExtra;

	public DownloadException(String message) {
		super(message);
	}

	public DownloadException(String message, String extra) {
		super(message);
		mExtra = extra;
	}

	public String getExtra() {
		return mExtra;
	}
}
