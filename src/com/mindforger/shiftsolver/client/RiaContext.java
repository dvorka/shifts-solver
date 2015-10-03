package com.mindforger.shiftsolver.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.mindforger.shiftsolver.client.ui.EmployeesTable;
import com.mindforger.shiftsolver.client.ui.StatusLine;
import com.mindforger.shiftsolver.shared.FieldVerifier;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

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
	private EmployeesTable employeesTable;
	
	// data
	private RiaState state;
	
	private Set<Object> initialized=new HashSet<Object>();
	
	public RiaContext(Ria ria) {
		this.ria=ria;
		
		i18n=GWT.create(RiaMessages.class);		
		service=GWT.create(GreetingService.class);		
		fieldVerifier=new FieldVerifier();
		
		// UI
		statusLine=new StatusLine(this);
		employeesTable=new EmployeesTable(this);		
		state=new RiaState();
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

	public EmployeesTable getEmployeesTable() {
		if(!initialized.contains(employeesTable)) {
			initialized.add(employeesTable);
			employeesTable.init();
		}
		return employeesTable;
	}

	public RiaState getState() {
		return state;
	}	
	
	public Ria getRia() {
		return ria;
	}
}
