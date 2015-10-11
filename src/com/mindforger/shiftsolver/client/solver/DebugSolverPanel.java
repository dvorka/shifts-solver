package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;

public class DebugSolverPanel implements SolverProgressPanels {
	
	private String progress;
	private String count;
	private String score;
	private String steps;
	private String failedOnDay;
	private String failedOnRole;
	private String failedOnShiftType;

	private String lastProgress;
	
	public DebugSolverPanel() {
		progress=count=score=steps=lastProgress="0";
	}
	
	@Override
	public void refresh(
			String progress,
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String count, 
			String score) 
	{		
		if(!lastProgress.equals(progress)) {
			this.lastProgress=progress;
			this.progress=(progress==null?this.progress:progress);
			this.failedOnDay=failedOnDay;
			this.failedOnRole=failedOnRole;
			this.failedOnShiftType=failedOnShiftType;
			this.steps=(steps==null?this.count:steps);
			this.count=(count==null?this.count:count);
			this.score=(score==null?this.score:score);
			
			System.out.println("PROGRESS: "+this.progress+"% "+this.steps+" steps"
//			        + ", "+this.count+" solutions"
//			        + ", best score "+ this.score+"pt, "
//					+ (!failedOnDay.equals("")?"failed: "+this.failedOnDay+" "+this.failedOnRole+" "+this.failedOnShiftType:"")
					);			
		}
	}
}
