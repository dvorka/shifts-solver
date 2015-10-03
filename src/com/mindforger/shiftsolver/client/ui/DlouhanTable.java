package com.mindforger.shiftsolver.client.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.TableSetSortingButton;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorPeriodPreferencesByYearAndMonth;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class DlouhanTable extends FlexTable implements SortableTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public DlouhanTable(RiaContext ctx) {
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
		ctx.getService().createPeriodPreferences(year, month, AsyncCallback<PeriodPreferences> callback);
		
	}
	
	private void addTableTitle() {
		setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_YEAR_AND_MONTH, this, ctx));
	}
		
	public void addRow(
			String id, 
			int year,
			int month)
	{
		int numRows = getRowCount();
				
		PeriodPreferencesTableToButton button = new PeriodPreferencesTableToEmployeeButton(
				id,
				year,
				month,
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
		
		final HTML womanHtml = new HTML((woman?i18n.female():i18n.male())+"&nbsp;&nbsp;");
		womanHtml.setStyleName("mf-progressHtml");
		final HTML editorHtml = new HTML((editor?i18n.yes():i18n.no())+"&nbsp;&nbsp;");
		// TODO color yes/no green/red
		editorHtml.setStyleName("mf-progressHtml");
		final HTML sportakHtml = new HTML((sportak?i18n.yes():i18n.no())+"&nbsp;&nbsp;");
		// TODO color yes/no green/red
		sportakHtml.setStyleName("mf-progressHtml");
		final HTML fulltimeHtml = new HTML((fulltime?i18n.yes():i18n.no())+"&nbsp;&nbsp;");
		// TODO color yes/no green/red
		fulltimeHtml.setStyleName("mf-progressHtml");
		
		setWidget(numRows, 0, button);
		setWidget(numRows, 1, womanHtml);
		setWidget(numRows, 2, editorHtml);
		setWidget(numRows, 3, sportakHtml);
		setWidget(numRows, 4, fulltimeHtml);
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