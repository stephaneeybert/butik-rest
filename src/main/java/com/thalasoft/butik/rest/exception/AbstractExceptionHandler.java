package com.thalasoft.butik.rest.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class AbstractExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	protected String localizeErrorMessage(String errorCode, Object args[]) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(errorCode, args, locale);
	}
	
	protected String localizeErrorMessage(String errorCode) {
		return localizeErrorMessage(errorCode, null);
	}
	
	protected String extractProductIdFromUrl(String url) {
		String productId = null;
		try {
			URI uri = new URI(url);
			String path = uri.getPath();
			productId = path.substring(path.lastIndexOf('/') + 1);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
        return productId;
	}

	protected String getStackTrace(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}
	
}
