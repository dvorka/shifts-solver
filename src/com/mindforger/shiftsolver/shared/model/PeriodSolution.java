package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeriodSolution implements Serializable {
	private static final long serialVersionUID = 7586400671035292788L;

	private String key;
	private String periodPreferencesKey;
	private int year;
	private int month; 
	private List<DaySolution> days;
	
	public PeriodSolution() {
	}
	
	public PeriodSolution(int year, int month) {
		this.year=year;
		this.month=month;
		this.days=new ArrayList<DaySolution>();
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

	public String getDlouhanKey() {
		return periodPreferencesKey;
	}

	public void setDlouhanKey(String dlouhanKey) {
		this.periodPreferencesKey = dlouhanKey;
	}
}
