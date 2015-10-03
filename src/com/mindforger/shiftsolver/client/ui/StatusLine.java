package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class StatusLine extends HTML implements ShiftSolverConstants {
	RootPanel panel;
	private RiaMessages i18n;
		
	public StatusLine(RiaContext ctx) {
		this.i18n = ctx.getI18n();
		
		panel=RootPanel.get(CONTAINER_STATUS_LINE);
		// hide "Loading ShiftsSolver..." message
		panel.getElement().getChild(0).removeFromParent();
	}

	public void showInfo(String text) {
		showStatus();
		setStyleName(CSS_INFO_STYLE);
		setHTML(text);
	}

	public void showProgress(String text) {
		showStatus();
		setStyleName(CSS_PROGRESS_STYLE);
		setHTML(text);
	}

	public void showError(String text) {
		showStatus();
		setStyleName(CSS_ERROR_STYLE);
		if(text==null || "".equals(text)) {
			text=i18n.ooops();
		}
		setHTML(text);
	}

	public void showHelp(String text) {
		showStatus();
		setStyleName(CSS_HELP_STYLE);
		setHTML(text);		
	}
	
	public void hideStatus() {
		panel.setVisible(false);		
	}
	
	public void showStatus() {
		panel.setVisible(true);				
	}
}
