package com.mindforger.shiftsolver.solver;

import java.util.List;
import java.util.Set;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.ShiftsSolver;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class ShiftSolverTest {

	public ShiftSolverTest() {		
	}
	
	public void testRiaDataSolution() {		
		ShiftsSolver solver=new ShiftsSolver();
		RiaState state = Utils.createBigFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];
		PeriodSolution solution = solver.solve(preferences.getEmployeeToPreferences().keySet(), preferences);

		System.out.println("Employee allocation ("+solver.getEmployeeAllocations().size()+"):");
		Set<String> keys = solver.getEmployeeAllocations().keySet();
		for(String key:keys) {
			EmployeeAllocation a = solver.getEmployeeAllocations().get(key);
			System.out.println("  "+a.employee.getFullName()+": "+a.shiftsCount+"/"+a.shiftsToGet);
		}
		
		List<DaySolution> days = solution.getDays();
		for(DaySolution ds:days) {
			System.out.println("Day "+ ds.getDay() +":");
			if(ds.isWorkday()) {
				System.out.println("  Morning:");
				System.out.println("    "+ds.getWorkdayMorningShift().editor.getFullName());
				System.out.println("    "+ds.getWorkdayMorningShift().drone6am.getFullName());
				System.out.println("    "+ds.getWorkdayMorningShift().drone7am.getFullName());
				System.out.println("    "+ds.getWorkdayMorningShift().drone8am.getFullName());
				System.out.println("    "+ds.getWorkdayMorningShift().editor.getFullName());
				
				System.out.println("  Afternoon:");
				System.out.println("    "+ds.getWorkdayAfternoonShift().editor.getFullName());
				System.out.println("    "+ds.getWorkdayAfternoonShift().drones[0].getFullName());
				System.out.println("    "+ds.getWorkdayAfternoonShift().drones[1].getFullName());
				System.out.println("    "+ds.getWorkdayAfternoonShift().drones[2].getFullName());
				System.out.println("    "+ds.getWorkdayAfternoonShift().drones[3].getFullName());
				System.out.println("    "+ds.getWorkdayAfternoonShift().editor.getFullName());
				
				System.out.println("  Night:");
				System.out.println("    "+ds.getNightShift().drone.getFullName());
			} else {
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		ShiftSolverTest shiftSolverRiaTest = new ShiftSolverTest();
		shiftSolverRiaTest.testRiaDataSolution();
	}
}
