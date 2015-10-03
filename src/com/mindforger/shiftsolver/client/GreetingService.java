package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	
	PeriodPreferences createPeriodPreferences(int year, int month);
	
	@Deprecated
	String greetServer(String name) throws IllegalArgumentException;
}
