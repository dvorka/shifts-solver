package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	@Deprecated
	String greetServer(String name) throws IllegalArgumentException;

	Employee newEmployee();
	PeriodPreferences newPeriodPreferences(int year, int month);
	PeriodPreferences newPeriodPreferences();
}
