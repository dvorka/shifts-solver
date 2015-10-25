package com.mindforger.shiftsolver.client.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.client.ui.buttons.TableSetSortingButton;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByName;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByRole;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class SolverNoSolutionPanel extends FlexTable implements SortableTable {

	private RiaMessages i18n;
	private RiaContext ctx;
	private boolean showButtonsPanel;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;
	
	private PeriodPreferences periodPreferences;
	private List<EmployeeAllocation> employeeAllocations;
	private int failday;
	
	public SolverNoSolutionPanel(RiaContext ctx, boolean showButtonsPanel) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.sortCriteria=TableSortCriteria.BY_NAME;
		this.showButtonsPanel=showButtonsPanel;
	}
	
	public void init() {
		addStyleName("mf-growsTable");
	}

	public void refresh(List<EmployeeAllocation> employeeAllocations, int failday, PeriodPreferences periodPreferences) {
		if(employeeAllocations==null || employeeAllocations.size()==0) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}
		
		this.employeeAllocations=employeeAllocations;
		this.failday=failday;
		this.periodPreferences=periodPreferences;
		
		Comparator<EmployeeAllocation> comparator;
		switch(sortCriteria) {
		case BY_NAME:
			comparator=new ComparatorAllocationByName(sortIsAscending);
			break;
		case BY_ROLE:
			comparator=new ComparatorAllocationByRole(sortIsAscending);
			break;
		default:
			comparator=new ComparatorAllocationByName(sortIsAscending);
			break;
		}		
		Collections.sort(employeeAllocations, comparator);
		
		removeAllRows();
		if(showButtonsPanel) {
			setWidget(0,0,newButtonPanel(ctx));
		}
		getFlexCellFormatter().setColSpan(0, 0, 8);
		addRows(employeeAllocations, failday);		
	}

	private FlowPanel newButtonPanel(final RiaContext ctx) {
		FlowPanel buttonPanel=new FlowPanel();
		
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

		Button solveButton=new Button("Solve Partially");
		solveButton.setStyleName("mf-buttonLooser");
		solveButton.setTitle("Solve what can be solved and skip the rest"); // TODO i18n
		solveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO
			}
		});		
		buttonPanel.add(solveButton);
		
		return buttonPanel;
	}
	
	private void addRows(List<EmployeeAllocation> result, int failday) {
		addTableTitle();
		if(result!=null) {
			for (EmployeeAllocation employeeAllocation:result) {
				addRow(employeeAllocation, failday);
			}			
		}
	}
	
	private void addTableTitle() {		
		int rows = getRowCount();
		setWidget(rows, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
		// TODO i18n
		setWidget(rows, 1, new TableSetSortingButton("Shifts",TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, 2, new TableSetSortingButton("Nights",TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, 3, new TableSetSortingButton("Today",TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, 4, new TableSetSortingButton("5 Days",TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, 5, new TableSetSortingButton("No",TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, 6, new TableSetSortingButton("Role",TableSortCriteria.BY_ROLE, this, ctx));
		setWidget(rows, 7, new TableSetSortingButton(i18n.fulltime(),TableSortCriteria.BY_FULLTIME, this, ctx));
		setWidget(rows, 8, new TableSetSortingButton("Days",TableSortCriteria.BY_NAME, this, ctx));
	}
		
	public void addRow(EmployeeAllocation a, int failday) {
		int rows = getRowCount();

		boolean isFullShifts=false;
		HTML shiftsAllocationHtml; 
		boolean isFullNights=false;
		HTML nightsAllocationHtml; 
		if(a.shifts<a.shiftsToGet) {
			shiftsAllocationHtml=new HTML(a.shifts+"/"+a.shiftsToGet);
		} else {
			isFullShifts=true;
			shiftsAllocationHtml=new HTML(a.shifts+"/"+a.shiftsToGet);
			shiftsAllocationHtml.addStyleName("s2-busy");
		}
		if(a.nights<2) {
			if(a.employee.isFulltime()) {
				nightsAllocationHtml=new HTML(a.nights+"/2");
			} else {
				nightsAllocationHtml=new HTML(a.nights+"/inf");				
			}
		} else {
			if(a.employee.isFulltime()) {
				nightsAllocationHtml=new HTML(a.nights+"/2");
				nightsAllocationHtml.addStyleName("s2-busy");
				isFullNights=true;
			} else {
				nightsAllocationHtml=new HTML(a.nights+"/inf");				
			}			
		}
		HTML last5DaysHtml;
		if(a.hadShiftsLast5Days(failday)) {
			last5DaysHtml=new HTML("yes");
			last5DaysHtml.setStyleName("s2-busy");
		} else {
			last5DaysHtml=new HTML("no");			
		}
		HTML todayHtml;
		if(a.hadShiftToday(failday)) {
			todayHtml=new HTML("yes");
			todayHtml.setStyleName("s2-busy");
		} else {
			todayHtml=new HTML("no");			
		}

		boolean isFullPrefs=false;
		HTML preferencesHtml=new HTML("?");
		EmployeePreferences employeePreferences = periodPreferences.getEmployeeToPreferences().get(a.employee.getKey());
		if(employeePreferences!=null) {
			DayPreference preferencesForDay = employeePreferences.getPreferencesForDay(failday);
			if(preferencesForDay!=null) {
				if(preferencesForDay.isNoDay()) {
					preferencesHtml=new HTML("Day");
					preferencesHtml.setStyleName("s2-busy");
					isFullPrefs=true;
				} else {
					if(preferencesForDay.isNoDay()) {
						preferencesHtml=new HTML("Holiday");
						preferencesHtml.setStyleName("s2-busy");
						isFullPrefs=true;
					} else {
						String text="";
						text+=preferencesForDay.isNoMorning6()?"6":"";
						text+=preferencesForDay.isNoMorning6()?"7":"";
						text+=preferencesForDay.isNoMorning6()?"8":"";
						text+=preferencesForDay.isNoAfternoon()?"A":"";
						text+=preferencesForDay.isNoNight()?"N":"";
						preferencesHtml=new HTML(text);
					}
				}			
			}			
		}
		
		boolean isFull=isFullShifts|isFullNights|a.hadShiftToday(failday)|a.hadShiftsLast5Days(failday)|isFullPrefs;
		
		EmployeesTableToEmployeeButton button = new EmployeesTableToEmployeeButton(
				a.employee.getKey(),
				a.employee.getFullName(),
				"mf-growsTableGoalButton", 
				ctx);
		if(isFull) {
			button.addStyleName("s2-busy");
		}
		
		// TODO shift types list
		
		String text="";
		if(a.shiftsOnDays!=null && !a.shiftsOnDays.isEmpty()) {
			for(Integer i:a.shiftsOnDays) {
				text+=i+", ";
			}
		}
		HTML shiftsDaysHtml=new HTML(text);
						
		HTML roleHtml=new HTML("Staffer");
		if(a.employee.isEditor()) {
			roleHtml=new HTML("Editor");
		} else {
			if(a.employee.isSportak()) {
				roleHtml=new HTML("Sportak");
			} else {
				if(a.employee.isMortak()) {
					roleHtml=new HTML("Mortak");
				}
			}
		}
		
		final HTML fulltimeHtml = new HTML((a.employee.isFulltime()?i18n.yes():i18n.no())+"&nbsp;&nbsp;");
		if(a.employee.isFulltime()) {
			fulltimeHtml.setStyleName("s2-match");
		} else {
			fulltimeHtml.setStyleName("s2-mismatch");			
		}
		
		setWidget(rows, 0, button);
		setWidget(rows, 1, shiftsAllocationHtml);
		setWidget(rows, 2, nightsAllocationHtml);
		setWidget(rows, 3, todayHtml);
		setWidget(rows, 4, last5DaysHtml);
		setWidget(rows, 5, preferencesHtml);
		setWidget(rows, 6, roleHtml);
		setWidget(rows, 7, fulltimeHtml);
		setWidget(rows, 8, shiftsDaysHtml);
	}

	@Override
	public void setSortingCriteria(TableSortCriteria criteria, boolean sortIsAscending) {
		this.sortCriteria=criteria;
		this.sortIsAscending=sortIsAscending;
	}

	@Override
	public TableSortCriteria getSortingCriteria() {
		return sortCriteria;
	}

	@Override
	public boolean isSortAscending() {
		return sortIsAscending;
	}
	
	@Override
	public void refreshWithNewSortingCriteria() {
		refresh(employeeAllocations, failday, periodPreferences);
	}
}
