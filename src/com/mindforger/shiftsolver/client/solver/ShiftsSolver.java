package com.mindforger.shiftsolver.client.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Job;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.model.Team;
import com.mindforger.shiftsolver.shared.model.shifts.NightShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendMorningShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayMorningShift;

/**
 * Solver runs on the client side (browser) to off-load work from the server (server is 
 * used for persistence only - team/preferences/solution).
 * 
 * Solver is stateful i.e. after initialization caller may get other solutions using
 * {@link ShiftsSolver#next()} method.
 */
public class ShiftsSolver {
	
	private static long sequence=0;

	private RiaContext ctx;
	private RiaMessages i18n;
	
	private PeriodPreferences preferences;
	private Map<String,EmployeeAllocation> employeeAllocations;
	private List<Employee> employees;

	public ShiftsSolver() {
	}
	
	public ShiftsSolver(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public PeriodSolution solve(Set<Employee> keySet, PeriodPreferences periodPreferences) {
  		Team team=new Team();
  		team.addEmployees(periodPreferences.getEmployeeToPreferences().keySet());
		return solve(team, periodPreferences);
	}	

	public Map<String, EmployeeAllocation> getEmployeeAllocations() {
		return employeeAllocations;
	}
	
	public PeriodSolution solve(Team team, PeriodPreferences periodPreferences) {
		this.preferences=periodPreferences;
		
		PeriodSolution result = new PeriodSolution(periodPreferences.getYear(), periodPreferences.getMonth());
		result.setDlouhanKey(periodPreferences.getKey());
		result.setKey(periodPreferences.getKey() + "/" + ++sequence);
		
		employees = team.getStableEmployeeList();
		employeeAllocations = new HashMap<String,EmployeeAllocation>();
		for(Employee e:employees) {
			employeeAllocations.put(e.getKey(), new EmployeeAllocation(e, periodPreferences.getMonthWorkDays()));
		}
		
		if(solveDay(1, result)) {
			// NO SOLUTION exists for this team and requirements
			DEBUG("NO SOLUTION EXISTS!");
			return null;
		} else {
			for(String key:employeeAllocations.keySet()) {
				result.addEmployeeJob(
						key, 
						new Job(employeeAllocations.get(key).shifts, employeeAllocations.get(key).shiftsToGet));
			}	
			return result;			
		}
	}

	private boolean solveDay(int d, PeriodSolution result) {
		DEBUG("Day "+d+":");

		DaySolution daySolution = new DaySolution();
		daySolution.setDay(d);
		
		if(Utils.isWeekend(d, preferences.getStartWeekDay())) {
			daySolution.setWorkday(false);
			
			DEBUG(" Weekend Morning");
			WeekendMorningShift weekendMorningShift = new WeekendMorningShift();
			daySolution.setWeekendMorningShift(weekendMorningShift);
			if(assignWeekendMorningEditor(d, daySolution, weekendMorningShift, null)) {
				// BACKTRACK previous day / END if on the first day
				return true;
			}
			assignWeekendMorningDrone6am(d, daySolution, weekendMorningShift, null);				
			assignWeekendMorningSportak(d, daySolution, weekendMorningShift, null);
							
			DEBUG(" Weekend Afternoon");
			WeekendAfternoonShift weekendAfternoonShift=new WeekendAfternoonShift();
			daySolution.setWeekendAfternoonShift(weekendAfternoonShift);			
			assignWeekendAfternoonEditor(d, daySolution, weekendAfternoonShift, weekendMorningShift, null);
			assignWeekendAfternoonDrone(d, daySolution, weekendAfternoonShift, weekendMorningShift, null);
			assignWeekendAfternoonSportak(d, daySolution, weekendAfternoonShift, weekendMorningShift, null);
			
			DEBUG(" Weekend Night");
			NightShift nightShift=new NightShift();
			daySolution.setNightShift(nightShift);
			assignWeekendNightDrone(d, daySolution, nightShift, weekendAfternoonShift, weekendMorningShift, null);
		} else {
			daySolution.setWorkday(true);

			DEBUG(" Morning");
			WorkdayMorningShift workdayMorningShift=new WorkdayMorningShift();
			daySolution.setWorkdayMorningShift(workdayMorningShift);
			if(assignWorkdayMorningEditor(d, daySolution, workdayMorningShift, null)) {
				// BACKTRACK previous day / END if on the first day
				return true;
			}
			assignWorkdayMorningDrone6am(d, daySolution, workdayMorningShift, null);
			assignWorkdayMorningDrone7am(d, daySolution, workdayMorningShift, null);
			assignWorkdayMorningDrone8am(d, daySolution, workdayMorningShift, null);
			assignWorkdayMorningSportak(d, daySolution, workdayMorningShift, null);
			
			DEBUG(" Afternoon");
			WorkdayAfternoonShift workdayAfternoonShift=new WorkdayAfternoonShift();
			daySolution.setWorkdayAfternoonShift(workdayAfternoonShift);
			assignWorkdayAfternoonEditor(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			assignWorkdayAfternoonDrone1(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			assignWorkdayAfternoonDrone2(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			assignWorkdayAfternoonDrone3(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			assignWorkdayAfternoonDrone4(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			assignWorkdayAfternoonSportak(d, daySolution, workdayAfternoonShift, workdayMorningShift, null);
			
			DEBUG(" Night");
			NightShift nightShift=new NightShift();
			daySolution.setNightShift(nightShift);
			assignWorkdayNightDrone(d, daySolution, nightShift, workdayAfternoonShift, workdayMorningShift, null);							
		}
		result.addDaySolution(daySolution);
		
		showProgress(preferences.getMonthDays(), d);
		
		if(d<preferences.getMonthDays()) {
			if(solveDay(d+1, result)) {
				// BACKTRACK this day
				// TODO remove last solution from the result (and ensure it is returned > perhaps NIGHT handler should insert it
				// TODO workday night
			}
		}
		
		return false;
	}

	/*
	 * assign a role to particular shift's slot
	 */
	
	private void assignWorkdayNightDrone(
			int d, 
			DaySolution daySolution, 
			NightShift nightShift, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		nightShift.drone=findDroneForWorkdayNight(employees, daySolution, lastAssignee);
		if(nightShift.drone==null) {
			DEBUG_BACKTRACK(d, "NIGHT", "STAFFER");			
			// BACKTRACK
			assignWorkdayAfternoonSportak(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		}  else {
			employeeAllocations.get(nightShift.drone.getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonSportak(
			int d, 
			DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.sportak=findSportakForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.sportak==null) {
			DEBUG_BACKTRACK(d, "AFTERNOON", "SPORTAK");
			// BACKTRACK
			assignWorkdayAfternoonDrone4(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.sportak.getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonDrone4(
			int d, 
			DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.drones[3]=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.drones[3]==null) {
			DEBUG_BACKTRACK(d, "Afternoon", "STAFFER");
			// BACKTRACK
			assignWorkdayAfternoonDrone3(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.drones[3].getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonDrone3(
			int d, 
			DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.drones[2]=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.drones[2]==null) {
			DEBUG_BACKTRACK(d, "Afternoon", "STAFFER");
			// BACKTRACK
			assignWorkdayAfternoonDrone2(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.drones[2].getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonDrone2(
			int d, DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.drones[1]=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.drones[1]==null) {
			DEBUG_BACKTRACK(d, "Afternoon", "STAFFER");
			// BACKTRACK
			assignWorkdayAfternoonDrone1(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.drones[1].getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonDrone1(
			int d, 
			DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.drones[0]=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.drones[0]==null) {
			DEBUG_BACKTRACK(d, "Afternoon", "STAFFER");
			// BACKTRACK
			assignWorkdayAfternoonEditor(d, daySolution, workdayAfternoonShift, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.drones[0].getKey()).assign();					
		}
	}

	private void assignWorkdayAfternoonEditor(
			int d, 
			DaySolution daySolution, 
			WorkdayAfternoonShift workdayAfternoonShift, 
			WorkdayMorningShift workdayMorningShift, 
			Employee lastAssignee) 
	{
		workdayAfternoonShift.editor=findEditorForWorkdayAfternoon(employees, daySolution, lastAssignee);
		if(workdayAfternoonShift.editor==null) {
			DEBUG_BACKTRACK(d, "AFTERNOON", "EDITOR");
			// BACKTRACK
			assignWorkdayMorningSportak(d, daySolution, workdayMorningShift, lastAssignee);
		} else {
			employeeAllocations.get(workdayAfternoonShift.editor.getKey()).assign();					
		}
	}

	private void assignWorkdayMorningSportak(int d, DaySolution daySolution, WorkdayMorningShift workdayMorningShift, Employee lastAssignee) {
		workdayMorningShift.sportak=findSportakForWorkdayMorning(employees, daySolution, lastAssignee);
		if(workdayMorningShift.sportak==null) {
			DEBUG_BACKTRACK(d, "MORNING", "SPORTAK");
			// BACKTRACK
			assignWorkdayMorningDrone8am(d, daySolution, workdayMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(workdayMorningShift.sportak.getKey()).assign();					
		}
	}

	private void assignWorkdayMorningDrone8am(int d, DaySolution daySolution, WorkdayMorningShift workdayMorningShift, Employee lastAssignee) {
		workdayMorningShift.drone8am=findDroneForWorkdayMorning(employees, daySolution, lastAssignee);
		if(workdayMorningShift.drone8am==null) {
			DEBUG_BACKTRACK(d, "MORNING", "STAFFER 8am");;
			// BACKTRACK
			assignWorkdayMorningDrone7am(d, daySolution, workdayMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(workdayMorningShift.drone8am.getKey()).assign();					
		}
	}

	private void assignWorkdayMorningDrone7am(int d, DaySolution daySolution, WorkdayMorningShift workdayMorningShift, Employee lastAssignee) {
		workdayMorningShift.drone7am=findDroneForWorkdayMorning(employees, daySolution, lastAssignee);
		if(workdayMorningShift.drone7am==null) {
			DEBUG_BACKTRACK(d, "MORNING", "STAFFER");
			// BACKTRACK
			assignWorkdayMorningDrone6am(d, daySolution, workdayMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(workdayMorningShift.drone7am.getKey()).assign();					
		}
	}

	private void assignWorkdayMorningDrone6am(int d, DaySolution daySolution, WorkdayMorningShift workdayMorningShift, Employee lastAssignee) {
		workdayMorningShift.drone6am=findDroneForWorkdayMorning(employees, daySolution, lastAssignee);
		if(workdayMorningShift.drone6am==null) {
			DEBUG_BACKTRACK(d, "MORNING", "STAFFER");
			// BACKTRACK
			assignWorkdayMorningEditor(d, daySolution, workdayMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(workdayMorningShift.drone6am.getKey()).assign();					
		}
	}

	private boolean assignWorkdayMorningEditor(int d, DaySolution daySolution, WorkdayMorningShift workdayMorningShift, Employee lastAssignee) {
		workdayMorningShift.editor=findEditorForWorkdayMorning(employees, daySolution, lastAssignee);
		if(workdayMorningShift.editor==null) {
			DEBUG_BACKTRACK(d, "MORNING", "EDITOR");
			// BACKTRACK to PREVIOUS DAY
			return true;
		} else {
			employeeAllocations.get(workdayMorningShift.editor.getKey()).assign();					
		}
		return false;
	}

	private void assignWeekendNightDrone(
			int d, 
			DaySolution daySolution, 
			NightShift nightShift, 
			WeekendAfternoonShift weekendAfternoonShift, 
			WeekendMorningShift weekendMorningShift, 
			Employee lastAssignee) 
	{
		nightShift.drone=findDroneForWeekendNight(employees, daySolution, lastAssignee);
		if(nightShift.drone==null) {
			DEBUG_BACKTRACK(d, "NIGHT", "STAFFER");					
			// BACKTRACK
			assignWeekendAfternoonSportak(d, daySolution, weekendAfternoonShift, weekendMorningShift, lastAssignee);			
		}  else {
			employeeAllocations.get(nightShift.drone.getKey()).assign();					
		}
	}

	private void assignWeekendAfternoonSportak(
			int d, 
			DaySolution daySolution, 
			WeekendAfternoonShift weekendAfternoonShift, 
			WeekendMorningShift weekendMorningShift, 
			Employee lastAssignee) 
	{
		weekendAfternoonShift.sportak=findSportakForWeekendAfternoon(employees, daySolution, lastAssignee);
		if(weekendAfternoonShift.sportak==null) {
			DEBUG_BACKTRACK(d, "AFTERNOON", "SPORTAK");
			// BACKTRACK
			assignWeekendAfternoonDrone(d, daySolution, weekendAfternoonShift, weekendMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(weekendAfternoonShift.sportak.getKey()).assign();					
		}
	}

	private void assignWeekendAfternoonDrone(
			int d, 
			DaySolution daySolution, 
			WeekendAfternoonShift weekendAfternoonShift, 
			WeekendMorningShift weekendMorningShift, 
			Employee lastAssignee) 
	{
		weekendAfternoonShift.drone=findDroneForWeekendAfternoon(employees, daySolution, lastAssignee);
		if(weekendAfternoonShift.drone==null) {
			DEBUG_BACKTRACK(d, "AFTERNOON", "STAFFER");
			// BACKTRACK
			assignWeekendAfternoonEditor(d, daySolution, weekendAfternoonShift, weekendMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(weekendAfternoonShift.drone.getKey()).assign();
		}
	}

	private void assignWeekendAfternoonEditor(
			int d, 
			DaySolution daySolution,
			WeekendAfternoonShift weekendAfternoonShift,
			WeekendMorningShift weekendMorningShift,
			Employee lastAssignee) 
	{
		weekendAfternoonShift.editor=findEditorForWeekendAfternoon(weekendMorningShift.editor, daySolution, lastAssignee);
		if(weekendAfternoonShift.editor==null) {
			DEBUG_BACKTRACK(d, "AFTERNOON", "EDITOR");
			// BACKTRACK
			assignWeekendMorningSportak(d, daySolution, weekendMorningShift, lastAssignee);			
		} else {
			employeeAllocations.get(weekendAfternoonShift.editor.getKey()).assign();					
		}
	}

	private boolean assignWeekendMorningEditor(int d, DaySolution daySolution, WeekendMorningShift shift, Employee lastAssignee) {
		// TODO editor should be friday afternoon editor (verify on Friday that editor has capacity for 3 shifts)
		// TODO PROBLEM if friday/saturday is in different month, there is no way to ensure editor continuity (Friday afternoon + Sat + Sun)
		// TODO simply introduce a field like year/month where from dropbox you can choose friday editor
		
		shift.editor=findEditorForWeekendMorning(employees, daySolution, lastAssignee);
		if(shift.editor==null) { 
			DEBUG_BACKTRACK(d, "MORNING", "EDITOR"); 
			// BACKTRACK to PREVIOUS DAY
			return true;
		} else {
			employeeAllocations.get(shift.editor.getKey()).assign();					
		}
		return false;
	}
	
	private void assignWeekendMorningSportak(int d, DaySolution daySolution, WeekendMorningShift shift, Employee lastAssignee) {
		shift.sportak=findSportakForWeekendMorning(employees, daySolution, lastAssignee);
		if(shift.sportak==null) {
			DEBUG_BACKTRACK(d, "MORNING", "SPORTAK");
			// BACKTRACK
			assignWeekendMorningDrone6am(d, daySolution, shift, shift.drone6am);
		} else {
			employeeAllocations.get(shift.sportak.getKey()).assign();					
		}
	}

	private void assignWeekendMorningDrone6am(int d, DaySolution daySolution, WeekendMorningShift  shift, Employee lastAssignee) {
		shift.drone6am=findDroneForWeekendMorning(employees, daySolution, lastAssignee);
		if(shift.drone6am==null) {
			DEBUG_BACKTRACK(d, "MORNING", "Editor");
			// BACKTRACK
			assignWeekendMorningEditor(d, daySolution, shift, shift.editor);
		} else {
			employeeAllocations.get(shift.drone6am.getKey()).assign();					
		}		
	}

	private int getLastAssigneeIndex(Employee lastAssignee) {
		int lastIndex=0;
		if(lastAssignee!=null) {
			EmployeeAllocation lastEmployeeAllocation = employeeAllocations.get(lastAssignee.getKey());
			lastEmployeeAllocation.unassign();
			lastIndex = lastEmployeeAllocation.stableArrayIndex;			
		}
		return lastIndex;
	}
	
	/*
	 * find a role for particular shift
	 */
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() && !e.isMorningSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as sportak");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isEditor()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as editor");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() && !e.isMorningSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as sportak");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isEditor()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as editor");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		 // anybody except sportak e.g. normal, editor, MorningSportak
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() && !e.isMorningSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as sportak");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWeekendAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendAfternoon(Employee e, DaySolution daySolution, Employee lastAssignee) {
		if(lastAssignee==null) {
			if(employeeAllocations.get(e.getKey()).hasCapacity()) {
				DEBUG("  Assigning "+e.getFullName()+" as editor");
				return e;
			}
			return null;			
		} else {
			// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
			return null;
		}
	}

	private Employee findDroneForWeekendNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		 // anybody except sportak e.g. normal, editor, MorningSportak
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() && !e.isMorningSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as sportak");
						return e;
					}
				}
			}
		}
		return null;
	}
	
	private Employee findDroneForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity()) {
						DEBUG("  Assigning "+e.getFullName()+" as staff");
						return e;
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {					
					if(e.isEditor()) {
						DEBUG("  Assigning "+e.getFullName()+" as editor");
						return e;
					}					
				}
			}
		}
		return null;
	}

	private void showProgress(int days, int processedDays) {
		int percent = Math.round(((float)processedDays) / (((float)days)/100f));
		String message="Building shifts schedule: "+percent+"% ("+processedDays+"/"+days+")";
		if(ctx!=null) {
			ctx.getStatusLine().showProgress(message);			
		} else {
			System.out.println(message);
		}		
		// TODO extra panel for progress ctx.getSolverProgressPanel().refresh(percent);
	}
	
	private void DEBUG_BACKTRACK(int d, String shiftType, String role) {
		DEBUG("--> Solution doesn't exist for day "+d+", shift "+shiftType+" and role "+role+" --> BACKTRACK");
	}
	
	private void DEBUG(String message) {
		if(ctx!=null) {
			ctx.getStatusLine().showProgress(message);			
		}		
	}
}
