package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.List;

import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class EmployeeAllocation {
	
	public Employee employee;
	
	public int shiftsToGet;
	public int shifts;

	public List<Integer> shiftsOnDays;
	public List<Integer> shiftTypesOnDays;
	public int nights;
	
	public boolean enforceAfternoonTo8am;
	public boolean enforceNightToAfternoon;
	
	public EmployeeAllocation(Employee employee, PeriodPreferences preferences) {
		this.employee=employee;
		this.shifts=0;
		this.nights=0;
		this.shiftsOnDays=new ArrayList<Integer>();
		this.shiftTypesOnDays=new ArrayList<Integer>();
		
		if(employee.isFulltime()) {
			shiftsToGet=Math.round(((float)preferences.getMonthWorkDays())/8f*7.5f);
		} else {
			shiftsToGet=Math.round((((float)preferences.getMonthWorkDays())/8f*7.5f)/2f);			
		}
		
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

	public void assign(int day, int shiftType) {
		shiftsOnDays.add(day);
		shiftTypesOnDays.add(shiftType);
		shifts++;
				
		if(shiftType==ShiftSolverConstants.SHIFT_NIGHT) {
			if(employee.isFulltime() && nights>=2) {
				throw new RuntimeException("Attemp to assign fulltime employee "+employee.getFullName()+" more than 2 night shifts (has "+nights+")");
			} else {
				nights++;				
			}
		}
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
			shiftsOnDays.add(day);
			shiftTypesOnDays.add(shiftType);
			return hasCapacity(capacityNeeded);
		}
	}

	public boolean hadShiftToday(int day) {
		if(shiftsOnDays.size()>0) {
			return day==shiftsOnDays.get(shiftsOnDays.size()-1);			
		}		
		return false;
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
}
