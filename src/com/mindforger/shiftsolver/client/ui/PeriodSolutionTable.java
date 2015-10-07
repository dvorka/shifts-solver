package com.mindforger.shiftsolver.client.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.PeriodPreferencesTableToEditorButton;
import com.mindforger.shiftsolver.client.ui.buttons.TableSetSortingButton;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorPeriodPreferencesByYearAndMonth;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodSolutionTable extends FlexTable implements SortableTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public PeriodSolutionTable(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public void init() {
		// TODO style rename
		addStyleName("mf-growsTable");
		sortCriteria=TableSortCriteria.BY_YEAR_AND_MONTH;
		sortIsAscending=true;
		refresh(ctx.getState().getPeriodPreferencesList());
	}

	public void refreshWithNewSortingCriteria() {
		refresh(ctx.getState().getPeriodPreferencesList());
	}
	
	public void refresh(PeriodPreferences[] result) {
		if(result==null || result.length==0) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}
				
		Comparator<PeriodPreferences> comparator;
		switch(sortCriteria) {
		case BY_YEAR_AND_MONTH:
		default:
			comparator=new ComparatorPeriodPreferencesByYearAndMonth(sortIsAscending);
			break;
		}
		
		Arrays.sort(result, comparator);
		
		removeAllRows();
		addRows(result);
	}
	
	private void addRows(PeriodPreferences[] result) {
		addNewPeriodPreferencesRow();
		addTableTitle();
		if(result!=null) {
			for (int i = 0; i < result.length; i++) {
				addRow(
						result[i].getKey(), 
						result[i].getYear(),
						result[i].getMonth());
			}			
		}
	}

	private void addNewPeriodPreferencesRow() {
		Button newPeriodPreferencesButton=new Button();
		newPeriodPreferencesButton.setText(i18n.create());
		newPeriodPreferencesButton.setTitle(i18n.createNewPeriodPreferences());
		// TODO newPeriodPreferencesButton.setStyleName();
		newPeriodPreferencesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
	    		ctx.getStatusLine().showProgress(ctx.getI18n().creatingNewPeriodPreferences());
	    		// TODO
	      		ctx.getStatusLine().showInfo("New period preferences created");
			}
		});

		ListBox yearListBox=new ListBox(false);
		for(int i=0; i<10; i++) {
			yearListBox.addItem(""+(2015+i));			
		}
		
		ListBox monthListBox=new ListBox(false);
		for(int i=1; i<=12; i++) {
			monthListBox.addItem(""+i);			
		}
		
		setWidget(0, 0, yearListBox);
		setWidget(0, 1, monthListBox);		
		setWidget(0, 2, newPeriodPreferencesButton);
	}
	
	private void addTableTitle() {
		// preferences link / solution timestamp is link to editor / view as calendar JSP / view by employee JSP 
		
		setWidget(1, 0, new TableSetSortingButton(i18n.yearAndMonth(),TableSortCriteria.BY_YEAR_AND_MONTH, this, ctx));
	}
		
	public void addRow(
			String id, 
			int year,
			int month)
	{
		int numRows = getRowCount();
				
		PeriodPreferencesTableToEditorButton button = new PeriodPreferencesTableToEditorButton(
				id,
				year,
				month,
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
				
		setWidget(numRows, 0, button);
	}

	public void removeRow() {
		int numRows = getRowCount();
		if (numRows > 1) {
			removeRow(numRows - 1);
			getFlexCellFormatter().setRowSpan(0, 1, numRows - 1);
		}
	}

	public void setSortingCriteria(TableSortCriteria criteria, boolean sortIsAscending) {
		this.sortCriteria=criteria;
		this.sortIsAscending=sortIsAscending;
	}

	public TableSortCriteria getSortingCriteria() {
		return sortCriteria;
	}

	public boolean isSortAscending() {
		return sortIsAscending;
	}
}