package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;

public class DebugSolverPanel implements SolverProgressPanels {
	
	private String progress;
	private String steps;

	private String lastProgress;
	
	public DebugSolverPanel() {
		progress=steps=lastProgress="0";
	}
	
	@Override
	public void refresh(
			String progress,
			String failedOnDay,
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String count, 
			String score) 
	{		
		if(!lastProgress.equals(progress)) {
			this.lastProgress=progress;
			this.progress=(progress==null?this.progress:progress);
			this.steps=(steps==null?this.steps:steps);
			
			System.out.println("PROGRESS: "+this.progress+"% "+this.steps+" steps");			
		}
	}
}
