package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
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
public class ShiftSolver implements ShiftSolverConstants {
	
	public static long STEPS_LIMIT=3000000;
	
	private static long sequence=0;

	private RiaContext ctx;
	private RiaMessages i18n;
	
	private PeriodPreferences preferences;
	private List<Employee> employees;
	private Map<String,EmployeeAllocation> employeeAllocations;

	private long steps;
	private int depth;
	private int failedOnMaxDepth;
	private String failedOnShiftType;
	private String failedOnRole;
	private int solutionsCount;
	private int bestScore;

	private SolverProgressPanels solverProgressPanel;

	private Employee lastMonthEditor;
	
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
		depth=0;
		clearFailedOn();
		solutionsCount=0;
		bestScore=0;
		
		employees = team.getStableEmployeeList();
		employeeAllocations = new HashMap<String,EmployeeAllocation>();
		lastMonthEditor = null;
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			EmployeeAllocation employeeAllocation 
				= new EmployeeAllocation(e, periodPreferences);
			employeeAllocations.put(e.getKey(), employeeAllocation);
			if(preferences.getLastMonthEditor()!=null && !preferences.getLastMonthEditor().isEmpty()) {
				if(e.getKey().equals(preferences.getLastMonthEditor())) {
					lastMonthEditor=e;
				}
			}
		}
		
		if(!solveDay(1, result).isSolutionFound()) {
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
			ShiftSolverLogger.debug("SOLUTION FOUND!");
			return result;			
		}
	}

	private List<Employee> sortEmployeesByShifts() {
		List<Employee> result=new ArrayList<Employee>(employees);
		Collections.sort(result, new EmployeeShiftsComparator(employeeAllocations));
		return result;
	}
	
	private int calculateSolutionScore(PeriodSolution result) {
		// TODO calculate number of matched greens
		// TODO calculate fulltime has all uvazky to be most full
		// TODO ...
		// TODO show the score w/ solution
		return 0;
	}
	
	private BacktrackFor solveDay(int d, PeriodSolution result) {
		debugDown(d, "DAY", "###", -1);
		showProgress(preferences.getMonthDays(), d-1);
		
		if(d>preferences.getMonthDays()) {
			solutionsCount++;
			bestScore=calculateSolutionScore(result);
			// TODO bestSolution=
			solverProgressPanel.refresh(
					"100", 
					(failedOnMaxDepth==-1?"":""+failedOnMaxDepth),
					(failedOnRole==null?"":failedOnRole),
					(failedOnShiftType==null?"":failedOnShiftType),
					""+steps, 
					""+solutionsCount, 
					""+bestScore);
			
			if(result.getSolutionNumber()>1) {
				ShiftSolverLogger.debug("SOLUTION FOUND >>> GOING FOR NEXT "+result.getSolutionNumber());
				result.setSolutionNumber(result.getSolutionNumber()-1);
				// going for another solution
				return new BacktrackFor(ROLE_ANYBODY);
			} else {
				ShiftSolverLogger.debug("SOLUTION FOUND");
				// solution DONE ;)				
				return BacktrackFor.SOLUTION; 
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
			
			BacktrackFor backtrackFor=assignWeekendMorningEditor(d, daySolution, result);
			if(!backtrackFor.isSolutionFound()) {				
				// BACKTRACK previous day / END if on the first day
				ShiftSolverLogger.debug("   <<< BACKTRACK ***DAY*** UP - day "+d+" --> WEEKEND DAY");
				result.getDays().remove(daySolution);
				return backtrackFor;
			}
		} else {
			daySolution.setWorkday(true);
			daySolution.setWorkdayMorningShift(new WorkdayMorningShift());
			daySolution.setWorkdayAfternoonShift(new WorkdayAfternoonShift());
			daySolution.setNightShift(new NightShift());

			BacktrackFor backtrackFor=assignWorkdayMorningEditor(d, daySolution, result);
			if(!backtrackFor.isSolutionFound()) {							
				// BACKTRACK previous day / END if on the first day
				ShiftSolverLogger.debug("   <<< BACKTRACK *DAY* UP - day "+d+" --> WORK DAY");
				result.getDays().remove(daySolution);
				return backtrackFor;				
			}
		}
		
		ShiftSolverLogger.debug("DAY "+d+" SOLVED > going *TOP* UP W/ RESULT");
		return BacktrackFor.SOLUTION; // day DONE
	}

	/*
	 * assign a role to particular shift's slot
	 */

	private BacktrackFor assignWeekendMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {						
		ShiftSolverLogger.debug(" Weekend Morning");
		int thisLevelRole=ROLE_EDITOR;
				
		// editor has Fri afternoon > Sat morning > Sat afternoon > Sun morning > Sun afternoon continuity
		// i.e. take editor from PREVIOUS day afternoon/morning
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
					throw new ShiftSolverException(
							"Workday in weekend? "+daySolution.getWeekday()+" - "+Utils.getDayLetter(d, preferences.getStartWeekDay()),
							d,
							failedOnMaxDepth,
							failedOnShiftType,
							failedOnRole);
				}
			}
		} else {
			if(lastMonthEditor!=null) {
				previousEditor=lastMonthEditor;
			} else {
				// TODO editor should be friday afternoon editor (verify on Friday that editor has capacity for 3 shifts)
				//      PROBLEM:  if friday/saturday is in different month, there is no way to ensure editor continuity (Friday afternoon + Sat + Sun)
				//      SOLUTION: simply introduce a field like year/month where from dropbox you can choose friday editor
				throw new ShiftSolverException(
						"Unable to load editor from previous day as don't have previous month",
						d,
						failedOnMaxDepth,
						failedOnShiftType,
						failedOnRole);				
			}
		}
		
		if(findEditorForWeekendMorning(previousEditor, daySolution)!=null) {
			debugDown(d, "MORNING", "EDITOR", -1);
			employeeAllocations.get(previousEditor.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().editor=previousEditor;
			BacktrackFor backtrackFor=assignWeekendMorningDrone6am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "EDITOR*");		
					employeeAllocations.get(previousEditor.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(previousEditor.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "EDITOR"); 
		//daySolution.getWeekendAfternoonShift().editor=null;
		return new BacktrackFor(thisLevelRole);
	}
	
	private BacktrackFor assignWeekendMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().drone6am=lastAssignee;
			BacktrackFor backtrackFor=assignWeekendMorningSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "DRONE*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE"); 
		//daySolution.getWeekendMorningShift().drone6am=null;
		return new BacktrackFor(thisLevelRole);
	}

	private BacktrackFor assignWeekendMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {		
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findSportakForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendMorningShift().sportak=lastAssignee;
			BacktrackFor backtrackFor=assignWeekendAfternoonEditor(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "SPORTAK*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "SPORTAK");		
		//daySolution.getWeekendMorningShift().sportak=null;
		return new BacktrackFor(thisLevelRole);
	}

	private BacktrackFor assignWeekendAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Weekend Afternoon");
		int thisLevelRole=ROLE_EDITOR;		
		Employee lastEditor=daySolution.getWeekendMorningShift().editor;
		if((findEditorForWeekendAfternoon(lastEditor, daySolution))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", -1);
			employeeAllocations.get(lastEditor.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().editor=lastEditor;
			BacktrackFor backtrackFor=assignWeekendAfternoonDrone(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "EDITOR*");		
					employeeAllocations.get(lastEditor.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastEditor.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWeekendAfternoonShift().editor=null;
		return new BacktrackFor(thisLevelRole);
	}
	
	private BacktrackFor assignWeekendAfternoonDrone(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().drone=lastAssignee;
			BacktrackFor backtrackFor=assignWeekendAfternoonSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "STAFFER"); 
		//daySolution.getWeekendAfternoonShift().drone=null;
		return new BacktrackFor(thisLevelRole);
	}
	
	private BacktrackFor assignWeekendAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findSportakForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWeekendAfternoonShift().sportak=lastAssignee;
			BacktrackFor backtrackFor=assignWeekendNightDrone(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "SPORTAK*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWeekendAfternoonShift().sportak=null;
		return new BacktrackFor(thisLevelRole);
	}

	private BacktrackFor assignWeekendNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Weekend Night");
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWeekendNight(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "NIGHT", "STAFFER", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, true);					
			daySolution.getNightShift().drone=lastAssignee;
			BacktrackFor backtrackFor=solveDay(d+1, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "NIGHT", "STAFFER*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(true);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(true);
		}
		debugUp(d, "NIGHT", "STAFFER");
		//daySolution.getNightShift().drone=null;
		return new BacktrackFor(thisLevelRole);
	}
	
	private BacktrackFor assignWorkdayMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Morning");
		int thisLevelRole=ROLE_EDITOR;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findEditorForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().editor=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayMorningDrone6am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "EDITOR*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "EDITOR"); 
		//daySolution.getWorkdayMorningShift().editor=null;
		return new BacktrackFor(thisLevelRole);		
	}

	private BacktrackFor assignWorkdayMorningDrone6am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE6AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone6am=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayMorningDrone7am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "DRONE6AM*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE6AM"); 
		//daySolution.getWorkdayMorningShift().drone6am=null;
		return new BacktrackFor(thisLevelRole);		
	}

	private BacktrackFor assignWorkdayMorningDrone7am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE7AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone7am=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayMorningDrone8am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "DRONE7AM*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE7AM"); 
		//daySolution.getWorkdayMorningShift().drone7am=null;
		return new BacktrackFor(thisLevelRole);		
	}

	private BacktrackFor assignWorkdayMorningDrone8am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "DRONE8AM", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().drone8am=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayMorningSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "DRONE8AM*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "DRONE8AM"); 
		//daySolution.getWorkdayMorningShift().drone8am=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private BacktrackFor assignWorkdayMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findSportakForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayMorningShift().sportak=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonEditor(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "SPORTAK*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "MORNING", "SPORTAK"); 
		//daySolution.getWorkdayMorningShift().sportak=null;
		return new BacktrackFor(thisLevelRole);		
	}

	private BacktrackFor assignWorkdayAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Afternoon");
		int thisLevelRole=ROLE_EDITOR;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findEditorForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().editor=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonDrone1(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "EDITOR*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "EDITOR"); 
		//daySolution.getWorkdayAfternoonShift().editor=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private BacktrackFor assignWorkdayAfternoonDrone1(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE1", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[0]=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonDrone2(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "DRONE1*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE1"); 
		//daySolution.getWorkdayAfternoonShift().drones[0]=null;
		return new BacktrackFor(thisLevelRole);		
	}
		
	private BacktrackFor assignWorkdayAfternoonDrone2(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE2", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[1]=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonDrone3(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "DRONE2*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE2"); 
		//daySolution.getWorkdayAfternoonShift().drones[1]=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private BacktrackFor assignWorkdayAfternoonDrone3(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE3", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[2]=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonDrone4(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "DRONE3*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE3"); 
		//daySolution.getWorkdayAfternoonShift().drones[2]=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private BacktrackFor assignWorkdayAfternoonDrone4(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "DRONE4", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().drones[3]=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayAfternoonSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "DRONE4*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "DRONE4"); 
		//daySolution.getWorkdayAfternoonShift().drones[3]=null;
		return new BacktrackFor(thisLevelRole);		
	}

	private BacktrackFor assignWorkdayAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts();
		while((lastAssignee=findSportakForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, false);					
			daySolution.getWorkdayAfternoonShift().sportak=lastAssignee;
			BacktrackFor backtrackFor=assignWorkdayNightDrone(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "SPORTAK*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(false);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(false);
		}
		debugUp(d, "AFTERNOON", "SPORTAK"); 
		//daySolution.getWorkdayAfternoonShift().sportak=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private BacktrackFor assignWorkdayNightDrone(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Night");
		int thisLevelRole=ROLE_DRONE;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> es=sortEmployeesByShifts();
		while((lastAssignee=findDroneForWorkdayNight(es, daySolution, lastAssignee))!=null) {
			debugDown(d, "NIGHT", "DRONE", c++);
			employeeAllocations.get(lastAssignee.getKey()).assign(d, true);					
			daySolution.getNightShift().drone=lastAssignee;
			BacktrackFor backtrackFor=solveDay(d+1, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "NIGHT", "DRONE*");		
					employeeAllocations.get(lastAssignee.getKey()).unassign(true);
					return backtrackFor;
				}
			}
			employeeAllocations.get(lastAssignee.getKey()).unassign(true);
		}
		debugUp(d, "NIGHT", "DRONE");
		//daySolution.getNightShift().drone=null;
		return new BacktrackFor(thisLevelRole);		
	}
	
	private int getLastAssigneeIndexWithSkip(Employee lastAssignee, List<Employee> es) {
		if(lastAssignee==null) {
			return 0;
		} else {
			for(int i=0; i<es.size(); i++) {
				if(es.get(i).getKey().equals(lastAssignee.getKey())) {
					return 1+i;					
				}
			}
			throw new ShiftSolverException(
					"Employee "+lastAssignee.getFullName()+" found!",
					-1,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);					
		}
	}

	private static final DayPreference NO_PREFERENCE=new DayPreference();
	private DayPreference getDayPreference(Employee e, DaySolution daySolution) {
		Map<String, EmployeePreferences> employeeToPreferences = preferences.getEmployeeToPreferences();
		if(employeeToPreferences!=null) {
			EmployeePreferences employeePreferences = employeeToPreferences.get(e.getKey());
			if(employeePreferences!=null) {
				DayPreference preferencesForDay = employeePreferences.getPreferencesForDay(daySolution.getDay());
				if(preferencesForDay!=null) {
					return preferencesForDay;
				}				
			}
		}
		return NO_PREFERENCE;
	}
		
	/*
	 * find a role for particular shift
	 * 
	 * TODO want - basically do 3 iterations, first iterate green, then don't care, finally NOT
	 */
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> es, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, es);
		for(int i=lastIndex; i<es.size(); i++) {
			Employee e=es.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(e.isEditor()) {
					if(!getDayPreference(e, daySolution).isNoAfternoon()) {
						if(daySolution.getWeekday()==Calendar.FRIDAY && employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false, 5, false)) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WORK FRIDAY AFTERNOON -> SUNDAY");
							return e;
						} else {						
							if(daySolution.getWeekday()!=Calendar.FRIDAY && employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false)) {
								ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WORK AFTERNOON");
								return e;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private Employee findSportakForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), false, 1, true)) {
			if(!getDayPreference(e, daySolution).isNoAfternoon()) {
				ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WEEKEND AFTERNOON");
				return e;
			}
		}
		// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
		return null;			
	}

	private Employee findDroneForWeekendNight(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {				
				if(!e.isSportak()) {
					if(employeeAllocations.get(e.getKey()).hasCapacity(daySolution.getDay(), true)) {
						if(!getDayPreference(e, daySolution).isNoNight()) {
							// part time on Saturday night, full time on Sunday night
							if(daySolution.getWeekday()==Calendar.SATURDAY && !e.isFulltime()) {
								ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT (Saturday part time)");
								return e;						
							} else {
								if(daySolution.getWeekday()==Calendar.SUNDAY && e.isFulltime()) {
									ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT (Sunday fulltime");
									return e;															
								}
							}
						}
					}
				}
			}
		}
		return null;		
	}

	private Employee findSportakForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
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
				(failedOnMaxDepth==-1?"":""+failedOnMaxDepth),
				(failedOnRole==null?"":failedOnRole),
				(failedOnShiftType==null?"":failedOnShiftType),
				""+steps, 
				null, 
				null);
	}
	
	private void debugDown(int d, String shiftType, String role, int count) {		
		ShiftSolverLogger.debug("   >>> DOWN - FOUND for day-shift-role "+d+"-"+shiftType+"-"+role+" #"+count+" ("+(steps++)+" steps, depth: "+(depth++)+")");
		
		if(count>employees.size()) {
			throw new ShiftSolverException(
					"LOOP DETECTED ("+count+">"+employees.size()+"="+employeeAllocations.size()+") "
					+ "when assigning WORKDAY/WEEKEND-"+ shiftType+"-"+ role+" for day "+d
					+" and solution number #"+solutionsCount,
					d,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);										
		}
		
		if(steps>STEPS_LIMIT) {
			throw new ShiftSolverException(
					"Steps exceeded - depth "+d+", "+shiftType+", "+role,
					d,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole
					);
		}
	}

	private void clearFailedOn() {
		failedOnMaxDepth=-1;
		failedOnShiftType=null;
		failedOnRole=null;
	}
	
	private void debugUp(int d, String shiftType, String role) {
		ShiftSolverLogger.debug("   <<< BACKTRACK UP - failed for day/shift/role "+d+"-"+shiftType+"-"+role+" ("+(steps++)+" steps, depth: "+depth+")");

		if(failedOnMaxDepth<depth) {
			failedOnMaxDepth=depth;
			failedOnShiftType=shiftType;
			failedOnRole=role;						
		}

		ShiftSolverLogger.debug("     BOTTOM CAUSE - failed for depth/shift/role "+failedOnMaxDepth+"-"+failedOnShiftType+"-"+failedOnRole);
		EmployeeCapacity capacity
			=new EmployeeCapacity(preferences, new ArrayList<EmployeeAllocation>(employeeAllocations.values()));
				capacity.printEmployeeAllocations(d);		

		depth--;
		
		if(steps>STEPS_LIMIT) {
			int failedOnDay=d;
			throw new ShiftSolverException(
					"Solution didn't found in "+STEPS_LIMIT+" steps, depth "+d+", "+shiftType+", "+role+")",
					failedOnDay,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);
		}
	}	
}
