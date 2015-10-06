package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Period preferences aka <i>dlouhan</i>.
 */
public class PeriodPreferences implements Serializable  {
	private static final long serialVersionUID = 8785936718601939671L;

	String key;
	int startWeekDay;
	int monthDays;
	int monthWorkDays;
	int year;
	int month;
	private Map<Employee,EmployeePreferences> employeeToPreferences;

	public PeriodPreferences() {		
	}
	
	public PeriodPreferences(int year, int month) {
		employeeToPreferences=new HashMap<Employee,EmployeePreferences>();
		this.year=year;
		this.month=month;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
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
	
	public int getStartWeekDay() {
		return startWeekDay;
	}

	public void setStartWeekDay(int startWeekDay) {
		this.startWeekDay = startWeekDay;
	}
	
	public int getMonthDays() {
		return monthDays;
	}

	public void setMonthDays(int monthDays) {
		this.monthDays = monthDays;
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

	public int getMonthWorkDays() {
		return monthWorkDays;
	}

	public void setMonthWorkDays(int monthWorkDays) {
		this.monthWorkDays = monthWorkDays;
	}
}
