package com.mindforger.shiftsolver.client.ui;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeeEditPanel extends FlexTable {

	private RiaMessages i18n;
	private RiaContext ctx;
	
	private TextBox firstNameTextBox;
	private TextBox familyNameTextBox;
	private CheckBox fulltimeCheckbox;
	private CheckBox sportakCheckbox;
	private CheckBox editorCheckbox;
	private CheckBox femaleCheckbox;

	public EmployeeEditPanel(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		
		int numRows=0;
		
		// TODO i18n
		HTML html = new HTML("First name");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		firstNameTextBox = new TextBox();
		setWidget(numRows, 1, firstNameTextBox);
				
		// TODO i18n
		html = new HTML("Family&nbsp;name");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		familyNameTextBox = new TextBox();
		setWidget(numRows, 1, familyNameTextBox);
		
		// TODO i18n
		html = new HTML("Female");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		femaleCheckbox = new CheckBox();
		femaleCheckbox.setValue(false);
		setWidget(numRows, 1, femaleCheckbox);

		// TODO i18n
		html = new HTML("Editor");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		editorCheckbox = new CheckBox();
		editorCheckbox.setValue(false);
		setWidget(numRows, 1, editorCheckbox);

		// TODO i18n
		html = new HTML("Sportak");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		sportakCheckbox = new CheckBox();
		sportakCheckbox.setValue(false);
		setWidget(numRows, 1, sportakCheckbox);

		// TODO i18n
		html = new HTML("Fulltime");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		fulltimeCheckbox = new CheckBox();
		fulltimeCheckbox.setValue(false);
		setWidget(numRows, 1, fulltimeCheckbox);
		
		// CREATE button (call server to create, handler refreshes table)
		// UPDATE button (... same)
		// DELETE button (... same)
	}
		
	public void refresh(Employee employee) {
		if(employee==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		firstNameTextBox.setText(employee.getFirstname());
		familyNameTextBox.setText(employee.getFamilyname());
		femaleCheckbox.setValue(employee.isFemale());
		sportakCheckbox.setValue(employee.isSportak());
		editorCheckbox.setValue(employee.isEditor());
		fulltimeCheckbox.setValue(employee.isFulltime());
	}
}