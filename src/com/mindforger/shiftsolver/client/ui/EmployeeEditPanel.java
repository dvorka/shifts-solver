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

	private RiaMessages i18n;
	private RiaContext ctx;
	
	private TextBox firstNameTextBox;
	private TextBox familyNameTextBox;
	private CheckBox fulltimeCheckbox;
	private CheckBox sportakCheckbox;
	private CheckBox morningSportakCheckbox;
	private CheckBox editorCheckbox;
	private CheckBox femaleCheckbox;
	
	private Employee employee;

	public EmployeeEditPanel(final RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
		
		int numRows=0;
		
		FlowPanel buttonPanel=new FlowPanel();
		Button saveButton=new Button("Save"); // TODO i18n
		saveButton.setStyleName("mf-button");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().savingEmployee());
		    		riaToObject();
		      		ctx.getRia().saveEmployee(employee);
		      		ctx.getStatusLine().hideStatus();					
				}
			}
		});		
		buttonPanel.add(saveButton);
		Button cancelButton=new Button("Cancel"); // TODO i18n
		cancelButton.setStyleName("mf-buttonLooser");
		cancelButton.setTitle("Discard changes"); // TODO i18n
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
		      		ctx.getRia().showEmployeesTable();
				}
			}
		});		
		buttonPanel.add(cancelButton);
		Button deleteButton=new Button("Delete"); // TODO i18n
		deleteButton.setStyleName("mf-button");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(employee!=null) {
		    		ctx.getStatusLine().showProgress(ctx.getI18n().deletingEmployee());
		      		ctx.getRia().deleteOrUpdateEmployee(employee, true);
		      		ctx.getStatusLine().hideStatus();					
				}
			}
		});		
		buttonPanel.add(deleteButton);
		
	    FlexCellFormatter cellFormatter = getFlexCellFormatter();
		cellFormatter.setColSpan(0, 0, 2);
		setWidget(++numRows, 0, buttonPanel);
		
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
		html = new HTML("Morning sportak");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		morningSportakCheckbox = new CheckBox();
		morningSportakCheckbox.setValue(false);
		setWidget(numRows, 1, morningSportakCheckbox);
		
		// TODO i18n
		html = new HTML("Fulltime");
		// TODO css
		html.setStyleName("mf-progressHtml");
		setWidget(++numRows, 0, html);
		fulltimeCheckbox = new CheckBox();
		fulltimeCheckbox.setValue(false);
		setWidget(numRows, 1, fulltimeCheckbox);		
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
		familyNameTextBox.setText(employee.getFamilyname());
		femaleCheckbox.setValue(employee.isFemale());
		sportakCheckbox.setValue(employee.isSportak());
		morningSportakCheckbox.setValue(employee.isMorningSportak());
		editorCheckbox.setValue(employee.isEditor());
		fulltimeCheckbox.setValue(employee.isFulltime());
	}

	private void riaToObject() {
		if(employee!=null) {
			employee.setEditor(editorCheckbox.getValue());
			employee.setFamilyname(familyNameTextBox.getText());
			employee.setFemale(femaleCheckbox.getValue());
			employee.setFirstname(firstNameTextBox.getText());
			employee.setFulltime(fulltimeCheckbox.getValue());
			employee.setSportak(sportakCheckbox.getValue());
			employee.setMorningSportak(morningSportakCheckbox.getValue());
		}
	}
}