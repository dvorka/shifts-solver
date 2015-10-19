package com.mindforger.shiftsolver.client.solver;

import java.util.Collection;
import java.util.List;

import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodPreferencesCapacity {
	
	// TODO check capacity button
	// TODO add check to solver > exit on fail w/ exception
	
	int neededEditorShifts;
	int neededDroneShifts;
	int neededMorningSportakShifts;
	int neededSportakShifts;
	
	int haveEditorShifts;
	int haveDroneShifts;
	int haveMorningSportakShifts;
	int haveSportakShifts;
	
	public PeriodPreferencesCapacity() {
	}
	
	public void calculate(PeriodPreferences preferences, Collection<EmployeeAllocation> allocations) {
		
		neededEditorShifts=neededDroneShifts=neededSportakShifts=0;		
		int dow=preferences.getStartWeekDay()-1;
		for(int i=0; i<preferences.getMonthDays(); i++) {
			if(dow!=0 && dow!=6) {
				neededEditorShifts+=2;
				neededDroneShifts+=8;
				neededMorningSportakShifts+=1;
				neededSportakShifts+=1;
			} else {
				neededEditorShifts+=2;
				neededDroneShifts+=3;
				neededSportakShifts+=2;				
			}
			dow++;
			dow=dow%7;
		}
		
		haveEditorShifts=haveDroneShifts=haveSportakShifts=0;
		for(EmployeeAllocation a:allocations) {
			if(a.employee.isEditor()) {
				haveEditorShifts+=a.shiftsToGet;
				haveDroneShifts+=a.shiftsToGet;
			} else {
				if(a.employee.isMorningSportak()) {
					haveMorningSportakShifts+=a.shiftsToGet;
				} else {
					if(a.employee.isSportak()) {
						haveSportakShifts+=a.shiftsToGet;					
					} else {
						haveDroneShifts+=a.shiftsToGet;			
					}					
				}
			}
		}
		
		if(haveMorningSportakShifts>=neededMorningSportakShifts) {
			haveMorningSportakShifts=neededMorningSportakShifts;
			haveDroneShifts+=haveMorningSportakShifts-neededMorningSportakShifts;
		}
		if(haveEditorShifts>=neededEditorShifts) {
			haveEditorShifts=neededEditorShifts;
			haveDroneShifts+=haveEditorShifts-neededEditorShifts;
		}		
	}

	public boolean isCapacitySufficient() {
		if((haveEditorShifts>=neededEditorShifts) &&
			(haveDroneShifts>=neededDroneShifts) &&
			(haveMorningSportakShifts>=neededMorningSportakShifts) &&
			(haveSportakShifts>=neededSportakShifts)) {
				return true;
		} else {
			throw new ShiftSolverException(
					"Insufficient capacity for "+
						(haveEditorShifts<neededEditorShifts?"editor ":"")+
						(haveDroneShifts<neededDroneShifts?"drone ":"")+
						(haveMorningSportakShifts<neededMorningSportakShifts?"morning sportak":"")+
						(haveSportakShifts<neededSportakShifts?" sportak":""), 
					0, 
					0, 
					"X", 
					"X");
		}
	}

	public void printCapacity() {
		ShiftSolverLogger.debug("Period capacity (HAVE >= NEEDED):");
		ShiftSolverLogger.debug("  "+(haveEditorShifts<neededEditorShifts?"FAIL":" OK ")+" editor   : "
				+haveEditorShifts+">="+neededEditorShifts);
		ShiftSolverLogger.debug("  "+(haveDroneShifts<neededDroneShifts?"FAIL":" OK ")+" drone    : "
				+haveDroneShifts+">="+neededDroneShifts);
		ShiftSolverLogger.debug("  "+(haveDroneShifts<neededDroneShifts?"FAIL":" OK ")+" m sportak: "
				+haveMorningSportakShifts+">="+neededMorningSportakShifts);
		ShiftSolverLogger.debug("  "+(haveSportakShifts<neededSportakShifts?"FAIL":" OK ")+" sportak  : "
				+haveSportakShifts+">="+neededSportakShifts);
		ShiftSolverLogger.debug("");
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
