package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.Team;

public class Utils {

	public static boolean isWeekend(int i, int startWeekDay) {
		int sundayBeginningDayNumber=(i-1+startWeekDay-1)%7;
		if(sundayBeginningDayNumber==0 || sundayBeginningDayNumber==6) {
			return true;
		} else {
			return false;
		}
	}
	
	public static RiaState createBigFooState() {
		RiaState state = new RiaState();

		Team team=new Team();
		int key=0;

		Employee e;

		/*
		 * editors
		 */
		
		e=new Employee();
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
		e.setMorningSportak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("Katarina");
		e.setFamilyname("B");
		e.setMorningSportak(true);
		e.setFulltime(true);
		e.setFemale(true);
		team.addEmployee(e);
		
		e=new Employee();
		e.setKey(""+key++);
		e.setFirstname("David");
		e.setFamilyname("B");
		e.setMorningSportak(true);
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
		
		EmployeePreferences employeePreferences = new EmployeePreferences();
		for(Employee ee:state.getEmployees()) {
			ArrayList<DayPreference> dayPreferencesList = new ArrayList<DayPreference>();
			employeePreferences.setPreferences(dayPreferencesList);
			if(ee.getFirstname().equals("Igor")) {
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
				dayPreference.setDay(2);
				dayPreference.setNoNight(true);
				dayPreferencesList.add(dayPreference);
			}
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
		periodPreferences.setMonthWorkDays(22);
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
