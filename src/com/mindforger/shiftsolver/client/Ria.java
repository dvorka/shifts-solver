package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.client.solver.EmployeeAllocation;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.ShiftSolverLogger;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;
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
				riaState.setPeriodSolutions(bean.getPeriodSolutions());
				ctx.setState(riaState);

				RootPanel pageTitlePanel = RootPanel.get(CONTAINER_PAGE_TITLE);
				RootPanel menuPanel = RootPanel.get(CONTAINER_MENU);
				RootPanel homePanel = RootPanel.get(CONTAINER_HOME);
				RootPanel settingsPanel = RootPanel.get(CONTAINER_SETTINGS);
				RootPanel employeesTablePanel = RootPanel.get(CONTAINER_EMPLOYEES_TABLE);
				RootPanel employeeEditPanel = RootPanel.get(CONTAINER_EMPLOYEE_EDITOR);
				RootPanel dlouhanTable = RootPanel.get(CONTAINER_DLOUHAN_TABLE);
				RootPanel dlouhanEditPanel = RootPanel.get(CONTAINER_DLOUHAN_EDITOR);
				RootPanel solutionsTable = RootPanel.get(CONTAINER_SOLUTION_TABLE);
				RootPanel solutionViewPanel = RootPanel.get(CONTAINER_SOLUTION_VIEW);
				RootPanel solverProgressPanel = RootPanel.get(CONTAINER_SOLVER_PROGRESS);
				RootPanel solverNoSolutionPanel= RootPanel.get(CONTAINER_SOLVER_NO_SOLUTION);
				RootPanel printButtonPanel= RootPanel.get(CONTAINER_PRINT_BUTTON);

				pageTitlePanel.add(ctx.getPageTitlePanel());
				menuPanel.add(ctx.getMenu());
				homePanel.add(ctx.getHomePanel());
				employeesTablePanel.add(ctx.getEmployeesTable());
				employeeEditPanel.add(ctx.getEmployeesEditPanel());
				dlouhanTable.add(ctx.getPeriodPreferencesTable());
				dlouhanEditPanel.add(ctx.getPreferencesPanel());
				solutionsTable.add(ctx.getSolutionsTable());
				solutionViewPanel.add(ctx.getSolutionPanel());
				solverProgressPanel.add(ctx.getSolverProgressPanel());
				solverNoSolutionPanel.add(ctx.getSolverNoSolutionPanel());
				settingsPanel.add(ctx.getSettingsPanel());
				printButtonPanel.add(ctx.getPrintButtonPanel());

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
		ctx.getPreferencesPanel().refresh(periodPreferences);
		showPeriodPreferencesEditPanel();
	}

	public void loadSolution(String solutionId) {
		// TODO consider loading from the server
		PeriodSolution solution= ctx.getState().getPeriodSolution(solutionId);
		List<Employee> employees=new ArrayList<Employee>();
		for(String k:solution.getEmployeeJobs().keySet()) {
			if(k!=null) {
				if(!ShiftSolver.FERDA_KEY.equals(k)) {
					employees.add(ctx.getState().getEmployee(k));					
				}
			}
		}
		List<EmployeeAllocation> allocations=EmployeeAllocation.calculateEmployeeAllocations(
				ctx.getState().getPeriodPreferences(solution.getPeriodPreferencesKey()), 
				solution,
				employees);
		ctx.getSolutionPanel().refresh(solution, allocations);
		showSolutionViewPanel();
	}
	
	private void hideAllContainers() {
		ctx.getPageTitlePanel().setHTML("");
		
		RootPanel.get(CONTAINER_HOME).setVisible(false);		
		RootPanel.get(CONTAINER_EMPLOYEES_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_EMPLOYEE_EDITOR).setVisible(false);
		RootPanel.get(CONTAINER_DLOUHAN_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_DLOUHAN_EDITOR).setVisible(false);
		RootPanel.get(CONTAINER_SOLUTION_TABLE).setVisible(false);
		RootPanel.get(CONTAINER_SOLUTION_VIEW).setVisible(false);
		RootPanel.get(CONTAINER_SOLVER_PROGRESS).setVisible(false);		
		RootPanel.get(CONTAINER_SOLVER_NO_SOLUTION).setVisible(false);		
		RootPanel.get(CONTAINER_SETTINGS).setVisible(false);		
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

	public void showSolverNoSolutionPanel() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML("No Solution"); // TODO i18n
		RootPanel.get(CONTAINER_SOLVER_NO_SOLUTION).setVisible(true);
	}

	public void showSettings() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.settings());
		RootPanel.get(CONTAINER_SETTINGS).setVisible(true);
		ctx.getStatusLine().clear();		
	}
	
	// TODO merge save and delete/update to single method
	public void saveEmployee(final Employee employee) {
		if(employee!=null) {
			ctx.getService().saveEmployee(employee, new AsyncCallback<Employee>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to save employee "+employee.getFullName());
				}

				@Override
				public void onSuccess(Employee result) {
					employee.setModified(result.getModified());
					employee.setModifiedPretty(result.getModifiedPretty());
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
			ctx.getService().savePeriodPreferences(preferences, new AsyncCallback<PeriodPreferences>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to save period preferences "+preferences.getYear()+"/"+preferences.getMonth());
				}

				@Override
				public void onSuccess(PeriodPreferences result) {
					preferences.setModified(result.getModified());
					preferences.setModifiedPretty(result.getModifiedPretty());
					PeriodPreferences[] array = ctx.getState().getPeriodPreferencesArray();
					if(array!=null) {
						if(ctx.getState().getPeriodPreferences(preferences.getKey())==null) {			
							List<PeriodPreferences> list = new ArrayList<PeriodPreferences>();
							for(PeriodPreferences p:array) list.add(p);
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

	// TODO merge save and delete/update to single method
	public void savePeriodSolution(final PeriodSolution solution) {
		if(solution!=null) {
			ctx.getService().savePeriodSolution(solution, new AsyncCallback<PeriodSolution>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to save period solution!");
				}

				@Override
				public void onSuccess(PeriodSolution result) {
					solution.setKey(result.getKey());
					solution.setModified(result.getModified());
					solution.setModifiedPretty(result.getModifiedPretty());					
					PeriodSolution[] array = ctx.getState().getPeriodSolutions();
					if(array!=null) {
						if(ctx.getState().getPeriodSolution(solution.getKey())==null) {			
							List<PeriodSolution> list = new ArrayList<PeriodSolution>();
							for(PeriodSolution s:array) list.add(s);
							list.add(solution);
							PeriodSolution[] newArray = list.toArray(new PeriodSolution[list.size()]);
							ctx.getState().setPeriodSolutions(newArray);
						} else {
							deleteOrUpdatePeriodSolution(solution, false);					
				      		ctx.getMenu().setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
							return;
						}
					}
					ctx.getSolutionsTable().refresh(ctx.getState().getPeriodSolutions());
					ctx.getMenu().setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
					showSolutionsTable();
				}
			});
		} else {
			showSolutionsTable();
		}
	}

	public void deletePeriodSolution(final PeriodSolution solution) {
		if(solution!=null) {
			ctx.getService().deletePeriodSolution(solution.getKey(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					ctx.getStatusLine().showError("Unable to delete period solution "+solution.getYear()+"/"+solution.getMonth());
				}

				@Override
				public void onSuccess(Void result) {
					deleteOrUpdatePeriodSolution(solution, true);
		      		ctx.getMenu().setPeriodSolutionsCount(ctx.getState().getPeriodSolutions().length);
				}
			});
		}
	}
	
	public void deleteOrUpdatePeriodSolution(PeriodSolution pref, boolean delete) {
		if(pref!=null) {
			PeriodSolution[] prefs = ctx.getState().getPeriodSolutions();
			if(prefs!=null && ctx.getState().getPeriodSolution(pref.getKey())!=null) {
				List<PeriodSolution> list = new ArrayList<PeriodSolution>();
				PeriodSolution victim=null;
				for(PeriodSolution e:prefs) {
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
				PeriodSolution[] newArray = list.toArray(new PeriodSolution[list.size()]);
				ctx.getState().setPeriodSolutions(newArray);
			}
			ctx.getSolutionsTable().refresh(ctx.getState().getPeriodSolutions());
		}
		showSolutionsTable();
	}	
}
