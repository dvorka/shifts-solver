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

public class ShiftsSolverTest {

	public ShiftsSolverTest() {		
	}
	
	public void testRiaDataSolution() {		
		ShiftsSolver solver=new ShiftsSolver();
		RiaState state = Utils.createBigFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];
		PeriodSolution solution = solver.solve(preferences.getEmployeeToPreferences().keySet(), preferences);

		System.out.println("----------------------------------------------------------------");
		System.out.println("Employee allocation ("+solver.getEmployeeAllocations().size()+"):");
		Set<String> keys = solver.getEmployeeAllocations().keySet();
		for(String key:keys) {
			EmployeeAllocation a = solver.getEmployeeAllocations().get(key);
			System.out.println("  "+a.employee.getFullName()+": "+a.shifts+"/"+a.shiftsToGet+" "+
					(a.employee.isEditor()?"editor":"")+
					(a.employee.isMorningSportak()?"morning-sportak":"")+
					(a.employee.isSportak()?"sportak":"")+
					" "+
					(a.employee.isFulltime()?"FULL":"PART")+
					"");
		}
		
		System.out.println("----------------------------------------------------------------");
		List<DaySolution> days = solution.getDays();
		for(DaySolution ds:days) {
			System.out.println((ds.isWorkday()?"Work":"Weekend") + " Day "+ ds.getDay() +":");
			if(ds.isWorkday()) {
				System.out.println("  Morning:");
				System.out.println("    E "+ds.getWorkdayMorningShift().editor.getFullName());
				System.out.println("    D "+ds.getWorkdayMorningShift().drone6am.getFullName());
				System.out.println("    D "+ds.getWorkdayMorningShift().drone7am.getFullName());
				System.out.println("    D "+ds.getWorkdayMorningShift().drone8am.getFullName());
				System.out.println("    E "+ds.getWorkdayMorningShift().sportak.getFullName());
				
				System.out.println("  Afternoon:");
				System.out.println("    E "+ds.getWorkdayAfternoonShift().editor.getFullName());
				System.out.println("    D "+ds.getWorkdayAfternoonShift().drones[0].getFullName());
				System.out.println("    D "+ds.getWorkdayAfternoonShift().drones[1].getFullName());
				System.out.println("    D "+ds.getWorkdayAfternoonShift().drones[2].getFullName());
				System.out.println("    D "+ds.getWorkdayAfternoonShift().drones[3].getFullName());
				System.out.println("    S "+ds.getWorkdayAfternoonShift().sportak.getFullName());
				
				System.out.println("  Night:");
				System.out.println("    D "+ds.getNightShift().drone.getFullName());
			} else {		
				System.out.println("  Morning:");
				System.out.println("    E "+ds.getWeekendMorningShift().editor.getFullName());
				System.out.println("    D "+ds.getWeekendMorningShift().drone6am.getFullName());
				System.out.println("    E "+ds.getWeekendMorningShift().sportak.getFullName());
				
				System.out.println("  Afternoon:");
				System.out.println("    E "+ds.getWeekendAfternoonShift().editor.getFullName());
				System.out.println("    D "+ds.getWeekendAfternoonShift().drone.getFullName());
				System.out.println("    S "+ds.getWeekendAfternoonShift().sportak.getFullName());
				
				System.out.println("  Night:");
				System.out.println("    D "+ds.getNightShift().drone.getFullName());
			}
		}
	}
	
	public static void main(String[] args) {
		ShiftsSolverTest shiftSolverRiaTest = new ShiftsSolverTest();
		shiftSolverRiaTest.testRiaDataSolution();
	}
}
