package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class BacktrackFor implements ShiftSolverConstants {
	
	public static final BacktrackFor SOLUTION = new BacktrackFor(ROOT);
	
	public int targetRole;
	
	public BacktrackFor(int targetRole) {
		this.targetRole=targetRole;
	}
	
	public boolean isTarget(int levelRole) {
		return targetRole==levelRole || ROLE_ANYBODY==targetRole;
	}
	
	public boolean isSolutionFound() {
		return targetRole==ROOT;
	}
}
