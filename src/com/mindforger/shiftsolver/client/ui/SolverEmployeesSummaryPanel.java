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
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByFulltime;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByName;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByNights;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByRole;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorAllocationByShifts;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class SolverEmployeesSummaryPanel extends FlexTable implements SortableTable, ShiftSolverConstants {

	private RiaMessages i18n;
	private RiaContext ctx;
	private boolean noSolutionPanelMode;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;
	
	private PeriodPreferences periodPreferences;
	private List<EmployeeAllocation> employeeAllocations;
	private int failday;
	
	public SolverEmployeesSummaryPanel(RiaContext ctx, boolean noSolutionPanelMode) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.sortCriteria=TableSortCriteria.BY_NAME;
		this.noSolutionPanelMode=noSolutionPanelMode;
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
		case BY_FULLTIME:
			comparator=new ComparatorAllocationByFulltime(sortIsAscending);
			break;
		case BY_SHIFTS:
			comparator=new ComparatorAllocationByShifts(sortIsAscending);
			break;
		case BY_NIGHTS:
			comparator=new ComparatorAllocationByNights(sortIsAscending);
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
		if(noSolutionPanelMode) {
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

//		Button solveButton=new Button(i18n.solvePartially());
//		solveButton.setStyleName("mf-buttonLooser");
//		solveButton.setTitle("Solve what can be solved and skip the rest"); // TODO i18n
//		solveButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				// TODO
//			}
//		});		
//		buttonPanel.add(solveButton);
		
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
		int c=0;
		setWidget(rows, c++, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
		setWidget(rows, c++, new TableSetSortingButton(i18n.shifts(),TableSortCriteria.BY_SHIFTS, this, ctx));
		setWidget(rows, c++, new TableSetSortingButton(i18n.nights(),TableSortCriteria.BY_NIGHTS, this, ctx));
		if(noSolutionPanelMode) {
			setWidget(rows, c++, new TableSetSortingButton(i18n.today(),TableSortCriteria.BY_NAME, this, ctx));
			setWidget(rows, c++, new TableSetSortingButton("5 "+i18n.days(),TableSortCriteria.BY_NAME, this, ctx));
			setWidget(rows, c++, new TableSetSortingButton("Y/N",TableSortCriteria.BY_NAME, this, ctx));			
		}
		setWidget(rows, c++, new TableSetSortingButton(i18n.role(),TableSortCriteria.BY_ROLE, this, ctx));
		setWidget(rows, c++, new TableSetSortingButton(i18n.fulltime(),TableSortCriteria.BY_FULLTIME, this, ctx));
		setWidget(rows, c++, new TableSetSortingButton(i18n.dayListing(),TableSortCriteria.BY_NAME, this, ctx));
	}
		
	public void addRow(EmployeeAllocation a, int failday) {
		int rows = getRowCount();

		boolean isFullShifts=false;
		HTML shiftsAllocationHtml; 
		boolean isFullNights=false;
		HTML nightsAllocationHtml; 
		if(a.shifts<a.shiftsToGet) {
			shiftsAllocationHtml=new HTML(a.shifts+"/"+a.shiftsToGet);
			if(a.shifts==0) {
				shiftsAllocationHtml.addStyleName("s2-mismatch");				
			}
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
			if(a.nights==0) {
				nightsAllocationHtml.addStyleName("s2-mismatch");				
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
			last5DaysHtml=new HTML(i18n.yes());
			last5DaysHtml.setStyleName("s2-busy");
		} else {
			last5DaysHtml=new HTML(i18n.no());			
			last5DaysHtml.setStyleName("s2-mismatch");
		}
		HTML todayHtml;
		if(a.hadShiftToday(failday)) {
			todayHtml=new HTML(i18n.yes());
			todayHtml.setStyleName("s2-busy");
		} else {
			todayHtml=new HTML(i18n.no());			
			todayHtml.setStyleName("s2-mismatch");
		}

		boolean isFullPrefs=false;
		HTML preferencesHtml=new HTML("?");
		preferencesHtml.setStyleName("s2-mismatch");
		EmployeePreferences employeePreferences = periodPreferences.getEmployeeToPreferences().get(a.employee.getKey());
		if(employeePreferences!=null) {
			DayPreference preferencesForDay = employeePreferences.getPreferencesForDay(failday);
			if(preferencesForDay!=null) {
				if(preferencesForDay.isNoDay()) {
					preferencesHtml=new HTML(i18n.day());
					preferencesHtml.setStyleName("s2-busy");
					isFullPrefs=true;
				} else {
					if(preferencesForDay.isNoDay()) {
						preferencesHtml=new HTML(i18n.holidays());
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
			for(int i=0; i<a.shiftsOnDays.size(); i++) {
				Integer d=a.shiftsOnDays.get(i);
				text+=d;
				switch(a.shiftTypesOnDays.get(i)) {
				case SHIFT_AFTERNOON:
					text+=i18n.afternoonShiftLetter();
					break;
				case SHIFT_NIGHT:
					text+=i18n.nightShiftLetter();
					break;
				case SHIFT_MORNING:
					text+=i18n.morningShiftLetter();
					break;
				case SHIFT_MORNING_6:
					text+=i18n.morning6ShiftLetter();
					break;
				case SHIFT_MORNING_7:
					text+=i18n.morning7ShiftLetter();
					break;
				case SHIFT_MORNING_8:
					text+=i18n.morning8ShiftLetter();
					break;
				}
				text+=" ";
			}
		}
		HTML shiftsDaysHtml=new HTML(text);
						
		HTML roleHtml=new HTML(i18n.staffer());
		if(a.employee.isEditor()) {
			roleHtml=new HTML(i18n.editor());
		} else {
			if(a.employee.isSportak()) {
				roleHtml=new HTML(i18n.sportak());
			} else {
				if(a.employee.isMortak()) {
					roleHtml=new HTML(i18n.morningSportak());
				}
			}
		}
		
		final HTML fulltimeHtml = new HTML((a.employee.isFulltime()?i18n.yes():i18n.no())+"&nbsp;&nbsp;");
		if(a.employee.isFulltime()) {
			fulltimeHtml.setStyleName("s2-match");
		} else {
			fulltimeHtml.setStyleName("s2-mismatch");			
		}
		
		int c=0;
		setWidget(rows, c++, button);
		setWidget(rows, c++, shiftsAllocationHtml);
		setWidget(rows, c++, nightsAllocationHtml);
		if(noSolutionPanelMode) {
			setWidget(rows, c++, todayHtml);
			setWidget(rows, c++, last5DaysHtml);
			setWidget(rows, c++, preferencesHtml);
		}
		setWidget(rows, c++, roleHtml);
		setWidget(rows, c++, fulltimeHtml);
		setWidget(rows, c++, shiftsDaysHtml);
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
