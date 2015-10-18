package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaePeriodPreferencesBean implements Serializable, GaeBean {
	private static final long serialVersionUID = -3749422859603583246L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;

	@Persistent
	int startWeekDay;
	@Persistent
	int monthDays;
	@Persistent
	int monthWorkDays;
	@Persistent
	int year;
	@Persistent
	int month;

	@Persistent
	String lastMonthEditor; 
	
	// private Map<String,EmployeePreferences> employeeToPreferences;
	
	public GaePeriodPreferencesBean() {		
	}
	
	@Override
	public Key getKey() {
		return key;
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

	public int getMonthWorkDays() {
		return monthWorkDays;
	}

	public void setMonthWorkDays(int monthWorkDays) {
		this.monthWorkDays = monthWorkDays;
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

	public void setKey(Key key) {
		this.key = key;
	}
	
	public String getLastMonthEditor() {
		return lastMonthEditor;
	}

	public void setLastMonthEditor(String lastMonthEditor) {
		this.lastMonthEditor = lastMonthEditor;
	}

	public void fromPojo(PeriodPreferences e) {
		key=ServerUtils.stringToKey(e.getKey());
		month=e.getMonth();
		monthDays=e.getMonthDays();
		monthWorkDays=e.getMonthWorkDays();
		startWeekDay=e.getStartWeekDay();
		year=e.getYear();
		lastMonthEditor=e.getLastMonthEditor();
	}
	
	public PeriodPreferences toPojo() {
		PeriodPreferences e=new PeriodPreferences();
		e.setKey(ServerUtils.keyToString(key));
		e.setMonth(month);
		e.setMonthDays(monthDays);
		e.setMonthWorkDays(monthWorkDays);
		e.setStartWeekDay(startWeekDay);
		e.setYear(year);
		e.setLastMonthEditor(lastMonthEditor);
		return e;
	}
}
