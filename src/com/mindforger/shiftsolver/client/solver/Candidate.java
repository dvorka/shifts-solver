package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.Employee;

public class Candidate {

	private boolean fallbackSearch;
	private Employee employee;
	
	public Candidate() {
		fallbackSearch=false;
	}

	public Candidate(Employee employee) {
		this();
		this.employee=employee;
	}

	public Candidate(boolean fallbackSearch) {
		this.fallbackSearch=fallbackSearch;
	}

	public Candidate(Employee e, boolean fallbackSearch) {
		this(e);
		this.fallbackSearch=fallbackSearch;
	}

	public boolean isFallbackSearch() {
		return fallbackSearch;
	}

	public void setFallbackSearch(boolean fallbackSearch) {
		this.fallbackSearch = fallbackSearch;
	}

	public Employee getEmployee() {
		return employee;
	}
	
	public String getKey() {
		if(employee!=null) {
			return employee.getKey();
		}
		return null;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
}
