package com.mindforger.shiftsolver.shared.model.shifts;

import java.io.Serializable;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Holder;

public class NightShift implements ShiftSolution, Serializable {
	private static final long serialVersionUID = -7463389417295119886L;

	public Holder<String> staffer;
	
	public NightShift() {		
	}

	public int isEmployeeAllocated(String key) {
		if(staffer!=null && staffer.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_NIGHT;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
