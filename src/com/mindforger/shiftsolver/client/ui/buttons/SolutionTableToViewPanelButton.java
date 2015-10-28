package com.mindforger.shiftsolver.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.mindforger.shiftsolver.client.RiaContext;

public class SolutionTableToViewPanelButton extends Button {
	
	public SolutionTableToViewPanelButton(
			final String solutionId,
			int year,
			int month,
			final String cssClass,
			final RiaContext ctx) {
		setText(year+"/"+month);
		// TODO i18n
		setTitle("Year/Month");
		setStyleName(cssClass);

		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {								
	    		ctx.getStatusLine().showProgress("Loading solution...");
	      		ctx.getRia().loadSolution(solutionId);
	      		ctx.getStatusLine().showInfo("Solution loaded");
			}
		});
	}
}
