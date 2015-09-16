package com.mindforger.shiftsolver.shared.model;

public class DayPreference {

	private int year;
	private int month;
	private int day;
	
	private boolean noMorning;
	private boolean noAfternoon;
	private boolean noNight;
	
	public DayPreference() {		
	}
	
	public DayPreference(int year, int month, int day, boolean noMorning, boolean noAfternoon, boolean noNight) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.noMorning = noMorning;
		this.noAfternoon = noAfternoon;
		this.noNight = noNight;
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
	public boolean isNoMorning() {
		return noMorning;
	}
	public void setNoMorning(boolean noMorning) {
		this.noMorning = noMorning;
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
	
	
}
