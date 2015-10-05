package com.mindforger.shiftsolver.client;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

public class GwtJavascriptClientExceptionHander implements UncaughtExceptionHandler {

	public GwtJavascriptClientExceptionHander() {
	}
	
	@Override
	public void onUncaughtException(Throwable cause) {
		System.out.println(cause.getMessage());
	}
}	
