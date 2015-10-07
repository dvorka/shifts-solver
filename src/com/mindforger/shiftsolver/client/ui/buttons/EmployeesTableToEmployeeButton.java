package com.mindforger.shiftsolver.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.mindforger.shiftsolver.client.RiaContext;

public class EmployeesTableToEmployeeButton extends Button {
	
	public EmployeesTableToEmployeeButton(
			final String employeeId,
			final String fullname,
			final String cssClass,
			final RiaContext ctx) {
		setText(fullname);
		setTitle(fullname);
		setStyleName(cssClass);

		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
	    		ctx.getStatusLine().showProgress(ctx.getI18n().loadingEmployee());
	      		ctx.getRia().loadEmployee(employeeId);
	      		ctx.getStatusLine().showInfo("Employees successufuly loaded");
			}
		});
	}
}
