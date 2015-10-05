package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class WorkdayMorningShift implements ShiftSolution {

	public Employee editor;
	public Employee drone6am;
	public Employee drone7am;
	public Employee drone8am;
	public Employee sportak;
	
	public WorkdayMorningShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(drone6am!=null && drone6am.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_6;
		}
		if(drone7am!=null && drone7am.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_7;
		}
		if(drone8am!=null && drone8am.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_8;
		}
		if(sportak!=null && sportak.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
