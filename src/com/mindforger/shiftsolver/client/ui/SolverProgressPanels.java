package com.mindforger.shiftsolver.client.ui;

public interface SolverProgressPanels {
	void refresh(
			String progressInPercent,
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String solutionsCount, 
			String bestSolutionScore);
}
