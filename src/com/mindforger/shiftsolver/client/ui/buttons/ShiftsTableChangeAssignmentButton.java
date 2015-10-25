package com.mindforger.shiftsolver.client.ui.buttons;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.PeriodSolutionViewPanel;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.Holder;

public class ShiftsTableChangeAssignmentButton extends Button {

	private Holder<Employee> employeeHolder;
	private RiaContext ctx;
	private RiaMessages i18n;
	private List<Employee> employees;
	private ListBox employeesListBox;
	private PeriodSolutionViewPanel periodSolutionPanel;
	
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
		
		addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
			    final DialogBox dialogBox = createDialogBox();
			    dialogBox.setGlassEnabled(true);
			    dialogBox.setAnimationEnabled(true);
	            dialogBox.center();
	            dialogBox.show();			}
		});
	}

	private DialogBox createDialogBox() {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Choose New Employee");

		FlexTable table=new FlexTable();
		dialogBox.setWidget(table);

		table.setWidget(0, 0, new HTML(i18n.employee()));

		employeesListBox = new ListBox();
		for(Employee e:employees) {
			employeesListBox.addItem(e.getFullName(), e.getKey());
		}
		table.setWidget(0, 1, employeesListBox);
		
		Button changeButton = new Button(i18n.save(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				setEmployee();
				periodSolutionPanel.onSolutionModification();
				dialogBox.hide();
			}
		});
		table.setWidget(1, 0, changeButton);

		Button closeButton = new Button(i18n.cancel(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		table.setWidget(1, 1, closeButton);
		
		return dialogBox;
	}
	
	public void setEmployee() {
		// TODO recalculate employee allocations (unassign & assign)
		employeeHolder.set(employees.get(employeesListBox.getSelectedIndex()));
		setText(employeeHolder.get().getFullName());
		setTitle(employeeHolder.get().getFullName());		
	}
}
