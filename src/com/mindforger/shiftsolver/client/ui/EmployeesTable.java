package com.mindforger.shiftsolver.client.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorGrowBeanByName;
import com.mindforger.shiftsolver.shared.model.Employee;

public class EmployeesTable extends FlexTable implements SortableTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public EmployeesTable(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public void init() {
		addStyleName("mf-growsTable");
		sortCriteria=TableSortCriteria.BY_TIMESTAMP;
		sortIsAscending=true;
		refresh(ctx.getState().getEmployees());
	}

	public void refreshWithNewSortingCriteria() {
		refresh(ctx.getState().getEmployees());
	}
	
	public void refresh(Employee[] result) {
		if(result==null || result.length==0) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}
				
		Comparator<Employee> comparator;
		switch(sortCriteria) {
		case BY_NAME:
			comparator=new ComparatorGrowBeanByName(sortIsAscending);
			break;
		case BY_TIMESTAMP:
		default:
			comparator=new ComparatorGrowBeanByName(sortIsAscending);
			break;
		}
		
		Arrays.sort(result, comparator);
		
		removeAllRows();
		addRows(result);
	}
	
	private void addRows(Employee[] result) {
		addTableTitle();
		if(result!=null) {
			for (int i = 0; i < result.length; i++) {
				addRow(result[i].getKey(), result[i].getName(), result[i].getDescription(), result[i].getImportance(), result[i].getUrgency(), result[i].getProgress(), result[i].getModifiedPretty());
			}			
		}
	}

	private void addTableTitle() {
		setWidget(0, 0, new TableSetSortingButton(i18n.goal(),TableSortCriteria.BY_NAME, this, ctx));
		setWidget(0, 1, new TableSetSortingButton(i18n.importance(),TableSortCriteria.BY_IMPORTANCE, this, ctx));
		setWidget(0, 2, new TableSetSortingButton(i18n.urgency(),TableSortCriteria.BY_URGENCY, this, ctx));
		setWidget(0, 3, new TableSetSortingButton(i18n.progress(),TableSortCriteria.BY_PROGRESS, this, ctx));
		setWidget(0, 4, new TableSetSortingButton(i18n.modified(),TableSortCriteria.BY_TIMESTAMP, this, ctx));
	}
		
	public void addRow(String id, String goalName, String description, int importance, int urgency, int progress, String modified) {
		int numRows = getRowCount();

		HorizontalPanel urgencyPanel=new HorizontalPanel();
		urgencyPanel.setStyleName("mf-showUrgencyPanel");		
		for (int i = 0; i < 5; i++) {
			if(i <= (urgency-1)) {
				urgencyPanel.add(new HTML("<span class='mf-showUrgencyOn' title='"+i18n.urgency()+" "+urgency+"'>!</span>"));				
			} else {
				urgencyPanel.add(new HTML("<span class='mf-showUrgencyOff' title='"+i18n.urgency()+" "+urgency+"'>!</span>"));
			}
		}		
		HorizontalPanel importancePanel=new HorizontalPanel();
		importancePanel.setStyleName("mf-showImportancePanel");		
		for (int i = 0; i < 5; i++) {
			if(i <= (importance-1)) {
				importancePanel.add(new HTML("<span class='mf-showImportanceOn' title='"+i18n.importance()+" "+importance+"'><img src='./images/star-black-white.png'/></span>"));
			} else {
				importancePanel.add(new HTML("<span class='mf-showImportanceOff' title='"+i18n.importance()+" "+importance+"'><img src='./images/star-gray-white.png'/></span>"));
			}
		}
		
		final HTML progressHtml = new HTML(progress+"%&nbsp;&nbsp;");
		progressHtml.setStyleName("mf-progressHtml");
		
		GrowsTableToGrowButton button = new GrowsTableToGrowButton(
				goalName, 
				description, 
				id, 
				"mf-growsTableGoalButton", 
				GrowsTableToGrowButton.FIRST_TAB,
				ctx);
		if(progress==100) {
			button.addStyleName("mf-growsTableGoalFinishedButton");
		}
		setWidget(numRows, 0, button);
		setWidget(numRows, 1, importancePanel);
		setWidget(numRows, 2, urgencyPanel);
		setWidget(numRows, 3, progressHtml);
		setWidget(numRows, 4, new HTML(""+modified));
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