package com.mindforger.shiftsolver.solver;

import java.util.ArrayList;
import java.util.Arrays;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.EmployeeCapacity;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.client.solver.ShiftSolverTimeoutException;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
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
				.printEmployeeAllocations(31);

			ShiftSolverLogger.debug("- SOLUTION ---------------------------------------------------------------");
			solution.printSchedule();
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
