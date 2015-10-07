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
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			EmployeeAllocation employeeAllocation = new EmployeeAllocation(e, periodPreferences.getMonthWorkDays());
			employeeAllocation.stableArrayIndex = i;
			employeeAllocations.put(e.getKey(), employeeAllocation);
		}
		
		if(!solveDay(1, result)) {
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
		showProgress(preferences.getMonthDays(), d-1);
		
		if(d>preferences.getMonthDays()) {
			DEBUG("SOLUTION FOUND");
			return true; // solution DONE ;)
		}
		
		DEBUG("Day "+d+":");

		DaySolution daySolution = new DaySolution();
		result.addDaySolution(daySolution);
		daySolution.setDay(d);
				
		if(Utils.isWeekend(d, preferences.getStartWeekDay())) {
			daySolution.setWorkday(false);
			daySolution.setWeekendMorningShift(new WeekendMorningShift());
			daySolution.setWeekendAfternoonShift(new WeekendAfternoonShift());
			daySolution.setNightShift(new NightShift());
						
			if(!assignWeekendMorningEditor(d, daySolution, result)) {
				// BACKTRACK previous day / END if on the first day
				DEBUG("--> Solution doesn't exist for day "+d+" --> BACKTRACK WEEKEND DAY");
				result.getDays().remove(daySolution);
				return false;
			}
		} else {
			daySolution.setWorkday(true);
			daySolution.setWorkdayMorningShift(new WorkdayMorningShift());
			daySolution.setWorkdayAfternoonShift(new WorkdayAfternoonShift());
			daySolution.setNightShift(new NightShift());

			if(!assignWorkdayMorningEditor(d, daySolution, result)) {
				// BACKTRACK previous day / END if on the first day
				DEBUG("--> Solution doesn't exist for day "+d+" --> BACKTRACK DAY");
				result.getDays().remove(daySolution);
				return false;
			}
		}
		
		return true; // day DONE
	}

	/*
	 * assign a role to particular shift's slot
	 */

	private boolean assignWeekendMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {		
		// TODO editor should be friday afternoon editor (verify on Friday that editor has capacity for 3 shifts)
		//      PROBLEM:  if friday/saturday is in different month, there is no way to ensure editor continuity (Friday afternoon + Sat + Sun)
		//      SOLUTION: simply introduce a field like year/month where from dropbox you can choose friday editor
		
		DEBUG(" Weekend Morning");
		
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendMorningShift().editor=lastAssignee;
			if(assignWeekendMorningDrone6am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "EDITOR"); 
		//daySolution.getWeekendMorningShift().editor=null;
		return false;
	}
	
	private boolean assignWeekendMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendMorningShift().drone6am=lastAssignee;
			if(assignWeekendMorningSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "DRONE"); 
//		/daySolution.getWeekendMorningShift().drone6am=null;
		return false;
	}

	private boolean assignWeekendMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendMorningShift().sportak=lastAssignee;
			if(assignWeekendAfternoonEditor(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "SPORTAK"); 
		//daySolution.getWeekendMorningShift().sportak=null;
		return false;
	}

	private boolean assignWeekendAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		DEBUG(" Weekend Afternoon");
		
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWeekendAfternoon(daySolution.getWeekendMorningShift().editor, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendAfternoonShift().editor=lastAssignee;
			if(assignWeekendAfternoonDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWeekendAfternoonShift().editor=null;
		return false;
	}
	
	private boolean assignWeekendAfternoonDrone(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendAfternoonShift().drone=lastAssignee;
			if(assignWeekendAfternoonSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "STAFFER"); 
		//daySolution.getWeekendAfternoonShift().drone=null;
		return false;
	}
	
	private boolean assignWeekendAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWeekendAfternoonShift().sportak=lastAssignee;
			if(assignWeekendNightDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWeekendAfternoonShift().sportak=null;
		return false;
	}

	private boolean assignWeekendNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		DEBUG(" Weekend Night");

		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendNight(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getNightShift().drone=lastAssignee;
			if(solveDay(d+1, result)) {
				return true;
			}			
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "NIGHT", "DRONE");
		//daySolution.getNightShift().drone=null;
		return false;
	}
	
	private boolean assignWorkdayMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {
		DEBUG(" Morning");
		
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayMorningShift().editor=lastAssignee;
			if(assignWorkdayMorningDrone6am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "EDITOR"); 
		//daySolution.getWorkdayMorningShift().editor=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayMorningShift().drone6am=lastAssignee;
			if(assignWorkdayMorningDrone7am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "DRONE6AM"); 
		//daySolution.getWorkdayMorningShift().drone6am=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone7am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayMorningShift().drone7am=lastAssignee;
			if(assignWorkdayMorningDrone8am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "DRONE7AM"); 
		//daySolution.getWorkdayMorningShift().drone7am=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone8am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayMorningShift().drone8am=lastAssignee;
			if(assignWorkdayMorningSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "DRONE8AM"); 
		//daySolution.getWorkdayMorningShift().drone8am=null;
		return false;		
	}
	
	private boolean assignWorkdayMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayMorningShift().sportak=lastAssignee;
			if(assignWorkdayAfternoonEditor(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "MORNING", "SPORTAK"); 
		//daySolution.getWorkdayMorningShift().sportak=null;
		return false;		
	}

	private boolean assignWorkdayAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		DEBUG(" Afternoon");
				
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().editor=lastAssignee;
			if(assignWorkdayAfternoonDrone1(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWorkdayAfternoonShift().editor=null;
		return false;		
	}
	
	private boolean assignWorkdayAfternoonDrone1(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().drones[0]=lastAssignee;
			if(assignWorkdayAfternoonDrone2(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "DRONE1"); 
		//daySolution.getWorkdayAfternoonShift().drones[0]=null;
		return false;				
	}
		
	private boolean assignWorkdayAfternoonDrone2(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().drones[1]=lastAssignee;
			if(assignWorkdayAfternoonDrone3(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "DRONE2"); 
		//daySolution.getWorkdayAfternoonShift().drones[1]=null;
		return false;				
	}
	
	private boolean assignWorkdayAfternoonDrone3(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().drones[2]=lastAssignee;
			if(assignWorkdayAfternoonDrone4(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "DRONE3"); 
		//daySolution.getWorkdayAfternoonShift().drones[2]=null;
		return false;				
	}
	
	private boolean assignWorkdayAfternoonDrone4(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().drones[3]=lastAssignee;
			if(assignWorkdayAfternoonSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "DRONE4"); 
		//daySolution.getWorkdayAfternoonShift().drones[3]=null;
		return false;				
	}

	private boolean assignWorkdayAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getWorkdayAfternoonShift().sportak=lastAssignee;
			if(assignWorkdayNightDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWorkdayAfternoonShift().sportak=null;
		return false;				
		
	}
	
	private boolean assignWorkdayNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		DEBUG(" Night");

		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayNight(employees, daySolution, lastAssignee))!=null) {
			DEBUG_DOWN(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign();					
			daySolution.getNightShift().drone=lastAssignee;
			if(solveDay(d+1, result)) {
				return true;
			}			
			employeeAllocations.get(lastAssignee.getKey()).unassign();
		}
		DEBUG_UP(d, "NIGHT", "DRONE");
		//daySolution.getNightShift().drone=null;
		return false;
	}
	
	private int getLastAssigneeIndex(Employee lastAssignee) {
		if(lastAssignee!=null) {
			return employeeAllocations.get(lastAssignee.getKey()).stableArrayIndex;
		}
		return 0;
	}
	
	/*
	 * find a role for particular shift
	 */
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex+1; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() || e.isMorningSportak()) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		if(employeeAllocations.get(e.getKey()).hasCapacity()) {
			DEBUG("  Assigning "+e.getFullName()+" as editor");
			return e;
		}
		// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
		return null;			
	}

	private Employee findDroneForWeekendNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		 // anybody except sportak e.g. normal, editor, MorningSportak
		int lastIndex = getLastAssigneeIndex(lastAssignee);
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		for(int i=lastIndex+1; i<employees.size(); i++) {
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
		int percent = processedDays==0?0:Math.round(((float)processedDays) / (((float)days)/100f));
		String message="Building shifts schedule: "+percent+"% ("+processedDays+"/"+days+")";
		if(ctx!=null) {
			ctx.getStatusLine().showProgress(message);			
		} else {
			System.out.println(message);
		}		
		// TODO extra panel for progress ctx.getSolverProgressPanel().refresh(percent);
	}
	
	private void DEBUG_DOWN(int d, String shiftType, String role, int count) {
		DEBUG("   >>> DOWN for day "+d+", shift "+shiftType+" and role "+role+" #"+count);
	}
	
	private void DEBUG_UP(int d, String shiftType, String role) {
		DEBUG("   <<< BACKTRACK UP - failed for day "+d+", shift "+shiftType+" and role "+role);
	}
	
	private void DEBUG(String message) {
		if(ctx==null) {
			System.out.println(message);
		}
	}
}
