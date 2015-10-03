package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.List;

public class ShiftSolution implements Serializable {
	private static final long serialVersionUID = -3636128024295557033L;

	private int year;
	private int month;
	private int day;
	
	private Employee sportak;
	private Employee editor;
	private List<Employee> drones;
	
	public ShiftSolution() {		
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

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Employee getSportak() {
		return sportak;
	}

	public void setSportak(Employee sportak) {
		this.sportak = sportak;
	}

	public Employee getEditor() {
		return editor;
	}

	public void setEditor(Employee editor) {
		this.editor = editor;
	}

	public List<Employee> getDrones() {
		return drones;
	}

	public void setDrones(List<Employee> drones) {
		this.drones = drones;
	}
	
	public void addDrone(Employee drone) {
		drones.add(drone);
	}

	public boolean solved(int dronesCount) {
		if(editor!=null && sportak!=null && drones.size()>=dronesCount) {
			return true;
		} else {
			return false;
		}
	}
}
