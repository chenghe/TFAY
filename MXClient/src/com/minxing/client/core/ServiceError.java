package com.minxing.client.core;

import java.io.Serializable;

public class ServiceError implements Serializable {

	private static final long serialVersionUID = -4709234314340568565L;
	private int errors;
	private String message;

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
