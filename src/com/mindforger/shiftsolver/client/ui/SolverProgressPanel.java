package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class SolverProgressPanel extends FlexTable implements SolverProgressPanels, ShiftSolverConstants {
	
	private TextBox currentSolutionPercentProgressTextBox;
	private TextBox bestSolutionScore;
	
	public SolverProgressPanel(final RiaContext ctx) {						
		HTML html;
		
		html = new HTML("Current solution progress");
		setWidget(0, 0, html);
		currentSolutionPercentProgressTextBox = new TextBox();
		setWidget(0, 1, currentSolutionPercentProgressTextBox);
		
		html = new HTML("Best solution score");
		setWidget(1, 0, html);
		bestSolutionScore = new TextBox();
		setWidget(1, 1, bestSolutionScore);
		
		refresh("0", "", "", "", "0", "0");
	}
	
	public void refresh(
			String progress, 
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String score) 
	{
		if(DEBUG) {
			objectToRia(progress, failedOnDay, failedOnRole, failedOnShiftType, steps, score);			
		}
	}

	private void objectToRia(
			String progress, 
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String score) {
		if(progress!=null) {
		    currentSolutionPercentProgressTextBox.setText(progress+"%");			
		}
	    if(progress!=null) {
		    bestSolutionScore.setText(score);	    	
	    }
	}
}
