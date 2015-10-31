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

public class EmployeeEditPanel extends FlexTable {
	
	private TextBox firstNameTextBox;
	private TextBox familyNameTextBox;
	private TextBox emailTextBox;
	private CheckBox fulltimeCheckbox;
	private CheckBox sportakCheckbox;
	private CheckBox morningSportakCheckbox;
	private CheckBox editorCheckbox;
	private CheckBox femaleCheckbox;
	
	private Employee employee;

	public EmployeeEditPanel(final RiaContext ctx) {
		final RiaMessages i18n = ctx.getI18n();
		
		int numRows=0;
		
		FlowPanel buttonPanel=new FlowPanel();
		Button saveButton=new Button(i18n.save());
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().savingEmployee());
		    		riaToObject();
		      		ctx.getRia().saveEmployee(employee);
		      		ctx.getStatusLine().showInfo(i18n.employee()+" '"+employee.getFullName()+"' "+i18n.saved()); // TODO i18n
				}
			}
		});
		buttonPanel.add(saveButton);
		
		Button deleteButton=new Button(i18n.delete());
		deleteButton.setStyleName("mf-button");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().deletingEmployee());
		      		ctx.getRia().deleteEmployee(employee);
		      		ctx.getStatusLine().showInfo("Employee '"+employee.getFullName()+"' deleted");
				}
			}
		});		
		buttonPanel.add(deleteButton);

		Button cancelButton=new Button(i18n.cancel());
		cancelButton.setStyleName("mf-buttonLooser");
		cancelButton.setTitle(i18n.discardChanges());
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
					ctx.getEmployeesTable().refresh(ctx.getState().getEmployees());
		      		ctx.getRia().showEmployeesTable();
				}
			}
		});		
		buttonPanel.add(cancelButton);
				
	    FlexCellFormatter cellFormatter = getFlexCellFormatter();
		cellFormatter.setColSpan(0, 0, 2);
		setWidget(++numRows, 0, buttonPanel);
		
		HTML html = new HTML(i18n.firstName());
		setWidget(++numRows, 0, html);
		firstNameTextBox = new TextBox();
		setWidget(numRows, 1, firstNameTextBox);
				
		html = new HTML(i18n.familyName());
		setWidget(++numRows, 0, html);
		familyNameTextBox = new TextBox();
		setWidget(numRows, 1, familyNameTextBox);

		html = new HTML(i18n.email());
		setWidget(++numRows, 0, html);
		emailTextBox = new TextBox();
		setWidget(numRows, 1, emailTextBox);
		
		html = new HTML(i18n.female());
		setWidget(++numRows, 0, html);
		femaleCheckbox = new CheckBox();
		femaleCheckbox.setValue(false);
		setWidget(numRows, 1, femaleCheckbox);

		html = new HTML(i18n.editor());
		setWidget(++numRows, 0, html);
		editorCheckbox = new CheckBox();
		editorCheckbox.setValue(false);
		setWidget(numRows, 1, editorCheckbox);

		html = new HTML(i18n.sportak());
		setWidget(++numRows, 0, html);
		sportakCheckbox = new CheckBox();
		sportakCheckbox.setValue(false);
		setWidget(numRows, 1, sportakCheckbox);

		html = new HTML(i18n.morningSportak());
		setWidget(++numRows, 0, html);
		morningSportakCheckbox = new CheckBox();
		morningSportakCheckbox.setValue(false);
		setWidget(numRows, 1, morningSportakCheckbox);
		
		html = new HTML(i18n.fulltime());
		setWidget(++numRows, 0, html);
		fulltimeCheckbox = new CheckBox();
		fulltimeCheckbox.setValue(false);
		setWidget(numRows, 1, fulltimeCheckbox);
		
		firstNameTextBox.setFocus(true);
	}
	
	public void refresh(Employee employee) {
		if(employee==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		objectToRia(employee);
	}

	private void objectToRia(Employee employee) {
		this.employee=employee;
		firstNameTextBox.setText(employee.getFirstname());
		firstNameTextBox.setFocus(true);
		familyNameTextBox.setText(employee.getFamilyname());
		emailTextBox.setText(employee.getEmail());
		femaleCheckbox.setValue(employee.isFemale());
		sportakCheckbox.setValue(employee.isSportak());
		morningSportakCheckbox.setValue(employee.isMortak());
		editorCheckbox.setValue(employee.isEditor());
		fulltimeCheckbox.setValue(employee.isFulltime());
	}

	private void riaToObject() {
		if(employee!=null) {
			employee.setEditor(editorCheckbox.getValue());
			employee.setFamilyname(familyNameTextBox.getText());
			employee.setFemale(femaleCheckbox.getValue());
			employee.setEmail(emailTextBox.getText());
			employee.setFirstname(firstNameTextBox.getText());
			employee.setFulltime(fulltimeCheckbox.getValue());
			employee.setSportak(sportakCheckbox.getValue());
			employee.setMortak(morningSportakCheckbox.getValue());
		}
	}
}