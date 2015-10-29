package com.mindforger.shiftsolver.client.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.SolutionTableToViewPanelButton;
import com.mindforger.shiftsolver.client.ui.buttons.TableSetSortingButton;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorPeriodSolutionByModified;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorPeriodSolutionByYearAndMonth;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class SolutionsTable extends FlexTable implements SortableTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public SolutionsTable(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public void init() {
		// TODO style rename
		addStyleName("mf-growsTable");
		sortCriteria=TableSortCriteria.BY_YEAR_AND_MONTH;
		sortIsAscending=true;
		refresh(ctx.getState().getPeriodSolutions());
	}

	public void refreshWithNewSortingCriteria() {
		refresh(ctx.getState().getPeriodSolutions());
	}
	
	public void refresh(PeriodSolution[] s) {
		if(s==null || s.length==0) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}
				
		Comparator<PeriodSolution> comparator;
		switch(sortCriteria) {
		case BY_MODIFIED:
			comparator=new ComparatorPeriodSolutionByModified(sortIsAscending);
			break;
		case BY_YEAR_AND_MONTH:
		default:
			comparator=new ComparatorPeriodSolutionByYearAndMonth(sortIsAscending);
			break;
		}
		
		Arrays.sort(s, comparator);
		
		removeAllRows();
		addRows(s);
	}
	
	private void addRows(PeriodSolution[] result) {
		addTableTitle();
		if(result!=null) {
			for (int i = 0; i < result.length; i++) {
				addRow(
						result[i].getKey(), 
						result[i].getYear(),
						result[i].getMonth(),
						result[i].getModifiedPretty());
			}			
		}
	}

	private void addTableTitle() {
		setWidget(1, 0, new TableSetSortingButton(i18n.yearAndMonth(),TableSortCriteria.BY_YEAR_AND_MONTH, this, ctx));
		setWidget(1, 1, new TableSetSortingButton(i18n.modified(),TableSortCriteria.BY_MODIFIED, this, ctx));
	}
		
	public void addRow(
			String id, 
			int year,
			int month,
			String modified)
	{
		int numRows = getRowCount();
				
		SolutionTableToViewPanelButton button = new SolutionTableToViewPanelButton(
				id,
				year,
				month,
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
				
		setWidget(numRows, 0, button);
		setWidget(numRows, 1, new HTML(modified));
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