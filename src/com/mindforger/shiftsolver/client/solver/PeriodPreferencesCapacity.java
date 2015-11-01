package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;
import java.util.Collection;

import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodPreferencesCapacity {
		
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
				if(a.employee.isMortak()) {
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
		} else {
			// nothing to reallocate
		}
		if(haveEditorShifts>=neededEditorShifts) {
			haveEditorShifts=neededEditorShifts;
			haveDroneShifts+=haveEditorShifts-neededEditorShifts;
		}		
	}

	public boolean isCapacitySufficient() {
		if((haveEditorShifts>=neededEditorShifts) &&
			(haveDroneShifts>=neededDroneShifts) &&
			// ... morning portaks are here just to help sportaks
			// (haveMorningSportakShifts>=neededMorningSportakShifts) && 
			(haveSportakShifts>=neededSportakShifts)) {
				return true;
		} else {
			throw new ShiftSolverException(
					"Insufficient capacity for role(s): "+
						(haveEditorShifts<neededEditorShifts?"editor / ":"")+
						(haveDroneShifts<neededDroneShifts?"staffer / ":"")+
						(haveMorningSportakShifts<neededMorningSportakShifts?"mortak / ":"")+
						(haveSportakShifts<neededSportakShifts?" sportak":""), 
					new ArrayList<EmployeeAllocation>(),
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
		ShiftSolverLogger.debug("  "+(haveDroneShifts<neededDroneShifts?"FAIL":" OK ")+" staffer  : "
				+haveDroneShifts+">="+neededDroneShifts);
		ShiftSolverLogger.debug("  "+(haveDroneShifts<neededDroneShifts?"FAIL":" OK ")+" mortak   : "
				+haveMorningSportakShifts+">="+neededMorningSportakShifts);
		ShiftSolverLogger.debug("  "+(haveSportakShifts<neededSportakShifts?"FAIL":" OK ")+" sportak  : "
				+haveSportakShifts+">="+neededSportakShifts);
		ShiftSolverLogger.debug("");
	}	
}
