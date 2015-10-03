package com.mindforger.shiftsolver.solver;

import com.mindforger.shiftsolver.client.solver.ShiftsSolver;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.model.Team;

public class ShiftSolverTest {

	public void buildModelTest() {

		Team team=new Team();
		
		/*
		 * editors
		 */

		Employee lenka=new Employee();
		lenka.setEditor(true);
		lenka.setFulltime(true);
		lenka.setWoman(false);
		team.addEmployee(lenka);

		Employee misa=new Employee();
		misa.setEditor(true);
		misa.setFulltime(true);
		misa.setWoman(false);
		team.addEmployee(misa);
		
		Employee mirek=new Employee();
		mirek.setEditor(true);
		mirek.setFulltime(true);
		mirek.setWoman(false);
		team.addEmployee(mirek);
		
		Employee martin=new Employee();
		martin.setEditor(true);
		martin.setFulltime(true);
		martin.setWoman(false);
		team.addEmployee(martin);

		Employee igor=new Employee();
		igor.setEditor(true);
		igor.setFulltime(true);
		igor.setWoman(false);
		team.addEmployee(igor);
		
		Employee alice=new Employee();
		alice.setEditor(true);
		alice.setFulltime(true);
		alice.setWoman(true);
		team.addEmployee(alice);
		
		/*
		 * sportaci
		 */

		Employee simona=new Employee();
		simona.setSportak(true);
		simona.setFulltime(true);
		simona.setWoman(false);
		team.addEmployee(simona);
		
		Employee david=new Employee();
		david.setSportak(true);
		david.setFulltime(true);
		david.setWoman(false);
		team.addEmployee(david);

		Employee vojta=new Employee();
		vojta.setSportak(true);
		vojta.setFulltime(true);
		vojta.setWoman(false);
		team.addEmployee(vojta);
		
		Employee katka=new Employee();
		katka.setSportak(true);
		katka.setFulltime(true);
		katka.setWoman(true);
		team.addEmployee(katka);
		
		/*
		 * fulltime
		 */

		Employee honza=new Employee();
		honza.setSportak(true);
		honza.setFulltime(true);
		honza.setWoman(true);
		team.addEmployee(honza);

		Employee kristina=new Employee();
		kristina.setSportak(true);
		kristina.setFulltime(true);
		kristina.setWoman(true);
		team.addEmployee(kristina);

		Employee dominika=new Employee();
		dominika.setSportak(true);
		dominika.setFulltime(true);
		dominika.setWoman(true);
		team.addEmployee(dominika);
		
		Employee anna=new Employee();
		anna.setSportak(true);
		anna.setFulltime(true);
		anna.setWoman(true);
		team.addEmployee(anna);
		
		Employee milan=new Employee();
		milan.setSportak(true);
		milan.setFulltime(true);
		milan.setWoman(true);
		team.addEmployee(milan);
		
		/*
		 * part time
		 */
		
		// ...
		
		/*
		 * preferences
		 */
		
		PeriodPreferences periodPreferences = new PeriodPreferences(2015,9);		
		EmployeePreferences employeePreferences;

		employeePreferences=createBlankEmployeePreferences();		
		periodPreferences.addEmployeePreferences(lenka, employeePreferences);
		// ...
		employeePreferences=createBlankEmployeePreferences();		
		periodPreferences.addEmployeePreferences(anna, employeePreferences);
		employeePreferences=createBlankEmployeePreferences();		
		periodPreferences.addEmployeePreferences(milan, employeePreferences);
		
		/*
		 * solver
		 */
		
		ShiftsSolver shiftsSolver=new ShiftsSolver();
		PeriodSolution periodSolution=shiftsSolver.solve(team, periodPreferences);		
		System.out.println(periodSolution.toString());
	}

	private EmployeePreferences createBlankEmployeePreferences() {
		EmployeePreferences employeePreferences;
		employeePreferences = new EmployeePreferences();
		for(int day=1; day<=31; day++) {
			employeePreferences.addPreference(new DayPreference(2015, 9, day, false, false, false));			
		}
		
		return employeePreferences;
	}
	
}
