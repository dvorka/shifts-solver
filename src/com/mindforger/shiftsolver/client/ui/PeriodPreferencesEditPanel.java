package com.mindforger.shiftsolver.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.Utils;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.client.solver.ShiftSolverTimeoutException;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.client.ui.buttons.YesNoDontcareButton;
import com.mindforger.shiftsolver.shared.model.DayPreference;
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
	private Map<String,List<YesNoDontcareButton>> preferenceButtons;
	
	private PeriodPreferences periodPreferences;

	private static final int CHECK_DAY=1;
	private static final int CHECK_MORNING_6=2;
	private static final int CHECK_MORNING_7=3;
	private static final int CHECK_MORNING_8=4;
	private static final int CHECK_AFTERNOON=5;
	private static final int CHECK_NIGHT=6;
	
	public PeriodPreferencesEditPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		this.preferenceButtons=new HashMap<String,List<YesNoDontcareButton>>();
		
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
				handleSolve(ctx, false);
			}
		});		
		buttonPanel.add(solveButton);

		Button shuffleAndSolveButton=new Button("Shuffle and Solve"); // TODO i18n
		shuffleAndSolveButton.setStyleName("mf-button");
		shuffleAndSolveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				handleSolve(ctx, true);
			}
		});		
		buttonPanel.add(shuffleAndSolveButton);
		
		Button saveButton=new Button("Save"); // TODO i18n
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(periodPreferences!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().savingPeriodPreferences());
		    		riaToObject();
		      		ctx.getRia().savePeriodPreferences(periodPreferences);
		      		ctx.getStatusLine().showInfo("Period preferences saved");
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
		    		ctx.getStatusLine().showProgress("Deleting period preferences...");
		      		ctx.getRia().deletePeriodPreferences(periodPreferences);
		      		ctx.getStatusLine().showInfo("Period preferences deleted");
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
		yearListBox.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				periodPreferences.setYear(Integer.parseInt(yearListBox.getValue(yearListBox.getSelectedIndex())));
				handleDateListboxChange();				
			}
		});
		flowPanel.add(yearListBox);
		
		monthListBox = new ListBox(false);
		for(int i=1; i<=12; i++) {
			monthListBox.addItem(""+i);			
		}
		monthListBox.setTitle("Month"); // TODO i18n
		monthListBox.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				periodPreferences.setMonth(Integer.parseInt(monthListBox.getValue(monthListBox.getSelectedIndex())));
				handleDateListboxChange();				
			}
		});
		flowPanel.add(monthListBox);
		
		return flowPanel;
	}
	
	private FlexTable newPreferencesTable() {
		FlexTable table = new FlexTable();
		table.setStyleName("mf-growsTable");

		table.removeAllRows();
				
		return table;		
	}

	private void refreshPreferencesTable(FlexTable table) {
		ctx.getStatusLine().showInfo(i18n.buildingPeriodPreferences());

		table.removeAllRows();
		preferenceButtons.clear();

		HTML html = new HTML("Employee"); // TODO i18n
		// TODO allow sorting the table by employee name
		// setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
		table.setWidget(0, 0, html);
		html = new HTML("Preferences"); // TODO i18n
		table.setWidget(0, 1, html);

		EmployeePreferences employeePreferences;
		int monthDays = periodPreferences.getMonthDays();
		for(Employee employee:ctx.getState().getEmployees()) {
			if(periodPreferences!=null && periodPreferences.getEmployeeToPreferences()!=null && periodPreferences.getEmployeeToPreferences().size()>0) {
				employeePreferences = periodPreferences.getEmployeeToPreferences().get(employee.getKey());
			} else {
				employeePreferences = null;
			}
			addEmployeeRow(preferencesTable, employee, employeePreferences, monthDays);
		}
		
		ctx.getStatusLine().showInfo("Period preferences built!");
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
			HTML html = new HTML(""+i+Utils.getDayLetter(i, periodPreferences.getStartWeekDay()));
			//html.addStyleName("mf-progressHtml");
			employeePrefsTable.setWidget(0, i, html);				
			if(Utils.isWeekend(i, periodPreferences.getStartWeekDay())) {
				html.addStyleName("s2-weekendDay");
			}
		}

		// TODO consider encoding this as bits in int/long
		employeePrefsTable.setWidget(CHECK_DAY, 0, new HTML("Day"));
		employeePrefsTable.setWidget(CHECK_MORNING_6, 0, new HTML("Morning&nbsp;6am"));
		employeePrefsTable.setWidget(CHECK_MORNING_7, 0, new HTML("Morning&nbsp;7am"));
		employeePrefsTable.setWidget(CHECK_MORNING_8, 0, new HTML("Morning&nbsp;8am"));
		employeePrefsTable.setWidget(CHECK_AFTERNOON, 0, new HTML("Afternoon"));
		employeePrefsTable.setWidget(CHECK_NIGHT, 0, new HTML("Night"));
		
		List<YesNoDontcareButton> employeeButtons=new ArrayList<YesNoDontcareButton>();
		DayPreference dayPreference;
		for(int c=1; c<=monthDays; c++) {
			if(employeePreferences!=null) {
				dayPreference = employeePreferences.getPreferencesForDay(c);				
			} else {
				dayPreference = null;
			}
			for(int r=1; r<=6; r++) {
				YesNoDontcareButton yesNoDontcare = new YesNoDontcareButton();
				if(dayPreference!=null) {
					switch(r) {
					case CHECK_DAY:
						if(dayPreference.isNoDay()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesDay()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					case CHECK_MORNING_6:
						if(dayPreference.isNoMorning6()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesMorning6()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					case CHECK_MORNING_7:
						if(dayPreference.isNoMorning7()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesMorning7()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					case CHECK_MORNING_8:
						if(dayPreference.isNoMorning8()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesMorning8()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					case CHECK_AFTERNOON:
						if(dayPreference.isNoAfternoon()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesAfternoon()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					case CHECK_NIGHT:
						if(dayPreference.isNoNight()) {
							yesNoDontcare.setYesNoValue(1);
							yesNoDontcare.setStylePrimaryName("s2-3stateNo");
						}
						if(dayPreference.isYesNight()) {
							yesNoDontcare.setYesNoValue(2);
							yesNoDontcare.setStylePrimaryName("s2-3stateYes");
						}
						break;
					}
				}
				employeePrefsTable.setWidget(r, c, yesNoDontcare);
				employeeButtons.add(yesNoDontcare);
			}
		}
		preferenceButtons.put(employee.getKey(), employeeButtons);
		
		table.setWidget(numRows, 1, employeePrefsTable);		
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
		
		refreshPreferencesTable(preferencesTable);
	}

	private void riaToObject() {
		if(periodPreferences!=null) {
			periodPreferences.setYear(Integer.parseInt(yearListBox.getValue(yearListBox.getSelectedIndex())));
			periodPreferences.setMonth(Integer.parseInt(monthListBox.getValue(monthListBox.getSelectedIndex())));
		}

		periodPreferences.getEmployeeToPreferences().clear();
		for(Employee e:ctx.getState().getEmployees()) {
			EmployeePreferences ep=new EmployeePreferences();
			List<DayPreference> dps=new ArrayList<DayPreference>();
			ep.setPreferences(dps);
			
			List<YesNoDontcareButton> employeeButtons=preferenceButtons.get(e.getKey());
			for(int c=1; c<=periodPreferences.getMonthDays(); c++) {
				DayPreference dayPreference=null;
				for(int r=1; r<=6; r++) {
					YesNoDontcareButton yesNoDontcareButton = employeeButtons.get((c-1)*6+(r-1));
					if(yesNoDontcareButton.getYesNoValue()>0) {
						if(dayPreference==null) {
							dayPreference=new DayPreference();
							dayPreference.setYear(periodPreferences.getYear());
							dayPreference.setMonth(periodPreferences.getMonth());
							dayPreference.setDay(c);
						}
						switch(r) {
						case CHECK_DAY:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesDay(true);
								break;
							case 1:
								dayPreference.setNoDay(true);
								break;
							default:
								break;
							}
							break;
						case CHECK_MORNING_6:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesMorning6(true);
								break;
							case 1:
								dayPreference.setNoMorning6(true);
								break;
							default:
								break;
							}
							break;
						case CHECK_MORNING_7:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesMorning7(true);
								break;
							case 1:
								dayPreference.setNoMorning7(true);
								break;
							default:
								break;
							}
							break;
						case CHECK_MORNING_8:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesMorning8(true);
								break;
							case 1:
								dayPreference.setNoMorning8(true);
								break;
							default:
								break;
							}
							break;
						case CHECK_AFTERNOON:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesAfternoon(true);
								break;
							case 1:
								dayPreference.setNoAfternoon(true);
								break;
							default:
								break;
							}
							break;
						case CHECK_NIGHT:
							switch(yesNoDontcareButton.getYesNoValue()) {
							case 2:
								dayPreference.setYesNight(true);
								break;
							case 1:
								dayPreference.setNoNight(true);
								break;
							default:
								break;
							}
							break;
						}
					}
				}
				if(dayPreference!=null) {
					dps.add(dayPreference);
				}
			}			
			periodPreferences.getEmployeeToPreferences().put(e.getKey(), ep);			
		}
	}

	private void handleSolve(final RiaContext ctx, final boolean shuffle) {
		if(periodPreferences!=null) {
			ctx.getStatusLine().showProgress(ctx.getI18n().solvingShifts());
			PeriodSolution solution;
			try {
				Employee[] employees = ctx.getState().getEmployees();
				try {
					if(shuffle) {
						Utils.shuffleArray(employees);						
					}
					solution = ctx.getSolver().solve(Arrays.asList(employees), periodPreferences, 0);							
		    		if(solution!=null) {
		    			ctx.getStatusLine().showInfo("Solution found!");
		    			ctx.getSolutionViewPanel().refresh(solution);
		    			ctx.getRia().showSolutionViewPanel();  			
		    		} else {
		    			ctx.getStatusLine().showError("No solution exists for this employees and their preferences!");
		    			ctx.getRia().showPeriodPreferencesEditPanel();
		    		}		    			
				} catch(RuntimeException e) {
					// TODO throw my solver exception to distinguish
					ctx.getStatusLine().showError("Unable to find solution: "+e.getMessage());
					ctx.getRia().showPeriodPreferencesEditPanel();
				}
			} catch(ShiftSolverTimeoutException e) {
				ctx.getStatusLine().showError("Solver didn't found schedule in "+ShiftSolver.STEPS_LIMIT+" steps. Click 'Solve' button to try again w/ different config."); // TODO i18n
				ctx.getRia().showPeriodPreferencesEditPanel();
			}
		}
	}

	private void handleDateListboxChange() {
		PeriodPreferences p=new PeriodPreferences(periodPreferences.getYear(), periodPreferences.getMonth());
		ctx.getService().setDaysAndStartDay(p, new AsyncCallback<PeriodPreferences>() {					
			@Override
			public void onSuccess(PeriodPreferences result) {
				periodPreferences.setMonthDays(result.getMonthDays());
				periodPreferences.setStartWeekDay(result.getStartWeekDay());
				refresh(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				ctx.getStatusLine().showError("Unable to determine month's properties!");
			}
		});
	}	
}
