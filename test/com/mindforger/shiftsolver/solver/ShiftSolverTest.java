package com.mindforger.shiftsolver.solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class ShiftSolverTest {

	private ShiftSolver solver;
	private RiaState state;

	public ShiftSolverTest() {		
		solver = new ShiftSolver();
						
		// log to file
		try {
			PrintStream out=new PrintStream(new File("/tmp/s2.txt"));
			ShiftSolverLogger.init(out);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	public void testRiaBigDataSolutionFirst() {
		state = Utils.createNovemberFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesArray()[0];

		// TODO ShiftSolver.STEPS_LIMIT=Long.MAX_VALUE;
		
		PeriodSolution solution;
		for(int i=0; i<1; i++) {
			// TODO Utils.shuffleArray(state.getEmployees());

			try {
				solution = solver.solve(
						Arrays.asList(state.getEmployees()),
						preferences, 
						1);

				showSolution(preferences, solution, state.getEmployees());				
			} catch(Exception e) {
				ShiftSolverLogger.debug("\nERROR: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void testRiaSmallDataSolutionFirst() {		
		RiaState state = Utils.createSmallFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesArray()[0];

		PeriodSolution solution = solver.solve(
				Arrays.asList(state.getEmployees()),
				preferences, 
				1);

		showSolution(preferences, solution, state.getEmployees());
	}

	private void showSolution(PeriodPreferences preferences, PeriodSolution solution, Employee[] employees) {
		if(solution==null) {
			System.out.println("Solution doesn't exist!");
			ShiftSolverLogger.debug("Solution doesn't exist for this team and employee preferences!");
			// TODO solver.getFirstBacktrackCause();
		} else {
			System.out.println("Solution exists!");
			ShiftSolverLogger.debug("- CAPACITY ---------------------------------------------------------------");
			EmployeeAllocation.printEmployeeAllocations(31,new ArrayList<EmployeeAllocation>(solver.getEmployeeAllocations().values()));

			ShiftSolverLogger.debug("- SOLUTION ---------------------------------------------------------------");
// TODO			solution.printSchedule();
		}
	}

//	public void printSchedule() {		
//		List<DaySolution> days = getDays();
//		for(DaySolution ds:days) {
//			ShiftSolverLogger.debug((ds.isWorkday()?"Work":"Weekend") + " Day "+ ds.getDay() +":");
//			if(ds.isWorkday()) {
//				ShiftSolverLogger.debug("  Morning:");
//				ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().editor.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer6am.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer7am.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayMorningShift().staffer8am1.get().getFullName());
//				ShiftSolverLogger.debug("    E "+ds.getWorkdayMorningShift().sportak.get().getFullName());
//
//				ShiftSolverLogger.debug("  Afternoon:");
//				ShiftSolverLogger.debug("    E "+ds.getWorkdayAfternoonShift().editor.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[0].get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[1].get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[2].get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWorkdayAfternoonShift().staffers[3].get().getFullName());
//				ShiftSolverLogger.debug("    S "+ds.getWorkdayAfternoonShift().sportak.get().getFullName());
//
//				ShiftSolverLogger.debug("  Night:");
//				ShiftSolverLogger.debug("    D "+ds.getNightShift().staffer.get().getFullName());
//			} else {		
//				ShiftSolverLogger.debug("  Morning:");
//				ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().editor.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWeekendMorningShift().staffer6am.get().getFullName());
//				ShiftSolverLogger.debug("    E "+ds.getWeekendMorningShift().sportak.get().getFullName());
//
//				ShiftSolverLogger.debug("  Afternoon:");
//				ShiftSolverLogger.debug("    E "+ds.getWeekendAfternoonShift().editor.get().getFullName());
//				ShiftSolverLogger.debug("    D "+ds.getWeekendAfternoonShift().staffer.get().getFullName());
//				ShiftSolverLogger.debug("    S "+ds.getWeekendAfternoonShift().sportak.get().getFullName());
//
//				ShiftSolverLogger.debug("  Night:");
//				ShiftSolverLogger.debug("    D "+ds.getNightShift().staffer.get().getFullName());
//			}
//		}
//	}
	
	public void calendarFun() {
		int year=2015;
		int month=11;
		
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		periodPreferences = ServerUtils.countDaysWorkdaysStartDay(periodPreferences);
		System.out.println("    Days: "+periodPreferences.getMonthDays());		
		System.out.println("Workdays: "+periodPreferences.getMonthWorkDays());
		System.out.println("Star day: "+periodPreferences.getStartWeekDay());
	}
	
	public static void main(String[] args) {
		ShiftSolverTest shiftSolverRiaTest = new ShiftSolverTest();
		
		//shiftSolverRiaTest.calendarFun();
		shiftSolverRiaTest.testRiaBigDataSolutionFirst();		
		//shiftSolverRiaTest.testRiaSmallDataSolutionFirst();		
		
		ShiftSolverLogger.debug("Test done!");
	}
}
