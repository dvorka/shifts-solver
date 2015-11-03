package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class SolutionPostprocessor {

	public SolutionPostprocessor() {
	}
	
	public PeriodSolution improve(PeriodSolution solution) {
		// try to make partial solution OK solution
		// balance shifts between employees so that they have same number
		// balance morning/afternoon shifts for one employee
		return solution;
	}
}
