package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class NightShift implements ShiftSolution {

	public Holder<Employee> staffer;
	
	public NightShift() {		
	}

	public int isEmployeeAllocated(String key) {
		if(staffer!=null && staffer.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_NIGHT;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
