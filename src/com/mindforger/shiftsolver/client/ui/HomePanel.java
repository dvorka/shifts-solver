package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.ShiftSolverConstants;

public class HomePanel extends VerticalPanel implements ShiftSolverConstants {
	
	private RiaContext ctx;
	private RiaMessages i18n;

	public HomePanel(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	// TODO i18n
	public void init() {
		setStyleName("mf-cheatSheet");

		HTML html;
										
		html = new HTML("<span class='mf-hint'>3. Documentation</span> Check <a class='mf-docLink' href='http://www.github.com/dvorka/shifts-solver/' target='blank'>ShiftSolver documentation</a>.");
		html.setTitle("Study documentation");
		html.setStyleName("mf-cheatSheetReadHelp");
		add(html);
		
		html = new HTML("<span class='mf-hint mf-hintGreen'>... follow green color</span>");
		html.setStyleName("mf-cheatSheetGreenColor");
		add(html);		
	}
}
