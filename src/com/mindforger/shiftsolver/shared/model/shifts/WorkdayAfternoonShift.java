package com.mindforger.shiftsolver.shared.model.shifts;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class WorkdayAfternoonShift implements ShiftSolution {

	public Holder<Employee> editor;
	public Holder<Employee>[] staffers;
	public Holder<Employee> sportak;
	
	@SuppressWarnings("unchecked")
	public WorkdayAfternoonShift() {
		staffers=new Holder[4];
	}
	
	public int isEmployeeAllocated(String key) {
		if(editor!=null && editor.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		if(staffers!=null) {
			for(Holder<Employee> e:staffers) {
				if(e!=null && e.get().getKey().equals(key)) {
					return ShiftSolverConstants.SHIFT_AFTERNOON;
				}
			}
		}
		if(sportak!=null && sportak.get().getKey().equals(key)) {
			return ShiftSolverConstants.SHIFT_AFTERNOON;
		}
		return ShiftSolverConstants.NO_SHIFT;		
	}
}
