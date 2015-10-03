package com.mindforger.shiftsolver.shared.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Period preferences aka <i>dlouhan</i>.
 */
public class PeriodPreferences {

	String key;
	int startWeekDay;
	int monthDays;
	int year;
	int month;
	private Map<Employee,EmployeePreferences> employeeToPreferences;

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
}
