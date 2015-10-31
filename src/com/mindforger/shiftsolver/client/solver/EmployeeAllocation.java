package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class EmployeeAllocation implements ShiftSolverConstants {
	
	public Employee employee;
	
	public int shiftsToGet;
	public int shifts;

	public List<Integer> shiftsOnDays;
	public List<Integer> shiftTypesOnDays;
	public int nights;
	
	public boolean enforceAfternoonTo8am;
	public boolean enforceNightToAfternoon;
	
	private EmployeeAllocation() {		
	}
	
	public EmployeeAllocation(Employee employee, PeriodPreferences preferences) {
		this.employee=employee;
		this.shifts=0;
		this.nights=0;
		this.shiftsOnDays=new ArrayList<Integer>();
		this.shiftTypesOnDays=new ArrayList<Integer>();
		
		shiftsToGet=calculateShiftToGet(employee, preferences);
		
		if(preferences.getEmployeeToPreferences()!=null) {
			EmployeePreferences employeePreferences = preferences.getEmployeeToPreferences().get(employee.getKey());
			if(employeePreferences!=null) {
				for(DayPreference dayPreference:employeePreferences.getPreferences()) {
					if(dayPreference.isHoliDay()) {
						if(Utils.isWorkday(
								dayPreference.getDay(), 
								preferences.getStartWeekDay(), 
								preferences.getMonthDays())) {
							shifts++;
						}
					}
				}
			}
		}
	}

	public static int calculateShiftToGet(Employee employee, PeriodPreferences preferences) {
		int shiftsToGet;
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)preferences.getMonthWorkDays())/8f*7.5f);
		} else {
			shiftsToGet=Math.round((((float)preferences.getMonthWorkDays())/8f*7.5f)/2f);			
		}
		return shiftsToGet;
	}

	public EmployeeAllocation clone() {
		EmployeeAllocation clone=new EmployeeAllocation();
		clone.employee=employee;
		clone.shiftsToGet=shiftsToGet;
		clone.shifts=shifts;
		clone.enforceAfternoonTo8am=enforceAfternoonTo8am;
		clone.enforceNightToAfternoon=enforceNightToAfternoon;
		clone.shiftsOnDays=new ArrayList<Integer>(shiftsOnDays);
		clone.shiftTypesOnDays=new ArrayList<Integer>(shiftTypesOnDays);
		return clone;
	}

	public static List<EmployeeAllocation> clone(Map<String,EmployeeAllocation> employeeAllocations) {
		return employeeAllocations==null?new ArrayList<EmployeeAllocation>():clone(new ArrayList<EmployeeAllocation>(employeeAllocations.values()));
	}
	
	public static List<EmployeeAllocation> clone(List<EmployeeAllocation> employeeAllocations) {
		List<EmployeeAllocation> result=new ArrayList<EmployeeAllocation>();
		if(employeeAllocations!=null && !employeeAllocations.isEmpty()) {
			for(EmployeeAllocation employeeAllocation:employeeAllocations) {
				result.add(employeeAllocation.clone());
			}
		}
		return result;
	}
	
	public void assign(int day, int shiftType) {
		// TODO this should go away: checks must be made earlier, here I set, check to be optional
		if(shiftType==ShiftSolverConstants.SHIFT_NIGHT) {
			if(employee.isFulltime() && nights>=2) {
				nights++;				
				// throw new RuntimeException("Attempt to assign fulltime employee "+employee.getFullName()+" more than 2 night shifts (has "+nights+")");
			} else {
				nights++;				
			}
		}
		
		shiftsOnDays.add(day);
		shiftTypesOnDays.add(shiftType);
		shifts++;
	}
	
	public void unassign(int shiftType) {
		shiftsOnDays.remove(shiftsOnDays.size()-1);
		shiftTypesOnDays.remove(shiftTypesOnDays.size()-1);
		shifts--;
		if(shiftType==ShiftSolverConstants.SHIFT_NIGHT) {
			nights--;			
		}
	}

	public boolean hasCapacity(int day, int shiftType) {
		return hasCapacity(day, shiftType, 1, false);
	}
	
	public boolean hasCapacity(int day, int shiftType, int capacityNeeded, boolean editorWeekendContinuity) {
		if(employee.isFulltime() && nights>=2 && shiftType==ShiftSolverConstants.SHIFT_NIGHT) {
			return false;
		}
		
		if(shiftsOnDays.size()>0) {
			int yesterday=day-1;
			if(shiftsOnDays.get(shiftsOnDays.size()-1)==yesterday) {
				if(!enforceNightToAfternoon
					 ||						
				   (shiftTypesOnDays.get(shiftTypesOnDays.size()-1) == ShiftSolverConstants.SHIFT_NIGHT
					  &&
				   (shiftType == ShiftSolverConstants.SHIFT_AFTERNOON || shiftType == ShiftSolverConstants.SHIFT_NIGHT))) 
				{					
					if(enforceAfternoonTo8am
					     ||
					   (shiftTypesOnDays.get(shiftTypesOnDays.size()-1) == ShiftSolverConstants.SHIFT_AFTERNOON
							&&
					   (editorWeekendContinuity ||
					    shiftType == ShiftSolverConstants.SHIFT_MORNING ||
					    shiftType == ShiftSolverConstants.SHIFT_MORNING_8 || 
					    shiftType == ShiftSolverConstants.SHIFT_AFTERNOON || 
					    shiftType == ShiftSolverConstants.SHIFT_NIGHT))) 
					{					
						// OK
					} else {
						return false;
					}					
				} else {
					return false;
				}
			}
			
			// RULE: at most 1 shift/day
			if(shiftsOnDays.get(shiftsOnDays.size()-1)!=day || editorWeekendContinuity) {
				// RULE: at most 5 consecutive days at work (last 4 days connected to today)
				if(hadShiftsLast5Days(day)) {
					return false;
				}
				return hasCapacity(capacityNeeded);
			} else {
				return false;
			}
		} else {
			// TODO was is probably BUG shiftsOnDays.add(day);
			// TODO was is probably BUG shiftTypesOnDays.add(shiftType);
			return hasCapacity(capacityNeeded);
		}
	}

	public boolean hadShiftToday(int day) {
		int count=0;
		if(shiftsOnDays.size()>0) {
			for(Integer i:shiftsOnDays) {
				if(day==i) {
					count++;
				}
			}
		}		
		return count>0;
	}

	public boolean hadMoreThanOneShiftToday(int day) {
		int count=0;
		if(shiftsOnDays.size()>0) {
			for(Integer i:shiftsOnDays) {
				if(day==i) {
					count++;
				}
			}
		}		
		return count>1;
	}
	
	public boolean hadShiftsLast5Days(int day) {
		if(shiftsOnDays.size()>=5) {
			int lastIndex=shiftsOnDays.size()-1;
			int lastDay=shiftsOnDays.get(lastIndex);
			if(lastDay==(day-1)
				 &&
			   shiftsOnDays.get(lastIndex-1)==(lastDay-1)
				 &&
			   shiftsOnDays.get(lastIndex-2)==(lastDay-2)
				 &&
			   shiftsOnDays.get(lastIndex-3)==(lastDay-3) 
				 &&
			   shiftsOnDays.get(lastIndex-4)==(lastDay-4)) 
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasCapacity(int capacityNeeded) {
		return shiftsToGet>0 && shiftsToGet>=(shifts+capacityNeeded);
	}

	public static List<EmployeeAllocation> calculateEmployeeAllocations(
			PeriodPreferences preferences,
			PeriodSolution solution,
			List<Employee> employees) 
	{
		Map<String,EmployeeAllocation> eToA=new HashMap<String, EmployeeAllocation>();
		for(Employee e:employees) {
			if(e!=null) eToA.put(e.getKey(), new EmployeeAllocation(e, preferences));
		}
		eToA.put(ShiftSolver.FERDA_KEY, new EmployeeAllocation(ShiftSolver.FERDA, preferences));
		
		for(int d=1; d<=preferences.getMonthDays(); d++) {
			DaySolution ds = solution.getSolutionForDay(d);
			if(ds!=null) {
				if(ds.isWorkday()) {
					eToA.get(ds.getWorkdayMorningShift().editor.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWorkdayMorningShift().staffer6am.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWorkdayMorningShift().staffer7am.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWorkdayMorningShift().staffer8am1.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWorkdayMorningShift().staffer8am2.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWorkdayMorningShift().sportak.get()).assign(d, SHIFT_MORNING);
					
					eToA.get(ds.getWorkdayAfternoonShift().editor.get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWorkdayAfternoonShift().staffers[0].get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWorkdayAfternoonShift().staffers[1].get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWorkdayAfternoonShift().staffers[2].get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWorkdayAfternoonShift().staffers[3].get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWorkdayAfternoonShift().sportak.get()).assign(d, SHIFT_AFTERNOON);
					
					eToA.get(ds.getNightShift().staffer.get()).assign(d, SHIFT_NIGHT);
					
				} else {
					eToA.get(ds.getWeekendMorningShift().editor.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWeekendMorningShift().staffer6am.get()).assign(d, SHIFT_MORNING);
					eToA.get(ds.getWeekendMorningShift().sportak.get()).assign(d, SHIFT_MORNING);

					eToA.get(ds.getWeekendAfternoonShift().editor.get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWeekendAfternoonShift().staffer.get()).assign(d, SHIFT_AFTERNOON);
					eToA.get(ds.getWeekendAfternoonShift().sportak.get()).assign(d, SHIFT_AFTERNOON);

					eToA.get(ds.getNightShift().staffer.get()).assign(d, SHIFT_NIGHT);
				}
			}
		}
		
		eToA.remove(ShiftSolver.FERDA_KEY);
		
		return new ArrayList<EmployeeAllocation>(eToA.values());
	}
	
	public static void printEmployeeAllocations(int day, List<EmployeeAllocation> allocations) {
		ShiftSolverLogger.debug("     Employee allocations ("+allocations.size()+"):");
		for(EmployeeAllocation a:allocations) {
			String fullShifts=a.shifts<a.shiftsToGet?"<":(a.shifts==a.shiftsToGet?"!":"X");
			String fullNights=a.nights<2?"<":(a.nights==2?"!":"X");
			ShiftSolverLogger.debug(
					"       "+
					fullShifts+fullNights+
					" "+
					(fullShifts.equals("!")?"!!!":
						(a.hadShiftsLast5Days(day)?"123":
							(a.hadShiftToday(day)?"ttt":"...")))+
					" "+
					(a.employee.isEditor()?"editor    ":
						(a.employee.isSportak()?"sportak   ":
							(a.employee.isMortak()?"am-sportak":"staffer   ")))+
					" "+
					(a.employee.isFulltime()?"FULL":"PART")+
					" "+
					a.employee.getFullName()+" "+
						"jobs: "+a.shifts+"/"+a.shiftsToGet+" ("+(a.shiftsToGet-a.shifts)+") "+
						"nights: "+a.nights+"/"+(a.employee.isFulltime()?"2":"X")+" ("+(2-a.nights)+")"
					);
		}		
	}
}
