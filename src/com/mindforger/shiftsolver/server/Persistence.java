package com.mindforger.shiftsolver.server;

import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public interface Persistence {
	
	Employee createEmployee();
	Employee saveEmployee(Employee employee);
	void deleteEmployee(String key);
	Employee[] getEmployees();

	PeriodPreferences createPeriodPreferences(PeriodPreferences periodPreferences);
	PeriodPreferences savePeriodPreferences(PeriodPreferences periodPreferences);
	void deletePeriodPreferences(String key);
	PeriodPreferences[] getPeriodPreferences();

	PeriodSolution createPeriodSolution(PeriodSolution bean);
	PeriodSolution savePeriodSolution(PeriodSolution periodSolution);
	void deletePeriodSolution(String key);
	PeriodSolution[] getPeriodSolution();
}
