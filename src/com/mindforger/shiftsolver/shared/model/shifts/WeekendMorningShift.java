package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class WeekendMorningShift implements ShiftSolution {

	public Employee editor;
	public Employee drone6am;
	public Employee sportak;

	public WeekendMorningShift() {
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(drone6am!=null && drone6am.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(sportak!=null && sportak.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
