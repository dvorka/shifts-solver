package com.mindforger.shiftsolver.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.PublicHolidays;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.client.ui.buttons.ChangeAssignmentButton;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class PeriodSolutionPanel extends FlexTable implements ShiftSolverConstants {

	private RiaMessages i18n;
	public RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	private PublicHolidays publicHolidays;
	public PeriodPreferences preferences;
	public PeriodSolution solution;
	public List<EmployeeAllocation> allocations;
	public Map<String,EmployeeAllocation> e2a;
	public List<Employee> employees;
	
	private HTML yearMonthHtml;
	private FlexTable scheduleTable;
	private FlexTable shiftsTable;
	private SolverNoSolutionPanel allocationsTable;
	private Button scheduleButton;
	private Button shiftsButton;
	private Button allocationButton;
	private Button validateButton;
	
	public PeriodSolutionPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.publicHolidays=new PublicHolidays();
		
		FlowPanel buttonPanel = newButtonPanel(ctx);
		setWidget(0, 0, buttonPanel);
		getFlexCellFormatter().setColSpan(0, 0, 5);
		
		FlowPanel datePanel = newDatePanel();
		setWidget(1, 0, datePanel);
		getFlexCellFormatter().setColSpan(1, 0, 5);
		
		scheduleTable = newScheduleTable();
		setWidget(2, 0, scheduleTable);
		
		shiftsTable = newShiftsTable();
		setWidget(3, 0, shiftsTable);
		
		allocationsTable = new SolverNoSolutionPanel(ctx, false);
		allocationsTable.init();
		setWidget(4, 0, allocationsTable);
	}
	
	private void showScheduleTable() {
		scheduleTable.setVisible(true);
		shiftsTable.setVisible(false);
		validateButton.setVisible(false);
		allocationsTable.setVisible(false);
		shiftsButton.setVisible(true);
		scheduleButton.setVisible(false);
		allocationButton.setVisible(true);
	}

	private void showShiftsTable() {
		scheduleTable.setVisible(false);
		shiftsTable.setVisible(true);
		validateButton.setVisible(true);
		allocationsTable.setVisible(false);
		shiftsButton.setVisible(false);
		scheduleButton.setVisible(true);
		allocationButton.setVisible(true);
	}

	private void showAllocationsTable() {
		scheduleTable.setVisible(false);
		validateButton.setVisible(false);
		shiftsTable.setVisible(false);
		allocationsTable.setVisible(true);
		shiftsButton.setVisible(true);
		scheduleButton.setVisible(true);
		allocationButton.setVisible(false);
	}
	
	private FlowPanel newButtonPanel(final RiaContext ctx) {
		FlowPanel buttonPanel=new FlowPanel();
		
		Button saveButton=new Button(i18n.save());
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO save solution
			}
		});		
		buttonPanel.add(saveButton);

		scheduleButton = new Button(i18n.schedule());
		scheduleButton.setStyleName("mf-buttonLooser");
		scheduleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showScheduleTable();
				ctx.getStatusLine().clear();
			}
		});		
		buttonPanel.add(scheduleButton);

		shiftsButton = new Button(i18n.shifts());
		shiftsButton.setStyleName("mf-button");
		shiftsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showShiftsTable();
				ctx.getStatusLine().clear();
			}
		});		
		buttonPanel.add(shiftsButton);
		
		allocationButton = new Button(i18n.employeesAllocation());
		allocationButton.setStyleName("mf-buttonLooser");
		allocationButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showAllocationsTable();
				ctx.getStatusLine().clear();
			}
		});		
		buttonPanel.add(allocationButton);

		validateButton = new Button(i18n.validate());
		validateButton.setStyleName("mf-buttonLooser");
		validateButton.setTitle("Validate solution on shifts schedule ");
		validateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getStatusLine().clear();
				// TODO
			}
		});		
		buttonPanel.add(validateButton);
		
		Button backButton=new Button(i18n.periodPreferences());
		backButton.setStyleName("mf-buttonLooser");
		backButton.setTitle("Show period preferences");
		backButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getStatusLine().clear();
				ctx.getRia().showPeriodPreferencesEditPanel();
			}
		});		
		buttonPanel.add(backButton);
		
		Button cancelButton=new Button(i18n.cancel());
		cancelButton.setStyleName("mf-buttonLooser");
		cancelButton.setTitle(i18n.discardChanges());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getRia().showHome();
				ctx.getStatusLine().clear();
			}
		});		
		buttonPanel.add(cancelButton);
		
		return buttonPanel;
	}

	private FlowPanel newDatePanel() {
		FlowPanel flowPanel=new FlowPanel();		
		yearMonthHtml = new HTML();
		yearMonthHtml.setTitle(i18n.year());
		flowPanel.add(yearMonthHtml);
		return flowPanel;
	}
	
	private FlexTable newScheduleTable() {
		FlexTable table = new FlexTable();
		table.setStyleName("s2-scheduleTable");
		table.removeAllRows();				
		return table;		
	}

	private FlexTable newShiftsTable() {
		FlexTable table = new FlexTable();
		table.setStyleName("s2-solutionTable");
		table.removeAllRows();
		return table;		
	}
	
	private void refreshScheduleTable() {		
		scheduleTable.removeAllRows();
				
		if(solution!=null && solution.getDays()!=null && solution.getDays().size()>0) {
			Button button = new Button(i18n.employee());
			button.setStyleName("s2-tableHeadColumnButton");
			scheduleTable.setWidget(0, 0, button);
			button = new Button(i18n.job());
			button.setStyleName("s2-tableHeadColumnButton");
			scheduleTable.setWidget(0, 1, button);			
			button = new Button(i18n.shifts());
			button.setStyleName("s2-tableHeadColumnButton");
			scheduleTable.setWidget(0, 2, button);
			
			for(Employee employee:ctx.getState().getEmployees()) {
				addEmployeeRow(
						scheduleTable,
						solution,
						preferences,
						employee, 
						preferences.getEmployeeToPreferences().get(employee),
						preferences.getMonthDays());
			}						
		}		
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
				employee.getFirstname()+"&nbsp;"+employee.getFamilyname(),
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);		
		button.setTitle(
				employee.getFullName()
				+" - "
				+(employee.isFulltime()?"fulltime ":"")
				+(employee.isSportak()?"sportak ":"")
				+(employee.isEditor()?"editor ":"")
				+(employee.isMortak()?"mortak":""));
		table.setWidget(numRows, 0, button);
		
		int shiftsAssigned = solution.getEmployeeJobs().get(employee.getKey()).shifts;
		int shiftsLimit = solution.getEmployeeJobs().get(employee.getKey()).shiftsLimit;
		HTML jobHtml = new HTML(shiftsAssigned+"/"+shiftsLimit);
		if(shiftsAssigned<shiftsLimit) {
			if(employee.isFulltime()) {
				jobHtml.addStyleName("s2-fulltimeNotFull");				
			} else {
				jobHtml.addStyleName("s2-partimeNotFull");								
			}
		}
		table.setWidget(numRows, 1, jobHtml); 
				
		for (int i = 0; i<monthDays; i++) {
			// TODO i18n
			Button b = new Button(""+(i+1)+Utils.getDayLetter(i+1, preferences.getStartWeekDay()));
			// TODO add Mon...Sun to title
			b.setStyleName("s2-tableHeadColumnButton");
			if(Utils.isWeekend(i+1, preferences.getStartWeekDay())
					|| publicHolidays.isHolidays(
							preferences.getYear(), 
							preferences.getMonth(), 
							i)) 
			{
				b.addStyleName("s2-weekendDay");
			}
			table.setWidget(0, i+2, b);
		}
						
		HTML html;
		for(int c=0; c<monthDays; c++) {
			if(solution.getDays().get(c).isEmployeeAllocatedToday(employee.getKey())) {
				switch(solution.getDays().get(c).getShiftTypeForEmployee(employee.getKey())) {
				case ShiftSolverConstants.SHIFT_MORNING:
					html = new HTML("&nbsp;M");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					// TODO i18n
					html.setTitle("Morning shift");
					table.setWidget(numRows, c+2, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_6:
					html = new HTML("&nbsp;6");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 6am");
					table.setWidget(numRows, c+2, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_7:
					html = new HTML("&nbsp;7");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 7am");
					table.setWidget(numRows, c+2, html);
					break;
				case ShiftSolverConstants.SHIFT_MORNING_8:
					html = new HTML("&nbsp;8");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
					html.setTitle("Morning shift 8am");
					table.setWidget(numRows, c+2, html);
					break;
				case ShiftSolverConstants.SHIFT_AFTERNOON:
					html = new HTML("&nbsp;A");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_AFTERNOON);
					html.setTitle("Afternoon shift");
					table.setWidget(numRows, c+2, html);
					break;
				case ShiftSolverConstants.SHIFT_NIGHT:
					html = new HTML("&nbsp;N");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_NIGHT);
					html.setTitle("Night shift");
					table.setWidget(numRows, c+2, html);
					break;
				}				
			} else {
				// TODO determine whether employee is NA, holidays, ... and render if needed
				
//				html = new HTML("N");
//				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_NA);
//				html.setTitle("N/A");
//				table.setWidget(numRows, c+2, html);
//				break;
//				
//				html = new HTML("V");
//				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_VACATIONS);
//				html.setTitle("Vacations");
//				table.setWidget(numRows, c+2, html);
//				break;
				
				html = new HTML(" "); // F
				html.setStyleName(ShiftSolverConstants.CSS_SHIFT_FREE);
				html.setTitle("Free day");
				table.setWidget(numRows, c+2, html);				
			}			
		}		
	}

	private void refreshAllocationsTable() {
		allocationsTable.refresh(allocations, 100, preferences);
	}
	
	private void refreshShiftsTable() {
		shiftsTable.removeAllRows();

		if(solution!=null && solution.getDays()!=null && solution.getDays().size()>0) {		

			// table title
			int row=0;		
			shiftsTable.setWidget(row, 0, new HTML("Shifts"));
			shiftsTable.setWidget(row, 1, new HTML("Monday"));
			shiftsTable.setWidget(row, 2, new HTML("Tuesday"));
			shiftsTable.setWidget(row, 3, new HTML("Wednesday"));
			shiftsTable.setWidget(row, 4, new HTML("Thursday"));
			shiftsTable.setWidget(row, 5, new HTML("Friday"));
			shiftsTable.setWidget(row, 6, new HTML("Shifts"));
			shiftsTable.setWidget(row, 7, new HTML("Saturday"));
			shiftsTable.setWidget(row, 8, new HTML("Sunday"));
			for(int ii=0; ii<=8; ii++) {
				shiftsTable.getCellFormatter().setStyleName(row, ii, "s2-solutionTableBlack");				
			}

			// week
			int columnOf1stDay=preferences.getStartWeekDay()==0?1:(8-preferences.getStartWeekDay());
			int c;
			int day=1, oldDay;

			int weeks=6;
			int nextWeekRow;
			for(int w=1; w<=weeks && day<=preferences.getMonthDays(); w++) {
				// week title
				c=1;
				row++;
				oldDay=day;
				shiftsTable.setWidget(row, 0, new HTML("Week"));
				// TODO this condition is broken - no number is shown - fix it
				shiftsTable.setWidget(row, 1, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 2, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 3, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 4, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 5, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 6, new HTML(" "));
				shiftsTable.setWidget(row, 7, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				shiftsTable.setWidget(row, 8, new HTML(c>=columnOf1stDay && day<=preferences.getMonthDays()?""+day++:""));
				for(int ii=0; ii<=8; ii++) {
					shiftsTable.getCellFormatter().setStyleName(row, ii, "s2-solutionTableYellow");				
				}
				day=oldDay;			

				// week body
				int r=++row;
				for(int ii=0; ii<13; ii++) {
					shiftsTable.getCellFormatter().setStyleName(r+ii, 0, "s2-solutionTableBlack");				
				}
				shiftsTable.setWidget(r++, 0, new HTML("Morning Editor<BR>6:00-14:00"));
				shiftsTable.setWidget(r++, 0, new HTML("Morning Staffer<BR>6:00-14:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Morning Staffer<BR>7:00-15:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Morning Staffer<BR>7:00-15:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Morning Staffer<BR>8:00-16:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Morning Sportak<BR>7:00-15:30"));		
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Editor<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Staffer<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Staffer<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Staffer<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Staffer<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Afternoon Sportak<BR>15:00-23:30"));
				shiftsTable.setWidget(r++, 0, new HTML("Night Staffer<BR>22:00-6:30"));
				nextWeekRow=r;

				r=row;
				int cc;
				for(cc=columnOf1stDay; cc<=5 && day<=preferences.getMonthDays(); cc++, day++) {
					if(day>0) {
						DaySolution ds=solution.getSolutionForDay(day);
						if(ds!=null) {
							if(ds.isWorkday()) {
								shiftsTable.setWidget(r-1+1, cc, new ChangeAssignmentButton(ds.getWorkdayMorningShift().editor, this, day, SHIFT_MORNING, ROLE_EDITOR));
								shiftsTable.setWidget(r-1+2, cc, new ChangeAssignmentButton(ds.getWorkdayMorningShift().staffer6am, this, day, SHIFT_MORNING_6, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+3, cc, new ChangeAssignmentButton(ds.getWorkdayMorningShift().staffer7am, this, day, SHIFT_MORNING_7, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+4, cc, new ChangeAssignmentButton(ds.getWorkdayMorningShift().staffer8am1, this, day, SHIFT_MORNING_8, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+5, cc, new HTML("MISSING"));
								shiftsTable.setWidget(r-1+6, cc, new ChangeAssignmentButton(ds.getWorkdayMorningShift().sportak, this, day, SHIFT_MORNING, ROLE_SPORTAK));
								shiftsTable.setWidget(r-1+7, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().editor, this, day, SHIFT_AFTERNOON, ROLE_EDITOR));
								shiftsTable.setWidget(r-1+8, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().staffers[0], this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+9, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().staffers[1], this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+10, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().staffers[2], this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+11, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().staffers[3], this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+12, cc, new ChangeAssignmentButton(ds.getWorkdayAfternoonShift().sportak, this, day, SHIFT_AFTERNOON, ROLE_SPORTAK));
								shiftsTable.setWidget(r-1+13, cc, new ChangeAssignmentButton(ds.getNightShift().staffer, this, day, SHIFT_NIGHT, ROLE_STAFFER));										
							} else {
								shiftsTable.setWidget(r-1+1, cc, new ChangeAssignmentButton(ds.getWeekendMorningShift().editor, this, day, SHIFT_MORNING, ROLE_EDITOR));
								shiftsTable.setWidget(r-1+2, cc, new ChangeAssignmentButton(ds.getWeekendMorningShift().staffer6am, this, day, SHIFT_MORNING_6, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+3, cc, new HTML(""));
								shiftsTable.setWidget(r-1+4, cc, new HTML(""));
								shiftsTable.setWidget(r-1+5, cc, new HTML(""));
								shiftsTable.setWidget(r-1+6, cc, new ChangeAssignmentButton(ds.getWeekendMorningShift().sportak, this, day, SHIFT_MORNING_6, ROLE_SPORTAK));
								shiftsTable.setWidget(r-1+7, cc, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().editor, this, day, SHIFT_AFTERNOON, ROLE_EDITOR));
								shiftsTable.setWidget(r-1+8, cc, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().staffer, this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
								shiftsTable.setWidget(r-1+9, cc, new HTML(""));
								shiftsTable.setWidget(r-1+10, cc, new HTML(""));
								shiftsTable.setWidget(r-1+11, cc, new HTML(""));
								shiftsTable.setWidget(r-1+12, cc, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().sportak, this, day, SHIFT_AFTERNOON, ROLE_SPORTAK));
								shiftsTable.setWidget(r-1+13, cc, new ChangeAssignmentButton(ds.getNightShift().staffer, this, day, SHIFT_NIGHT, ROLE_STAFFER));
							}
							for(int ii=1; ii<=6; ii++) shiftsTable.getCellFormatter().setStyleName(r-1+ii, cc, "s2-solutionTableMorning");				
							for(int ii=7; ii<=12; ii++) shiftsTable.getCellFormatter().setStyleName(r-1+ii, cc, "s2-solutionTableAfternoon");				
							shiftsTable.getCellFormatter().setStyleName(r-1+13, cc, "s2-solutionTableNight");				
						}
					}
					columnOf1stDay=6;
				}

				r=row;
				for(int ii=0; ii<7; ii++) {
					shiftsTable.getCellFormatter().setStyleName(r+ii, 6, "s2-solutionTableBlack");				
				}
				shiftsTable.setWidget(r++, 6, new HTML("Morning Editor<BR>6:00-14:00"));
				shiftsTable.setWidget(r++, 6, new HTML("Morning Staffer<BR>6:00-14:30"));
				shiftsTable.setWidget(r++, 6, new HTML("Morning Sportak<BR>7:00-15:30"));
				shiftsTable.setWidget(r++, 6, new HTML("Afternoon Editor<BR>14:00-20:30"));
				shiftsTable.setWidget(r++, 6, new HTML("Afternoon Staffer<BR>14:00-22:30"));
				shiftsTable.setWidget(r++, 6, new HTML("Afternoon Sportak<BR>15:00-23:30"));
				shiftsTable.setWidget(r++, 6, new HTML("Nigt Staffer<BR>22:00-6:30"));

				r=row;

				for(cc=columnOf1stDay; cc<=7 && day<=preferences.getMonthDays(); cc++, day++) {
					if(day>0) {
						DaySolution ds=solution.getSolutionForDay(day);
						if(ds!=null) {
							shiftsTable.setWidget(r+1-1, cc+1, new ChangeAssignmentButton(ds.getWeekendMorningShift().editor, this, day, SHIFT_MORNING, ROLE_EDITOR));
							shiftsTable.setWidget(r+2-1, cc+1, new ChangeAssignmentButton(ds.getWeekendMorningShift().staffer6am, this, day, SHIFT_MORNING, ROLE_STAFFER));
							shiftsTable.setWidget(r+3-1, cc+1, new ChangeAssignmentButton(ds.getWeekendMorningShift().sportak, this, day, SHIFT_MORNING, ROLE_SPORTAK));
							for(int ii=1; ii<=3; ii++) shiftsTable.getCellFormatter().setStyleName(r+ii-1, cc+1, "s2-solutionTableMorning");							
							shiftsTable.setWidget(r+4-1, cc+1, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().editor, this, day, SHIFT_AFTERNOON, ROLE_EDITOR));
							shiftsTable.setWidget(r+5-1, cc+1, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().staffer, this, day, SHIFT_AFTERNOON, ROLE_STAFFER));
							shiftsTable.setWidget(r+6-1, cc+1, new ChangeAssignmentButton(ds.getWeekendAfternoonShift().sportak, this, day, SHIFT_AFTERNOON, ROLE_SPORTAK));
							for(int ii=4; ii<=6; ii++) shiftsTable.getCellFormatter().setStyleName(r+ii-1, cc+1, "s2-solutionTableAfternoon");
							shiftsTable.setWidget(r+7-1, cc+1, new ChangeAssignmentButton(ds.getNightShift().staffer, this, day, SHIFT_NIGHT, ROLE_STAFFER));
							shiftsTable.getCellFormatter().setStyleName(r+7-1, cc+1, "s2-solutionTableNight");				
						}
					}
				}

				columnOf1stDay=1;
				row=nextWeekRow-1;
			}
			for(row=0; row<shiftsTable.getRowCount(); row++) {
				for(int column=0; column<=8; column++) {
					try {
						if(shiftsTable.getWidget(row, column)==null) {
							shiftsTable.setWidget(row, column, new HTML(" "));
						}
					} catch(IndexOutOfBoundsException e) {
						shiftsTable.setWidget(row, column, new HTML(" "));					
					}
				}
			}

		}
	}

	public void refresh(PeriodSolution solution, List<EmployeeAllocation> allocations) {
		if(solution==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		this.solution=solution;
		this.allocations=allocations;
		e2a=new HashMap<String, EmployeeAllocation>();
		employees=new ArrayList<Employee>();
		for(EmployeeAllocation ea:allocations) {
			e2a.put(ea.employee.getKey(), ea);
			employees.add(ea.employee);		
		}
		this.preferences = ctx.getState().getPeriodPreferences(solution.getDlouhanKey());
		
		objectToRia();
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
	
	private void objectToRia() {
		if(solution!=null) {
			yearMonthHtml.setHTML("Solution for: "+solution.getYear()+"/"+solution.getMonth());
			yearMonthHtml.setVisible(true);
			refreshScheduleTable();
			refreshShiftsTable();
			refreshAllocationsTable();
			showShiftsTable();
			validateButton.setVisible(true);
		} else {
			yearMonthHtml.setVisible(false);
			scheduleTable.setVisible(false);
			shiftsTable.setVisible(false);
			allocationsTable.setVisible(false);
			shiftsButton.setVisible(false);
			scheduleButton.setVisible(false);
			allocationButton.setVisible(false);
			validateButton.setVisible(false);
		}
	}
	
	public void onSolutionModification() {
		refreshScheduleTable();
		// TODO consider dirty flag to recalculate on change, otherwise just make visible
		allocations=EmployeeAllocation.calculateEmployeeAllocations(preferences, solution, employees);
		e2a.clear();
		for(EmployeeAllocation ea:allocations) {
			e2a.put(ea.employee.getKey(), ea);
		}
		refreshAllocationsTable();
	}
}
