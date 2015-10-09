package com.mindforger.shiftsolver.solver;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
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

	private void shuffleArray(Employee[] array) {
		Random random = ThreadLocalRandom.current();
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			Employee a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}

	@Deprecated
	public void testRiaBigDataSolutionAll() {
		state = Utils.createBigFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];

		PeriodSolution solution = solver.solve(
				Arrays.asList(state.getEmployees()),
				preferences, 
				Integer.MAX_VALUE);

		showSolution(solution, state.getEmployees());
	}

	public void testRiaBigDataSolutionFirst() {
		state = Utils.createBigFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];

		PeriodSolution solution;
		for(int i=0; i<1; i++) {
			//shuffleArray(state.getEmployees());

			solution = solver.solve(
					Arrays.asList(state.getEmployees()),
					preferences, 
					1);

			showSolution(solution, state.getEmployees());			
		}
	}

	public void testRiaSmallDataSolutionFirst() {		
		RiaState state = Utils.createSmallFooState();
		PeriodPreferences preferences = state.getPeriodPreferencesList()[0];

		PeriodSolution solution = solver.solve(
				Arrays.asList(state.getEmployees()),
				preferences, 
				1);

		showSolution(solution, state.getEmployees());
	}

	private void showSolution(PeriodSolution solution, Employee[] employees) {
		if(solution==null) {
			System.out.println("Solution doesn't exist for this team and employee preferences!");
			// TODO solver.getFirstBacktrackCause();
		} else {

			System.out.println("----------------------------------------------------------------");
			System.out.println("Employee allocation ("+solver.getEmployeeAllocations().size()+"):");
			for(Employee e:employees) {
				EmployeeAllocation a = solver.getEmployeeAllocations().get(e.getKey());
				String prefix=a.shifts<a.shiftsToGet?"<":(a.shifts==a.shiftsToGet?"=":">");
				System.out.println("  "+prefix+" "+a.employee.getFullName()+": "+a.shifts+"/"+a.shiftsToGet+" "+
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
	}

	public static void main(String[] args) {
		ShiftSolverTest shiftSolverRiaTest = new ShiftSolverTest();
		shiftSolverRiaTest.testRiaBigDataSolutionFirst();
		//shiftSolverRiaTest.testRiaSmallDataSolutionFirst();
		//shiftSolverRiaTest.testRiaBigDataSolutionAll();
		System.out.println("Test done!");
	}
}
