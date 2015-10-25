package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WeekendAfternoonShift implements ShiftSolution {

	public Holder<Employee> editor;
	public Holder<Employee> staffer;
	public Holder<Employee> sportak;
	
	public WeekendAfternoonShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(staffer!=null && staffer.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(sportak!=null && sportak.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
