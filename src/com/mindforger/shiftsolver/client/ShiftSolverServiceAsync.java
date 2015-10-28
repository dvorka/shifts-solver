package com.mindforger.shiftsolver.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;

public interface ShiftSolverServiceAsync {

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

	void newPeriodSolution(PeriodSolution periodSolution, AsyncCallback<PeriodSolution> callback);
	void savePeriodSolution(PeriodSolution periodSolution,AsyncCallback<Void> callback);
	void deletePeriodSolution(String key, AsyncCallback<Void> callback);
	void getPeriodSolution(AsyncCallback<PeriodSolution[]> callback);
}
