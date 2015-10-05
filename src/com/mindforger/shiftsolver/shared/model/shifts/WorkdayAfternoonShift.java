package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class WorkdayAfternoonShift implements ShiftSolution {

	public Employee editor;
	public Employee[] drones;
	public Employee sportak;
	
	public WorkdayAfternoonShift() {
		drones=new Employee[4];
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(drones!=null) {
			for(Employee e:drones) {
				if(e!=null && e.getKey().equals(key)) {
					return ShiftSolverConstants.SHIFT_AFTERNOON;
				}
			}
		}
		if(sportak!=null && sportak.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}
}
