package com.mindforger.shiftsolver.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class PeriodPreferencesEditPanel extends FlexTable {

	private static final int YEAR=2015;
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;
	
	private ListBox yearListBox;
	private ListBox monthListBox;
	private FlexTable preferencesTable;
	private Map<String,CheckBox> checkboxes;
	
	private PeriodPreferences periodPreferences;

	private static final int CHECK_NA=1;
	private static final int CHECK_VACATIONS=2;
	private static final int CHECK_MORNING_6=3;
	private static final int CHECK_MORNING_7=4;
	private static final int CHECK_MORNING_8=5;
	private static final int CHECK_AFTERNOON=6;
	private static final int CHECK_NIGHT=7;
	
	public PeriodPreferencesEditPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.checkboxes=new HashMap<String,CheckBox>();
		
		FlowPanel buttonPanel = newButtonPanel(ctx);
		setWidget(0, 0, buttonPanel);
		
		FlowPanel datePanel = newDatePanel();
		setWidget(1, 0, datePanel);
		
		preferencesTable = newPreferencesTable();
		setWidget(2, 0, preferencesTable);
	}

	private FlowPanel newButtonPanel(final RiaContext ctx) {
		FlowPanel buttonPanel=new FlowPanel();

		Button solveButton=new Button("Solve"); // TODO i18n
		solveButton.setStyleName("mf-button");
		solveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(periodPreferences!=null) {		      		
		    		ctx.getStatusLine().showProgress(ctx.getI18n().solvingShifts());
		      		PeriodSolution solution = ctx.getSolver().solve(periodPreferences.getEmployeeToPreferences().keySet(), periodPreferences);		      		
		      		ctx.getSolutionViewPanel().refresh(solution);
		      		ctx.getRia().showSolutionViewPanel();
				}
			}
		});		
		buttonPanel.add(solveButton);
		
		Button saveButton=new Button("Save"); // TODO i18n
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(periodPreferences!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().savingEmployee());
		    		riaToObject();
		      		ctx.getRia().savePeriodPreferences(periodPreferences);
		      		ctx.getStatusLine().hideStatus();					
				}
			}
		});		
		buttonPanel.add(saveButton);
		Button cancelButton=new Button("Cancel"); // TODO i18n
		cancelButton.setStyleName("mf-buttonLooser");
		cancelButton.setTitle("Discard changes"); // TODO i18n
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(periodPreferences!=null) {
		      		ctx.getRia().showPeriodPreferencesTable();
				}
			}
		});		
		buttonPanel.add(cancelButton);
		Button deleteButton=new Button("Delete"); // TODO i18n
		deleteButton.setStyleName("mf-button");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(periodPreferences!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().deletingEmployee());
		      		ctx.getRia().deleteOrUpdatePeriodPreferences(periodPreferences, true);
		      		ctx.getStatusLine().hideStatus();					
				}
			}
		});		
		buttonPanel.add(deleteButton);
		
		return buttonPanel;
	}

	private FlowPanel newDatePanel() {
		FlowPanel flowPanel=new FlowPanel();
		
		yearListBox = new ListBox(false);
		for(int i=0; i<10; i++) {
			yearListBox.addItem(""+(YEAR+i));			
		}
		yearListBox.setTitle("Year"); // TODO i18n
		flowPanel.add(yearListBox);
		
		monthListBox = new ListBox(false);
		for(int i=1; i<=12; i++) {
			monthListBox.addItem(""+i);			
		}
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

	private void refreshPreferencesTable(FlexTable table, PeriodPreferences result) {
		ctx.getStatusLine().showInfo(i18n.buildingPeriodPreferences());

		table.removeAllRows();
		checkboxes.clear();
		
		if(result!=null && result.getEmployeeToPreferences()!=null && result.getEmployeeToPreferences().size()>0) {
			HTML html = new HTML("Employee"); // TODO i18n
			// TODO allow sorting the table by employee name
			// setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
			table.setWidget(0, 0, html);
			html = new HTML("Preferences"); // TODO i18n
			table.setWidget(0, 1, html);

			for(Employee employee:result.getEmployeeToPreferences().keySet()) {
				addEmployeeRow(
						preferencesTable,
						employee, 
						result.getEmployeeToPreferences().get(employee),
						result.getMonthDays());
			}						
		}		
		
		ctx.getStatusLine().hideStatus();		
	}
		
	public void addEmployeeRow(
			FlexTable table, 
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
		
		FlexTable employeePrefsTable=new FlexTable();
		
		for (int i = 1; i<=monthDays; i++) {
			// TODO append Mon...Sun to the number; weekend to have different color
			HTML html = new HTML(""+i+getDayLetter(i));
			//html.addStyleName("mf-progressHtml");
			employeePrefsTable.setWidget(0, i, html);				
			if(isWeekend(i)) {
				html.addStyleName("s2-weekendDay");
			}
		}

		// TODO consider encoding this as bits in int/long
		employeePrefsTable.setWidget(CHECK_NA, 0, new HTML("N/A"));
		employeePrefsTable.setWidget(CHECK_VACATIONS, 0, new HTML("Vacations"));
		employeePrefsTable.setWidget(CHECK_MORNING_6, 0, new HTML("Morning&nbsp;6"));
		employeePrefsTable.setWidget(CHECK_MORNING_7, 0, new HTML("Morning&nbsp;7"));
		employeePrefsTable.setWidget(CHECK_MORNING_8, 0, new HTML("Morning&nbsp;8"));
		employeePrefsTable.setWidget(CHECK_AFTERNOON, 0, new HTML("Afternoon"));
		employeePrefsTable.setWidget(CHECK_NIGHT, 0, new HTML("Night"));
		
		for(int c=1; c<=monthDays; c++) {
			for(int r=1; r<=7; r++) {
				CheckBox checkbox = new CheckBox();
				employeePrefsTable.setWidget(r, c, checkbox);
				// TODO make this composite key
				checkboxes.put(employee.getKey()+"#"+c+"#"+r,checkbox);
			}
		}
		
		table.setWidget(numRows, 1, employeePrefsTable);		
	}

	private boolean isWeekend(int i) {
		return "S".equals(getDayLetter(i));
	}

	private static final String[] WEEKDAY_LETTERS = {
		"S", "M", "T", "W", "T", "F", "S"
	};
	
	private String getDayLetter(int i) {
		int startWeekDay = periodPreferences.getStartWeekDay();
		return WEEKDAY_LETTERS[(i-1+startWeekDay-1)%7];
	}

	public void refresh(PeriodPreferences result) {
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
	
	private void objectToRia(PeriodPreferences periodPreferences) {
		this.periodPreferences=periodPreferences;
		
		yearListBox.setSelectedIndex(periodPreferences.getYear()-YEAR);
		monthListBox.setSelectedIndex(periodPreferences.getMonth()-1);
		
		refreshPreferencesTable(preferencesTable, periodPreferences);
		// TODO insert preferences
	}

	private void riaToObject() {
		if(periodPreferences!=null) {
			periodPreferences.setYear(Integer.parseInt(yearListBox.getValue(yearListBox.getSelectedIndex())));
			periodPreferences.setMonth(Integer.parseInt(monthListBox.getValue(monthListBox.getSelectedIndex())));
		}
		
		// TODO preferences
//		for(String key:checkboxes.keySet()) {
//			if(checkboxes.get(key).getValue()) {
//				DayPreference dayPreference = new DayPreference();
//				dayPreference.setYear(year);
//				dayPreference.setMonth(month);
//				dayPreference.setDay(day);
//				
//				int preferenceType=0;
//				switch(preferenceType) {
//				case CHECK_NA:
//					dayPreference.setNoDay(true);
//					break;
//				case CHECK_VACATIONS:
//					dayPreference.setNoDay(true);
//					break;
//				case CHECK_MORNING:
//					dayPreference.setNoMorning(true);
//					break;
//				case CHECK_AFTERNOON:
//					dayPreference.setNoAfternoon(true);
//					break;
//				case CHECK_NIGHT:
//					dayPreference.setNoNight(true);
//				default:
//					break;
//				}
//			}
//		}
	}	
}
