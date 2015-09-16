package com.mindforger.shiftsolver.shared.model;

import java.util.HashMap;
import java.util.Map;

public class Team {

	private Map<String, Employee> employees;
	private Map<String, Employee> editors;
	private Map<String, Employee> sportaci;
	private Map<String, Employee> men;
	private Map<String, Employee> women;
	
	public Team() {
		employees=new HashMap<>();
		editors=new HashMap<>();
		sportaci=new HashMap<>();		
		men=new HashMap<>();		
		women=new HashMap<>();		
	}
	
	public void addEmployee(Employee employee) {
		employees.put(employee.getFullName(), employee);
	}
	
	public Map<String, Employee> getEmployees() {
		return employees;
	}
	
	public Map<String, Employee> getEditors() {
		return editors;
	}
	
	public Map<String, Employee> getSportaci() {
		return sportaci;
	}

	public Map<String, Employee> getMen() {
		return men;
	}

	public Map<String, Employee> getWomen() {
		return women;
	}
}

