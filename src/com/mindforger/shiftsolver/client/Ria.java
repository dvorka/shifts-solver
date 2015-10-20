package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ria implements EntryPoint, ShiftSolverConstants {
	
	private RiaContext ctx;
	private RiaMessages i18n;
	
	public Ria() {
		ctx=new RiaContext(this);
		i18n = ctx.getI18n();		
	}
		
	public void onModuleLoad() {
		if(DEBUG) {
			// log to standard output
			ShiftSolverLogger.init(System.out);
			// debugging in GWT super dev mode ensuring that exception is thrown on console
			GWT.setUncaughtExceptionHandler(new GwtJavascriptClientExceptionHander());				
		}

		RootPanel statusPanel = RootPanel.get(CONTAINER_STATUS_LINE);
		statusPanel.add(ctx.getStatusLine());

		ctx.getService().getRiaBootImage(new AsyncCallback<RiaBootImageBean>() {
			public void onSuccess(RiaBootImageBean bean) {
				ctx.getStatusLine().showProgress(i18n.loadingEmployeesAndPreferences());
				
				RiaState riaState=new RiaState();
				riaState.setEmployees(bean.getEmployees());
				riaState.setPeriodPreferencesList(bean.getPeriodPreferencesList());
				ctx.setState(riaState);

				RootPanel pageTitlePanel = RootPanel.get(CONTAINER_PAGE_TITLE);
				RootPanel menuPanel = RootPanel.get(CONTAINER_MENU);
				RootPanel homePanel = RootPanel.get(CONTAINER_HOME);
				RootPanel employeesTablePanel = RootPanel.get(CONTAINER_EMPLOYEES_TABLE);
				RootPanel employeeEditPanel = RootPanel.get(CONTAINER_EMPLOYEE_EDITOR);
				RootPanel dlouhanTable = RootPanel.get(CONTAINER_DLOUHAN_TABLE);
				RootPanel dlouhanEditPanel = RootPanel.get(CONTAINER_DLOUHAN_EDITOR);
				RootPanel solutionTable = RootPanel.get(CONTAINER_SOLUTION_TABLE);
				RootPanel solutionViewPanel = RootPanel.get(CONTAINER_SOLUTION_VIEW);
				RootPanel solverProgressPanel = RootPanel.get(CONTAINER_SOLVER_PROGRESS);

				pageTitlePanel.add(ctx.getPageTitlePanel());
				menuPanel.add(ctx.getMenu());
				homePanel.add(ctx.getHomePanel());
				employeesTablePanel.add(ctx.getEmployeesTable());
				employeeEditPanel.add(ctx.getEmployeesEditPanel());
				dlouhanTable.add(ctx.getPeriodPreferencesTable());
				dlouhanEditPanel.add(ctx.getPeriodPreferencesEditPanel());
				solutionTable.add(ctx.getSolutionTable());
				solutionViewPanel.add(ctx.getSolutionViewPanel());
				solverProgressPanel.add(ctx.getSolverProgressPanel());

				showHome();

				ctx.getStatusLine().clear();
			}

			@Override
			public void onFailure(Throwable caught) {
				ctx.getStatusLine().showError("Unable to load RIA image - please reload application");
			}
		});
	}

	public void handleServiceError(Throwable caught) {
		final String errorMessage = caught.getMessage();
		GWT.log("Error: "+errorMessage, caught);
		ctx.getStatusLine().showError(i18n.ooops());
	}
		
	public void loadEmployee(String employeeId) {
		// TODO consider loading from the server
		Employee employee = ctx.getState().getEmployee(employeeId);
		ctx.getEmployeesEditPanel().refresh(employee);
		showEmployeeEditPanel();
	}

	public void loadPeriodPreferences(String periodPreferencesId) {
		// TODO consider loading from the server
		PeriodPreferences periodPreferences = ctx.getState().getPeriodPreferences(periodPreferencesId);
		ctx.getPeriodPreferencesEditPanel().refresh(periodPreferences);
		showPeriodPreferencesEditPanel();
	}

	private void hideAllContainers() {
		ctx.getPageTitlePanel().setHTML("");
		
		RootPanel.get(CONTAINER_EMPLOYEES_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_EMPLOYEE_EDITOR).setVisible(false);
		RootPanel.get(CONTAINER_DLOUHAN_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_DLOUHAN_EDITOR).setVisible(false);
		RootPanel.get(CONTAINER_SOLUTION_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_SOLUTION_VIEW).setVisible(false);
		RootPanel.get(CONTAINER_SOLVER_PROGRESS).setVisible(false);		
		RootPanel.get(CONTAINER_HOME).setVisible(false);		
	}
	
	public void showHome() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.home());
		RootPanel.get(CONTAINER_HOME).setVisible(true);
		ctx.getStatusLine().clear();
	}
	
	public void showEmployeesTable() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.employees());
		RootPanel.get(CONTAINER_EMPLOYEES_TABLE).setVisible(true);
	}

	public void showEmployeeEditPanel() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.employee());
		RootPanel.get(CONTAINER_EMPLOYEE_EDITOR).setVisible(true);
	}
	
	public void showPeriodPreferencesTable() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.periodPreferences());
		RootPanel.get(CONTAINER_DLOUHAN_TABLE).setVisible(true);
	}

	public void showPeriodPreferencesEditPanel() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.periodPreferences());
		RootPanel.get(CONTAINER_DLOUHAN_EDITOR).setVisible(true);
	}
		
	// TODO merge save and delete/update to single method
	public void saveEmployee(final Employee employee) {
		if(employee!=null) {
			ctx.getService().saveEmployee(employee, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to save employee "+employee.getFullName());
				}

				@Override
				public void onSuccess(Void result) {
					Employee[] employees = ctx.getState().getEmployees();
					if(employees!=null) {
						if(ctx.getState().getEmployee(employee.getKey())==null) {			
							List<Employee> list = new ArrayList<Employee>();
							for(Employee e:employees) list.add(e); // Arrays.asList() returns unmodifiable list
							list.add(employee);
							Employee[] newArray = list.toArray(new Employee[list.size()]);
							ctx.getState().setEmployees(newArray);
						} else {
							deleteOrUpdateEmployee(employee, false);					
				      		ctx.getMenu().setEmployeesCount(ctx.getState().getEmployees().length);
							return;
						}
					} else {
						employees=new Employee[1];
						employees[0]=employee;
						ctx.getState().setEmployees(employees);
					}
					ctx.getEmployeesTable().refresh(ctx.getState().getEmployees());
		      		ctx.getMenu().setEmployeesCount(ctx.getState().getEmployees().length);
				}				
			});			
		}
		showEmployeesTable();			
	}
	
	public void deleteEmployee(final Employee employee) {
		if(employee!=null) {
			ctx.getService().deleteEmployee(employee.getKey(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to delete employee "+employee.getFullName());
				}

				@Override
				public void onSuccess(Void result) {
					deleteOrUpdateEmployee(employee, true);
		      		ctx.getMenu().setEmployeesCount(ctx.getState().getEmployees().length);
				}
			});
		}
	}
	
	public void deleteOrUpdateEmployee(Employee employee, boolean delete) {
		if(employee!=null) {
			Employee[] employees = ctx.getState().getEmployees();
			if(employees!=null && ctx.getState().getEmployee(employee.getKey())!=null) {
				List<Employee> list = new ArrayList<Employee>();
				Employee victim=null;
				for(Employee e:employees) {
					list.add(e);
					if(e.getKey().equals(employee.getKey())) {
						victim=e;
					}
				}
				if(victim!=null) {
					list.remove(victim);					
					if(!delete) {
						list.add(employee);
					}
				}
				Employee[] newArray = list.toArray(new Employee[list.size()]);
				ctx.getState().setEmployees(newArray);
			}
			ctx.getEmployeesTable().refresh(ctx.getState().getEmployees());
		}
		showEmployeesTable();		
	}

	// TODO merge save and delete/update to single method
	public void savePeriodPreferences(final PeriodPreferences preferences) {
		if(preferences!=null) {
			ctx.getService().savePeriodPreferences(preferences, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to save period preferences "+preferences.getYear()+"/"+preferences.getMonth());
				}

				@Override
				public void onSuccess(Void result) {
					PeriodPreferences[] array = ctx.getState().getPeriodPreferencesArray();
					if(array!=null) {
						if(ctx.getState().getPeriodPreferences(preferences.getKey())==null) {			
							List<PeriodPreferences> list = new ArrayList<PeriodPreferences>();
							for(PeriodPreferences e:array) list.add(e);
							list.add(preferences);
							PeriodPreferences[] newArray = list.toArray(new PeriodPreferences[list.size()]);
							ctx.getState().setPeriodPreferencesList(newArray);
						} else {
							deleteOrUpdatePeriodPreferences(preferences, false);					
				      		ctx.getMenu().setPeriodPreferencesCount(ctx.getState().getPeriodPreferencesArray().length);
							return;
						}
					}
					ctx.getPeriodPreferencesTable().refresh(ctx.getState().getPeriodPreferencesArray());
		      		ctx.getMenu().setPeriodPreferencesCount(ctx.getState().getPeriodPreferencesArray().length);
				}
			});
		}
		showPeriodPreferencesTable();
	}

	public void deletePeriodPreferences(final PeriodPreferences preferences) {
		if(preferences!=null) {
			ctx.getService().deletePeriodPreferences(preferences.getKey(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to delete period preferences "+preferences.getYear()+"/"+preferences.getMonth());
				}

				@Override
				public void onSuccess(Void result) {
					deleteOrUpdatePeriodPreferences(preferences, true);
		      		ctx.getMenu().setPeriodPreferencesCount(ctx.getState().getPeriodPreferencesArray().length);
				}
			});
		}
	}
	
	public void deleteOrUpdatePeriodPreferences(PeriodPreferences pref, boolean delete) {
		if(pref!=null) {
			PeriodPreferences[] prefs = ctx.getState().getPeriodPreferencesArray();
			if(prefs!=null && ctx.getState().getPeriodPreferences(pref.getKey())!=null) {
				List<PeriodPreferences> list = new ArrayList<PeriodPreferences>();
				PeriodPreferences victim=null;
				for(PeriodPreferences e:prefs) {
					list.add(e);
					if(e.getKey().equals(pref.getKey())) {
						victim=e;
					}
				}
				if(victim!=null) {
					list.remove(victim);					
					if(!delete) {
						list.add(pref);
					}
				}
				PeriodPreferences[] newArray = list.toArray(new PeriodPreferences[list.size()]);
				ctx.getState().setPeriodPreferencesList(newArray);
			}
			ctx.getPeriodPreferencesTable().refresh(ctx.getState().getPeriodPreferencesArray());
		}
		showPeriodPreferencesTable();		
	}

	public void showSolutionViewPanel() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.solution());
		RootPanel.get(CONTAINER_SOLUTION_VIEW).setVisible(true);
	}

	public void showSolutionsTable() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.solutions());
		RootPanel.get(CONTAINER_SOLUTION_TABLE).setVisible(true);
	}

	public void showSolverProgressPanel() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML("Solver Progress"); // TODO i18n
		RootPanel.get(CONTAINER_SOLVER_PROGRESS).setVisible(true);
	}
}
