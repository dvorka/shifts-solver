package com.mindforger.shiftsolver.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.EmployeeCapacity;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.client.solver.ShiftSolverTimeoutException;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class ShiftSolverTest {

	private ShiftSolver solver;
	private RiaState state;

	public ShiftSolverTest() {		
		solver = new ShiftSolver();
	}

	public void testRiaBigDataSolutionFirst() {
		state = Utils.createBigFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];

		ShiftSolver.STEPS_LIMIT=Long.MAX_VALUE;
		
		PeriodSolution solution;
		for(int i=0; i<1; i++) {
			Utils.shuffleArray(state.getEmployees());

			try {
				solution = solver.solve(
						Arrays.asList(state.getEmployees()),
						preferences, 
						1);

				showSolution(preferences, solution, state.getEmployees());				
			} catch(ShiftSolverTimeoutException e) {
				ShiftSolverLogger.debug("\nERROR:"+e.getMessage());
			}
		}
	}

	public void testRiaSmallDataSolutionFirst() {		
		RiaState state = Utils.createSmallFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];

		PeriodSolution solution = solver.solve(
				Arrays.asList(state.getEmployees()),
				preferences, 
				1);

		showSolution(preferences, solution, state.getEmployees());
	}

	private void showSolution(PeriodPreferences preferences, PeriodSolution solution, Employee[] employees) {
		if(solution==null) {
			ShiftSolverLogger.debug("Solution doesn't exist for this team and employee preferences!");
			// TODO solver.getFirstBacktrackCause();
		} else {

			ShiftSolverLogger.debug("- CAPACITY ---------------------------------------------------------------");
			new EmployeeCapacity(preferences, new ArrayList<EmployeeAllocation>(solver.getEmployeeAllocations().values()))
				.printEmployeeAllocations();;

			ShiftSolverLogger.debug("- SOLUTION ---------------------------------------------------------------");
			List<DaySolution> days = solution.getDays();
			for(DaySolution ds:days) {
				ShiftSolverLogger.debug((ds.isWorkday()?"Work":"Weekend") + " Day "+ ds.getDay() +":");
				if(ds.isWorkday()) {
					ShiftSolverLogger.debug("  Morning:");
					ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().editor.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().drone6am.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().drone7am.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().drone8am.getFullName());
					ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().sportak.getFullName());

					ShiftSolverLogger.debug("  Afternoon:");
					ShiftSolverLogger.debug("    E "+ds.getWorkdayAfternoonShift().editor.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().drones[0].getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().drones[1].getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().drones[2].getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().drones[3].getFullName());
					ShiftSolverLogger.debug("    S "+ds.getWorkdayAfternoonShift().sportak.getFullName());

					ShiftSolverLogger.debug("  Night:");
					ShiftSolverLogger.debug("    D "+ds.getNightShift().drone.getFullName());
				} else {		
					ShiftSolverLogger.debug("  Morning:");
					ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().editor.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWeekendMorningShift().drone6am.getFullName());
					ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().sportak.getFullName());

					ShiftSolverLogger.debug("  Afternoon:");
					ShiftSolverLogger.debug("    E "+ds.getWeekendAfternoonShift().editor.getFullName());
					ShiftSolverLogger.debug("    D "+ds.getWeekendAfternoonShift().drone.getFullName());
					ShiftSolverLogger.debug("    S "+ds.getWeekendAfternoonShift().sportak.getFullName());

					ShiftSolverLogger.debug("  Night:");
					ShiftSolverLogger.debug("    D "+ds.getNightShift().drone.getFullName());
				}
			}
		}
	}

	public static void main(String[] args) {
		ShiftSolverTest shiftSolverRiaTest = new ShiftSolverTest();
		shiftSolverRiaTest.testRiaBigDataSolutionFirst();
		//shiftSolverRiaTest.testRiaSmallDataSolutionFirst();
		//shiftSolverRiaTest.testRiaBigDataSolutionAll();
		ShiftSolverLogger.debug("Test done!");
	}
}
