package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class PrintButtonPanel extends Button implements ShiftSolverConstants {
	
	private boolean visible;
	
	public PrintButtonPanel(final RiaContext ctx) {
		setStyleName("s2-printButton");
		setText(ctx.getI18n().print());
		visible=true;
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctx.getStatusLine().clear();
				if(visible) {
					RootPanel.get(CONTAINER_MENU).setVisible(false);
					RootPanel.get(CONTAINER_TITLE_LOGO).setVisible(false);
					RootPanel.get(CONTAINER_PAGE_TITLE).setVisible(false);
					ctx.getPreferencesPanel().print(false);
					ctx.getSolutionPanel().print(false);
					ctx.getEmployeesTable().print(false);
					visible=false;
				} else {
					RootPanel.get(CONTAINER_MENU).setVisible(true);					
					RootPanel.get(CONTAINER_TITLE_LOGO).setVisible(true);
					RootPanel.get(CONTAINER_PAGE_TITLE).setVisible(true);
					ctx.getPreferencesPanel().print(true);
					ctx.getSolutionPanel().print(true);
					ctx.getEmployeesTable().print(true);
					visible=true;
				}
			}
		});		
	}
}
