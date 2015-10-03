package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Team implements Serializable {
	private static final long serialVersionUID = -1013823027310866432L;

	private Map<String, Employee> employees;
	private Map<String, Employee> editors;
	private Map<String, Employee> sportaci;
	private Map<String, Employee> men;
	private Map<String, Employee> women;
	
	public Team() {
		employees=new HashMap<String, Employee>();
		editors=new HashMap<String, Employee>();
		sportaci=new HashMap<String, Employee>();		
		men=new HashMap<String, Employee>();		
		women=new HashMap<String, Employee>();		
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

