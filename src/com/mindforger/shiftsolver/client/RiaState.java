package com.mindforger.shiftsolver.client;

import java.util.HashMap;
import java.util.Map;

import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;
import com.mindforger.shiftsolver.shared.service.UserBean;
import com.mindforger.shiftsolver.shared.service.UserSettingsBean;

public class RiaState {

	private UserBean currentUser;
	private UserSettingsBean userSettings;

	private Employee[] employees;
	private PeriodPreferences[] periodPreferencesList;
		
	private Map<String,Employee> employeesByKey;
	private Map<String,PeriodPreferences> periodPreferencesByKey;
	
	public RiaState() {
		employeesByKey=new HashMap<String,Employee>();
		periodPreferencesByKey=new HashMap<String,PeriodPreferences>();
	}
	
	public void init(RiaBootImageBean bean) {
		currentUser=bean.getUser();
		userSettings=bean.getUserSettings();
		
		setEmployees(bean.getEmployees());
		setPeriodPreferencesList(bean.getPeriodPreferencesList());
	}
	
	public Employee getEmployee(String employeeId) {
		if(employeeId!=null) {
			return employeesByKey.get(employeeId);
		}
		return null;
	}

	public PeriodPreferences getPeriodPreferences(String periodPreferencesId) {
		if(periodPreferencesId!=null) {
			return periodPreferencesByKey.get(periodPreferencesId);
		}
		return null;
	}
	
	public UserBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserBean currentUser) {
		this.currentUser = currentUser;
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

	public PeriodPreferences[] getPeriodPreferencesList() {
		return periodPreferencesList;
	}
	
	public void setEmployees(Employee[] employees) {
		this.employees= employees;
		employeesByKey.clear();
		if(employees!=null) {
			for(Employee employee: employees) {
				employeesByKey.put(employee.getKey(), employee);
			}
		}
	}

	public void setPeriodPreferencesList(PeriodPreferences[] periodPreferencesList) {
		this.periodPreferencesList = periodPreferencesList;
		periodPreferencesByKey.clear();
		if(periodPreferencesList!=null) {
			for(PeriodPreferences periodPreferences: periodPreferencesList) {
				periodPreferencesByKey.put(periodPreferences.getKey(), periodPreferences);
			}
		}
	}
}
