package com.mindforger.shiftsolver.client.ui.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.Ria;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ShiftSolverServiceAsync;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class LeftMenubar extends FlexTable implements ShiftSolverConstants {
	
	public static final String HTML_MENU_DELIMITER = "<div class='mf-leftMenuHr'></div>";
	
	private RiaContext ctx;
	
	HTML menuSectionsDelimiter;

	Button homeButton;
	Button newEmployeeButton;
	Button employeesButton;
	Button newPeriodPreferencesButton;
	Button periodPreferencesButton;
	Button periodSolutionsButton;
	Button settingsButton;

	private Ria ria;
	private RiaMessages i18n;
	private ShiftSolverServiceAsync service;

	public LeftMenubar(final RiaContext ctx) {
		this.ctx=ctx;
	}
	
	public void init() {
		this.ria=ctx.getRia();
		this.i18n=ctx.getI18n();
		this.service=ctx.getService();
		
		setStyleName("mf-leftMenu");
				
		employeesButton = new Button(ctx.getI18n().employees(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(ctx.getState().getEmployees()!=null && ctx.getState().getEmployees().length>0) {
					setEmployeesCount(ctx.getState().getEmployees().length);
					showEmployeesTable();
				} else {
					showHome();					
				}
			}			
		});
		employeesButton.setStyleName("mf-menuButtonOff");
		
		newEmployeeButton = new Button(ctx.getI18n().newEmployee(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				createNewEmployee();		
			}
		});
	    newEmployeeButton.setStyleName("mf-helpGuideButton");
	    newEmployeeButton.addStyleName("mf-newGoalButton");	    		
						
		homeButton = new Button(i18n.home(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showHome();
			}
		});
		homeButton.setStyleName("mf-menuButtonOff");

		periodSolutionsButton=new Button(i18n.solutions(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(ctx.getState().getPeriodSolutions()!=null && ctx.getState().getPeriodSolutions().length>0) {
					setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
					showSolutionsTable();
				} else {
					showHome();					
				}
			}
		});
		periodSolutionsButton.setStyleName("mf-menuButtonOff");

		periodPreferencesButton=new Button(i18n.periodPreferences(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(ctx.getState().getPeriodPreferencesArray()!=null && ctx.getState().getPeriodPreferencesArray().length>0) {
					int count=ctx.getState().getPeriodPreferencesArray().length;
					setPeriodPreferencesCount(count);
					showPeriodPreferencesTable();
				} else {
					showHome();					
				}
			}
		});
		periodPreferencesButton.setStyleName("mf-menuButtonOff");
		
		newPeriodPreferencesButton = new Button(i18n.newPeriodPreferences(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				createNewPeriodPreferences();
			}
		});
	    newPeriodPreferencesButton.setStyleName("mf-mrGuideButton");
	    newPeriodPreferencesButton.addStyleName("mf-newGoalButton");

		settingsButton = new Button(i18n.settings(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showSettings();
			}
		});
		settingsButton.setStyleName("mf-menuButtonOff");
	    
		reinitialize();
		
		setEmployeesCount(ctx.getState().getEmployees().length);
		setPeriodPreferencesCount(ctx.getState().getPeriodPreferencesArray().length);
		setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
	}

	public void reinitialize() {
		removeAllRows();
				
		menuSectionsDelimiter=new HTML(HTML_MENU_DELIMITER);
		
		int row=0;
						
		setWidget(row++, 0, homeButton);

		setWidget(row++, 0, new HTML(HTML_MENU_DELIMITER));			
		
		setWidget(row++, 0, newEmployeeButton);
		setWidget(row++, 0, employeesButton);
		setWidget(row++, 0, new HTML(HTML_MENU_DELIMITER));			

		setWidget(row++, 0, newPeriodPreferencesButton);
		setWidget(row++, 0, periodPreferencesButton);

		setWidget(row++, 0, periodSolutionsButton);
		setWidget(row++, 0, new HTML(HTML_MENU_DELIMITER));			
		
		setWidget(row++, 0, settingsButton);
	}
	
	private void switchOfAllButtons() {
		homeButton.setStyleName("mf-menuButtonOff"); // mf-menuButtonOffCheatSheet
		employeesButton.setStyleName("mf-menuButtonOff");
		periodPreferencesButton.setStyleName("mf-menuButtonOff");
		periodSolutionsButton.setStyleName("mf-menuButtonOff");
		settingsButton.setStyleName("mf-menuButtonOff");
	}
	
	public void showEmployeesTable() {
		ria.showEmployeesTable();
		switchOfAllButtons();
		employeesButton.setStyleName("mf-menuButtonOn");
	}
	
	public void showHome() {
		ctx.getHomePanel().refresh();
		ria.showHome();
		switchOfAllButtons();
		homeButton.setStyleName("mf-menuButtonOn");
	}
	
	public void showSettings() {
		ctx.getSettingsPanel().refresh(ctx.getSolver());
		ria.showSettings();
		switchOfAllButtons();
		settingsButton.setStyleName("mf-menuButtonOn");
	}
	
	public void showPeriodPreferencesTable() {
		ria.showPeriodPreferencesTable();
		switchOfAllButtons();
		periodPreferencesButton.setStyleName("mf-menuButtonOn");
	}

	public void showSolutionsTable() {
		ria.showSolutionsTable();
		ctx.getSolutionsTable().refresh(ctx.getState().getPeriodSolutions());
		switchOfAllButtons();
		periodSolutionsButton.setStyleName("mf-menuButtonOn");
	}
			
	public Button getNewGrowButton() {
		return newEmployeeButton;
	}
	
	public Button getNewPeriodPreferencesButton() {
		return newPeriodPreferencesButton;
	}
	
	public void newGrowButtonToNormalStyle() {
		newEmployeeButton.setStyleName("mf-button");
		newEmployeeButton.addStyleName("mf-newGoalButton");		
	}
	
	public void createNewEmployee() {
		service.newEmployee(new AsyncCallback<Employee>() {
			public void onFailure(Throwable caught) {
				ria.handleServiceError(caught);
			}
			public void onSuccess(Employee result) {
				GWT.log("RIA - new employee succesfuly created! "+result);
				result.setFulltime(true);
				ctx.getState().addEmployee(result);
				ctx.getEmployeesEditPanel().refresh(result);
				ria.showEmployeeEditPanel();
				ctx.getStatusLine().showInfo("New employee created");
			}
		});
	}

	public void createNewPeriodPreferences() {
		service.newPeriodPreferences(new AsyncCallback<PeriodPreferences>() {
			public void onFailure(Throwable caught) {
				ria.handleServiceError(caught);
			}
			public void onSuccess(PeriodPreferences result) {
				GWT.log("RIA - new preferences succesfuly created! "+result);
				
				Employee[] employees = ctx.getState().getEmployees();
				Map<String,EmployeePreferences> prefs=new HashMap<String,EmployeePreferences>();
				for(Employee e:employees) {
					prefs.put(e.getKey(), new EmployeePreferences());
				}
				result.setEmployeeToPreferences(prefs);
				
				ctx.getPeriodPreferencesEditPanel().refresh(result);
				ctx.getState().addPeriodPreferences(result);
				ria.showPeriodPreferencesEditPanel();
				ctx.getStatusLine().showInfo("New period preferences created");
			}
		});
	}

	public void createNewSolution(final PeriodPreferences periodPreferences) {
		PeriodSolution s=ShiftSolver.createSolutionSkeleton(periodPreferences, ctx.getState());
		
		service.newPeriodSolution(s,new AsyncCallback<PeriodSolution>() {
			public void onFailure(Throwable caught) {
				ria.handleServiceError(caught);
			}
			public void onSuccess(PeriodSolution result) {
				GWT.log("RIA - new solution succesfuly created! "+result);
				List<EmployeeAllocation> allocations=new ArrayList<EmployeeAllocation>();
				for(Employee e:ctx.getState().getEmployees()) {
					EmployeeAllocation a=new EmployeeAllocation(e, periodPreferences);
					allocations.add(a);
				}
				ctx.getState().addPeriodSolution(result);
				setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
				ctx.getSolutionViewPanel().refresh(result, allocations);
				ria.showSolutionViewPanel();
				ctx.getStatusLine().showInfo("New period solution created");
			}
		});
	}
	
	// TODO optimize this (perhaps one reinitialize doing everything is OK
	public void setEmployeesCount(int count) {
		reinitialize();

		boolean visibility=true;
		if(count==0) {
			visibility=false;
		}
		menuSectionsDelimiter.setVisible(visibility);
		employeesButton.setVisible(visibility);
		
		employeesButton.setText(ctx.getI18n().employees()+" ("+count+")");		
	}

	// TODO optimize this (perhaps one reinitialize doing everything is OK
	public void setPeriodPreferencesCount(int count) {
		reinitialize();

		boolean visibility=true;
		if(count==0) {
			visibility=false;
		}
		
		periodPreferencesButton.setVisible(visibility);		
		periodPreferencesButton.setText(ctx.getI18n().periodPreferences()+" ("+count+")");		
	}

	// TODO optimize this (perhaps one reinitialize doing everything is OK
	public void setPeriodSolutionsCount(int count) {
		reinitialize();

		boolean visibility=true;
		if(count==0) {
			visibility=false;
		}
		
		periodSolutionsButton.setVisible(visibility);		
		periodSolutionsButton.setText(ctx.getI18n().solutions()+" ("+count+")");		
	}
}
