package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.solver.ShiftSolver;
import com.mindforger.shiftsolver.client.solver.ShiftSolverConfigurer;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class HomePanel extends VerticalPanel implements ShiftSolverConstants {

	private RiaContext ctx;
	
	private TextBox iterationsLimit;
	private CheckBox afternoonToMorningGap;
	private CheckBox nightToAfternoonGap;
	
	private ShiftSolverConfigurer solver;
	
	public HomePanel(RiaContext ctx) {
		this.ctx=ctx;
		solver = ctx.getSolver();
	}
	
	public void refresh(ShiftSolver solver) {
		iterationsLimit.setText(""+ctx.getSolver().stepsLimit);
		afternoonToMorningGap.setValue(solver.isEnforceAfternoonTo8am());
		nightToAfternoonGap.setValue(solver.isEnforceNightToAfternoon());		
	}
	
	public void init() {
		HTML html = new HTML(
				"<a class='mf-docLink' href='http://www.github.com/dvorka/shifts-solver/' target='blank'>ShiftsSolver</a> "
				+ "is a web application for creation of schedules in shift based operations. For a team of employees with different "
				+ "roles and preferences (e.g. I'm on holidays, I prefer morning shift, I'm unable to make night shift) "
				+ "is created a schedule for month.");
		html.setTitle("Study documentation");
		html.setStyleName("mf-cheatSheetReadHelp");
		add(html);

		html=new HTML("Solver settings:");
		html.setStyleName("s2-homeSettingsLabel");
		add(html);
		
		FlexTable settingsTable=new FlexTable();
		add(settingsTable);
		int rows=0;
		
		html = new HTML("Enforce iterations limit:");
		settingsTable.setWidget(rows, 0, html);
		iterationsLimit = new TextBox();
		iterationsLimit.setText(""+ctx.getSolver().stepsLimit);
		iterationsLimit.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				try {
					long limit=Long.parseLong(iterationsLimit.getText());
					ctx.getStatusLine().showInfo("Solver iterations limit set to "+limit);
					solver.setIterationsLimit(limit);
				} catch(Exception e) {					
				}
			}
		});
		settingsTable.setWidget(rows++, 1, iterationsLimit);

		html = new HTML("Enforce afternoon to 8AM morning gap:");
		settingsTable.setWidget(rows, 0, html);
		afternoonToMorningGap = new CheckBox();
		afternoonToMorningGap.setValue(solver.isEnforceAfternoonTo8am());
		afternoonToMorningGap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				solver.setEnforceAfternoonTo8am(afternoonToMorningGap.getValue());
			}
		});
		settingsTable.setWidget(rows++, 1, afternoonToMorningGap);

		html = new HTML("Enforce night to afternoon gap:");
		settingsTable.setWidget(rows, 0, html);
		nightToAfternoonGap = new CheckBox();
		nightToAfternoonGap.setValue(solver.isEnforceNightToAfternoon());
		nightToAfternoonGap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				solver.setEnforceNightToAfternoon(nightToAfternoonGap.getValue());
			}
		});
		settingsTable.setWidget(rows++, 1, nightToAfternoonGap);		
	}
}
