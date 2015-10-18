package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;

public interface GreetingServiceAsync {

	void setDaysWorkdaysStartDay(PeriodPreferences periodPreferences, AsyncCallback<PeriodPreferences> callback);
	
	void getRiaBootImage(AsyncCallback<RiaBootImageBean> asyncCallback);
	
	void newEmployee(AsyncCallback<Employee> asyncCallback);
	void saveEmployee(Employee employee, AsyncCallback<Void> callback);
	void deleteEmployee(String key, AsyncCallback<Void> callback);
	void getEmployees(AsyncCallback<Employee[]> callback);
	
	void newPeriodPreferences(AsyncCallback<PeriodPreferences> callback);
	void savePeriodPreferences(PeriodPreferences periodPreferences, AsyncCallback<Void> callback);
	void deletePeriodPreferences(String key, AsyncCallback<Void> callback);
	void getPeriodPreferences(AsyncCallback<PeriodPreferences[]> callback);
}
