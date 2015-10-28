package com.mindforger.shiftsolver.shared.service;

import java.io.Serializable;

import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class RiaBootImageBean implements Serializable {
	
	private UserBean user;
	private UserSettingsBean userSettings;
	private Employee[] employees;
	private PeriodPreferences[] periodPreferencesList;
	private PeriodSolution[] periodSolutions;
	
	public RiaBootImageBean() {
	}
	
	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public UserSettingsBean getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(UserSettingsBean userSettings) {
		this.userSettings = userSettings;
	}
	
	public Employee[] getEmployees() {
		return employees;
	}

	public void setEmployees(Employee[] employees) {
		this.employees = employees;
	}
	
	public PeriodPreferences[] getPeriodPreferencesList() {
		return periodPreferencesList;
	}

	public void setPeriodPreferencesList(PeriodPreferences[] periodPreferencesList) {
		this.periodPreferencesList = periodPreferencesList;
	}
	
	public PeriodSolution[] getPeriodSolutions() {
		return periodSolutions;
	}

	public void setPeriodSolutions(PeriodSolution[] periodSolutions) {
		this.periodSolutions = periodSolutions;
	}

	private static final long serialVersionUID = 5219928239332050881L;
}
