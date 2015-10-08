package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.client.ui.SolverProgressPanels;

public class DebugSolverPanel implements SolverProgressPanels {
	
	private String progress;
	private String count;
	private String score;
	
	public DebugSolverPanel() {
		progress=count=score="0";
	}
	
	@Override
	public void refresh(String progress, String count, String score) {
		// TODO to be removed for RIA
		
		this.progress=(progress==null?this.progress:progress);
		this.count=(count==null?this.count:count);
		this.score=(score==null?this.score:score);
		
		System.out.println("##### progress: "+
				this.progress+"% - "+
				this.count+" solutions, best score "+
				this.score+"pt #####");
	}
}
