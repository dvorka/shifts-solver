package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.shared.ShiftSolverLogger;

public class PeriodSolution implements Serializable {
	private static final long serialVersionUID = 7586400671035292788L;

	private String key;
	private String periodPreferencesKey;
	private int year;
	private int month;
	private Map<String, Job> employeeJobs;
	private List<DaySolution> days;
	private int solutionNumber;
	
	public PeriodSolution() {
		this.days=new ArrayList<DaySolution>();
		this.employeeJobs=new HashMap<String,Job>();
	}
	
	public PeriodSolution(int year, int month) {
		this();
		this.year=year;
		this.month=month;
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

	public void setSolutionNumber(int solutionNumber) {
		this.solutionNumber=solutionNumber;
	}

	public int getSolutionNumber() {
		return solutionNumber;
	}

	public void printSchedule() {		
		List<DaySolution> days = getDays();
		for(DaySolution ds:days) {
			ShiftSolverLogger.debug((ds.isWorkday()?"Work":"Weekend") + " Day "+ ds.getDay() +":");
			if(ds.isWorkday()) {
				ShiftSolverLogger.debug("  Morning:");
				ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().editor.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer6am.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer7am.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer8am.get().getFullName());
				ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().sportak.get().getFullName());

				ShiftSolverLogger.debug("  Afternoon:");
				ShiftSolverLogger.debug("    E "+ds.getWorkdayAfternoonShift().editor.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[0].get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[1].get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[2].get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[3].get().getFullName());
				ShiftSolverLogger.debug("    S "+ds.getWorkdayAfternoonShift().sportak.get().getFullName());

				ShiftSolverLogger.debug("  Night:");
				ShiftSolverLogger.debug("    D "+ds.getNightShift().staffer.get().getFullName());
			} else {		
				ShiftSolverLogger.debug("  Morning:");
				ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().editor.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWeekendMorningShift().staffer6am.get().getFullName());
				ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().sportak.get().getFullName());

				ShiftSolverLogger.debug("  Afternoon:");
				ShiftSolverLogger.debug("    E "+ds.getWeekendAfternoonShift().editor.get().getFullName());
				ShiftSolverLogger.debug("    D "+ds.getWeekendAfternoonShift().staffer.get().getFullName());
				ShiftSolverLogger.debug("    S "+ds.getWeekendAfternoonShift().sportak.get().getFullName());

				ShiftSolverLogger.debug("  Night:");
				ShiftSolverLogger.debug("    D "+ds.getNightShift().staffer.get().getFullName());
			}
		}
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
