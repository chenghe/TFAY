package com.minxing.client.http;

public enum HttpMethod {
	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

	private String mMethod;

	HttpMethod(String method) {
		mMethod = method;
	}

	public String getMethod() {
		return mMethod;
	}
}
