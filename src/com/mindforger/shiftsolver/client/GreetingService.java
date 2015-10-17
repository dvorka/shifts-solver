package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	RiaBootImageBean getRiaBootImage();
	
	Employee newEmployee();
	void saveEmployee(Employee employee);
	Employee[] getEmployees();
	void deleteEmployee(String key);
	
	PeriodPreferences newPeriodPreferences();
	void savePeriodPreferences(PeriodPreferences periodPreferences);
	void deletePeriodPreferences(String key);
	PeriodPreferences[] getPeriodPreferences();
}
