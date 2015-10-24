package com.mindforger.shiftsolver.client.ui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.PublicHolidays;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class PeriodSolutionViewPanel extends FlexTable {

	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	private PublicHolidays publicHolidays;
	private PeriodSolution solution;
	
	private HTML yearMonthHtml;
	private FlexTable scheduleTable;
	private FlexTable shiftsTable;
	private FlexTable allocationsTable;
	private PeriodPreferences preferences;
	
	
	public PeriodSolutionViewPanel(final RiaContext ctx) {
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
	}
	
	private void showScheduleTable() {
		scheduleTable.setVisible(true);
		shiftsTable.setVisible(false);
	}

	private void showShiftsTable() {
		scheduleTable.setVisible(false);
		shiftsTable.setVisible(true);
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
		// TODO buttonPanel.add(saveButton);

		Button shiftsButton=new Button("Show Shifts");
		shiftsButton.setStyleName("mf-buttonLooser");
		shiftsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showShiftsTable();
			}
		});		
		buttonPanel.add(shiftsButton);

		Button scheduleButton=new Button("Show Schedule");
		scheduleButton.setStyleName("mf-buttonLooser");
		scheduleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showScheduleTable();
			}
		});		
		buttonPanel.add(scheduleButton);

		Button allocationButton=new Button("Show Allocation");
		allocationButton.setStyleName("mf-buttonLooser");
		allocationButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO
			}
		});		
		//buttonPanel.add(allocationButton);
				
		Button backButton=new Button(i18n.backToPreferences());
		backButton.setStyleName("mf-buttonLooser");
		backButton.setTitle("Return back to period preferences"); // TODO i18n
		backButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getStatusLine().clear();
				ctx.getRia().showPeriodPreferencesEditPanel();
			}
		});		
		buttonPanel.add(backButton);
		
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
		table.removeAllRows();				
		return table;		
	}

	private FlexTable newShiftsTable() {
		FlexTable table = new FlexTable();
		table.setStyleName("mf-growsTable");
		table.removeAllRows();
		return table;		
	}
	
	private void refreshScheduleTable() {		
		scheduleTable.removeAllRows();
				
		if(solution!=null && solution.getDays()!=null && solution.getDays().size()>0) {
			HTML html = new HTML(i18n.employee());
			// TODO allow sorting the table by employee name
			// setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
			scheduleTable.setWidget(0, 0, html);
			html = new HTML(i18n.job());
			scheduleTable.setWidget(0, 1, html);			
			html = new HTML(i18n.shifts());
			scheduleTable.setWidget(0, 2, html);
			
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
				employee.getFullName()
				  // +" ("+(employee.isFulltime()?"F":"")+(employee.isSportak()?"S":"")+(employee.isEditor()?"E":"")+")",
				  +(employee.isFulltime()?"*":""),
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
		if(employee.isFemale()) {
			button.addStyleName("s2-female");			
		} else {
			button.addStyleName("s2-male");						
		}
		if(employee.isFulltime()) {
			button.addStyleName("s2-fulltime");			
		} else {
			button.addStyleName("s2-parttime");						
		}
		if(employee.isEditor()) {
			button.setTitle(employee.getFullName()+" - Editor");
			button.addStyleName("s2-editor");			
		}
		if(employee.isSportak()) {
			button.setTitle(employee.getFullName()+" - Sportak");
			button.addStyleName("s2-sportak");			
		}
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
			// TODO append Mon...Sun to the number; weekend to have different color
			HTML html = new HTML(""+(i+1)+Utils.getDayLetter(i+1, preferences.getStartWeekDay()));
			if(Utils.isWeekend(i+1, preferences.getStartWeekDay())
					|| publicHolidays.isHolidays(
							preferences.getYear(), 
							preferences.getMonth(), 
							i)) 
			{
				html.addStyleName("s2-weekendDay");
			}
			table.setWidget(0, i+2, html);
		}
		
		// TODO change HTML to button that will allow to edit solution; click rotates meaningful employee allocation/shift
				
		HTML html;
		for(int c=0; c<monthDays; c++) {
			if(solution.getDays().get(c).isEmployeeAllocatedToday(employee.getKey())) {
				switch(solution.getDays().get(c).getShiftTypeForEmployee(employee.getKey())) {
				case ShiftSolverConstants.SHIFT_MORNING:
					html = new HTML("&nbsp;M");
					html.setStyleName(ShiftSolverConstants.CSS_SHIFT_MORNING);
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

	public void refreshShiftsTable() {
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
			shiftsTable.getCellFormatter().setStyleName(row, ii, "s2-solBlack");				
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
			shiftsTable.setWidget(row, 0, new HTML("Week "+w));
			shiftsTable.setWidget(row, 1, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 2, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 3, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 4, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 5, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 6, new HTML(" "));
			shiftsTable.setWidget(row, 7, new HTML(c>=columnOf1stDay?""+day++:""));
			shiftsTable.setWidget(row, 8, new HTML(c>=columnOf1stDay?""+day++:""));
			for(int ii=0; ii<=8; ii++) {
				shiftsTable.getCellFormatter().setStyleName(row, ii, "s2-solBlack");				
			}
			day=oldDay;			

			// week body
			int r=++row;
			for(int ii=0; ii<13; ii++) {
				shiftsTable.getCellFormatter().setStyleName(r+ii, 0, "s2-solBlack");				
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
							shiftsTable.setWidget(r-1+1, cc, new HTML(ds.getWorkdayMorningShift().editor.getFullName()));
							shiftsTable.getCellFormatter().setStyleName(r-1+1, cc, "s2-solMorning");				
							shiftsTable.setWidget(r-1+2, cc, new HTML(ds.getWorkdayMorningShift().drone6am.getFullName()));
							shiftsTable.setWidget(r-1+3, cc, new HTML(ds.getWorkdayMorningShift().drone7am.getFullName()));
							shiftsTable.setWidget(r-1+4, cc, new HTML(ds.getWorkdayMorningShift().drone8am.getFullName()));
							shiftsTable.setWidget(r-1+5, cc, new HTML("MISSING"));
							shiftsTable.setWidget(r-1+6, cc, new HTML(ds.getWorkdayMorningShift().sportak.getFullName()));
							shiftsTable.setWidget(r-1+7, cc, new HTML(ds.getWorkdayAfternoonShift().editor.getFullName()));
							shiftsTable.getCellFormatter().setStyleName(r-1+7, cc, "s2-solAfternoon");				
							shiftsTable.setWidget(r-1+8, cc, new HTML(ds.getWorkdayAfternoonShift().drones[0].getFullName()));
							shiftsTable.setWidget(r-1+9, cc, new HTML(ds.getWorkdayAfternoonShift().drones[1].getFullName()));
							shiftsTable.setWidget(r-1+10, cc, new HTML(ds.getWorkdayAfternoonShift().drones[2].getFullName()));
							shiftsTable.setWidget(r-1+11, cc, new HTML(ds.getWorkdayAfternoonShift().drones[3].getFullName()));
							shiftsTable.setWidget(r-1+12, cc, new HTML(ds.getWorkdayAfternoonShift().sportak.getFullName()));
							shiftsTable.setWidget(r-1+13, cc, new HTML(ds.getNightShift().drone.getFullName()));																		
							shiftsTable.getCellFormatter().setStyleName(r-1+13, cc, "s2-solNight");				
						} else {
							shiftsTable.setWidget(r-1+1, cc, new HTML(ds.getWeekendMorningShift().editor.getFullName()));
							shiftsTable.setWidget(r-1+2, cc, new HTML(ds.getWeekendMorningShift().drone6am.getFullName()));
							shiftsTable.setWidget(r-1+3, cc, new HTML(""));
							shiftsTable.setWidget(r-1+4, cc, new HTML(""));
							shiftsTable.setWidget(r-1+5, cc, new HTML(""));
							shiftsTable.setWidget(r-1+6, cc, new HTML(ds.getWeekendMorningShift().sportak.getFullName()));
							shiftsTable.setWidget(r-1+7, cc, new HTML(ds.getWeekendAfternoonShift().editor.getFullName()));
							shiftsTable.setWidget(r-1+8, cc, new HTML(ds.getWeekendAfternoonShift().drone.getFullName()));
							shiftsTable.setWidget(r-1+9, cc, new HTML(""));
							shiftsTable.setWidget(r-1+10, cc, new HTML(""));
							shiftsTable.setWidget(r-1+11, cc, new HTML(""));
							shiftsTable.setWidget(r-1+12, cc, new HTML(ds.getWeekendAfternoonShift().sportak.getFullName()));
							shiftsTable.setWidget(r-1+13, cc, new HTML(ds.getNightShift().drone.getFullName()));																		
						}
					}
				}
				columnOf1stDay=6;
			}
			
			r=row;
			for(int ii=0; ii<7; ii++) {
				shiftsTable.getCellFormatter().setStyleName(r+ii, 6, "s2-solBlack");				
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
						shiftsTable.setWidget(r+1-1, cc+1, new HTML(ds.getWeekendMorningShift().editor.getFullName()));
						shiftsTable.setWidget(r+2-1, cc+1, new HTML(ds.getWeekendMorningShift().drone6am.getFullName()));
						shiftsTable.setWidget(r+3-1, cc+1, new HTML(ds.getWeekendMorningShift().sportak.getFullName()));
						shiftsTable.setWidget(r+4-1, cc+1, new HTML(ds.getWeekendAfternoonShift().editor.getFullName()));
						shiftsTable.setWidget(r+5-1, cc+1, new HTML(ds.getWeekendAfternoonShift().drone.getFullName()));
						shiftsTable.setWidget(r+6-1, cc+1, new HTML(ds.getWeekendAfternoonShift().sportak.getFullName()));
						shiftsTable.setWidget(r+7-1, cc+1, new HTML(ds.getNightShift().drone.getFullName()));
					}
				}
			}
			
			columnOf1stDay=1;
			row=nextWeekRow;
		}
				
		// TODO obsolete
		//StringBuffer s = createShiftsHtml(solution);		
		//shiftsTable.setWidget(0, 0, new HTML(s.toString()));
		}
	}

	private StringBuffer createShiftsHtml(PeriodSolution solution) {
		StringBuffer s=new StringBuffer();
		s.append("<br><b>Shifts Schedule:</b><br>");
		List<DaySolution> days = solution.getDays();
		s.append("<ul>");
		for(DaySolution ds:days) {
			s.append("<li>");
			s.append((ds.isWorkday()?"Work":"Weekend") + " Day "+ ds.getDay() +":");
			s.append("<ul>");
			if(ds.isWorkday()) {
				s.append("<li>");				
				s.append("Morning:");
				s.append("<ul>");
				s.append("<li>    E "+ds.getWorkdayMorningShift().editor.getFullName());
				s.append("<li>    D "+ds.getWorkdayMorningShift().drone6am.getFullName());
				s.append("<li>    D "+ds.getWorkdayMorningShift().drone7am.getFullName());
				s.append("<li>    D "+ds.getWorkdayMorningShift().drone8am.getFullName());
				s.append("<li>    E "+ds.getWorkdayMorningShift().sportak.getFullName());
				s.append("</ul>");
				s.append("</li>");

				s.append("<li>");
				s.append("  Afternoon:");
				s.append("<ul>");
				s.append("<li>    E "+ds.getWorkdayAfternoonShift().editor.getFullName());
				s.append("<li>    D "+ds.getWorkdayAfternoonShift().drones[0].getFullName());
				s.append("<li>    D "+ds.getWorkdayAfternoonShift().drones[1].getFullName());
				s.append("<li>    D "+ds.getWorkdayAfternoonShift().drones[2].getFullName());
				s.append("<li>    D "+ds.getWorkdayAfternoonShift().drones[3].getFullName());
				s.append("<li>    S "+ds.getWorkdayAfternoonShift().sportak.getFullName());
				s.append("</ul>");
				s.append("</li>");

				s.append("<li>");
				s.append("  Night:");
				s.append("<ul>");
				s.append("    <li>D "+ds.getNightShift().drone.getFullName());
				s.append("</ul>");
				s.append("</li>");
			} else {		
				s.append("<li>");
				s.append("  Morning:");
				s.append("<ul>");
				s.append("<li>    E "+ds.getWeekendMorningShift().editor.getFullName());
				s.append("<li>    D "+ds.getWeekendMorningShift().drone6am.getFullName());
				s.append("<li>    E "+ds.getWeekendMorningShift().sportak.getFullName());
				s.append("</ul>");
				s.append("</li>");

				s.append("<li>");				
				s.append("  Afternoon:");
				s.append("<ul>");
				s.append("<li>    E "+ds.getWeekendAfternoonShift().editor.getFullName());
				s.append("<li>    D "+ds.getWeekendAfternoonShift().drone.getFullName());
				s.append("<li>    S "+ds.getWeekendAfternoonShift().sportak.getFullName());
				s.append("</ul>");
				s.append("</li>");

				s.append("<li>");				
				s.append("  Night:");
				s.append("<ul>");
				s.append("<li>    D "+ds.getNightShift().drone.getFullName());
				s.append("</ul>");
				s.append("</li>");
			}
			s.append("</ul>");
		}
		s.append("</ul>");
		return s;
	}
	
	public void refresh(PeriodSolution solution) {
		if(solution==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		this.solution=solution;
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
			yearMonthHtml.setVisible(true);
			scheduleTable.setVisible(true);
			shiftsTable.setVisible(false);
			
			yearMonthHtml.setHTML("Solution for: "+solution.getYear()+"/"+solution.getMonth());
			refreshScheduleTable();
			refreshShiftsTable();			
		} else {
			yearMonthHtml.setVisible(false);
			scheduleTable.setVisible(false);
			shiftsTable.setVisible(false);
		}
	}
}
