package com.mindforger.shiftsolver.solver.commons;

import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.Team;

public abstract class AbstractShiftSolverTest {

	public AbstractShiftSolverTest() {
	}
	
	public RiaState createNovemberFooState() {
		RiaState state = new RiaState();

		Team team=new Team();
		int key=0;

		Employee e, igor, mirko;

		/*
		 * editors
		 */
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Alice");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		igor=e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Igor");
		e.setFamilyname("M");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Lenka");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
				
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Martin");
		e.setFamilyname("B");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		mirko=e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Mirko");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		/*
		 * mortaci
		 */

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Simona");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Katarina");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		/*
		 * sportaci
		 */
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("David");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(false);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marek");
		e.setFamilyname("A");
		e.setSportak(true);
		e.setFulltime(false);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Standa");
		e.setFamilyname("Z");
		e.setSportak(true);
		e.setFulltime(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojta");
		e.setFamilyname("M");
		e.setSportak(true);
		e.setFulltime(true);
		team.addEmployee(e);
				
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Newcomer1");
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(false);
		team.addEmployee(e);

//		e=new Employee();
//		e.setKey(""+key++);
//		e.setFirstname("Newcomer2");
//		e.setFamilyname("X");
//		e.setSportak(true);
//		e.setFulltime(false);
//		team.addEmployee(e);
		
		/*
		 * fulltime
		 */

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Martin");
		e.setFamilyname("H");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Milan");
		e.setFamilyname("K");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Jan");
		e.setFamilyname("P");
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marika");
		e.setFamilyname("T");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Michaela");
		e.setFamilyname("V");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Kristina");
		e.setFamilyname("W");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojtech");
		e.setFamilyname("K");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Anna");
		e.setFamilyname("K");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		/*
		 * part time
		 */
				
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Matej");
		e.setFamilyname("S");
		e.setFulltime(false);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marie");
		e.setFamilyname("H");
		e.setFulltime(false);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Dominika");
		e.setFamilyname("P");
		e.setFulltime(false);
		e.setFemale(true);
		team.addEmployee(e);
		
		// ...
		
		state.setEmployees(team.getStableEmployeeList().toArray(new Employee[team.getStableEmployeeList().size()]));
		
		int year=2015;
		int month=11;
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		periodPreferences.setKey("2");
		periodPreferences.setMonthDays(30);
		periodPreferences.setMonthWorkDays(21);
		periodPreferences.setStartWeekDay(1);
		periodPreferences.setLastMonthEditor(mirko.getKey());
		
		EmployeePreferences employeePreferences;
		for(Employee ee:state.getEmployees()) {
			ArrayList<DayPreference> dayPreferencesList = new ArrayList<DayPreference>();
			if(ee.getKey().equals(igor.getKey())) {
				DayPreference dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(1);
				dayPreference.setNoDay(true);
				dayPreferencesList.add(dayPreference);

				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(2);
				dayPreference.setNoMorning6(true);
				dayPreferencesList.add(dayPreference);
				
				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(3);
				dayPreference.setNoAfternoon(true);
				dayPreferencesList.add(dayPreference);
				
				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(4);
				dayPreference.setNoNight(true);
				dayPreferencesList.add(dayPreference);
			}
			employeePreferences = new EmployeePreferences();
			employeePreferences.setPreferences(dayPreferencesList);
			periodPreferences.addEmployeePreferences(ee.getKey(), employeePreferences);			
		}
		
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);

		ShiftSolverLogger.debug("Test data:");
		ShiftSolverLogger.debug("  "+team.getEmployees().size()+" employees");		
		ShiftSolverLogger.debug("  "+team.getEditors().size()+" editors");		
		ShiftSolverLogger.debug("  "+team.getSportaci().size()+" sportaks");		
		ShiftSolverLogger.debug("");
		
		return state;		
	}
	
	public RiaState createSmallFooState() {
		RiaState state = new RiaState();
		
		List<Employee> employeesList=new ArrayList<Employee>();
		
		Employee lenka=new Employee();
		lenka.setKey("1");
		lenka.setFirstname("Lenka");
		lenka.setFamilyname("Strelenka");
		lenka.setEditor(true);
		lenka.setFulltime(false);
		lenka.setFemale(true);
		employeesList.add(lenka);
		
		Employee misa=new Employee();
		misa.setKey("2");
		misa.setFirstname("Misa");
		misa.setFamilyname("Plysak");
		misa.setEditor(true);
		misa.setFulltime(true);
		misa.setFemale(true);
		employeesList.add(misa);
		
		Employee mirek=new Employee();
		mirek.setKey("3");
		mirek.setFirstname("Mirek");
		mirek.setFamilyname("Drson");
		mirek.setEditor(true);
		mirek.setFulltime(true);
		mirek.setFemale(false);
		employeesList.add(mirek);
		
		state.setEmployees(employeesList.toArray(new Employee[employeesList.size()]));
		
		PeriodPreferences periodPreferences = new PeriodPreferences(2015, 10);
		periodPreferences.setKey("1");
		periodPreferences.setMonthDays(31);
		periodPreferences.setMonthWorkDays(22);
		periodPreferences.setStartWeekDay(5);
		EmployeePreferences employeePreferences = new EmployeePreferences();
		employeePreferences.setPreferences(new ArrayList<DayPreference>());
		periodPreferences.addEmployeePreferences(lenka.getKey(), employeePreferences);
		periodPreferences.addEmployeePreferences(misa.getKey(), employeePreferences);
		periodPreferences.addEmployeePreferences(mirek.getKey(), employeePreferences);
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);
		
		return state;
	}
	
	public RiaState createBigFooState() {
		RiaState state = new RiaState();

		Team team=new Team();
		int key=0;

		Employee e, igor;

		/*
		 * editors
		 */
		
		igor=e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Igor");
		e.setFamilyname("M");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Mirko");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Alice");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Martin");
		e.setFamilyname("B");
		e.setEditor(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Lenka");
		e.setFamilyname("K");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		/*
		 * sportaci
		 */

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Simona");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Katarina");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("David");
		e.setFamilyname("B");
		e.setMortak(true);
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojta");
		e.setFamilyname("M");
		e.setSportak(true);
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojta");
		e.setFamilyname("B");
		e.setSportak(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Standa");
		e.setFamilyname("Z");
		e.setSportak(true);
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marek");
		e.setFamilyname("A");
		e.setSportak(true);
		e.setFulltime(true);
		team.addEmployee(e);
		
		/*
		 * fulltime
		 */

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Martin");
		e.setFamilyname("H");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Milan");
		e.setFamilyname("K");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Jan");
		e.setFamilyname("P");
		e.setFulltime(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marika");
		e.setFamilyname("T");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Michaela");
		e.setFamilyname("V");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Kristina");
		e.setFamilyname("W");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojtech");
		e.setFamilyname("K");
		e.setFulltime(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Anna");
		e.setFamilyname("K");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		/*
		 * part time
		 */
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("David");
		e.setFamilyname("B");
		e.setFulltime(false);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Matej");
		e.setFamilyname("S");
		e.setFulltime(false);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Marie");
		e.setFamilyname("H");
		e.setFulltime(false);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Dominika");
		e.setFamilyname("P");
		e.setFulltime(false);
		e.setFemale(true);
		team.addEmployee(e);
		
		// ...
		
		state.setEmployees(team.getStableEmployeeList().toArray(new Employee[team.getStableEmployeeList().size()]));
		
		int year=2015;
		int month=10;
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		periodPreferences.setKey("2");
		periodPreferences.setMonthDays(31);
		periodPreferences.setMonthWorkDays(22);
		periodPreferences.setStartWeekDay(5);
		
		EmployeePreferences employeePreferences;
		for(Employee ee:state.getEmployees()) {
			ArrayList<DayPreference> dayPreferencesList = new ArrayList<DayPreference>();
			if(ee.getKey().equals(igor.getKey())) {
				DayPreference dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(1);
				dayPreference.setNoDay(true);
				dayPreferencesList.add(dayPreference);

				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(2);
				dayPreference.setNoMorning6(true);
				dayPreferencesList.add(dayPreference);
				
				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(3);
				dayPreference.setNoAfternoon(true);
				dayPreferencesList.add(dayPreference);
				
				dayPreference = new DayPreference();
				dayPreference.setYear(year);
				dayPreference.setMonth(month);
				dayPreference.setDay(4);
				dayPreference.setNoNight(true);
				dayPreferencesList.add(dayPreference);
			}
			employeePreferences = new EmployeePreferences();
			employeePreferences.setPreferences(dayPreferencesList);
			periodPreferences.addEmployeePreferences(ee.getKey(), employeePreferences);			
		}
		
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);
		
		return state;		
	}
}
