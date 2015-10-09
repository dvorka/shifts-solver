package com.mindforger.shiftsolver.client.solver;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DayPreference;
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
 * {@link ShiftSolver#next()} method.
 * 
 * Performance improvements:
 *  - prune the space and detect that there is no solution as soon as possible:
 *    > count jobs for all roles and compare it with people you have > fail even without calling any depth if e.g. not enough sportaks
 *  - iterate only sportaks for sportak, editors for editor (not all)
 */
// Split to solver work to show progress
// http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsDelayed.html
public class ShiftSolver {
	
	public static int STEPS_LIMIT=3000000;
	
	private static long sequence=0;

	private RiaContext ctx;
	private RiaMessages i18n;
	
	private PeriodPreferences preferences;
	private Map<String,EmployeeAllocation> employeeAllocations;
	private List<Employee> employees;

	private SolverProgressPanels solverProgressPanel;
	private int steps;
	private int failedOnDay; // deepest
	private String failedOnShiftType;
	private String failedOnRole;
	private int solutionsCount;
	private int bestScore;
	private PeriodSolution bestSolution; // TODO must be cloned

	public ShiftSolver() {
		this.solverProgressPanel=new DebugSolverPanel();
	}
	
	public ShiftSolver(final RiaContext ctx) {
		this();
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public PeriodSolution solve(List<Employee> employees, PeriodPreferences periodPreferences, int solutionNumber) {
  		Team team=new Team();
  		team.addEmployees(employees);
		return solve(team, periodPreferences, solutionNumber);
	}	

	public Map<String, EmployeeAllocation> getEmployeeAllocations() {
		return employeeAllocations;
	}
	
	public PeriodSolution solve(Team team, PeriodPreferences periodPreferences, int solutionNumber) {
		if(ctx!=null) {
			ctx.getStatusLine().showProgress("Calculating shifts schedule solution...");
			this.solverProgressPanel=ctx.getSolverProgressPanel();
			ctx.getRia().showSolverProgressPanel();
		}

		this.preferences=periodPreferences;
		
		PeriodSolution result = new PeriodSolution(periodPreferences.getYear(), periodPreferences.getMonth());
		result.setDlouhanKey(periodPreferences.getKey());
		result.setKey(periodPreferences.getKey() + "/" + ++sequence);
		result.setSolutionNumber(solutionNumber);
		
		steps=0;
		clearFailedOn();
		solutionsCount=0;
		bestScore=0;
		
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
			ShiftSolverLogger.debug("NO SOLUTION EXISTS!");
			return null;
		} else {
			for(String key:employeeAllocations.keySet()) {
				result.addEmployeeJob(
						key, 
						new Job(employeeAllocations.get(key).shifts, employeeAllocations.get(key).shiftsToGet));
				if(ctx!=null) ctx.getStatusLine().showInfo("Solution #"+solutionNumber+" found!");
			}	
			return result;			
		}
	}

	private int calculateSolutionScore(PeriodSolution result) {
		// TODO calculate number of matched greens
		// TODO calculate fulltime has all uvazky to be most full
		// TODO ...
		// TODO show the score w/ solution
		return 0;
	}
	
	private boolean solveDay(int d, PeriodSolution result) {
		debugDown(d, "###DAY###", "ALL", -1);
		showProgress(preferences.getMonthDays(), d-1);
		
		if(d>preferences.getMonthDays()) {
			solutionsCount++;
			bestScore=calculateSolutionScore(result);
			// TODO bestSolution=
			solverProgressPanel.refresh(
					"100", 
					(failedOnDay==-1?"":""+failedOnDay),
					(failedOnRole==null?"":failedOnRole),
					(failedOnShiftType==null?"":failedOnShiftType),
					""+steps, 
					""+solutionsCount, 
					""+bestScore);
			
			if(result.getSolutionNumber()>1) {
				ShiftSolverLogger.debug("SOLUTION FOUND >>> GOING FOR NEXT "+result.getSolutionNumber());
				result.setSolutionNumber(result.getSolutionNumber()-1);
				// going for another solution
				return false;
			} else {
				ShiftSolverLogger.debug("SOLUTION FOUND");
				// solution DONE ;)				
				return true; 
			}
		}
		
		ShiftSolverLogger.debug("Day "+d+":");

		DaySolution daySolution = new DaySolution(
				d, 
				Utils.getWeekdayNumber(d, preferences.getStartWeekDay())+1, // Calendar.(weekday) starts with 1
				!Utils.isWeekend(d, preferences.getStartWeekDay()));
		result.addDaySolution(daySolution);
				
		if(Utils.isWeekend(d, preferences.getStartWeekDay())) {
			daySolution.setWorkday(false);
			daySolution.setWeekendMorningShift(new WeekendMorningShift());
			daySolution.setWeekendAfternoonShift(new WeekendAfternoonShift());
			daySolution.setNightShift(new NightShift());
			
			if(!assignWeekendMorningEditor(d, daySolution, result)) {
				// BACKTRACK previous day / END if on the first day
				ShiftSolverLogger.debug("   <<< BACKTRACK ***DAY*** UP - day "+d+" --> WEEKEND DAY");
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
				ShiftSolverLogger.debug("   <<< BACKTRACK *DAY* UP - day "+d+" --> WORK DAY");
				result.getDays().remove(daySolution);
				return false;
			}
		}
		
		ShiftSolverLogger.debug("DAY "+d+" SOLVED > going *TOP* UP W/ RESULT");
		return true; // day DONE
	}

	/*
	 * assign a role to particular shift's slot
	 */

	private boolean assignWeekendMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {						
		ShiftSolverLogger.debug(" Weekend Morning");
				
		// editor has Fri afternoon > Sat morning > Sat afternoon > Sun morning > Sun afternoon continuity
		// i.e. take editor from PREVIOUS day morning
		DaySolution previousDaySolution;
		Employee previousEditor;
		if(result.getDays().size()>1) {
			previousDaySolution=result.getDays().get(result.getDays().size()-2);
			if(daySolution.getWeekday()==Calendar.SATURDAY) {
				previousEditor=previousDaySolution.getWorkdayAfternoonShift().editor;				
			} else {
				if(daySolution.getWeekday()==Calendar.SUNDAY) {
					previousEditor=previousDaySolution.getWeekendMorningShift().editor;
				} else {
					throw new RuntimeException("Workday in weekend? "+daySolution.getWeekday()+" - "+Utils.getDayLetter(d, preferences.getStartWeekDay()));
				}
			}
		} else {
			// TODO editor should be friday afternoon editor (verify on Friday that editor has capacity for 3 shifts)
			//      PROBLEM:  if friday/saturday is in different month, there is no way to ensure editor continuity (Friday afternoon + Sat + Sun)
			//      SOLUTION: simply introduce a field like year/month where from dropbox you can choose friday editor

			// TODO take previous day editors from configuration > to be implemented UI
			throw new RuntimeException("Unable to load editor from previous day as don't have previous month");
		}
		
		if(findEditorForWeekendMorning(previousEditor, daySolution)!=null) {
			debugDown(d, "MORNING", "EDITOR", -1);
			employeeAllocations.get(previousEditor.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().editor=previousEditor;
			if(assignWeekendMorningDrone6am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(previousEditor.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "EDITOR"); 
		//daySolution.getWeekendAfternoonShift().editor=null;
		return false;
	}
	
	private boolean assignWeekendMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().drone6am=lastAssignee;
			if(assignWeekendMorningSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE"); 
		//daySolution.getWeekendMorningShift().drone6am=null;
		return false;
	}

	private boolean assignWeekendMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().sportak=lastAssignee;
			if(assignWeekendAfternoonEditor(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "SPORTAK"); 
		//daySolution.getWeekendMorningShift().sportak=null;
		return false;
	}

	private boolean assignWeekendAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Weekend Afternoon");
		
		Employee lastEditor=daySolution.getWeekendMorningShift().editor;
		if((findEditorForWeekendAfternoon(lastEditor, daySolution))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", -1);
			employeeAllocations.get(lastEditor.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().editor=lastEditor;
			if(assignWeekendAfternoonDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastEditor.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWeekendAfternoonShift().editor=null;
		return false;
	}
	
	private boolean assignWeekendAfternoonDrone(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().drone=lastAssignee;
			if(assignWeekendAfternoonSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "STAFFER"); 
		//daySolution.getWeekendAfternoonShift().drone=null;
		return false;
	}
	
	private boolean assignWeekendAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().sportak=lastAssignee;
			if(assignWeekendNightDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWeekendAfternoonShift().sportak=null;
		return false;
	}

	private boolean assignWeekendNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Weekend Night");

		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWeekendNight(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "NIGHT", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, true);					
			daySolution.getNightShift().drone=lastAssignee;
			if(solveDay(d+1, result)) {
				return true;
			}			
			employeeAllocations.get(lastAssignee.getKey()).unassign(true);
		}
		debugUp(d, "NIGHT", "DRONE");
		//daySolution.getNightShift().drone=null;
		return false;
	}
	
	private boolean assignWorkdayMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Morning");
		
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().editor=lastAssignee;
			if(assignWorkdayMorningDrone6am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "EDITOR"); 
		//daySolution.getWorkdayMorningShift().editor=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE6AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone6am=lastAssignee;
			if(assignWorkdayMorningDrone7am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE6AM"); 
		//daySolution.getWorkdayMorningShift().drone6am=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone7am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE7AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone7am=lastAssignee;
			if(assignWorkdayMorningDrone8am(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE7AM"); 
		//daySolution.getWorkdayMorningShift().drone7am=null;
		return false;		
	}

	private boolean assignWorkdayMorningDrone8am(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE8AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone8am=lastAssignee;
			if(assignWorkdayMorningSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE8AM"); 
		//daySolution.getWorkdayMorningShift().drone8am=null;
		return false;		
	}
	
	private boolean assignWorkdayMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().sportak=lastAssignee;
			if(assignWorkdayAfternoonEditor(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "SPORTAK"); 
		//daySolution.getWorkdayMorningShift().sportak=null;
		return false;		
	}

	private boolean assignWorkdayAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Afternoon");
				
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findEditorForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().editor=lastAssignee;
			if(assignWorkdayAfternoonDrone1(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWorkdayAfternoonShift().editor=null;
		return false;		
	}
	
	private boolean assignWorkdayAfternoonDrone1(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE1", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[0]=lastAssignee;
			if(assignWorkdayAfternoonDrone2(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE1"); 
		//daySolution.getWorkdayAfternoonShift().drones[0]=null;
		return false;				
	}
		
	private boolean assignWorkdayAfternoonDrone2(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE2", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[1]=lastAssignee;
			if(assignWorkdayAfternoonDrone3(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE2"); 
		//daySolution.getWorkdayAfternoonShift().drones[1]=null;
		return false;				
	}
	
	private boolean assignWorkdayAfternoonDrone3(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE3", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[2]=lastAssignee;
			if(assignWorkdayAfternoonDrone4(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE3"); 
		//daySolution.getWorkdayAfternoonShift().drones[2]=null;
		return false;				
	}
	
	private boolean assignWorkdayAfternoonDrone4(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE4", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[3]=lastAssignee;
			if(assignWorkdayAfternoonSportak(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE4"); 
		//daySolution.getWorkdayAfternoonShift().drones[3]=null;
		return false;				
	}

	private boolean assignWorkdayAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findSportakForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().sportak=lastAssignee;
			if(assignWorkdayNightDrone(d, daySolution, result)) {
				return true;
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWorkdayAfternoonShift().sportak=null;
		return false;				
		
	}
	
	private boolean assignWorkdayNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Night");

		Employee lastAssignee=null;
		int c=0;
		while((lastAssignee=findDroneForWorkdayNight(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "NIGHT", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, true);					
			daySolution.getNightShift().drone=lastAssignee;
			if(solveDay(d+1, result)) {
				return true;
			}			
			employeeAllocations.get(lastAssignee.getKey()).unassign(true);
		}
		debugUp(d, "NIGHT", "DRONE");
		//daySolution.getNightShift().drone=null;
		return false;
	}
	
	private int getLastAssigneeIndexWithSkip(Employee lastAssignee) {
		if(lastAssignee==null) {
			return 0;
		} else {
			return 1+employeeAllocations.get(lastAssignee.getKey()).stableArrayIndex;			
		}
	}

	private static final DayPreference NO_PREFERENCE=new DayPreference();
	private DayPreference getDayPreference(Employee e, DaySolution daySolution) {
		DayPreference preferencesForDay = preferences.getEmployeeToPreferences().get(e.getKey()).getPreferencesForDay(daySolution.getDay());
		if(preferencesForDay!=null) {
			return preferencesForDay;
		} else {
			return NO_PREFERENCE;
		}
	}
		
	/*
	 * find a role for particular shift
	 * 
	 * TODO want - basically do 3 iterations, first iterate green, then don't care, finally NOT
	 */
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as sportak WORK AFTERNOON");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK AFTERNOON");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isEditor()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WORK AFTERNOON");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak() || e.isMorningSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as sportak WORK MORNING");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK MORNING");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isEditor()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WORK MORNING");
							return e;							
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isSportak()
					 &&
				   (daySolution.getWeekday()!=Calendar.FRIDAY
					 ||
				   (daySolution.getWeekday()==Calendar.FRIDAY && !e.isFulltime()))
				  ) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), true)) {
						if(!getDayPreference(e, daySolution).isNoNight()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK NIGHT");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as sportak WEEKEND AFTERNOON");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findDroneForWeekendAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND AFTERNOON");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendAfternoon(Employee e, DaySolution daySolution) {
		if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
			if(!getDayPreference(e, daySolution).isNoAfternoon()) {
				ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WEEKEND AFTERNOON");
				return e;
			}
		}
		// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
		return null;			
	}

	private Employee findDroneForWeekendNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isSportak()
				   &&
				   (daySolution.getWeekday()!=Calendar.SATURDAY
			        ||
				    (daySolution.getWeekday()==Calendar.SATURDAY && !e.isFulltime()))
				   &&
				   (daySolution.getWeekday()!=Calendar.SUNDAY
			        ||
				    (daySolution.getWeekday()==Calendar.SUNDAY && e.isFulltime()))
				  ) 
				{
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), true)) {
						if(!getDayPreference(e, daySolution).isNoNight()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as sportak WEEKEND MORNING");
							return e;
						}
					}
				}
			}
		}
		return null;
	}
	
	private Employee findDroneForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocated(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND MORNING");
							return e;
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendMorning(Employee e, DaySolution daySolution) {
		if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
			if(!getDayPreference(e, daySolution).isNoMorning6()) {
				ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WEEKEND MORNING");
				return e;
			}
		}
		// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
		return null;			
	}

	private void showProgress(int days, int processedDays) {
		int percent = processedDays==0?0:Math.round(((float)processedDays) / (((float)days)/100f));
		solverProgressPanel.refresh(
				""+percent,
				(failedOnDay==-1?"":""+failedOnDay),
				(failedOnRole==null?"":failedOnRole),
				(failedOnShiftType==null?"":failedOnShiftType),
				""+steps, 
				null, 
				null);
	}
	
	private void debugDown(int d, String shiftType, String role, int count) {		
		ShiftSolverLogger.debug("   >>> DOWN - FOUND for day "+d+", shift "+shiftType+" and role "+role+" #"+count+" ("+(steps++)+")");
		
		if(count>employeeAllocations.size()) {
			throw new RuntimeException("LOOP DETECTED when assigning WORKDAY/WEEKEND-"+
					shiftType+"-"+
					role+" for day "+d+" and solution number #"+solutionsCount);
		}		
		if(steps>STEPS_LIMIT) {
			throw new ShiftSolverTimeoutException(
					"Steps exceeded (depth "+d+", "+shiftType+", "+role+") "+
					"fail: "+failedOnDay+" "+failedOnShiftType+" "+failedOnRole
					);
		}
		
		clearFailedOn();
	}

	private void clearFailedOn() {
		failedOnDay=-1;
		failedOnShiftType=null;
		failedOnRole=null;
	}
	
	private void debugUp(int d, String shiftType, String role) {
		ShiftSolverLogger.debug("   <<< BACKTRACK UP - failed for day "+d+", shift "+shiftType+" and role "+role+" ("+(steps++)+")");
		if(failedOnDay<=d) {
			// remember last
			failedOnDay=d;
			failedOnShiftType=shiftType;
			failedOnRole=role;
		}
		
		if(steps>STEPS_LIMIT) {
			throw new ShiftSolverTimeoutException(
					"Steps exceeded (depth "+d+", "+shiftType+", "+role+") "+
					"fail: "+failedOnDay+" "+failedOnShiftType+" "+failedOnRole
					);
		}
	}	
}
