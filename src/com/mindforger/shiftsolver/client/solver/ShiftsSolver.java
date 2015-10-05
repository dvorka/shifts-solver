package com.mindforger.shiftsolver.client.solver;

import java.util.ArrayList;

import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
import com.mindforger.shiftsolver.shared.model.ShiftSolution;
import com.mindforger.shiftsolver.shared.model.Team;

/**
 * Solver runs on the client side (browser) to off-load work from
 * the server (server is used for persistence only - team/preferences/solution).
 * 
 * Solver is stateful i.e. after initialization caller gets solutions using
 * next() method.
 */
public class ShiftsSolver {

	private long runs;
	
	private static final int LIMIT_EMPLOYEE_SHIFTS_PER_PERIOD=5;
	private static final int LIMIT_NORMAL_SHIFT_REST_GAP_HOURS=4;
	private static final int LIMIT_NIGHT_SHIFT_REST_GAP_HOURS=8;
	
	// shift = editor + sportak + drones
	private static final int NORMAL_SHIFT_DRONES=4;
	
	public ShiftsSolver() {
		runs=0;
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
		PeriodSolution result = new PeriodSolution(periodPreferences.getYear(), periodPreferences.getMonth());
		result.setDlouhanKey(periodPreferences.getKey());
		result.setKey(periodPreferences.getKey() + "/" + ++runs);
		
		ArrayList<DaySolution> daysSolutions = new ArrayList<DaySolution>();
		DaySolution daySolution = new DaySolution();
		ShiftSolution shiftSolution = new ShiftSolution();
		daySolution.setMorning(shiftSolution);
		daysSolutions.add(daySolution);
		result.setDays(daysSolutions);
//		
//		
//		for(ShiftSolution shift:result.getShifts()) {
//
//			// TODO assign editors
//			for(Employee editor:team.getEditors().values()) {
//				if(fitsEditorPreferences(editor, periodPreferences)) {
//					shift.setEditor(editor);
//					break;
//				}
//			}
//
//			// TODO assign sportaci
//			for(Employee sportak:team.getSportaci().values()) {
//				if(fitsSportakPreferences(sportak, periodPreferences)) {
//					shift.setSportak(sportak);
//					break;
//				}
//			}
//
//			// TODO fill with full time drones > editors(with free shifts) > part time drones
//			for(Employee employee:team.getEmployees().values()) {
//				if(fitsFullTimeDronePreferences(employee, periodPreferences)) {
//					shift.addDrone(employee);
//					if(shift.solved(NORMAL_SHIFT_DRONES)) {
//						break;
//					}
//				}
//				// ... > editors
//				if(!shift.solved(NORMAL_SHIFT_DRONES)) {
//					for(Employee editor:team.getEditors().values()) {
//						if(fitsEditorDronePreferences(editor, periodPreferences)) {
//							shift.addDrone(editor);
//							if(shift.solved(NORMAL_SHIFT_DRONES)) {
//								break;
//							}
//						}
//					}
//					// ... > part time drones
//					if(!shift.solved(NORMAL_SHIFT_DRONES)) {
//						for(Employee partTimeDrone:team.getEmployees().values()) {
//							if(fitsPartTimeDronePreferences(partTimeDrone, periodPreferences)) {
//								shift.addDrone(partTimeDrone);
//								if(shift.solved(NORMAL_SHIFT_DRONES)) {
//									break;
//								}
//							}
//						}				
//					}
//				}
//			}
//					
//			if(!shift.solved(NORMAL_SHIFT_DRONES)) {
//				// TODO this is FAILURE > backtrack and try other variant
//			}			
//		}
		
		return result;
	}

	private boolean fitsPartTimeDronePreferences(Employee partTimeDrone, PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean fitsEditorDronePreferences(Employee editor, PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean fitsFullTimeDronePreferences(Employee employee, PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean fitsSportakPreferences(Employee sportak, PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean fitsEditorPreferences(Employee editor, PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return true;
	}
}
