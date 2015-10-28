package com.mindforger.shiftsolver.shared.model.shifts;

import java.io.Serializable;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WeekendAfternoonShift implements ShiftSolution, Serializable {
	private static final long serialVersionUID = -1760207914285681011L;

	public Holder<String> editor;
	public Holder<String> staffer;
	public Holder<String> sportak;
	
	public WeekendAfternoonShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(staffer!=null && staffer.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(sportak!=null && sportak.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
