package com.mindforger.shiftsolver.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ria implements EntryPoint, ShiftSolverConstants {
	
	private RiaContext ctx;
	private RiaMessages i18n;
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	@Deprecated
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	@Deprecated
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	public Ria() {
		ctx=new RiaContext(this);
		i18n = ctx.getI18n();
	}
	
	public void onModuleLoad() {
		
		ctx.getStatusLine().showProgress(i18n.loadingEmployeesAndPreferences());

//		ctx.getService().getRiaBootImage(new AsyncCallback<RiaBootImageBean>() {
//			public void onSuccess(RiaBootImageBean bean) {
//				ctx.getStatusLine().showProgress(i18n.initializingMf());
		
		createFooRiaState();
		
		ctx.getStatusLine().hideStatus();
		
		RootPanel statusPanel = RootPanel.get(CONTAINER_STATUS_LINE);
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
				
		statusPanel.add(ctx.getStatusLine());
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
//      }		
	}

	public void handleServiceError(Throwable caught) {
		final String errorMessage = caught.getMessage();
		GWT.log("Error: "+errorMessage, caught);
		ctx.getStatusLine().showError(i18n.ooops());
	}
	
	private void createFooRiaState() {
		RiaState state = new RiaState();
		List<Employee> employeesList=new ArrayList<Employee>();
		Employee lenka=new Employee();
		lenka.setKey("1");
		lenka.setFirstname("Lenka");
		lenka.setFamilyname("Strelenka");
		lenka.setEditor(true);
		lenka.setFulltime(false);
		lenka.setFemale(true);
		employeesList.add(lenka);
		Employee misa=new Employee();
		misa.setKey("2");
		misa.setFirstname("Misa");
		misa.setFamilyname("Plysak");
		misa.setEditor(true);
		misa.setFulltime(true);
		misa.setFemale(true);
		employeesList.add(misa);
		Employee mirek=new Employee();
		mirek.setKey("3");
		mirek.setFirstname("Mirek");
		mirek.setFamilyname("Drson");
		mirek.setEditor(true);
		mirek.setFulltime(true);
		mirek.setFemale(false);
		employeesList.add(mirek);
		state.setEmployees(employeesList.toArray(new Employee[employeesList.size()]));
		
		PeriodPreferences periodPreferences = new PeriodPreferences(2015, 10);
		periodPreferences.setKey("1");
		periodPreferences.setMonthDays(31);
		periodPreferences.setStartWeekDay(5);
		EmployeePreferences employeePreferences = new EmployeePreferences();
		employeePreferences.setPreferences(new ArrayList<DayPreference>());
		periodPreferences.addEmployeePreferences(lenka, employeePreferences);
		periodPreferences.addEmployeePreferences(misa, employeePreferences);
		periodPreferences.addEmployeePreferences(mirek, employeePreferences);
		PeriodPreferences[] periodPreferencesArray=new PeriodPreferences[1];
		periodPreferencesArray[0]=periodPreferences;
		state.setPeriodPreferencesList(periodPreferencesArray);
		
		ctx.setState(state);
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
		RootPanel.get(CONTAINER_HOME).setVisible(false);		
	}
	
	public void showHome() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.home());
		RootPanel.get(CONTAINER_HOME).setVisible(true);
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
	@Deprecated
	public void saveEmployee(Employee employee) {
		if(employee!=null) {
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
					return;
				}
			}
			ctx.getEmployeesTable().refresh(ctx.getState().getEmployees());
		}
		showEmployeesTable();		
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
	@Deprecated
	public void savePeriodPreferences(PeriodPreferences preferences) {
		if(preferences!=null) {
			PeriodPreferences[] array = ctx.getState().getPeriodPreferencesList();
			if(array!=null) {
				if(ctx.getState().getPeriodPreferences(preferences.getKey())==null) {			
					List<PeriodPreferences> list = new ArrayList<PeriodPreferences>();
					for(PeriodPreferences e:array) list.add(e); // Arrays.asList() returns unmodifiable list
					list.add(preferences);
					PeriodPreferences[] newArray = list.toArray(new PeriodPreferences[list.size()]);
					ctx.getState().setPeriodPreferencesList(newArray);
				} else {
					deleteOrUpdatePeriodPreferences(preferences, false);					
					return;
				}
			}
			ctx.getPeriodPreferencesTable().refresh(ctx.getState().getPeriodPreferencesList());
		}
		showPeriodPreferencesTable();		
	}
	public void deleteOrUpdatePeriodPreferences(PeriodPreferences employee, boolean delete) {
		if(employee!=null) {
			PeriodPreferences[] employees = ctx.getState().getPeriodPreferencesList();
			if(employees!=null && ctx.getState().getPeriodPreferences(employee.getKey())!=null) {
				List<PeriodPreferences> list = new ArrayList<PeriodPreferences>();
				PeriodPreferences victim=null;
				for(PeriodPreferences e:employees) {
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
				PeriodPreferences[] newArray = list.toArray(new PeriodPreferences[list.size()]);
				ctx.getState().setPeriodPreferencesList(newArray);
			}
			ctx.getPeriodPreferencesTable().refresh(ctx.getState().getPeriodPreferencesList());
		}
		showPeriodPreferencesTable();		
	}

	public void showSolution(PeriodSolution solution) {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.solution());
		RootPanel.get(CONTAINER_SOLUTION_VIEW).setVisible(true);
	}

	public void showSolutionsTable() {
		hideAllContainers();
		ctx.getPageTitlePanel().setHTML(i18n.solutions());
		RootPanel.get(CONTAINER_SOLUTION_TABLE).setVisible(true);
	}

	
	
//	@Deprecated
//	public void onModuleLoadExample() {
//		final Button sendButton = new Button("Send");
//		final TextBox nameField = new TextBox();
//		nameField.setText("GWT User");
//		final Label errorLabel = new Label();
//
//		// We can add style names to widgets
//		sendButton.addStyleName("sendButton");
//
//		// Add the nameField and sendButton to the RootPanel
//		// Use RootPanel.get() to get the entire body element
//		RootPanel.get("nameFieldContainer").add(nameField);
//		RootPanel.get("sendButtonContainer").add(sendButton);
//		RootPanel.get("errorLabelContainer").add(errorLabel);
//
//		// Focus the cursor on the name field when the app loads
//		nameField.setFocus(true);
//		nameField.selectAll();
//
//		// Create the popup dialog box
//		final DialogBox dialogBox = new DialogBox();
//		dialogBox.setText("Remote Procedure Call");
//		dialogBox.setAnimationEnabled(true);
//		final Button closeButton = new Button("Close");
//		// We can set the id of a widget by accessing its Element
//		closeButton.getElement().setId("closeButton");
//		final Label textToServerLabel = new Label();
//		final HTML serverResponseLabel = new HTML();
//		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
//		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//		dialogVPanel.add(textToServerLabel);
//		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//		dialogVPanel.add(serverResponseLabel);
//		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
//		dialogVPanel.add(closeButton);
//		dialogBox.setWidget(dialogVPanel);
//
//		// Add a handler to close the DialogBox
//		closeButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				dialogBox.hide();
//				sendButton.setEnabled(true);
//				sendButton.setFocus(true);
//			}
//		});
//
//		// Create a handler for the sendButton and nameField
//		class MyHandler implements ClickHandler, KeyUpHandler {
//			/**
//			 * Fired when the user clicks on the sendButton.
//			 */
//			public void onClick(ClickEvent event) {
//				sendNameToServer();
//			}
//
//			/**
//			 * Fired when the user types in the nameField.
//			 */
//			public void onKeyUp(KeyUpEvent event) {
//				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//					sendNameToServer();
//				}
//			}
//
//			/**
//			 * Send the name from the nameField to the server and wait for a response.
//			 */
//			private void sendNameToServer() {
//				// First, we validate the input.
//				errorLabel.setText("");
//				String textToServer = nameField.getText();
//				if (!FieldVerifier.isValidName(textToServer)) {
//					errorLabel.setText("Please enter at least four characters");
//					return;
//				}
//
//				// Then, we send the input to the server.
//				sendButton.setEnabled(false);
//				textToServerLabel.setText(textToServer);
//				serverResponseLabel.setText("");
//				greetingService.greetServer(textToServer,
//						new AsyncCallback<String>() {
//							public void onFailure(Throwable caught) {
//								// Show the RPC error message to the user
//								dialogBox
//										.setText("Remote Procedure Call - Failure");
//								serverResponseLabel
//										.addStyleName("serverResponseLabelError");
//								serverResponseLabel.setHTML(SERVER_ERROR);
//								dialogBox.center();
//								closeButton.setFocus(true);
//							}
//
//							public void onSuccess(String result) {
//								dialogBox.setText("Remote Procedure Call");
//								serverResponseLabel
//										.removeStyleName("serverResponseLabelError");
//								serverResponseLabel.setHTML(result);
//								dialogBox.center();
//								closeButton.setFocus(true);
//							}
//						});
//			}
//		}
//
//		// Add a handler to send the name to the server
//		MyHandler handler = new MyHandler();
//		sendButton.addClickHandler(handler);
//		nameField.addKeyUpHandler(handler);
//	}
}
