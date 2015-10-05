package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class WeekendAfternoonShift implements ShiftSolution {

	public Employee editor;
	public Employee drone;
	public Employee sportak;
	
	public WeekendAfternoonShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(drone!=null && drone.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(sportak!=null && sportak.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
