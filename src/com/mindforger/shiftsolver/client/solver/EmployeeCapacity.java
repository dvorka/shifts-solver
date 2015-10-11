package com.mindforger.shiftsolver.client.solver;

import java.util.List;

import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class EmployeeCapacity {

	private PeriodPreferences preferences;
	private List<EmployeeAllocation> allocations;
	
	int neededEditorShifts;
	int neededSportakShifts;
	int neededDroneShifts;
	
	int remainingEditorShifts;
	int remainingSportakShifts;
	int remainingDroneShifts;
	
	public EmployeeCapacity(PeriodPreferences preferences, List<EmployeeAllocation> allocations) {
		this.preferences=preferences;
		this.allocations=allocations;
	}
	
	public void calculate() {
		// TODO
	}
	
	public void printEmployeeAllocations() {
		ShiftSolverLogger.debug("     Employee allocations ("+allocations.size()+"):");
		for(EmployeeAllocation a:allocations) {
			String fullShifts=a.shifts<a.shiftsToGet?"<":(a.shifts==a.shiftsToGet?"!":"X");
			String fullNights=a.nights<2?"<":(a.nights==2?"!":"X");
			ShiftSolverLogger.debug(
					"       "+fullShifts+fullNights+
					" "+
					(a.employee.isEditor()?"editor    ":
						(a.employee.isSportak()?"sportak   ":
							(a.employee.isMorningSportak()?"am-sportak":"drone     ")))+
					" "+
					(a.employee.isFulltime()?"FULL":"PART")+
					" "+
					a.employee.getFullName()+" "+
						"jobs: "+a.shifts+"/"+a.shiftsToGet+" ("+(a.shiftsToGet-a.shifts)+") "+
						"nights: "+a.nights+"/"+(a.employee.isFulltime()?"2":"X")+" ("+(2-a.nights)+")"
					);
		}		
	}
}
