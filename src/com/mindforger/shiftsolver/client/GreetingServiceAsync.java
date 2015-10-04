package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public interface GreetingServiceAsync {

	@Deprecated
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	void newEmployee(AsyncCallback<Employee> asyncCallback);
	void newPeriodPreferences(int year, int month, AsyncCallback<PeriodPreferences> callback);
	void newPeriodPreferences(AsyncCallback<PeriodPreferences> asyncCallback);
}
