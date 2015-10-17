package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.DayPreference;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaeEmployeeDayPreferenceBean  implements Serializable, GaeBean {
	private static final long serialVersionUID = 6260867295415811314L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;

	@Persistent
	private String periodPreferencesKey;
	@Persistent
	private String employeeKey;
	
	@Persistent
	private int year;
	@Persistent
	private int month;
	@Persistent
	private int day;
	
	@Persistent
	private boolean noDay;
	@Persistent
	private boolean noMorning6;
	@Persistent
	private boolean noMorning7;
	@Persistent
	private boolean noMorning8;
	@Persistent
	private boolean noAfternoon;
	@Persistent
	private boolean noNight;

	@Persistent
	private boolean yesDay;
	@Persistent
	private boolean yesMorning6;
	@Persistent
	private boolean yesMorning7;
	@Persistent
	private boolean yesMorning8;
	@Persistent
	private boolean yesAfternoon;
	@Persistent
	private boolean yesNight;	

	public GaeEmployeeDayPreferenceBean() {
	}
	
	@Override
	public Key getKey() {
		return key;
	}

	public String getPeriodPreferencesKey() {
		return periodPreferencesKey;
	}

	public void setPeriodPreferencesKey(String periodPreferencesKey) {
		this.periodPreferencesKey = periodPreferencesKey;
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

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isNoDay() {
		return noDay;
	}

	public void setNoDay(boolean noDay) {
		this.noDay = noDay;
	}

	public boolean isNoMorning6() {
		return noMorning6;
	}

	public void setNoMorning6(boolean noMorning6) {
		this.noMorning6 = noMorning6;
	}

	public boolean isNoMorning7() {
		return noMorning7;
	}

	public void setNoMorning7(boolean noMorning7) {
		this.noMorning7 = noMorning7;
	}

	public boolean isNoMorning8() {
		return noMorning8;
	}

	public void setNoMorning8(boolean noMorning8) {
		this.noMorning8 = noMorning8;
	}

	public boolean isNoAfternoon() {
		return noAfternoon;
	}

	public void setNoAfternoon(boolean noAfternoon) {
		this.noAfternoon = noAfternoon;
	}

	public boolean isNoNight() {
		return noNight;
	}

	public void setNoNight(boolean noNight) {
		this.noNight = noNight;
	}

	public boolean isYesDay() {
		return yesDay;
	}

	public void setYesDay(boolean yesDay) {
		this.yesDay = yesDay;
	}

	public boolean isYesMorning6() {
		return yesMorning6;
	}

	public void setYesMorning6(boolean yesMorning6) {
		this.yesMorning6 = yesMorning6;
	}

	public boolean isYesMorning7() {
		return yesMorning7;
	}

	public void setYesMorning7(boolean yesMorning7) {
		this.yesMorning7 = yesMorning7;
	}

	public boolean isYesMorning8() {
		return yesMorning8;
	}

	public void setYesMorning8(boolean yesMorning8) {
		this.yesMorning8 = yesMorning8;
	}

	public boolean isYesAfternoon() {
		return yesAfternoon;
	}

	public void setYesAfternoon(boolean yesAfternoon) {
		this.yesAfternoon = yesAfternoon;
	}

	public boolean isYesNight() {
		return yesNight;
	}

	public void setYesNight(boolean yesNight) {
		this.yesNight = yesNight;
	}

	public void setKey(Key key) {
		this.key = key;
	}
	
	public String getEmployeeKey() {
		return employeeKey;
	}

	public void setEmployeeKey(String employeeKey) {
		this.employeeKey = employeeKey;
	}

	public void fromPojo(DayPreference e) {
		key=ServerUtils.stringToKey(e.getKey());
		
		year=e.getYear();
		month=e.getMonth();
		day=e.getDay();
		
		noDay=e.isNoDay();
		noMorning6=e.isNoMorning6();
		noMorning7=e.isNoMorning7();
		noMorning8=e.isNoMorning8();
		noAfternoon=e.isNoAfternoon();
		noNight=e.isNoNight();
		
		yesDay=e.isYesDay();
		yesMorning6=e.isYesMorning6();
		yesMorning7=e.isYesMorning7();
		yesMorning8=e.isYesMorning8();
		yesAfternoon=e.isYesAfternoon();
		yesNight=e.isYesNight();
	}
	
	public DayPreference toPojo() {
		DayPreference e=new DayPreference();
		
		e.setYear(year);
		e.setMonth(month);
		e.setDay(day);
		
		e.setNoDay(noDay);
		e.setNoMorning6(noMorning6);
		e.setNoMorning7(noMorning7);
		e.setNoMorning8(noMorning8);
		e.setNoAfternoon(noAfternoon);
		e.setNoNight(noNight);
		
		e.setYesDay(yesDay);
		e.setYesMorning6(yesMorning6);
		e.setYesMorning7(yesMorning7);
		e.setYesMorning8(yesMorning8);
		e.setYesAfternoon(yesAfternoon);
		e.setYesNight(yesNight);
				
		return e;
	}	
}
