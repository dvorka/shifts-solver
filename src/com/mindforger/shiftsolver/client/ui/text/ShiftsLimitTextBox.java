package com.mindforger.shiftsolver.client.ui.text;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;

public class ShiftsLimitTextBox extends TextBox {

	private int shiftLimit;
	
	public ShiftsLimitTextBox(final RiaContext ctx, final EmployeePreferences employeePreferences) {
		shiftLimit=employeePreferences.getShiftsLimit();
		
		setStyleName("s2-preferencesJobsTextBox");
		setTitle(ctx.getI18n().job());
		setText(""+shiftLimit);

		addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					shiftLimit=(Integer.parseInt(getText()));
					ctx.getStatusLine().showInfo("Shift limit set to "+getText()); // TODO i18n
				} catch(Exception e) {
					ctx.getStatusLine().showError(getText()+" is not valid shift limit number"); // TODO i18n
				}
			}
		});
	}

	public int getShiftLimit() {
		return shiftLimit;
	}	
}
