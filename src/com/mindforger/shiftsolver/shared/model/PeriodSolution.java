package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeriodSolution implements Serializable {
	private static final long serialVersionUID = 7586400671035292788L;

	private String key;
	private long modified;
	private String modifiedPretty;
	private String periodPreferencesKey;
	private int year;
	private int month;
	private Map<String, Job> employeeJobs;
	private List<DaySolution> days;
	
	public PeriodSolution() {
		this.days=new ArrayList<DaySolution>();
		this.employeeJobs=new HashMap<String,Job>();
	}
	
	public PeriodSolution(int year, int month) {
		this();
		this.year=year;
		this.month=month;
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

	public List<DaySolution> getDays() {
		return days;
	}

	public void setDays(List<DaySolution> days) {
		this.days = days;
	}
	
	public void addDaySolution(DaySolution daySolution) {
		this.days.add(daySolution);
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPeriodPreferencesKey() {
		return periodPreferencesKey;
	}

	public void setPeriodPreferencesKey(String periodPreferencesKey) {
		this.periodPreferencesKey = periodPreferencesKey;
	}

	public Map<String, Job> getEmployeeJobs() {
		return employeeJobs;
	}

	public void setEmployeeJobs(Map<String, Job> employeeJobs) {
		this.employeeJobs = employeeJobs;
	}

	public void addEmployeeJob(String employeeKey, Job job) {
		this.employeeJobs.put(employeeKey, job);
	}

	// TODO inefficient iterations > create map
	public DaySolution getSolutionForDay(int i) {
		if(days!=null && days.size()>=i) {
			for(DaySolution ds:days) {
				if(ds.getDay()==i) {
					return ds;
				}
			}
		}
		return null;
	}
}
