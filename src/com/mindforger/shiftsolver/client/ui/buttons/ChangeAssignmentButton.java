package com.mindforger.shiftsolver.client.ui.buttons;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.client.ui.SolutionPanel;
import com.mindforger.shiftsolver.client.ui.dialogs.ChangeAssignmentDialog;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class ChangeAssignmentButton extends Button implements ShiftSolverConstants {

	private Holder<String> employeeHolder;
	private RiaContext ctx;
	private List<Employee> employees;
	private ChangeAssignmentDialog dialog;
	private SolutionPanel periodSolutionPanel;
	
	public ChangeAssignmentButton(
			final Holder<String> employeeHolder,
			final SolutionPanel periodSolutionPanel,
			final int day,
			final int shift,
			final int role) 
	{
		this.employeeHolder=employeeHolder;
		this.periodSolutionPanel=periodSolutionPanel;
		this.employees=periodSolutionPanel.employees;
		this.ctx=periodSolutionPanel.ctx;
				
		String fullName;
		if(employeeHolder.get().equals(ShiftSolver.FERDA.getKey())) {
			fullName = "X";
		} else {
			fullName = periodSolutionPanel.e2a.get(employeeHolder.get()).employee.getFullName();			
		}
		setText(fullName);
		setTitle(fullName);
		
		switch(shift) {
		case SHIFT_MORNING:
		case SHIFT_MORNING_6:
		case SHIFT_MORNING_7:
		case SHIFT_MORNING_8:
			setStyleName("s2-solutionTableMorningB");
			break;
		case SHIFT_AFTERNOON:
			setStyleName("s2-solutionTableAfternoonB");
			break;
		case SHIFT_NIGHT:
			setStyleName("s2-solutionTableNightB");
			break;
		}
						
		addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				dialog=new ChangeAssignmentDialog(
						ctx, 
						ChangeAssignmentButton.this, 
						employees,
						day,
						shift,
						role,
						periodSolutionPanel.preferences,
						periodSolutionPanel.solution,
						periodSolutionPanel.e2a);				
			    dialog.setGlassEnabled(true);
			    dialog.setAnimationEnabled(true);
	            dialog.center();
	            dialog.show();			}
		});
	}

	public void setEmployee() {
		employeeHolder.set(dialog.getSelectedEmployee().getKey());
		String fullName = periodSolutionPanel.e2a.get(employeeHolder.get()).employee.getFullName();
		setText(fullName);
		setTitle(fullName);		
	}
}
