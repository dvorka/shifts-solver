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
	private GaePeriodPreferencesBean periodPreferences;
	
	@Persistent
	private String employeeKey;
	
	@Persistent
	private int year;
	@Persistent
	private int month;
	@Persistent
	private int day;
	
	@Persistent
	private boolean isHoliDay;
	
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

	public GaePeriodPreferencesBean getGaePeriodPreferencesBean() {
		return periodPreferences;
	}

	public void setGaePeriodPreferencesBean(GaePeriodPreferencesBean periodPreferences) {
		this.periodPreferences = periodPreferences;
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

	public boolean isHoliDay() {
		return isHoliDay;
	}

	public void setHoliDay(boolean isHoliDay) {
		this.isHoliDay = isHoliDay;
	}

	public void fromPojo(DayPreference p) {
		key=ServerUtils.stringToKey(p.getKey());
		
		year=p.getYear();
		month=p.getMonth();
		day=p.getDay();

		isHoliDay=p.isHoliDay();
		
		noDay=p.isNoDay();
		noMorning6=p.isNoMorning6();
		noMorning7=p.isNoMorning7();
		noMorning8=p.isNoMorning8();
		noAfternoon=p.isNoAfternoon();
		noNight=p.isNoNight();
		
		yesDay=p.isYesDay();
		yesMorning6=p.isYesMorning6();
		yesMorning7=p.isYesMorning7();
		yesMorning8=p.isYesMorning8();
		yesAfternoon=p.isYesAfternoon();
		yesNight=p.isYesNight();
	}
	
	public DayPreference toPojo() {
		DayPreference p=new DayPreference();
		
		p.setKey(ServerUtils.keyToString(key));
		
		p.setYear(year);
		p.setMonth(month);
		p.setDay(day);
		
		p.setHoliDay(isHoliDay);
		
		p.setNoDay(noDay);
		p.setNoMorning6(noMorning6);
		p.setNoMorning7(noMorning7);
		p.setNoMorning8(noMorning8);
		p.setNoAfternoon(noAfternoon);
		p.setNoNight(noNight);
		
		p.setYesDay(yesDay);
		p.setYesMorning6(yesMorning6);
		p.setYesMorning7(yesMorning7);
		p.setYesMorning8(yesMorning8);
		p.setYesAfternoon(yesAfternoon);
		p.setYesNight(yesNight);
				
		return p;
	}	
}
