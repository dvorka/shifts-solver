package com.mindforger.shiftsolver.client;

import java.util.Map;

import com.google.gwt.dev.util.collect.HashMap;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;
import com.mindforger.shiftsolver.shared.service.UserBean;
import com.mindforger.shiftsolver.shared.service.UserSettingsBean;

public class RiaState {

	private UserBean currentUser;
	private UserSettingsBean userSettings;

	private Employee[] employees;
		
	private Map<String,Employee> employeesByKey;
	
	public RiaState() {
		employeesByKey=new HashMap<String,Employee>();
	}
	
	public void init(RiaBootImageBean bean) {
		currentUser=bean.getUser();
		userSettings=bean.getUserSettings();
		
		setEmployees(bean.getEmployees());
	}
	
	public Employee getEmployee(String employeeId) {
		if(employeeId!=null) {
			return employeesByKey.get(employeeId);
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

	public void setEmployees(Employee[] employees) {
		this.employees= employees;
		employeesByKey.clear();
		if(employees!=null) {
			for(Employee employee: employees) {
				employeesByKey.put(employee.getKey(), employee);
			}
		}
	}
}
