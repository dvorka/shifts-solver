package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Period preferences aka <i>dlouhan</i>.
 */
public class PeriodPreferences implements Serializable  {
	private static final long serialVersionUID = 8785936718601939671L;

	private String key;
	private long modified;
	private String modifiedPretty;
	private int startWeekDay;
	private int monthDays;
	private int monthWorkDays;
	private int year;
	private int month;
	private Map<String,EmployeePreferences> employeeToPreferences;
	private String lastMonthEditor;
	
	public PeriodPreferences() {		
	}
	
	public PeriodPreferences(int year, int month) {
		employeeToPreferences=new HashMap<String,EmployeePreferences>();
		this.year=year;
		this.month=month;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getModifiedPretty() {
		return modifiedPretty;
	}

	public void setModifiedPretty(String modifiedPretty) {
		this.modifiedPretty = modifiedPretty;
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

	public void addEmployeePreferences(String employeeKey, EmployeePreferences preferences) {
		employeeToPreferences.put(employeeKey, preferences);
	}
	
	public Map<String, EmployeePreferences> getEmployeeToPreferences() {
		return employeeToPreferences;
	}

	public void setEmployeeToPreferences(Map<String, EmployeePreferences> employeeToPreferences) {
		this.employeeToPreferences = employeeToPreferences;
	}

	public int getMonthWorkDays() {
		return monthWorkDays;
	}

	public void setMonthWorkDays(int monthWorkDays) {
		this.monthWorkDays = monthWorkDays;
	}

	public String getLastMonthEditor() {
		return lastMonthEditor;
	}

	public void setLastMonthEditor(String lastMonthEditor) {
		this.lastMonthEditor = lastMonthEditor;
	}
}
