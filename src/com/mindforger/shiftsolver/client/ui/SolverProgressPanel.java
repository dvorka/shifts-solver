package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;

public class SolverProgressPanel extends FlexTable implements SolverProgressPanels {
	
	private TextBox currentSolutionPercentProgressTextBox;
	private TextBox solutionsCount;
	private TextBox bestSolutionScore;
	
	public SolverProgressPanel(final RiaContext ctx) {						
		HTML html;
		
		html = new HTML("Current solution progress");
		setWidget(0, 0, html);
		currentSolutionPercentProgressTextBox = new TextBox();
		setWidget(0, 1, currentSolutionPercentProgressTextBox);
		
		html = new HTML("Solution count");
		setWidget(1, 0, html);
		solutionsCount = new TextBox();
		setWidget(1, 1, solutionsCount);

		html = new HTML("Best solution score");
		setWidget(2, 0, html);
		bestSolutionScore = new TextBox();
		setWidget(2, 1, bestSolutionScore);
		
		refresh("0", "", "", "", "0", "0", "0");
	}
	
	public void refresh(
			String progress, 
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String count, 
			String score) {
		objectToRia(progress, failedOnDay, failedOnRole, failedOnShiftType, steps, count, score);
	}

	private void objectToRia(
			String progress, 
			String failedOnDay, // deepest one
			String failedOnShiftType,
			String failedOnRole,
			String steps, 
			String count, 
			String score) {
		if(progress!=null) {
		    currentSolutionPercentProgressTextBox.setText(progress+"%");			
		}
	    if(count!=null) {
		    solutionsCount.setText(count);	    	
	    }
	    if(progress!=null) {
		    bestSolutionScore.setText(score);	    	
	    }
	}
}
