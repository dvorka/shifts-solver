package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;

public class DayPreference implements Serializable {
	private static final long serialVersionUID = 1302160963964962116L;

	private String key;
	
	private int year;
	private int month;
	private int day;
	
	// three state preferences: NO, I CANNOT MAKE IT (bool) / YES PLEASE, I WANT IT (bool) / I DONT CARE (nothing)
	
	private boolean isHoliDay;
	
	private boolean noDay;
	private boolean noMorning6; // 6 AM or entire day
	private boolean noMorning7;
	private boolean noMorning8;
	private boolean noAfternoon;
	private boolean noNight;

	private boolean yesDay;
	private boolean yesMorning6;
	private boolean yesMorning7;
	private boolean yesMorning8;
	private boolean yesAfternoon;
	private boolean yesNight;
	
	public DayPreference() {		
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
		return noMorning6 || noDay;
	}

	public void setNoMorning6(boolean noMorning6) {
		this.noMorning6 = noMorning6;
	}

	public boolean isNoMorning7() {
		return noMorning7 || noDay;
	}

	public void setNoMorning7(boolean noMorning7) {
		this.noMorning7 = noMorning7;
	}

	public boolean isNoMorning8() {
		return noMorning8 || noDay;
	}

	public void setNoMorning8(boolean noMorning8) {
		this.noMorning8 = noMorning8;
	}

	public boolean isNoAfternoon() {
		return noAfternoon || noDay;
	}

	public void setNoAfternoon(boolean noAfternoon) {
		this.noAfternoon = noAfternoon;
	}

	public boolean isYesNight() {
		return yesNight;
	}

	public void setYesNight(boolean yesNight) {
		this.yesNight = yesNight;
	}
	
	public boolean isNoNight() {
		return noNight || noDay;
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

	public boolean isHoliDay() {
		return isHoliDay;
	}

	public void setHoliDay(boolean isHoliDay) {
		this.isHoliDay = isHoliDay;
		if(isHoliDay) setNoDay(true);
	}	
}
