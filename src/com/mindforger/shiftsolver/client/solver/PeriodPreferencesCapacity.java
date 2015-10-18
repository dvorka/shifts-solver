package com.mindforger.shiftsolver.client.solver;

import java.util.Collection;
import java.util.List;

import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodPreferencesCapacity {
	
	/*
		count what's needed > then try to fill it just using capacity (use morning sportaks, ...)

		ranni sportaci (pricist ke sportakum) + pocitadlo kapacity na preference stranku
		check capacity button
		disable solve?
	 */
	
	int neededEditorShifts;
	int neededSportakShifts;
	int neededDroneShifts;
	
	int haveEditorShifts;
	int haveDroneShifts;
	int haveSportakShifts;
	
	public PeriodPreferencesCapacity() {
	}
	
	public void calculate(PeriodPreferences preferences, Collection<EmployeeAllocation> allocations) {		
		haveEditorShifts=haveDroneShifts=haveSportakShifts=0;
		for(EmployeeAllocation a:allocations) {
			if(a.employee.isEditor()) {
				haveEditorShifts+=a.shiftsToGet;
				haveDroneShifts+=a.shiftsToGet;
			} else {
				if(a.employee.isSportak()) {
					haveSportakShifts+=a.shiftsToGet;					
				} else {
					haveDroneShifts+=a.shiftsToGet;					
				}
			}
		}
		
		neededEditorShifts=neededDroneShifts=neededSportakShifts=0;		
		int dow=preferences.getStartWeekDay()-1;
		for(int i=0; i<preferences.getMonthDays(); i++) {
			if(dow!=0 && dow!=6) {
				neededEditorShifts+=2;
				neededDroneShifts+=8;
				neededSportakShifts+=2;
			} else {
				neededEditorShifts+=2;
				neededDroneShifts+=3;
				neededSportakShifts+=2;				
			}
			dow++;
			dow=dow%7;
		}

	}

	public boolean isCapacitySufficient() {
		return 
				(haveEditorShifts<neededEditorShifts) &&
				(haveDroneShifts<neededDroneShifts) &&
				(haveSportakShifts<neededSportakShifts);
	}
	
	public void printCapacity() {
		ShiftSolverLogger.debug("     Period capacity (HAVE >= NEEDED):");
		ShiftSolverLogger.debug("       editor  : "+haveEditorShifts+"x"+neededEditorShifts+" "
				+(haveEditorShifts<neededEditorShifts?"FAIL":""));
		ShiftSolverLogger.debug("       drone   : "+haveDroneShifts+"x"+neededDroneShifts+" "
				+(haveDroneShifts<neededDroneShifts?"FAIL":""));
		ShiftSolverLogger.debug("       sportak  : "+haveSportakShifts+"x"+neededSportakShifts+" "
				+(haveSportakShifts<neededSportakShifts?"FAIL":""));
	}
	
	public void printEmployeeAllocations(int day, List<EmployeeAllocation> allocations) {
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
							(a.employee.isMorningSportak()?"am-sportak":"drone     ")))+
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
