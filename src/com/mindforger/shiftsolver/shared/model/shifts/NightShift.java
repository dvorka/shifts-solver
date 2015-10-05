package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class NightShift implements ShiftSolution {

	public Employee drone;
	
	public NightShift() {		
	}

	public int isEmployeeAllocated(String key) {
		if(drone!=null && drone.getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_NIGHT;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
