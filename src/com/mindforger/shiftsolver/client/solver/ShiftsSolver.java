package com.mindforger.shiftsolver.client.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.model.Team;
import com.mindforger.shiftsolver.shared.model.shifts.NightShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendMorningShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayMorningShift;

/**
 * Solver runs on the client side (browser) to off-load work from
 * the server (server is used for persistence only - team/preferences/solution).
 * 
 * Solver is stateful i.e. after initialization caller gets solutions using
 * next() method.
 */
public class ShiftsSolver {

	@Deprecated
	private static final int LIMIT_EMPLOYEE_SHIFTS_PER_PERIOD=5;
	@Deprecated
	private static final int LIMIT_NORMAL_SHIFT_REST_GAP_HOURS=4;
	@Deprecated
	private static final int LIMIT_NIGHT_SHIFT_REST_GAP_HOURS=8;	
	// shift = editor + sportak + drones
	@Deprecated
	private static final int NORMAL_SHIFT_DRONES=4;
	
	private long sequence;

	private RiaContext ctx;
	private RiaMessages i18n;
	private Map<String,EmployeeAllocation> employeeAllocations;
	private PeriodPreferences preferences;

	public ShiftsSolver() {
	}
	
	public ShiftsSolver(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.sequence=0;
	}
	
	public PeriodSolution solve(Set<Employee> keySet, PeriodPreferences periodPreferences) {
  		Team team=new Team();
  		team.addEmployees(periodPreferences.getEmployeeToPreferences().keySet());
		return solve(team, periodPreferences);
	}	
	
	/**
	 * Solver uses backtracking with a few heuristics and solutions pruning.
	 * 
	 * Constraints:
	 * <ul>
	 *   <li>There MUST be at least one EDITOR in work week day.
	 *   <li>There MUST be at least one SPORTAK in work week day.
	 *   <li>Limit on number of shifts per employee ?.
	 *   <li>There must be at least 4 hours between two shifts employee is assigned for morning/afternoon shift.
	 *   <li>There must be at least 8 hours between two shifts employee is assigned for night shift.
	 * </ul>
	 * 
	 * Method:
	 * <ul>
	 *   <li>Iterate days 1st, ...
	 *   <li>Is it working day? Is it weekend day?
	 *   <li>Based on day type determine shift slots (e.g. 1x editor, 2x drones, ...) to be filled i.e. tuple.
	 *   <li>Take shift's slot (e.g. editor) and iterate corresponding employees who WANT (if nobody, who CAN), ... 
	 *   <li>Check whether employee can be allocated (constraints): Is fully busy for the month? Is there mandatory hour shift gap?
	 *   <li>... until all slots, shifts and days are allocated.
	 *   <li>If slot has NO SOLUTION, then BACKTRACK (requires FIXED order of employees, days and slots in days > enables backtrack).
	 *   <li>... iterate other employees until the end of list
	 *   <li>... if this slot doesn't have solution, try to iterate PREVIOUS slot - take employee that was chosen and iterate employees
	 *           from there.
	 *   <li>If slot still doesn't have SOLUTION, then report slot not filled e.g. Sunday morning editor slot (and show partial result).
	 * </ul>
	 * 
	 * @param periodPreferences		preferences of employees for a given period.
	 * @return	employees allocated to shifts.
	 */
	public PeriodSolution solve(Team team, PeriodPreferences periodPreferences) {
		this.preferences=periodPreferences;
		
		PeriodSolution result = new PeriodSolution(periodPreferences.getYear(), periodPreferences.getMonth());
		result.setDlouhanKey(periodPreferences.getKey());
		result.setKey(periodPreferences.getKey() + "/" + ++sequence);
		
		List<Employee> employees=team.getStableEmployeeList();
		employeeAllocations = new HashMap<String,EmployeeAllocation>();
		for(Employee e:employees) {
			employeeAllocations.put(e.getKey(), new EmployeeAllocation(e, periodPreferences.getMonthDays()));
		}
		
		for(int d=1; d<=periodPreferences.getMonthDays(); d++) {
			
			if(isWeekend(d, periodPreferences.getStartWeekDay())) {
				DaySolution daySolution = new DaySolution();
				daySolution.setWorkday(false);
				daySolution.setDay(d);
				
				// morning
				WeekendMorningShift weekendMorningShift = new WeekendMorningShift();
				// TODO editor should be friday afternoon editor (verify on Friday that editor has capacity for 3 shifts)
				// TODO PROBLEM if friday/saturday is in different month, there is no way to ensure editor continuity (Friday afternoon + Sat + Sun)
				weekendMorningShift.editor=findEditorForWeekendMorning(employees);
				if(weekendMorningShift.editor==null) { 
					reportSolutionDoesntExist(d, "MORNING", "EDITOR"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendMorningShift.editor.getKey()).assign();					
				}
				weekendMorningShift.drone6am=findDroneForWeekendMorning(employees);
				if(weekendMorningShift.drone6am==null) {
					reportSolutionDoesntExist(d, "MORNING", "STAFFER"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendMorningShift.drone6am.getKey()).assign();					
				}
				weekendMorningShift.sportak=findSportakForWeekendMorning(employees);
				if(weekendMorningShift.sportak==null) {
					reportSolutionDoesntExist(d, "MORNING", "SPORTAK"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendMorningShift.sportak.getKey()).assign();					
				}
				daySolution.setWeekendMorningShift(weekendMorningShift);
								
				// afternoon
				WeekendAfternoonShift weekendAfternoonShift=new WeekendAfternoonShift();
				weekendAfternoonShift.editor=findEditorForWeekendAfternoon(weekendMorningShift.editor);
				if(weekendAfternoonShift.editor==null) {
					reportSolutionDoesntExist(d, "AFTERNOON", "EDITOR"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendAfternoonShift.editor.getKey()).assign();					
				}
				weekendAfternoonShift.drone=findDroneForWeekendAfternoon(employees);
				if(weekendAfternoonShift.drone==null) {
					reportSolutionDoesntExist(d, "AFTERNOON", "STAFFER"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendAfternoonShift.drone.getKey()).assign();					
				}
				weekendAfternoonShift.sportak=findSportakForWeekendAfternoon(employees);
				if(weekendAfternoonShift.sportak==null) {
					reportSolutionDoesntExist(d, "AFTERNOON", "SPORTAK"); // TODO BACKTRACK;
				} else {
					employeeAllocations.get(weekendAfternoonShift.sportak.getKey()).assign();					
				}
				daySolution.setWeekendAfternoonShift(weekendAfternoonShift);
				
				// night
				NightShift nightShift=new NightShift();
				nightShift.drone=findDroneForWeekendNight(employees);
				if(nightShift.drone==null) {
					reportSolutionDoesntExist(d, "NIGHT", "STAFFER"); // TODO BACKTRACK;					
				}  else {
					employeeAllocations.get(nightShift.drone.getKey()).assign();					
				}
				daySolution.setNightShift(nightShift);

				result.addDaySolution(daySolution);
			} else {
				DaySolution daySolution = new DaySolution();
				daySolution.setWorkday(true);
				daySolution.setDay(d);

				// morning
				WorkdayMorningShift workdayMorningShift=new WorkdayMorningShift();
				workdayMorningShift.editor=findEditorForWorkdayMorning(employees);
				if(workdayMorningShift.editor==null) reportSolutionDoesntExist(d, "MORNING", "EDITOR"); // TODO BACKTRACK;
				workdayMorningShift.drone6am=findDroneForWorkdayMorning(employees);
				if(workdayMorningShift.drone6am==null) reportSolutionDoesntExist(d, "MORNING", "STAFFER"); // TODO BACKTRACK;
				workdayMorningShift.drone7am=findDroneForWorkdayMorning(employees);
				if(workdayMorningShift.drone7am==null) reportSolutionDoesntExist(d, "MORNING", "STAFFER"); // TODO BACKTRACK;
				workdayMorningShift.drone8am=findDroneForWorkdayMorning(employees);
				if(workdayMorningShift.drone8am==null) reportSolutionDoesntExist(d, "MORNING", "STAFFER"); // TODO BACKTRACK;
				workdayMorningShift.sportak=findSportakForWorkdayMorning(employees);
				if(workdayMorningShift.sportak==null) reportSolutionDoesntExist(d, "MORNING", "STAFFER"); // TODO BACKTRACK;
				daySolution.setWorkdayMorningShift(workdayMorningShift);
				
				// afternoon
				WorkdayAfternoonShift workdayAfternoonShift=new WorkdayAfternoonShift();
				workdayAfternoonShift.editor=findEditorForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.editor==null) reportSolutionDoesntExist(d, "AFTERNOON", "EDITOR"); // TODO BACKTRACK;
				workdayAfternoonShift.drones[0]=findDroneForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.drones[0]==null) reportSolutionDoesntExist(d, "Afternoon", "STAFFER"); // TODO BACKTRACK;
				workdayAfternoonShift.drones[1]=findDroneForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.drones[1]==null) reportSolutionDoesntExist(d, "Afternoon", "STAFFER"); // TODO BACKTRACK;
				workdayAfternoonShift.drones[2]=findDroneForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.drones[2]==null) reportSolutionDoesntExist(d, "Afternoon", "STAFFER"); // TODO BACKTRACK;
				workdayAfternoonShift.drones[3]=findDroneForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.drones[3]==null) reportSolutionDoesntExist(d, "Afternoon", "STAFFER"); // TODO BACKTRACK;
				workdayAfternoonShift.sportak=findSportakForWorkdayAfternoon(employees);
				if(workdayAfternoonShift.sportak==null) reportSolutionDoesntExist(d, "AFTERNOON", "STAFFER"); // TODO BACKTRACK;
				daySolution.setWorkdayAfternoonShift(workdayAfternoonShift);
				
				// night
				NightShift nightShift=new NightShift();
				nightShift.drone=findDroneForWorkdayNight(employees);
				if(nightShift.drone==null) {
					reportSolutionDoesntExist(d, "NIGHT", "STAFFER"); // TODO BACKTRACK;					
				}  else {
					employeeAllocations.get(nightShift.drone.getKey()).assign();					
				}
				daySolution.setNightShift(nightShift);
								
				result.addDaySolution(daySolution);
			}
			
			showProgress(preferences.getMonthDays(), d);
		}
		
		return result;
	}
	
	private Employee findSportakForWorkdayAfternoon(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(e.isSportak() && !e.isMorningSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as sportak");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayAfternoon(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(!e.isEditor() && !e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayAfternoon(List<Employee> employees) {
		// TODO employee preferences
		for(Employee e:employees) {
			if(e.isEditor()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as editor");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findSportakForWorkdayMorning(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(e.isSportak() && !e.isMorningSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as sportak");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayMorning(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(!e.isEditor() && !e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findEditorForWorkdayMorning(List<Employee> employees) {
		// TODO employee preferences
		for(Employee e:employees) {
			if(e.isEditor()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as editor");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findDroneForWorkdayNight(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		 // anybody except sportak e.g. normal, editor, MorningSportak
		for(Employee e:employees) {
			if(!e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendAfternoon(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(e.isSportak() && !e.isMorningSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as sportak");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findDroneForWeekendAfternoon(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(!e.isEditor() && !e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendAfternoon(Employee e) {
		if(employeeAllocations.get(e.getKey()).hasCapacity()) {
			showProgress("Assigning "+e.getFullName()+" as editor");
			return e;
		} else {
			return null;
		}
	}

	private Employee findDroneForWeekendNight(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		 // anybody except sportak e.g. normal, editor, MorningSportak
		for(Employee e:employees) {
			if(!e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findSportakForWeekendMorning(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(e.isSportak() && !e.isMorningSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as sportak");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findDroneForWeekendMorning(List<Employee> employees) {
		// TODO employee preferences > find the one who WANTS this first, SKIP who cannot
		for(Employee e:employees) {
			if(!e.isEditor() && !e.isSportak()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as staff");
					return e;
				}
			}
		}
		return null;
	}

	private Employee findEditorForWeekendMorning(List<Employee> employees) {
		// TODO employee preferences
		for(Employee e:employees) {
			if(e.isEditor()) {
				if(employeeAllocations.get(e.getKey()).hasCapacity()) {
					showProgress("Assigning "+e.getFullName()+" as editor");
					return e;
				}
			}
		}
		return null;
	}

	private void reportSolutionDoesntExist(int d, String shiftType, String role) {
		String message = "Solution doesn't exist for day "+d+", shift "+shiftType+" and role "+role+"!";
		if(ctx!=null) {
			ctx.getStatusLine().showError(message);			
		} else {
			System.err.println(message);
		}
		// TODO throw exception > throw it above to abort progress and report problem + keep error message
	}
	
	private boolean isWeekend(int i, int startWeekDay) {
		int sundayBeginningDayNumber=(i-1+startWeekDay-1)%7;
		if(sundayBeginningDayNumber==0 || sundayBeginningDayNumber==6) {
			return true;
		} else {
			return false;
		}
	}
	
	private void showProgress(int days, int processedDays) {
		int percent = Math.round(((float)processedDays) / (((float)days)/100f));
		showProgress("Building shifts schedule: "+percent+"% ("+processedDays+"/"+days+")");
		// TODO extra panel for progress ctx.getSolverProgressPanel().refresh(percent);
	}
	
	private void showProgress(String message) {
		if(ctx!=null) {
			ctx.getStatusLine().showProgress(message);			
		} else {
			System.out.println(message);
		}		
	}

	public Map<String, EmployeeAllocation> getEmployeeAllocations() {
		return employeeAllocations;
	}
}
