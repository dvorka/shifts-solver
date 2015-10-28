package com.mindforger.shiftsolver.shared.model.shifts;

import java.io.Serializable;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WeekendMorningShift implements ShiftSolution, Serializable {
	private static final long serialVersionUID = -4329311376897580289L;

	public Holder<String> editor;
	public Holder<String> staffer6am;
	public Holder<String> sportak;

	public WeekendMorningShift() {
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(staffer6am!=null && staffer6am.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(sportak!=null && sportak.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}	
}
