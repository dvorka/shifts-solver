package com.mindforger.shiftsolver.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.mindforger.shiftsolver.client.ui.DlouhanEditPanel;
import com.mindforger.shiftsolver.client.ui.DlouhanTable;
import com.mindforger.shiftsolver.client.ui.EmployeeEditPanel;
import com.mindforger.shiftsolver.client.ui.EmployeesTable;
import com.mindforger.shiftsolver.client.ui.HomePanel;
import com.mindforger.shiftsolver.client.ui.PeriodSolutionTable;
import com.mindforger.shiftsolver.client.ui.PeriodSolutionViewPanel;
import com.mindforger.shiftsolver.client.ui.StatusLine;
import com.mindforger.shiftsolver.client.ui.menu.LeftMenubar;
import com.mindforger.shiftsolver.shared.FieldVerifier;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;
import com.mindforger.shiftsolver.shared.model.PageTitlePanel;

/**
 * This is an analogy of Spring Application Context. It gives overview of the most
 * important beans, simplifies beans and avoid singletons across the source code:
 * <ul>
 *   <li>get*() methods are used to get SINGLETON objects</li>
 *   <li>create*() methods are used to get a new instance of the initialized object - FACTORY</li>
 *   <li>All beans are instantiated in the ctx's constructor using
 *       either default constructor or the constructor that takes ctx as parameter</li>
 *   <li>Ctx does LAZY initialization of beans in ctx.get*() methods.</li>
 * </ul>
 */
public class RiaContext implements ShiftSolverConstants {

	// RIA
	private Ria ria;
	
	// i18n and l10n
	private RiaMessages i18n;
	
	// server
	private GreetingServiceAsync service;
	
	// validation
	private FieldVerifier fieldVerifier;

	// UI components
	private StatusLine statusLine;
	private PageTitlePanel pageTitlePanel;
	private LeftMenubar menu;
	private HomePanel homePanel;
	private EmployeesTable employeesTable;
	private EmployeeEditPanel employeeEditPanel;
	private DlouhanTable dlouhanTable;
	private DlouhanEditPanel dlouhanPanel;
	private PeriodSolutionTable periodSolutionTable;
	private PeriodSolutionViewPanel periodSolutionViewPanel;
	
	// data
	private RiaState state;
	
	private Set<Object> initialized=new HashSet<Object>();
	
	public RiaContext(Ria ria) {
		this.ria=ria;
		
		i18n=GWT.create(RiaMessages.class);		
		service=GWT.create(GreetingService.class);		
		fieldVerifier=new FieldVerifier();
		state=new RiaState();
		
		// UI
		statusLine=new StatusLine(this);
		pageTitlePanel=new PageTitlePanel();
		menu=new LeftMenubar(this);
		homePanel=new HomePanel(this);
		employeesTable=new EmployeesTable(this);
		employeeEditPanel=new EmployeeEditPanel(this);
		dlouhanTable=new DlouhanTable(this);		
		dlouhanPanel=new DlouhanEditPanel(this);
		periodSolutionTable=new PeriodSolutionTable(this);
		periodSolutionViewPanel=new PeriodSolutionViewPanel(this);
	}

	public RiaMessages getI18n() {
		return i18n;
	}

	public GreetingServiceAsync getService() {
		return service;
	}

	public FieldVerifier getFieldVerifier() {
		return fieldVerifier;
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public LeftMenubar getMenu() {
		if(!initialized.contains(menu)) {
			initialized.add(menu);
			menu.init();
		}
		return menu;
	}
	
	public EmployeesTable getEmployeesTable() {
		if(!initialized.contains(employeesTable)) {
			initialized.add(employeesTable);
			employeesTable.init();
		}
		return employeesTable;
	}

	public EmployeeEditPanel getEmployeesEditPanel() {
		return employeeEditPanel;
	}
	
	public DlouhanTable getDlouhanTable() {
		if(!initialized.contains(dlouhanTable)) {
			initialized.add(dlouhanTable);
			dlouhanTable.init();
		}
		return dlouhanTable;
	}
	
	public DlouhanEditPanel getDlouhanEditPanel() {
		return dlouhanPanel;
	}
	
	public RiaState getState() {
		return state;
	}	
	
	public void setState(RiaState state) {
		this.state = state;
	}

	public Ria getRia() {
		return ria;
	}

	public PeriodSolutionTable getSolutionTable() {
		if(!initialized.contains(periodSolutionTable)) {
			initialized.add(periodSolutionTable);
			periodSolutionTable.init();
		}
		return periodSolutionTable;
	}

	public PeriodSolutionViewPanel getSolutionViewPanel() {
		return periodSolutionViewPanel;
	}

	public PageTitlePanel getPageTitlePanel() {
		return pageTitlePanel;
	}
	
	public HomePanel getHomePanel() {
		return homePanel;
	}
}
