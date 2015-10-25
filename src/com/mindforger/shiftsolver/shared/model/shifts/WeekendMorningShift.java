package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WeekendMorningShift implements ShiftSolution {

	public Holder<Employee> editor;
	public Holder<Employee> staffer6am;
	public Holder<Employee> sportak;

	public WeekendMorningShift() {
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(staffer6am!=null && staffer6am.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(sportak!=null && sportak.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
