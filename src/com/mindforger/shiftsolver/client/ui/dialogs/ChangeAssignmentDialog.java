package com.mindforger.shiftsolver.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.EmployeeAvailability;
import com.mindforger.shiftsolver.client.ui.buttons.ChangeAssignmentButton;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class ChangeAssignmentDialog extends DialogBox {

	private RiaMessages i18n;

	private static final String EMPLOYEES_RADIO_GROUP="employees";
	private List<RadioButton> radios;
	private List<Employee> employees;
	
	public ChangeAssignmentDialog(
			final RiaContext ctx,
			final ChangeAssignmentButton trigger,
			final List<Employee> employees,
			int day,
			int shift,
			int role,
			PeriodPreferences p,
			PeriodSolution s,
			Map<String,EmployeeAllocation> a) 
	{
		this.i18n=ctx.getI18n();
		this.employees=employees;
		
		setText(i18n.assignEmployee());

		FlexTable panel=new FlexTable();
		panel.getFlexCellFormatter().setColSpan(0, 0, 2);
		setWidget(panel);
		
		FlexTable table=new FlexTable();
		panel.setWidget(0, 0, table);
		HTML html;
		table.setWidget(0, 0, new HTML(i18n.employee()));
		html=new HTML();
		html.setTitle(i18n.freeHolidaysBusyWant());
		table.setWidget(0, 1, new HTML(" "));
		html=new HTML();
		html.setTitle(i18n.canEmployeeBeAssignedToThisDay());
		table.setWidget(0, 2, new HTML(i18n.validationResult()));

		radios = new ArrayList<RadioButton>();
		int r=1;
		EmployeeAvailability employeeAvailability;
		for(Employee e:employees) {
			employeeAvailability = ctx.getSolver().getAvailabilityStringForEmployee(
					e,
					day,
					shift,
					role,
					p,
					s,
					a);

			RadioButton radio=new RadioButton(EMPLOYEES_RADIO_GROUP, e.getFullName());
			radios.add(radio);
			table.setWidget(r, 0, radio);
			html=new HTML("&nbsp;&nbsp;&nbsp;");
			if(employeeAvailability.isNotBusy()) {
				html.setStyleName("s2-3stateYes");				
			} else {
				html.setStyleName("s2-3stateNo");				
			}
			table.setWidget(r, 1, html);
			html=new HTML(employeeAvailability.toString());
			if(!employeeAvailability.isAvailable()) {
				html.setStyleName("s2-busy");
			}
			table.setWidget(r, 2, html);
			r++;
		}
		
		Button changeButton = new Button(i18n.save(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				trigger.setEmployee();
				ctx.getSolutionPanel().onSolutionModification();
				hide();
			}
		});
		panel.setWidget(1, 0, changeButton);

		Button closeButton = new Button(i18n.cancel(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		panel.setWidget(1, 1, closeButton);		
	}

	public Employee getSelectedEmployee() {
		int i=0;
		for(RadioButton rb:radios) {
			if(rb.getValue()) {
				return employees.get(i);
			}
			i++;
		}
		return null;
	}
}
