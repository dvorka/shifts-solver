package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.Team;

public class Utils {

	public static RiaState createBigFooState() {
		RiaState state = new RiaState();

		Team team=new Team();
		int key=0;

		/*
		 * editors
		 */

		Employee e;
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Lenka");
		e.setFamilyname("X");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Misa");
		e.setFamilyname("X");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Mirek");
		e.setFamilyname("X");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Martin");
		e.setFamilyname("X");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Igor");
		e.setFamilyname("X");
		e.setEditor(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Alice");
		e.setFamilyname("X");
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
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("David");
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Vojta");
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(true);
		e.setFemale(false);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Katka");
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		/*
		 * fulltime
		 */

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Honza");
		e.setFamilyname("X");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Kristina");
		e.setFamilyname("X");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);

		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Dominika");
		e.setFamilyname("X");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setFirstname("Anna");
		e.setFamilyname("X");
		e.setSportak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Milan");
		e.setFamilyname("X");
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		/*
		 * part time
		 */
		
		// ...
		state.setEmployees(team.getStableEmployeeList().toArray(new Employee[team.getStableEmployeeList().size()]));
		
		PeriodPreferences periodPreferences = new PeriodPreferences(2015, 10);
		periodPreferences.setKey("2");
		periodPreferences.setMonthDays(31);
		periodPreferences.setStartWeekDay(5);
		
		EmployeePreferences employeePreferences = new EmployeePreferences();
		employeePreferences.setPreferences(new ArrayList<DayPreference>());
		for(Employee ee:state.getEmployees()) {
			periodPreferences.addEmployeePreferences(ee, employeePreferences);			
		}
		
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);
		
		return state;		
	}
	
	public static RiaState createSmallFooState() {
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
		periodPreferences.setStartWeekDay(5);
		EmployeePreferences employeePreferences = new EmployeePreferences();
		employeePreferences.setPreferences(new ArrayList<DayPreference>());
		periodPreferences.addEmployeePreferences(lenka, employeePreferences);
		periodPreferences.addEmployeePreferences(misa, employeePreferences);
		periodPreferences.addEmployeePreferences(mirek, employeePreferences);
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);
		
		return state;
	}
}
