package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public interface GreetingServiceAsync {

	void createPeriodPreferences(int year, int month, AsyncCallback<PeriodPreferences> callback);
	
	@Deprecated
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
