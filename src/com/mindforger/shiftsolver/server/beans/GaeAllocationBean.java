package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.Job;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaeAllocationBean implements Serializable, GaeBean {
	private static final long serialVersionUID = 404791535199143808L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;

	@Persistent
	private GaePeriodSolutionBean periodSolution;
	
	@Persistent
	String employeeKey;
	@Persistent
	int shiftsLimit;
	@Persistent
	int shifts;
	
	public GaeAllocationBean() {		
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public GaePeriodSolutionBean getPeriodSolution() {
		return periodSolution;
	}

	public void setPeriodSolution(GaePeriodSolutionBean periodSolution) {
		this.periodSolution = periodSolution;
	}

	public int getShiftsLimit() {
		return shiftsLimit;
	}

	public void setShiftsLimit(int shiftsLimit) {
		this.shiftsLimit = shiftsLimit;
	}

	public int getShifts() {
		return shifts;
	}

	public void setShifts(int shifts) {
		this.shifts = shifts;
	}

	public void setEmployeeKey(String k) {
		this.employeeKey=k;
	}
	
	public String getEmployeeKey() {
		return employeeKey;
	}

	public void fromPojo(Job job) {
		key=ServerUtils.stringToKey(job.key);
		shifts=job.shifts;
		shiftsLimit=job.shiftsLimit;
	}
	
	public Job toPojo() {
		Job j=new Job();
		j.key=ServerUtils.keyToString(key);
		j.shifts=shifts;
		j.shiftsLimit=shiftsLimit;
		return j;
	}
}
