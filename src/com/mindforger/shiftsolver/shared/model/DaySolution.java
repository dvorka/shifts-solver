package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;

import com.mindforger.shiftsolver.shared.model.shifts.NightShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendMorningShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayMorningShift;

public class DaySolution implements Serializable  {
	private static final long serialVersionUID = -2908720137028791233L;

	private String key;
	
	int day;
	boolean isWorkday;
	int weekday;
	WorkdayMorningShift workdayMorningShift;
	WorkdayAfternoonShift workdayAfternoonShift;
	WeekendMorningShift weekendMorningShift;
	WeekendAfternoonShift weekendAfternoonShift;
	NightShift nightShift;
		
	public DaySolution() {
	}
	
	public DaySolution(int day, int weekday, boolean isWorkday) {
		this.day=day;
		this.weekday=weekday;
		this.isWorkday=isWorkday;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isWorkday() {
		return isWorkday;
	}

	public void setWorkday(boolean isWorkday) {
		this.isWorkday = isWorkday;
	}

	public WorkdayAfternoonShift getWorkdayAfternoonShift() {
		return workdayAfternoonShift;
	}

	public WorkdayMorningShift getWorkdayMorningShift() {
		return workdayMorningShift;
	}
	
	public void setWorkdayAfternoonShift(WorkdayAfternoonShift workdayAfternoonShift) {
		this.workdayAfternoonShift = workdayAfternoonShift;
	}

	public WeekendMorningShift getWeekendMorningShift() {
		return weekendMorningShift;
	}

	public void setWeekendMorningShift(WeekendMorningShift weekendMorningShift) {
		this.weekendMorningShift = weekendMorningShift;
	}

	public WeekendAfternoonShift getWeekendAfternoonShift() {
		return weekendAfternoonShift;
	}

	public void setWeekendAfternoonShift(WeekendAfternoonShift weekendAfternoonShift) {
		this.weekendAfternoonShift = weekendAfternoonShift;
	}

	public NightShift getNightShift() {
		return nightShift;
	}

	public void setNightShift(NightShift nightShift) {
		this.nightShift = nightShift;
	}

	public boolean isEmployeeAllocatedToday(String employeeKey) {
		return getShiftTypeForEmployee(employeeKey)>0;
	}
	
	public int getShiftTypeForEmployee(String employeeKey) {
		int result=0, shift;
		
		if(nightShift!=null) {
			if((shift=nightShift.isEmployeeAllocated(employeeKey))>0) {
				result=shift|result;
			}
		}
		
		if(isWorkday) {
			if(workdayMorningShift!=null) {
				if((shift=workdayMorningShift.isEmployeeAllocated(employeeKey))>0) {
					result=shift|result;
				}
			}
			if(workdayAfternoonShift!=null) {
				if((shift=workdayAfternoonShift.isEmployeeAllocated(employeeKey))>0) {
					result=shift|result;
				}
			}
		} else {
			if(weekendMorningShift!=null) {
				if((shift=weekendMorningShift.isEmployeeAllocated(employeeKey))>0) {
					result=shift|result;
				}
			}
			if(weekendAfternoonShift!=null) {
				if((shift=weekendAfternoonShift.isEmployeeAllocated(employeeKey))>0) {
					result=shift|result;
				}
			}
		}
		return result;
	}

	public void setWorkdayMorningShift(WorkdayMorningShift workdayMorningShift) {
		this.workdayMorningShift=workdayMorningShift;
	}

	public int getWeekday() {
		return weekday;
	}
}
