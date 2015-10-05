package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.model.Employee;

public class SolverProgressPanel extends FlexTable {

	private RiaMessages i18n;
	private RiaContext ctx;
	
	private TextBox progressPercentTextBox;

	public SolverProgressPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
						
		// TODO i18n
		HTML html = new HTML("Progress");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(0, 0, html);
		progressPercentTextBox = new TextBox();
		setWidget(0, 1, progressPercentTextBox);
		refresh(0);
	}
	
	public void refresh(int percent) {
		objectToRia(employee);
	}

	private void objectToRia(int percent) {
	    progressPercentTextBox.setText(percent+"%");
	}
}
