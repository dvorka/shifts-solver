package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class HomePanel extends VerticalPanel implements ShiftSolverConstants {
	
	public HomePanel(RiaContext ctx) {
	}
	
	public void init() {
		setStyleName("mf-cheatSheet");

		HTML html = new HTML(
				"<a class='mf-docLink' href='http://www.github.com/dvorka/shifts-solver/' target='blank'>ShiftsSolver</a> "
				+ "is a web application for creation of schedules in shift based operations. For a team of employees with different "
				+ "roles and preferences (e.g. I'm on holidays, I prefer morning shift, I'm unable to make night shift) "
				+ "is created a schedule for month.");
		html.setTitle("Study documentation");
		html.setStyleName("mf-cheatSheetReadHelp");
		add(html);
		
		// TODO Solver settings (taken from this panel):
		// TODO   Step limit
		// TODO   ...
	}
}
