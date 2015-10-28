package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;
import com.mindforger.shiftsolver.shared.service.UserBean;
import com.mindforger.shiftsolver.shared.service.UserSettingsBean;

public class RiaState {

	private UserBean currentUser;
	private UserSettingsBean userSettings;

	private Employee[] employees;
	private PeriodPreferences[] periodPreferencesArray;
	private PeriodSolution[] periodSolutions;
		
	private Map<String,Employee> employeesByKey;
	private Map<String,PeriodPreferences> periodPreferencesByKey;
	private Map<String,PeriodSolution> periodSolutionsByKey;
	
	public RiaState() {
		employees=new Employee[0];
		periodPreferencesArray=new PeriodPreferences[0];
		periodSolutions=new PeriodSolution[0];
		employeesByKey=new HashMap<String,Employee>();
		periodPreferencesByKey=new HashMap<String,PeriodPreferences>();
		periodSolutionsByKey=new HashMap<String,PeriodSolution>();
	}
	
	public void init(RiaBootImageBean bean) {
		currentUser=bean.getUser();
		userSettings=bean.getUserSettings();
		
		setEmployees(bean.getEmployees());
		setPeriodPreferencesList(bean.getPeriodPreferencesList());
		setPeriodSolutions(bean.getPeriodSolutions());
	}
	
	public Employee getEmployee(String employeeId) {
		if(employeeId!=null) {
			return employeesByKey.get(employeeId);
		}
		return null;
	}

	public PeriodSolution getPeriodSolution(String solutionId) {
		if(solutionId!=null) {
			return periodSolutionsByKey.get(solutionId);
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

	public PeriodPreferences[] getPeriodPreferencesArray() {
		return periodPreferencesArray;
	}
	
	public PeriodSolution[] getPeriodSolutions() {
		return periodSolutions;
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

	public void addEmployee(Employee employee) {
		List<Employee> employeeList;
		if(employee!=null) {
			employeeList=new ArrayList<Employee>();
			if(employees!=null) {
				for(Employee e:employees) employeeList.add(e);
			}
			employeeList.add(employee);
			employeesByKey.put(employee.getKey(), employee);
			employees=employeeList.toArray(new Employee[employeeList.size()]);
		}
	}
	
	public void setPeriodPreferencesList(PeriodPreferences[] periodPreferencesList) {
		this.periodPreferencesArray = periodPreferencesList;
		periodPreferencesByKey.clear();
		if(periodPreferencesList!=null) {
			for(PeriodPreferences periodPreferences: periodPreferencesList) {
				periodPreferencesByKey.put(periodPreferences.getKey(), periodPreferences);
			}
		}
	}

	public void addPeriodPreferences(PeriodPreferences preferences) {
		List<PeriodPreferences> periodPreferencesList;
		if(preferences!=null) {
			periodPreferencesList=new ArrayList<PeriodPreferences>();
			if(periodPreferencesArray!=null) {
				for(PeriodPreferences p:periodPreferencesArray) periodPreferencesList.add(p);
			}
			periodPreferencesList.add(preferences);
			periodPreferencesByKey.put(preferences.getKey(),preferences);
			periodPreferencesArray=periodPreferencesList.toArray(new PeriodPreferences[periodPreferencesList.size()]);
		}
	}

	public void setPeriodSolutions(PeriodSolution[] periodSolutions) {
		this.periodSolutions = periodSolutions;
		periodSolutionsByKey.clear();
		if(periodSolutions!=null) {
			for(PeriodSolution periodSolution: periodSolutions) {
				periodSolutionsByKey.put(periodSolution.getKey(), periodSolution);
			}
		}
	}

	public void addPeriodSolution(PeriodSolution solution) {
		List<PeriodSolution> periodSolutionsList;
		if(solution!=null) {
			periodSolutionsList =new ArrayList<PeriodSolution>();
			if(periodSolutions!=null) {
				for(PeriodSolution p:periodSolutions) periodSolutionsList.add(p);
			}
			periodSolutionsList.add(solution);
			periodSolutionsByKey.put(solution.getKey(),solution);
			periodSolutions=periodSolutionsList.toArray(new PeriodSolution[periodSolutionsList.size()]);
		}
	}
}
