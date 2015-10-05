package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team implements Serializable {
	private static final long serialVersionUID = -1013823027310866432L;

	private Map<String, Employee> employees;
	private Map<String, Employee> editors;
	private Map<String, Employee> sportaci;
	private Map<String, Employee> men;
	private Map<String, Employee> women;

	private ArrayList<Employee> employeesList;
	
	public Team() {
		employees=new HashMap<String, Employee>();
		employeesList=new ArrayList<Employee>();
		editors=new HashMap<String, Employee>();
		sportaci=new HashMap<String, Employee>();		
		men=new HashMap<String, Employee>();		
		women=new HashMap<String, Employee>();		
	}
	
	public void addEmployee(Employee employee) {
		employees.put(employee.getFullName(), employee);
		employeesList.add(employee);
	}

	public void addEmployees(Collection<Employee> employees) {
		if(employees!=null) {
			for(Employee e:employees) {
				addEmployee(e);
			}			
		}
	}
	
	public Map<String, Employee> getEmployees() {
		return employees;
	}
	
	public List<Employee> getStableEmployeeList() {
		return employeesList;
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
