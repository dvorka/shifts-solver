package com.mindforger.shiftsolver.shared.model;

import java.util.HashMap;
import java.util.Map;

public class PeriodPreferences {

	int year;
	int month;
	private Map<Employee,EmployeePreferences> employeeToPreferences;

	public PeriodPreferences(int year, int month) {
		employeeToPreferences=new HashMap<Employee,EmployeePreferences>();
		this.year=year;
		this.month=month;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void addEmployeePreferences(Employee employee, EmployeePreferences preferences) {
		employeeToPreferences.put(employee, preferences);
	}
	
	public Map<Employee, EmployeePreferences> getEmployeeToPreferences() {
		return employeeToPreferences;
	}

	public void setEmployeeToPreferences(
			Map<Employee, EmployeePreferences> employeeToPreferences) {
		this.employeeToPreferences = employeeToPreferences;
	}
}
