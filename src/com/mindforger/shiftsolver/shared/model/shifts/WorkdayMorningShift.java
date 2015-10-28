package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WorkdayMorningShift implements ShiftSolution {

	public Holder<String> editor;
	public Holder<String> staffer6am;
	public Holder<String> staffer7am;
	public Holder<String> staffer8am1;
	public Holder<String> staffer8am2;
	public Holder<String> sportak;
	
	public WorkdayMorningShift() {		
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		if(staffer6am!=null && staffer6am.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_6;
		}
		if(staffer7am!=null && staffer7am.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_7;
		}
		if(staffer8am1!=null && staffer8am1.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_8;
		}
		if(staffer8am2!=null && staffer8am2.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING_8;
		}
		if(sportak!=null && sportak.get().equals(key)) {
			return ShiftSolverConstants.SHIFT_MORNING;
		}
		return ShiftSolverConstants.NO_SHIFT;
	}
}
