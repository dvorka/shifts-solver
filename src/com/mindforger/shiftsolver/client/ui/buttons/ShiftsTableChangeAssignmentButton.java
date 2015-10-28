package com.mindforger.shiftsolver.client.ui.buttons;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.PeriodSolutionViewPanel;
import com.mindforger.shiftsolver.client.ui.dialogs.ChangeAssignmentDialog;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class ShiftsTableChangeAssignmentButton extends Button implements ShiftSolverConstants {

	private Holder<Employee> employeeHolder;
	private RiaContext ctx;
	private RiaMessages i18n;
	private List<Employee> employees;
	private ListBox employeesListBox;
	private PeriodSolutionViewPanel periodSolutionPanel;
	private ChangeAssignmentDialog dialog;
	
	public ShiftsTableChangeAssignmentButton(
			Holder<Employee> employeeHolder,
			PeriodSolutionViewPanel periodSolutionPanel, 
			String style) 
	{
		this.employeeHolder=employeeHolder;
		this.employees=periodSolutionPanel.employees;
		this.ctx=periodSolutionPanel.ctx;
		this.periodSolutionPanel=periodSolutionPanel;
		this.i18n=ctx.getI18n();
				
		setText(employeeHolder.get().getFullName());
		setTitle(employeeHolder.get().getFullName());
		setStyleName(style);
		
		this.dialog=new ChangeAssignmentDialog(ctx, this, employees);
		
		addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
			    dialog.setGlassEnabled(true);
			    dialog.setAnimationEnabled(true);
	            dialog.center();
	            dialog.show();			}
		});
	}

	public void setEmployee() {
		// TODO recalculate employee allocations (unassign & assign)
		employeeHolder.set(dialog.getSelectedEmployee());
		setText(employeeHolder.get().getFullName());
		setTitle(employeeHolder.get().getFullName());		
	}
}
