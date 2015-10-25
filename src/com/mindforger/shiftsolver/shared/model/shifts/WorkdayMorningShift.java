package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WorkdayMorningShift implements ShiftSolution {

	public Holder<Employee> editor;
	public Holder<Employee> staffer6am;
	public Holder<Employee> staffer7am;
	// TODO bug = there are 2x 8am staffers!
	public Holder<Employee> staffer8am;
	public Holder<Employee> sportak;
	
	public WorkdayMorningShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(staffer6am!=null && staffer6am.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_6;
		}
		if(staffer7am!=null && staffer7am.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_7;
		}
		if(staffer8am!=null && staffer8am.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_8;
		}
		if(sportak!=null && sportak.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
