package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class PeriodSolutionViewPanel extends FlexTable {

	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;
	
	private TextBox yearListBox;
	private TextBox monthListBox;
	private FlexTable preferencesTable;
	
	public PeriodSolutionViewPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		
		FlowPanel buttonPanel = newButtonPanel(ctx);
		setWidget(0, 0, buttonPanel);
		
		FlowPanel datePanel = newDatePanel();
		setWidget(1, 0, datePanel);
		
		preferencesTable = newPreferencesTable();
		setWidget(2, 0, preferencesTable);
	}

	private FlowPanel newButtonPanel(final RiaContext ctx) {
		FlowPanel buttonPanel=new FlowPanel();

		Button solveButton=new Button("Next Solution"); // TODO i18n
		solveButton.setStyleName("mf-button");
		solveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO count next solution
			}
		});		
		buttonPanel.add(solveButton);
		
		Button saveButton=new Button("Save"); // TODO i18n
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO save solution
//				if(periodPreferences!=null) {
//		    		ctx.getStatusLine().showProgress(ctx.getI18n().savingEmployee());
//		    		riaToObject();
//		      		ctx.getRia().savePeriodPreferences(periodPreferences);
//		      		ctx.getStatusLine().hideStatus();					
//				}
			}
		});		
		buttonPanel.add(saveButton);
		Button cancelButton=new Button("Cancel"); // TODO i18n
		cancelButton.setStyleName("mf-buttonLooser");
		cancelButton.setTitle("Discard changes"); // TODO i18n
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getRia().showSolutionsTable();
			}
		});		
		buttonPanel.add(cancelButton);
		
		return buttonPanel;
	}

	private FlowPanel newDatePanel() {
		FlowPanel flowPanel=new FlowPanel();
		
		yearListBox = new TextBox();
		yearListBox.setEnabled(false);
		yearListBox.setTitle("Year"); // TODO i18n
		flowPanel.add(yearListBox);
		
		monthListBox = new TextBox();
		monthListBox.setEnabled(false);
		monthListBox.setTitle("Month"); // TODO i18n
		flowPanel.add(monthListBox);
		
		return flowPanel;
	}
	
	private FlexTable newPreferencesTable() {
		FlexTable table = new FlexTable();
		table.setStyleName("mf-growsTable");

		table.removeAllRows();
				
		return table;		
	}

	private void refreshPreferencesTable(FlexTable table, PeriodSolution solution) {
		ctx.getStatusLine().showInfo(i18n.buildingPeriodPreferences());

		table.removeAllRows();
		
		if(solution!=null && solution.getDays()!=null && solution.getDays().size()>0) {
			HTML html = new HTML("Employee"); // TODO i18n
			// TODO allow sorting the table by employee name
			// setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
			table.setWidget(0, 0, html);
			html = new HTML("Shifts"); // TODO i18n
			table.setWidget(0, 1, html);

			PeriodPreferences preferences = ctx.getState().getPeriodPreferences(solution.getDlouhanKey());
			
			for(Employee employee:preferences.getEmployeeToPreferences().keySet()) {
				addEmployeeRow(
						preferencesTable,
						solution,
						preferences,
						employee, 
						preferences.getEmployeeToPreferences().get(employee),
						preferences.getMonthDays());
			}						
		}		
		
		ctx.getStatusLine().hideStatus();		
	}
		
	public void addEmployeeRow(
			FlexTable table, 
			PeriodSolution solution, 
			PeriodPreferences preferences, 
			Employee employee, 
			EmployeePreferences employeePreferences, 
			int monthDays) 
	{
		int numRows = table.getRowCount();		
		EmployeesTableToEmployeeButton button = new EmployeesTableToEmployeeButton(
				employee.getKey(),
				employee.getFullName(),
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
		table.setWidget(numRows, 0, button);
				
		for (int i = 0; i<monthDays; i++) {
			// TODO append Mon...Sun to the number; weekend to have different color
			HTML html = new HTML(""+(i+1));
			//html.addStyleName("mf-progressHtml");
			table.setWidget(0, i+1, html);
		}
				
		HTML html;
		for(int c=0; c<monthDays; c++) {
			if(solution.getDays().get(c+1).isEmployeeAllocated(employee.getKey())) {
				switch(solution.getDays().get(c+1).getShiftTypeForEmployee(employee.getKey())) {
				case ShiftSolverConstants.SHIFT_MORNING:
					html = new HTML("M");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift");
					table.setWidget(numRows, c+1, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_6:
					html = new HTML("6");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 6am");
					table.setWidget(numRows, c+1, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_7:
					html = new HTML("7");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 7am");
					table.setWidget(numRows, c+1, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_8:
					html = new HTML("8");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 8am");
					table.setWidget(numRows, c+1, html);
					break;
				case ShiftSolverConstants.SHIFT_AFTERNOON:
					html = new HTML("A");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_AFTERNOON);
					html.setTitle("Afternoon shift");
					table.setWidget(numRows, c+1, html);
					break;
				case ShiftSolverConstants.SHIFT_NIGHT:
					html = new HTML("N");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_NIGHT);
					html.setTitle("Night shift");
					table.setWidget(numRows, c+1, html);
					break;
				}				
			} else {
				// TODO determine whether employee is NA, holidays, ... and render if needed
				
//				html = new HTML("N");
//				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_NA);
//				html.setTitle("N/A");
//				table.setWidget(numRows, c+1, html);
//				break;
//				
//				html = new HTML("V");
//				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_VACATIONS);
//				html.setTitle("Vacations");
//				table.setWidget(numRows, c+1, html);
//				break;
				
				html = new HTML("F");
				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_FREE);
				html.setTitle("Free day");
				table.setWidget(numRows, c+1, html);				
			}			
		}		
	}

	public void refresh(PeriodSolution result) {
		if(result==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}
		
		objectToRia(result);
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
	
	private void objectToRia(PeriodSolution periodSolution) {		
		yearListBox.setText(""+periodSolution.getYear());
		monthListBox.setText(""+periodSolution.getMonth());
		
		refreshPreferencesTable(preferencesTable, periodSolution);
	}

	private void riaToObject() {
		// VIEW only for now
	}	
}
