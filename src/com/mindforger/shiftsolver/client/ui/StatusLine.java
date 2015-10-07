package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class StatusLine extends FlexTable implements ShiftSolverConstants {
	RootPanel panel;
	private RiaMessages i18n;
	int row;
		
	public StatusLine(RiaContext ctx) {
		this.i18n = ctx.getI18n();
		
		panel=RootPanel.get(CONTAINER_STATUS_LINE);
		// hide "Loading ShiftsSolver..." message
		panel.getElement().getChild(0).removeFromParent();
		
		panel.setVisible(true);
		row=0;
	}

	private void setHtml(String text) {
		setWidget(row++%3, 0, new HTML(row+" "+text));
	}
	
	public void showInfo(String text) {
		setStyleName(CSS_INFO_STYLE);
		setHtml(text);
	}

	public void showProgress(String text) {
		setStyleName(CSS_PROGRESS_STYLE);
		setHtml(text);
	}

	public void showError(String text) {
		setStyleName(CSS_ERROR_STYLE);
		if(text==null || "".equals(text)) {
			text=i18n.ooops();
		}
		setHtml(text);
	}
	
	public void showHelp(String text) {
		setStyleName(CSS_HELP_STYLE);
		setHtml(text);		
	}	

	public void clear() {
		setStyleName(CSS_NOTHING_STYLE);
		setHtml("");
	}
}
