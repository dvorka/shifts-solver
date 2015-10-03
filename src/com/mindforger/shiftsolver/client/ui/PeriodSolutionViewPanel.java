package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodSolutionViewPanel extends FlexTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public PeriodSolutionViewPanel(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public void refresh(PeriodPreferences result) {
		if(result==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		// TODO clear and re-generate widget
		addRows(result);
	}
	
	private void addRows(PeriodPreferences result) {
		addTableTitle(result);
		
		if(result!=null) {
			for(Employee employee:result.getEmployeeToPreferences().keySet()) {
				addRow(employee, result.getEmployeeToPreferences().get(employee),result.getMonthDays());
			}			
		}
	}

	private void addTableTitle(PeriodPreferences result) {
		// TODO i18n
		HTML html = new HTML("Employee");
		html.setStyleName("mf-progressHtml");
		setWidget(0, 0, html);
		// TODO allow sorting the table by employee name
		// setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));

		for (int i = 1; i <= result.getMonthDays(); i++) {
			// TODO append Mon...Sun to the number; weekend to have different color
			html = new HTML(""+i);
			html.setStyleName("mf-progressHtml");
			setWidget(0, i, html);
		}				
	}
		
	public void addRow(Employee employee, EmployeePreferences employeePreferences, int monthDays) {
		int numRows = getRowCount();
				
		EmployeesTableToEmployeeButton button = new EmployeesTableToEmployeeButton(
				employee.getKey(),
				employee.getFullName(),
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
		setWidget(numRows, 0, button);
		
		VerticalPanel verticalPanel;
		for(int i=1; i<=monthDays; i++) {
			verticalPanel=new VerticalPanel();
			verticalPanel.add(new CheckBox("N/A")); // TODO i18n
			verticalPanel.add(new CheckBox("Vacations")); // TODO i18n
			verticalPanel.add(new CheckBox("Morning")); // TODO i18n
			verticalPanel.add(new CheckBox("Afternoon")); // TODO i18n
			verticalPanel.add(new CheckBox("Night")); // TODO i18n
			setWidget(numRows, i, verticalPanel);			
		}
	}

	public void removeRow() {
		int numRows = getRowCount();
		if (numRows > 1) {
			removeRow(numRows - 1);
			getFlexCellFormatter().setRowSpan(0, 1, numRows - 1);
		}
	}

	public void setSortingCriteria(TableSortCriteria criteria, boolean sortIsAscending) {
		this.sortCriteria=criteria;
		this.sortIsAscending=sortIsAscending;
	}

	public TableSortCriteria getSortingCriteria() {
		return sortCriteria;
	}

	public boolean isSortAscending() {
		return sortIsAscending;
	}
}