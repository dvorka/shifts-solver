package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class HomePanel extends VerticalPanel implements ShiftSolverConstants {

	private RiaContext ctx;
	
	private Button employeesButton;
	private Button preferencesButton;
	private Button solutionsButton;
		
	public HomePanel(RiaContext ctx) {
		this.ctx=ctx;
	}
	
	public void refresh() {
		// TODO show and hide buttons
	}
	
	public void init() {
		HTML html = new HTML(ctx.getI18n().homePageApplicationDescription());
		html.setStyleName("mf-cheatSheetReadHelp");
		add(html);
		
		employeesButton=new Button();
		employeesButton.setText(ctx.getI18n().employees());
	    employeesButton.setStyleName("mf-helpGuideButton");
	    employeesButton.addStyleName("mf-newGoalButton");	    		
		
		preferencesButton=new Button();
		preferencesButton.setText(ctx.getI18n().periodPreferences());
	    preferencesButton.setStyleName("mf-mrGuideButton");
	    preferencesButton.addStyleName("mf-newGoalButton");
		
		solutionsButton=new Button();
		solutionsButton.setText(ctx.getI18n().solutions());
		solutionsButton.setStyleName("mf-button");
		
		FlexTable table=new FlexTable();
		table.setWidget(0,0,new HTML(""));
		table.setWidget(1,0,employeesButton);		
		html = new HTML(">");
		table.setWidget(1,1,html);		
		table.setWidget(1,2,preferencesButton);
		html = new HTML(">");
		table.setWidget(1,3,html);		
		table.setWidget(1,4,solutionsButton);		
		add(table);
	}
}
