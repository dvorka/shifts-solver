package com.mindforger.shiftsolver.client.ui;

import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;
import com.mindforger.shiftsolver.client.ui.buttons.EmployeesTableToEmployeeButton;
import com.mindforger.shiftsolver.client.ui.buttons.TableSetSortingButton;
import com.mindforger.shiftsolver.client.ui.comparators.ComparatorEmployeeByName;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class DlouhanEditPanel extends FlexTable {
	
	private RiaMessages i18n;
	private RiaContext ctx;

	private TableSortCriteria sortCriteria;
	private boolean sortIsAscending;

	public DlouhanEditPanel(RiaContext ctx) {
		this.ctx=ctx;
		this.i18n=ctx.getI18n();
	}
	
	public void refresh(PeriodPreferences result) {
		if(result==null) {
			setVisible(false);
			return;
		} else {
			setVisible(true);
		}

		// TODO clear and re-generate widget
	}
	
	private void addRows(Employee[] result) {
		addTableTitle();
		if(result!=null) {
			for (int i = 0; i < result.length; i++) {
				addRow(
						result[i].getKey(), 
						result[i].getFullName(),
						result[i].isFemale(),
						result[i].isEditor(),
						result[i].isSportak(),
						result[i].isFulltime());
			}			
		}
	}

	private void addTableTitle() {
		setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
		
		
		
		setWidget(0, 1, new TableSetSortingButton(i18n.gender(),TableSortCriteria.BY_GENDER, this, ctx));
		setWidget(0, 2, new TableSetSortingButton(i18n.editor(),TableSortCriteria.BY_EDITOR, this, ctx));
		setWidget(0, 3, new TableSetSortingButton(i18n.sportak(),TableSortCriteria.BY_SPORTAK, this, ctx));
		setWidget(0, 4, new TableSetSortingButton(i18n.fulltime(),TableSortCriteria.BY_FULLTIME, this, ctx));
	}
		
	public void addRow(
			String id, 
			String fullname,
			boolean woman, 
			boolean editor, 
			boolean sportak, 
			boolean fulltime) 
	{
		int numRows = getRowCount();
				
		EmployeesTableToEmployeeButton button = new EmployeesTableToEmployeeButton(
				id,
				fullname,
				woman,
				editor,
				sportak,
				fulltime,
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