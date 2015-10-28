package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WeekendAfternoonShift implements ShiftSolution {

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
