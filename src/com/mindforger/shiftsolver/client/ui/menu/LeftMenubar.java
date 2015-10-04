package com.mindforger.shiftsolver.client.ui.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.GreetingServiceAsync;
import com.mindforger.shiftsolver.client.Ria;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.Employee;

public class LeftMenubar extends FlexTable implements ShiftSolverConstants {
	
	@Deprecated
	public static final String HTML_MENU_SECTION_PREFIX="<div class=\"mf-leftMenuItemSection\">";
	@Deprecated
	public static final String HTML_MENU_SECTION_POSTFIX="</div>";

	public static final String HTML_MENU_DELIMITER = "<div class='mf-leftMenuHr'></div>";
	
	private RiaContext ctx;
	
	HTML menuSectionsDelimiter;
	
	Button newEmployeeButton;
	Button employeesButton; // TODO employees
	Button newPeriodPreferencesButton;
	Button periodPreferencesButton;	// TODO dlouhan

	// help
	Button homeButton;

	private Ria ria;
	private RiaMessages i18n;
	private GreetingServiceAsync service;

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
					int count=(ctx.getState().getEmployees()!=null?ctx.getState().getEmployees().length:0);
					setEmployeesCount(count);
					showEmployeesTable();
				} else {
					showHome();					
				}
			}			
		});
		employeesButton.setStyleName("mf-menuButtonOff");
		
		newEmployeeButton = new Button(ctx.getI18n().newEmployee(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				createNewEmployee(null);		
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

		periodPreferencesButton=new Button(i18n.periodPreferences(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showPeriodPreferencesTable();
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
				
		reinitialize();
		setEmployeesCount(ctx.getState().getEmployees().length);
		setPeriodPreferencesCount(ctx.getState().getPeriodPreferencesList().length);
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
	}
	
	private void switchOfAllButtons() {
		employeesButton.setStyleName("mf-menuButtonOff");
		homeButton.setStyleName("mf-menuButtonOff"); // mf-menuButtonOffCheatSheet
		periodPreferencesButton.setStyleName("mf-menuButtonOff");
	}
	
	public void showEmployeesTable() {
		ria.showEmployeesTable();
		switchOfAllButtons();
		employeesButton.setStyleName("mf-menuButtonOn");
	}
	
	public void showHome() {
		ria.showHome();
		switchOfAllButtons();
		homeButton.setStyleName("mf-menuButtonOn");
	}
	
	public void showPeriodPreferencesTable() {
		ria.showPeriodPreferencesTable();
		switchOfAllButtons();
		periodPreferencesButton.setStyleName("mf-menuButtonOn");
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
	
	public void createNewEmployee(final String growTitle) {
		service.newEmployee(new AsyncCallback<Employee>() {
			public void onFailure(Throwable caught) {
				ria.handleServiceError(caught);
			}
			public void onSuccess(Employee result) {
				GWT.log("RIA - new employee succesfuly created! "+result);
				ctx.getEmployeesEditPanel().refresh(result);
				ria.showEmployeeEditPanel();
				ctx.getStatusLine().hideStatus();
			}
		});
	}

	public void createNewPeriodPreferences() {
//		service.newOutline(new AsyncCallback<OutlineBean>() {
//			public void onFailure(Throwable caught) {
//				ria.handleServiceError(caught);
//			}
//			public void onSuccess(OutlineBean result) {
//				// TODO fix error reporting
////				if(result!=null && result.startsWith(UserLimitsBean.LIMIT_EXCEEDED)) {
////					statusLine.showError(
////							i18n.youExceededOutlinesLimit()+
////							" - "+
////							result.substring(UserLimitsBean.LIMIT_EXCEEDED.length())+
////							"!");
////				} else {
//					GWT.log("RIA - new outline succesfuly created! "+result);
//					
//					outlinePanel.onNewOutline(result);
//					ria.showOutline();
//					
//				    newOutlineButton.setStyleName("mf-button");
//					newOutlineButton.addStyleName("mf-newGoalButton");
//					statusLine.hideStatus();
////				}
//			}
//		});
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
}
