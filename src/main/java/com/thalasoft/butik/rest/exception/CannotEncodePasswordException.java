package com.thalasoft.butik.rest.exception;

import com.thalasoft.butik.data.exception.EnrichableException;

@SuppressWarnings("serial")
public class CannotEncodePasswordException extends EnrichableException {

	public CannotEncodePasswordException() {
		super("The password could not be encoded.");
	}

	public CannotEncodePasswordException(String message) {
		super(message);
	}

}