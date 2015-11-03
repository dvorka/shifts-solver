package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.RiaState;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.Holder;
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
 */
public class ShiftSolver implements ShiftSolverConstants, ShiftSolverConfigurer {
	
	public static final String FERDA_KEY="FERDAKEY";
	public static final Employee FERDA;
	static {
		FERDA=new Employee();
		FERDA.setKey(FERDA_KEY);
		FERDA.setBirthdayDay(1);
		FERDA.setBirthdayMonth(1);
		FERDA.setBirthdayYear(1965);
		FERDA.setEditor(true);
		FERDA.setEmail("ferda@mraveniste.cz");
		FERDA.setFamilyname("Mravenec");
		FERDA.setFemale(false);
		FERDA.setFirstname("Ferda");
		FERDA.setFulltime(true);
		FERDA.setMortak(true);
		FERDA.setSportak(true);
	}
	
	public long stepsLimit=3000000;
	
	private RiaContext ctx;
	private RiaMessages i18n;
	
	private SolutionPostprocessor postprocessor;
	
	private PeriodPreferences preferences;
	private List<Employee> employees;
	private Map<String,EmployeeAllocation> e2a;
	private Employee lastMonthEditor;

	private boolean partialSolution;	
	private boolean enforceAfternoonTo8am;
	private boolean enforceNightToAfternoon;
	
	private long steps;
	private int depth;
	private int bestScore;

	private int failedOnMaxDay;
	private int failedOnMaxDepth;
	private String failedOnShiftType;
	private String failedOnRole;
	private List<EmployeeAllocation> failedWithEmployeeAllocations;

	private SolverProgressPanels solverProgressPanel;

	private PublicHolidays publicHolidays;
	
	public ShiftSolver() {
		this.postprocessor=new SolutionPostprocessor();
		this.solverProgressPanel=new DebugSolverPanel();
		this.publicHolidays=new PublicHolidays();
		this.enforceAfternoonTo8am=true;
		this.enforceNightToAfternoon=true;
		this.partialSolution=false;
	}
	
	public ShiftSolver(final RiaContext ctx) {
		this();
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public PeriodSolution solve(List<Employee> employees, PeriodPreferences periodPreferences, boolean partialSolution) {
  		Team team=new Team();
  		team.addEmployees(employees);
		PeriodSolution result = solve(team, periodPreferences);
		this.partialSolution=partialSolution;
		return postprocessor.improve(result);
	}	

	public Map<String, EmployeeAllocation> getEmployeeAllocations() {
		return e2a;
	}

	public List<EmployeeAllocation> getFailedWithEmployeeAllocations() {
		return failedWithEmployeeAllocations;
	}
	
	private PeriodSolution solve(Team team, PeriodPreferences periodPreferences) {
		if(ctx!=null) {
			ctx.getStatusLine().showProgress("Calculating shifts schedule solution...");
			this.solverProgressPanel=ctx.getSolverProgressPanel();
			ctx.getRia().showSolverProgressPanel();
		}

		this.preferences=periodPreferences;
		
		PeriodSolution result = new PeriodSolution(periodPreferences.getYear(), periodPreferences.getMonth());
		result.setPeriodPreferencesKey(periodPreferences.getKey());
		result.setKey(null);
		
		steps=0;
		depth=0;
		clearFailedOn();
		bestScore=0;
		
		employees = team.getStableEmployeeList();
		e2a = new HashMap<String,EmployeeAllocation>();
		lastMonthEditor = null;
		for(int i=0; i<employees.size(); i++) {
			Employee e = employees.get(i);
			EmployeeAllocation employeeAllocation = new EmployeeAllocation(e, periodPreferences);
			e2a.put(e.getKey(), employeeAllocation);
			if(preferences.getLastMonthEditor()!=null && !preferences.getLastMonthEditor().isEmpty()) {
				if(e.getKey().equals(preferences.getLastMonthEditor())) {
					lastMonthEditor=e;
				}
			}
		}
		if(partialSolution) {
			EmployeeAllocation employeeAllocation = new EmployeeAllocation(FERDA, periodPreferences);
			e2a.put(FERDA.getKey(), employeeAllocation);
		}		
		
		PeriodPreferencesCapacity capacity = new PeriodPreferencesCapacity();
		capacity.calculate(periodPreferences, e2a.values());
		capacity.printCapacity();
		capacity.isCapacitySufficient();
		
		if(!solveDay(1, result).isSolutionFound()) {
			// NO SOLUTION exists for this team and requirements
			ShiftSolverLogger.debug("NO SOLUTION EXISTS!");
			throw new ShiftSolverException(
					"No solution exist for these employees and their preferences!",
					failedWithEmployeeAllocations,
					failedOnMaxDay,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole
					);
		} else {
			if(partialSolution) {
				e2a.remove(FERDA.getKey());
			}
			for(String key:e2a.keySet()) {
				result.addEmployeeJob(
						key, 
						new Job(e2a.get(key).shifts, e2a.get(key).shiftsToGet));
				if(ctx!=null) ctx.getStatusLine().showInfo("Solution found!");
			}	
			ShiftSolverLogger.debug((partialSolution?"PARTIAL":"")+"SOLUTION FOUND!");
			return result;			
		}
	}

	private List<Employee> sortEmployeesByShifts(int day) {
		List<Employee> result=new ArrayList<Employee>(employees);
		Collections.sort(result, new EmployeeFulltimeYesterdayAndShiftsComparator(e2a, day));
		//		ShiftSolverLogger.debug("- SORTED -F-Y-S-N------------------ ");
		//		for(Employee e:result) {
		//			ShiftSolverLogger.debug(
		//					e.isFulltime()+" "+
		//					e2a.get(e.getKey()).hadShiftYesterday(day)+" "+
		//					e2a.get(e.getKey()).shifts+" "+
		//					e.getFullName());
		//		}
		//		ShiftSolverLogger.debug("- SORTED END ------------------");		
		return result;
	}
	
	private int calculateSolutionScore(PeriodSolution result) {
		// TODO calculate number of matched greens in percent (total vs. matched)
		// TODO calculate fulltime has all uvazky to be most full
		// TODO show the score w/ solution
		return 0;
	};
	
	private BacktrackFor solveDay(int d, PeriodSolution result) {
		debugDown(d, "DAY", "###", -1);
		showProgress(preferences.getMonthDays(), d-1);
		
		boolean holidays=false;
		if(publicHolidays.isHolidays(preferences.getYear(), preferences.getMonth(), d)) {
			holidays=true;
		}
		
		if(d>preferences.getMonthDays()) {
			bestScore=calculateSolutionScore(result);
			
			solverProgressPanel.refresh(
					(failedOnMaxDay==-1?"":""+failedOnMaxDay),
					(failedOnMaxDepth==-1?"":""+failedOnMaxDepth),
					(failedOnRole==null?"":failedOnRole),
					(failedOnShiftType==null?"":failedOnShiftType),
					""+steps, 
					""+bestScore);
			ShiftSolverLogger.debug("SOLUTION FOUND");
			
			// solution DONE ;)				
			return BacktrackFor.SOLUTION; 
		}
		
		ShiftSolverLogger.debug("Day "+d+":"+(holidays?" *HOLIDAYS*":""));

		DaySolution daySolution = new DaySolution(
				d, 
				Utils.getWeekdayNumber(d, preferences.getStartWeekDay())+1, // Calendar.(weekday) starts with 1
				!holidays && !Utils.isWeekend(d, preferences.getStartWeekDay()));
		result.addDaySolution(daySolution);
			
		if(holidays) {
			daySolution.setWorkday(false);
			daySolution.setWeekendMorningShift(new WeekendMorningShift());
			daySolution.setWeekendAfternoonShift(new WeekendAfternoonShift());
			daySolution.setNightShift(new NightShift());
			
			BacktrackFor backtrackFor=assignWeekendMorningEditor(d, daySolution, result, true);
			if(!backtrackFor.isSolutionFound()) {				
				// BACKTRACK previous day / END if on the first day
				ShiftSolverLogger.debug("   <<< BACKTRACK ***DAY*** UP - day "+d+" --> HOLIDAYS DAY");
				result.getDays().remove(daySolution);
				return backtrackFor;
			}			
		} else {
			if(Utils.isWeekend(d, preferences.getStartWeekDay())) {
				daySolution.setWorkday(false);
				daySolution.setWeekendMorningShift(new WeekendMorningShift());
				daySolution.setWeekendAfternoonShift(new WeekendAfternoonShift());
				daySolution.setNightShift(new NightShift());
				
				BacktrackFor backtrackFor=assignWeekendMorningEditor(d, daySolution, result, false);
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
		}
		
		ShiftSolverLogger.debug("DAY "+d+" SOLVED > going *TOP* UP W/ RESULT");
		return BacktrackFor.SOLUTION;
	}

	/*
	 * assign a role to particular shift's slot
	 */

	private BacktrackFor assignWeekendMorningEditor(
			int d, 
			DaySolution daySolution, 
			PeriodSolution result,
			boolean isHolidays) 
	{						
		ShiftSolverLogger.debug(" Weekend Morning");
		int thisLevelRole=ROLE_EDITOR;
				
		// editor has Fri afternoon > Sat morning > Sat afternoon > Sun morning > Sun afternoon continuity
		// i.e. take editor from PREVIOUS day afternoon/morning
		DaySolution previousDaySolution;
		Employee previousEditor;
		if(result.getDays().size()>1) {
			previousDaySolution=result.getDays().get(result.getDays().size()-2);
			if(isHolidays) {
				if(previousDaySolution.getWeekday()==Calendar.SATURDAY) {
					previousEditor=e2a.get(previousDaySolution.getWeekendMorningShift().editor.get()).employee;					
				} else {
					if(previousDaySolution.getWeekday()==Calendar.SATURDAY) {
						previousEditor=e2a.get(previousDaySolution.getWeekendMorningShift().editor.get()).employee;						
					} else {
						previousEditor=e2a.get(previousDaySolution.getWorkdayAfternoonShift().editor.get()).employee;						
					}					
				}
			} else {
				if(daySolution.getWeekday()==Calendar.SATURDAY) {
					previousEditor=e2a.get(previousDaySolution.getWorkdayAfternoonShift().editor.get()).employee;
				} else {
					if(daySolution.getWeekday()==Calendar.SUNDAY) {
						previousEditor=e2a.get(previousDaySolution.getWeekendMorningShift().editor.get()).employee;
					} else {
						throw new ShiftSolverException(
								"Workday in weekend? "+daySolution.getWeekday()+" - "+Utils.getDayLetter(d, preferences.getStartWeekDay(), i18n),
								failedWithEmployeeAllocations,
								d,
								failedOnMaxDepth,
								failedOnShiftType,
								failedOnRole);
					}
				}				
			}
		} else {
			if(lastMonthEditor!=null) {
				previousEditor=lastMonthEditor;
			} else {
				throw new ShiftSolverException(
						"Unable to load editor from previous day as don't have previous month",
						failedWithEmployeeAllocations,						
						d,
						failedOnMaxDepth,
						failedOnShiftType,
						failedOnRole);				
			}
		}
		
		if(findEditorForWeekendMorning(previousEditor, daySolution)!=null) {
			debugDown(d, "MORNING", "EDITOR", -1);
			e2a.get(previousEditor.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWeekendMorningShift().editor=new Holder<String>(previousEditor.getKey());
			BacktrackFor backtrackFor=assignWeekendMorningStaffer6am(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "EDITOR*");		
					e2a.get(previousEditor.getKey()).unassign(SHIFT_MORNING);
					return backtrackFor;
				}
			}
			e2a.get(previousEditor.getKey()).unassign(SHIFT_MORNING);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWeekendMorningShift().editor=new Holder<String>(FERDA.getKey());
			return assignWeekendMorningStaffer6am(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "MORNING", "EDITOR"); 
			//daySolution.getWeekendAfternoonShift().editor=null;
			return new BacktrackFor(thisLevelRole);			
		}
	}
	
	private BacktrackFor assignWeekendMorningStaffer6am(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {
		int thisLevelRole=ROLE_STAFFER;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "STAFFER", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING_6);					
			daySolution.getWeekendMorningShift().staffer6am=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWeekendMorningSportak(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "STAFFER*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_6);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_6);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING_6);					
			daySolution.getWeekendMorningShift().staffer6am=new Holder<String>(FERDA.getKey());
			return assignWeekendMorningSportak(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "MORNING", "STAFFER"); 
			//daySolution.getWeekendMorningShift().staffer6am=null;
			return new BacktrackFor(thisLevelRole);			
		}
	}

	private BacktrackFor assignWeekendMorningSportak(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {		
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findSportakForWeekendMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING);			
			daySolution.getWeekendMorningShift().sportak=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWeekendAfternoonEditor(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "SPORTAK*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWeekendMorningShift().sportak=new Holder<String>(FERDA.getKey());
			return assignWeekendAfternoonEditor(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "MORNING", "SPORTAK");		
			//daySolution.getWeekendMorningShift().sportak=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWeekendAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {
		ShiftSolverLogger.debug(" Weekend Afternoon");
		int thisLevelRole=ROLE_EDITOR;		
		Employee lastEditor=e2a.get(daySolution.getWeekendMorningShift().editor.get()).employee;
		if((findEditorForWeekendAfternoon(lastEditor, daySolution))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", -1);
			e2a.get(lastEditor.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().editor=new Holder<String>(lastEditor.getKey());
			BacktrackFor backtrackFor=assignWeekendAfternoonStaffer(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "EDITOR*");		
					e2a.get(lastEditor.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastEditor.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().editor=new Holder<String>(FERDA.getKey());
			return assignWeekendAfternoonStaffer(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "AFTERNOON", "EDITOR"); 
			//daySolution.getWeekendAfternoonShift().editor=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWeekendAfternoonStaffer(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {
		int thisLevelRole=ROLE_STAFFER;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().staffer=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWeekendAfternoonSportak(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().staffer=new Holder<String>(FERDA.getKey());
			return assignWeekendAfternoonSportak(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "AFTERNOON", "STAFFER"); 
			//daySolution.getWeekendAfternoonShift().staffer=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWeekendAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findSportakForWeekendAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().sportak=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWeekendNightStaffer(d, daySolution, result, isHolidays);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "SPORTAK*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWeekendAfternoonShift().sportak=new Holder<String>(FERDA.getKey());
			return assignWeekendNightStaffer(d, daySolution, result, isHolidays);
		} else {
			debugUp(d, "AFTERNOON", "SPORTAK"); 
			//daySolution.getWeekendAfternoonShift().sportak=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWeekendNightStaffer(int d, DaySolution daySolution, PeriodSolution result, boolean isHolidays) {
		ShiftSolverLogger.debug(" Weekend Night");
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWeekendNight(employees, daySolution, lastAssignee, isHolidays))!=null) {
			debugDown(d, "NIGHT", "STAFFER", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_NIGHT);					
			daySolution.getNightShift().staffer=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=solveDay(d+1, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "NIGHT", "STAFFER*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_NIGHT);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_NIGHT);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_NIGHT);					
			daySolution.getNightShift().staffer=new Holder<String>(FERDA.getKey());
			return solveDay(d+1, result);
		} else {
			debugUp(d, "NIGHT", "STAFFER");
			//daySolution.getNightShift().staffer=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWorkdayMorningEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Morning");
		int thisLevelRole=ROLE_EDITOR;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findEditorForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "EDITOR", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWorkdayMorningShift().editor=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayMorningStaffer6am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "EDITOR*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWorkdayMorningShift().editor=new Holder<String>(FERDA.getKey());
			return assignWorkdayMorningStaffer6am(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "EDITOR"); 
			//daySolution.getWorkdayMorningShift().editor=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWorkdayMorningStaffer6am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "STAFFER6AM", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING_6);					
			daySolution.getWorkdayMorningShift().staffer6am=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayMorningStaffer7am(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "STAFFER6AM*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_6);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_6);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING_6);					
			daySolution.getWorkdayMorningShift().staffer6am=new Holder<String>(FERDA.getKey());
			return assignWorkdayMorningStaffer7am(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "STAFFER6AM"); 
			//daySolution.getWorkdayMorningShift().staffer6am=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWorkdayMorningStaffer7am(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "STAFFER7AM", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING_7);					
			daySolution.getWorkdayMorningShift().staffer7am=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayMorningStaffer8am1(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "STAFFER7AM*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_7);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_7);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING_7);					
			daySolution.getWorkdayMorningShift().staffer7am=new Holder<String>(FERDA.getKey());
			return assignWorkdayMorningStaffer8am1(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "STAFFER7AM"); 
			//daySolution.getWorkdayMorningShift().staffer7am=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWorkdayMorningStaffer8am1(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "STAFFER8AM1", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING_8);					
			daySolution.getWorkdayMorningShift().staffer8am1=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayMorningStaffer8am2(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "STAFFER8AM1*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_8);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_8);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING_8);					
			daySolution.getWorkdayMorningShift().staffer8am1=new Holder<String>(FERDA.getKey());
			return assignWorkdayMorningStaffer8am2(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "STAFFER8AM1"); 
			//daySolution.getWorkdayMorningShift().staffer8am=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWorkdayMorningStaffer8am2(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "STAFFER8AM2", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING_8);					
			daySolution.getWorkdayMorningShift().staffer8am2=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayMorningSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "STAFFER8AM2*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_8);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING_8);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING_8);					
			daySolution.getWorkdayMorningShift().staffer8am2=new Holder<String>(FERDA.getKey());
			return assignWorkdayMorningSportak(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "STAFFER8AM2"); 
			//daySolution.getWorkdayMorningShift().staffer8am=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWorkdayMorningSportak(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findSportakForWorkdayMorning(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "MORNING", "SPORTAK", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_MORNING);			
			daySolution.getWorkdayMorningShift().sportak=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonEditor(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "MORNING", "SPORTAK*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_MORNING);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_MORNING);					
			daySolution.getWorkdayMorningShift().sportak=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonEditor(d, daySolution, result);
		} else {
			debugUp(d, "MORNING", "SPORTAK"); 
			//daySolution.getWorkdayMorningShift().sportak=null;
			return new BacktrackFor(thisLevelRole);		
		}
	}

	private BacktrackFor assignWorkdayAfternoonEditor(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Afternoon");
		int thisLevelRole=ROLE_EDITOR;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findEditorForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "EDITOR", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().editor=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonStaffer1(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "EDITOR*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().editor=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonStaffer1(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "EDITOR"); 
			//daySolution.getWorkdayAfternoonShift().editor=null;
			return new BacktrackFor(thisLevelRole);		
		}
	}
	
	private BacktrackFor assignWorkdayAfternoonStaffer1(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER1", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[0]=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonStaffer2(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER1*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[0]=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonStaffer2(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "STAFFER1"); 
			//daySolution.getWorkdayAfternoonShift().staffers[0]=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
		
	private BacktrackFor assignWorkdayAfternoonStaffer2(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER2", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);			
			daySolution.getWorkdayAfternoonShift().staffers[1]=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonStaffer3(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER2*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[1]=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonStaffer3(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "STAFFER2"); 
			//daySolution.getWorkdayAfternoonShift().staffers[1]=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWorkdayAfternoonStaffer3(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER3", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[2]=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonStaffer4(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER3*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[2]=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonStaffer4(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "STAFFER3"); 
			//daySolution.getWorkdayAfternoonShift().staffers[2]=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWorkdayAfternoonStaffer4(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "STAFFER4", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[3]=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayAfternoonSportak(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "STAFFER4*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().staffers[3]=new Holder<String>(FERDA.getKey());
			return assignWorkdayAfternoonSportak(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "STAFFER4"); 
			//daySolution.getWorkdayAfternoonShift().staffers[3]=null;
			return new BacktrackFor(thisLevelRole);
		}
	}

	private BacktrackFor assignWorkdayAfternoonSportak(int d, DaySolution daySolution, PeriodSolution result) {
		int thisLevelRole=ROLE_SPORTAK;
		Employee lastAssignee=null;
		int c=0;
		List<Employee> employees=sortEmployeesByShifts(d);
		while((lastAssignee=findSportakForWorkdayAfternoon(employees, daySolution, lastAssignee))!=null) {
			debugDown(d, "AFTERNOON", "SPORTAK", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().sportak=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=assignWorkdayNightStaffer(d, daySolution, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "AFTERNOON", "SPORTAK*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_AFTERNOON);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_AFTERNOON);					
			daySolution.getWorkdayAfternoonShift().sportak=new Holder<String>(FERDA.getKey());
			return assignWorkdayNightStaffer(d, daySolution, result);
		} else {
			debugUp(d, "AFTERNOON", "SPORTAK"); 
			//daySolution.getWorkdayAfternoonShift().sportak=null;
			return new BacktrackFor(thisLevelRole);
		}
	}
	
	private BacktrackFor assignWorkdayNightStaffer(int d, DaySolution daySolution, PeriodSolution result) {
		ShiftSolverLogger.debug(" Night");
		int thisLevelRole=ROLE_STAFFER;
		Candidate lastAssignee=new Candidate();
		int c=0;
		List<Employee> es=sortEmployeesByShifts(d);
		while((lastAssignee=findStafferForWorkdayNight(es, daySolution, lastAssignee))!=null) {
			debugDown(d, "NIGHT", "STAFFER", c++);
			e2a.get(lastAssignee.getKey()).assign(d, SHIFT_NIGHT);					
			daySolution.getNightShift().staffer=new Holder<String>(lastAssignee.getKey());
			BacktrackFor backtrackFor=solveDay(d+1, result);
			if(backtrackFor.isSolutionFound()) {
				return backtrackFor;
			} else {
				if(!backtrackFor.isTarget(thisLevelRole)) {
					debugUp(d, "NIGHT", "STAFFER*");		
					e2a.get(lastAssignee.getKey()).unassign(SHIFT_NIGHT);
					return backtrackFor;
				}
			}
			e2a.get(lastAssignee.getKey()).unassign(SHIFT_NIGHT);
		}
		if(partialSolution) {
			e2a.get(FERDA.getKey()).assign(d, SHIFT_NIGHT);					
			daySolution.getNightShift().staffer=new Holder<String>(FERDA.getKey());
			return solveDay(d+1, result);
		} else {
			debugUp(d, "NIGHT", "STAFFER");
			//daySolution.getNightShift().staffer=null;
			return new BacktrackFor(thisLevelRole);
		}
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
					failedWithEmployeeAllocations,					
					-1,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);					
		}
	}

	private static final DayPreference NO_PREFERENCE=new DayPreference();
	private DayPreference getDayPreference(Employee e, DaySolution daySolution) {
		return getDayPreference(e, preferences, daySolution);
	}
	private DayPreference getDayPreference(Employee e, PeriodPreferences p, DaySolution daySolution) {
		Map<String, EmployeePreferences> employeeToPreferences = p.getEmployeeToPreferences();
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
	 * TODO WANT preference - basically do 3 iterations, first iterate green, then don't care, finally NOT
	 */
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> es, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, es);
		for(int i=lastIndex; i<es.size(); i++) {
			Employee e=es.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
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

	private Candidate findStafferForWorkdayAfternoon(List<Employee> employees, DaySolution daySolution, Candidate lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee.getEmployee(), employees);
		if(!lastAssignee.isFallbackSearch()) {
			for(int i=lastIndex; i<employees.size(); i++) {
				Employee e=employees.get(i);
				if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
					if(!e.isEditor() && !e.isSportak()) {
						if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
							if(!getDayPreference(e, daySolution).isNoAfternoon()) {
								ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK AFTERNOON");
								return new Candidate(e);
							}
						}
					}
				}
			}
			return findWorkdayAfternoonStafferFallback(employees, daySolution, lastIndex);
		} else {
			return findWorkdayAfternoonStafferFallback(employees, daySolution, lastIndex);
		}
	}

	private Candidate findWorkdayAfternoonStafferFallback(List<Employee> employees, DaySolution daySolution, int lastIndex) {
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(!e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
						if(!getDayPreference(e, daySolution).isNoAfternoon()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK AFTERNOON (FALLBACK)");
							return new Candidate(e, true);
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
						if(daySolution.getWeekday()==Calendar.FRIDAY && e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON, 5, false)) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WORK FRIDAY AFTERNOON -> SUNDAY");
							return e;
						} else {						
							if(daySolution.getWeekday()!=Calendar.FRIDAY && e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
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
				if(e.isSportak() || e.isMortak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
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

	private Candidate findStafferForWorkdayMorning(List<Employee> employees, DaySolution daySolution, Candidate lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee.getEmployee(), employees);
		if(!lastAssignee.isFallbackSearch()) {
			for(int i=lastIndex; i<employees.size(); i++) {
				Employee e=employees.get(i);
				if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
					if(!e.isEditor() && !e.isSportak()) {
						if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
							if(!getDayPreference(e, daySolution).isNoMorning6()) {
								ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK MORNING");
								return new Candidate(e);
							}
						}
					}
				}
			}
			return findWorkdayMorningStafferFallback(employees, daySolution, lastIndex);
		} else {
			return findWorkdayMorningStafferFallback(employees, daySolution, lastIndex);
		}
	}
	
	private Candidate findWorkdayMorningStafferFallback(List<Employee> employees, DaySolution daySolution, int lastIndex) {
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
						if(!getDayPreference(e, daySolution).isNoMorning6()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK MORNING (FALLBACK)");
							return new Candidate(e, true);
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
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
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

	private Candidate findStafferForWorkdayNight(List<Employee> employees, DaySolution daySolution, Candidate lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee.getEmployee(), employees);		
		if(!lastAssignee.isFallbackSearch()) {
			for(int i=lastIndex; i<employees.size(); i++) {
				Employee e=employees.get(i);
				if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
					if(!e.isSportak()
						 &&
					   (daySolution.getWeekday()!=Calendar.FRIDAY
						 ||
					   (daySolution.getWeekday()==Calendar.FRIDAY && !e.isFulltime()))
					  ) {
						if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_NIGHT)) {
							if(!getDayPreference(e, daySolution).isNoNight()) {
								ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK NIGHT");
								return new Candidate(e);
							}
						}
					}
				}
			}
			return findWorkdayNightStafferFallback(employees, daySolution, lastIndex);
		} else {
			return findWorkdayNightStafferFallback(employees, daySolution, lastIndex);
		}
	}

	private Candidate findWorkdayNightStafferFallback(List<Employee> employees, DaySolution daySolution, int lastIndex) {
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(!e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_NIGHT)) {
						if(!getDayPreference(e, daySolution).isNoNight()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WORK NIGHT (FALLBACK)");
							return new Candidate(e, true);
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
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
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

	private Employee findStafferForWeekendAfternoon(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON)) {
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
		if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_AFTERNOON, 1, true)) {
			if(!getDayPreference(e, daySolution).isNoAfternoon()) {
				ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as editor WEEKEND AFTERNOON");
				return e;
			}
		}
		// if BACKTRACK that fail to let editor be assigned on FRI and/or SUN
		return null;			
	}

	private Candidate findStafferForWeekendNight(
			List<Employee> employees, 
			DaySolution daySolution, 
			Candidate lastAssignee, 
			boolean isHolidays) 
	{
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee.getEmployee(), employees);		
		if(!lastAssignee.isFallbackSearch()) {
			for(int i=lastIndex; i<employees.size(); i++) {
				Employee e=employees.get(i);
				if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {				
					if(!e.isSportak()) {
						if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_NIGHT)) {
							if(!getDayPreference(e, daySolution).isNoNight()) {
								// part time on Saturday night, full time on Sunday night
								if(daySolution.getWeekday()==Calendar.SATURDAY && !e.isFulltime()) {
									ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT (Saturday part time)");
									return new Candidate(e);						
								} else {
									if(isHolidays || (daySolution.getWeekday()==Calendar.SUNDAY && e.isFulltime())) {
										ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT (Sunday fulltime");
										return new Candidate(e);															
									}
								}
							}
						}
					}
				}
			}
			return findWeekendNightStafferFallback(employees, daySolution, lastIndex, isHolidays);
		} else {
			return findWeekendNightStafferFallback(employees, daySolution, lastIndex, isHolidays);
		}
	}

	private Candidate findWeekendNightStafferFallback(List<Employee> employees, DaySolution daySolution, int lastIndex, boolean isHolidays) {
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {				
				if(!e.isSportak()) {
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_NIGHT)) {
						if(!getDayPreference(e, daySolution).isNoNight()) {
							ShiftSolverLogger.debug("  Assigning "+e.getFullName()+" as staff WEEKEND NIGHT (FALLBACK)");
							return new Candidate(e, true);						
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
					if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
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
	
	private Employee findStafferForWeekendMorning(List<Employee> employees, DaySolution daySolution, Employee lastAssignee) {
		int lastIndex = getLastAssigneeIndexWithSkip(lastAssignee, employees);
		for(int i=lastIndex; i<employees.size(); i++) {
			Employee e=employees.get(i);
			if(!daySolution.isEmployeeAllocatedToday(e.getKey())) {
				if(!e.isEditor() && !e.isSportak()) {
					EmployeeAllocation employeeAllocation = e2a.get(e.getKey());
					if(employeeAllocation.hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
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
		if(e2a.get(e.getKey()).hasCapacity(daySolution.getDay(), SHIFT_MORNING)) {
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
				null);
	}
	
	private void debugDown(int d, String shiftType, String role, int count) {		
		ShiftSolverLogger.debug("   >>> DOWN - FOUND for day-shift-role "+d+"-"+shiftType+"-"+role+" #"+count+" ("+(steps++)+" steps, depth: "+(depth++)+")");
		
		if(count>employees.size()) {
			throw new ShiftSolverException(
					"LOOP DETECTED ("+count+">"+employees.size()+"="+e2a.size()+") "
					+ "when assigning WORKDAY/WEEKEND-"+ shiftType+"-"+ role+" for day "+d
					+" and solution number",
					failedWithEmployeeAllocations,
					d,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);										
		}
		
		if(steps>stepsLimit) {
			throw new ShiftSolverException(
					"Steps exceeded - depth "+d+", "+shiftType+", "+role,
					failedWithEmployeeAllocations,
					d,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole
					);
		}
	}

	private void clearFailedOn() {
		failedOnMaxDay=-1;
		failedOnMaxDepth=-1;
		failedOnShiftType=null;
		failedOnRole=null;
		failedWithEmployeeAllocations=null;
	}
	
	private void debugUp(int d, String shiftType, String role) {
		ShiftSolverLogger.debug("   <<< BACKTRACK UP - failed for day/shift/role "+d+"-"+shiftType+"-"+role+" ("+(steps++)+" steps, depth: "+depth+")");

		if(failedOnMaxDepth<depth) {
			failedOnMaxDay=d;
			failedOnMaxDepth=depth;
			failedOnShiftType=shiftType;
			failedOnRole=role;
			failedWithEmployeeAllocations=EmployeeAllocation.clone(e2a);
		}

		ShiftSolverLogger.debug("     BOTTOM CAUSE - failed for depth/shift/role "+failedOnMaxDepth+"-"+failedOnShiftType+"-"+failedOnRole);
		EmployeeAllocation.printEmployeeAllocations(d,new ArrayList<EmployeeAllocation>(e2a.values()));		

		depth--;
		
		if(steps>stepsLimit) {
			throw new ShiftSolverException(
					i18n.exceptionSolutionNotFoundStepLimitExceeded(stepsLimit,d,shiftType,role),
					failedWithEmployeeAllocations,
					failedOnMaxDay,
					failedOnMaxDepth,
					failedOnShiftType,
					failedOnRole);
		}
	}
	
	public boolean isEnforceAfternoonTo8am() {
		return enforceAfternoonTo8am;
	}

	public void setEnforceAfternoonTo8am(boolean enforceAfternoonTo8am) {
		this.enforceAfternoonTo8am = enforceAfternoonTo8am;
	}

	public boolean isEnforceNightToAfternoon() {
		return enforceNightToAfternoon;
	}

	public void setEnforceNightToAfternoon(boolean enforceNightToAfternoon) {
		this.enforceNightToAfternoon = enforceNightToAfternoon;
	}

	@Override
	public void setIterationsLimit(long limit) {
		this.stepsLimit=limit;
	}

	public EmployeeAvailability getAvailabilityStringForEmployee(
			Employee e,
			int needDay,
			int needShiftType,
			int needRole,
			PeriodPreferences p,
			PeriodSolution s,
			Map<String,EmployeeAllocation> a) 
	{
		EmployeeAvailability result=new EmployeeAvailability(ctx);
				
		// this is simplified e.g. mortaks ... not treated > false OK candidates
		switch(needRole) {
		case ROLE_EDITOR:
			if(e.isEditor()) {
				result.setRoleMatch(true);
			}
			break;
		case ROLE_SPORTAK:
			if(e.isSportak()) {
				result.setRoleMatch(true);
			}
			break;
		case ROLE_STAFFER:
			if(!e.isSportak()) {
				result.setRoleMatch(true);
			}
			break;
		case ROLE_ANYBODY:
			result.setRoleMatch(true);
			break;
		}
		
		switch(needShiftType) {
		case SHIFT_MORNING:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoMorning6()) {
				result.setNotBusy(true);
			}
			break;
		case SHIFT_MORNING_6:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoMorning6()) {
				result.setNotBusy(true);
			}
			break;
		case SHIFT_MORNING_7:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoMorning7()) {
				result.setNotBusy(true);
			}
			break;
		case SHIFT_MORNING_8:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoMorning8()) {
				result.setNotBusy(true);
			}
			break;
		case SHIFT_AFTERNOON:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoAfternoon()) {
				result.setNotBusy(true);
			}
			break;
		case SHIFT_NIGHT:
			if(!getDayPreference(e, p, s.getSolutionForDay(needDay)).isNoNight()) {
				result.setNotBusy(true);
			}
			break;
		}
		
		if(a.get(e.getKey()).hasCapacity(needDay, needShiftType)) {
			result.setShiftCapacity(true);
		}
		
		if(!a.get(e.getKey()).hadShiftsLast5Days(needDay)) {
			result.setNotAllocated5Days(true);
		}

		if(!a.get(e.getKey()).hadShiftToday(needDay)) {
			result.setNotAllocatedToday(true);
		}
		
		// TODO night capacity (checked, but not explicit)

		if(needRole==ROLE_EDITOR &&
			(Utils.isWeekend(needDay, p.getStartWeekDay())
			  ||
			 s.getSolutionForDay(needDay).getWeekday()==Calendar.FRIDAY)) {
			result.warnEditorToContinue(true);
		}
		
		return result;
	}
	
	public static PeriodSolution createSolutionSkeleton(PeriodPreferences periodPreferences, RiaState riaState) {
		PeriodSolution s=new PeriodSolution();
		s.setYear(periodPreferences.getYear());
		s.setMonth(periodPreferences.getMonth());
		s.setPeriodPreferencesKey(periodPreferences.getKey());

		Map<String,Job> jobs=new HashMap<String,Job>();
		for(Employee e:riaState.getEmployees()) {
			Job job = new Job();
			job.shiftsLimit=EmployeeAllocation.calculateShiftToGet(e, periodPreferences);
			jobs.put(e.getKey(), job);
		}
		s.setEmployeeJobs(jobs);
		
		PublicHolidays publicHolidays=new PublicHolidays();
		boolean holidays=false;		
		for(int d=1; d<=periodPreferences.getMonthDays(); d++) {
			if(publicHolidays.isHolidays(periodPreferences.getYear(), periodPreferences.getMonth(), d)) {
				holidays=true;
			} else {
				holidays=false;
			}
			
			DaySolution daySolution = new DaySolution(
					d, 
					Utils.getWeekdayNumber(d, periodPreferences.getStartWeekDay())+1,
					!holidays && !Utils.isWeekend(d, periodPreferences.getStartWeekDay()));
			s.addDaySolution(daySolution);
			
			if(holidays) {
				WeekendMorningShift weekendMorningShift = new WeekendMorningShift();
				weekendMorningShift.editor=new Holder<String>(FERDA.getKey());
				weekendMorningShift.staffer6am=new Holder<String>(FERDA.getKey());
				weekendMorningShift.sportak=new Holder<String>(FERDA.getKey());
				
				WeekendAfternoonShift weekendAfternoonShift = new WeekendAfternoonShift();
				weekendAfternoonShift.editor=new Holder<String>(FERDA.getKey());
				weekendAfternoonShift.staffer=new Holder<String>(FERDA.getKey());
				weekendAfternoonShift.sportak=new Holder<String>(FERDA.getKey());
								
				NightShift nightShift = new NightShift();
				nightShift.staffer=new Holder<String>(FERDA.getKey());
						
				daySolution.setWorkday(false);
				daySolution.setWeekendMorningShift(weekendMorningShift);
				daySolution.setWeekendAfternoonShift(weekendAfternoonShift);
				daySolution.setNightShift(nightShift);
			} else {
				if(Utils.isWeekend(d, periodPreferences.getStartWeekDay())) {
					WeekendMorningShift weekendMorningShift = new WeekendMorningShift();
					weekendMorningShift.editor=new Holder<String>(FERDA.getKey());
					weekendMorningShift.staffer6am=new Holder<String>(FERDA.getKey());
					weekendMorningShift.sportak=new Holder<String>(FERDA.getKey());
					
					WeekendAfternoonShift weekendAfternoonShift = new WeekendAfternoonShift();
					weekendAfternoonShift.editor=new Holder<String>(FERDA.getKey());
					weekendAfternoonShift.staffer=new Holder<String>(FERDA.getKey());
					weekendAfternoonShift.sportak=new Holder<String>(FERDA.getKey());
					
					NightShift nightShift = new NightShift();
					nightShift.staffer=new Holder<String>(FERDA.getKey());
					
					daySolution.setWorkday(false);
					daySolution.setWeekendMorningShift(weekendMorningShift);
					daySolution.setWeekendAfternoonShift(weekendAfternoonShift);
					daySolution.setNightShift(nightShift);
				} else {
					daySolution.setWorkday(true);
					WorkdayMorningShift workdayMorningShift = new WorkdayMorningShift();
					daySolution.setWorkdayMorningShift(workdayMorningShift);
					workdayMorningShift.editor=new Holder<String>(FERDA.getKey());
					workdayMorningShift.staffer6am=new Holder<String>(FERDA.getKey());
					workdayMorningShift.staffer7am=new Holder<String>(FERDA.getKey());
					workdayMorningShift.staffer8am1=new Holder<String>(FERDA.getKey());
					workdayMorningShift.staffer8am2=new Holder<String>(FERDA.getKey());
					workdayMorningShift.sportak=new Holder<String>(FERDA.getKey());
					
					WorkdayAfternoonShift workdayAfternoonShift = new WorkdayAfternoonShift();
					daySolution.setWorkdayAfternoonShift(workdayAfternoonShift);
					workdayAfternoonShift.editor=new Holder<String>(FERDA.getKey());
					workdayAfternoonShift.staffers[0]=new Holder<String>(FERDA.getKey());
					workdayAfternoonShift.staffers[1]=new Holder<String>(FERDA.getKey());
					workdayAfternoonShift.staffers[2]=new Holder<String>(FERDA.getKey());
					workdayAfternoonShift.staffers[3]=new Holder<String>(FERDA.getKey());
					workdayAfternoonShift.sportak=new Holder<String>(FERDA.getKey());
					
					NightShift nightShift = new NightShift();
					daySolution.setNightShift(nightShift);
					nightShift.staffer=new Holder<String>(FERDA.getKey());
				}			
			}
		}
		
		return s;
	}

	public boolean validateEmployeeAssignment(
			Employee e, 
			int day,
			int shift, 
			int role, 
			PeriodPreferences p,
			PeriodSolution s, 
			Map<String, EmployeeAllocation> a) 
	{
		switch(role) {
		case ROLE_EDITOR:
			if(!e.isEditor()) {
				return false;
			}
			break;
		case ROLE_SPORTAK:
			if(!e.isSportak()) {
				if(!Utils.isWeekend(day, p.getStartWeekDay()) && shift==SHIFT_MORNING && e.isMortak()) {
					// OK
				} else {
					return false;					
				}
			}
			break;
		case ROLE_STAFFER:
			if(e.isSportak()) {
				return false;
			}
			break;
		}
		
		switch(shift) {
		case SHIFT_MORNING:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoMorning6()) {
				return false;
			}
			break;
		case SHIFT_MORNING_6:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoMorning6()) {
				return false;
			}
			break;
		case SHIFT_MORNING_7:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoMorning7()) {
				return false;
			}
			break;
		case SHIFT_MORNING_8:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoMorning8()) {
				return false;
			}
			break;
		case SHIFT_AFTERNOON:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoAfternoon()) {
				return false;
			}
			break;
		case SHIFT_NIGHT:
			if(getDayPreference(e, p, s.getSolutionForDay(day)).isNoNight()) {
				return false;
			}
			break;
		}

		// don't check capacity (too many red buttons) - it's shown on allocations panel
		
		if(a.get(e.getKey()).hadShiftsLast5Days(day)) {
			return false;
		}

		if(a.get(e.getKey()).hadMoreThanOneShiftToday(day)) {
			if(e.isEditor() && Utils.isWeekend(day, p.getStartWeekDay())) {
				// OK
			} else {
				if(e.isEditor() && publicHolidays.isHolidays(preferences.getYear(), preferences.getMonth(), day)) {
					// OK
				} else {
					return false;
				}
			}
		}
		
		// don't check night capacity - it's shown on allocations panel

		// TODO editor continuity to be checked (day and go backward)
		
		return true;
	}
}
