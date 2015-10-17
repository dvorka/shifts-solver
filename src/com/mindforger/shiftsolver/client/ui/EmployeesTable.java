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
		// TODO style rename
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
			comparator=new ComparatorEmployeeByName(sortIsAscending);
			break;
		case BY_EDITOR:
		case BY_EMAIL:
		case BY_SPORTAK:
		case BY_GENDER:
		case BY_TIMESTAMP:
		default:
			comparator=new ComparatorEmployeeByName(sortIsAscending);
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
				addRow(
						result[i].getKey(), 
						result[i].getFullName(),
						result[i].getEmail(),
						result[i].isFemale(),
						result[i].isEditor(),
						result[i].isSportak(),
						result[i].isFulltime());
			}			
		}
	}

	private void addTableTitle() {		
		setWidget(0, 0, new TableSetSortingButton(i18n.name(),TableSortCriteria.BY_NAME, this, ctx));
		setWidget(0, 1, new TableSetSortingButton("Email",TableSortCriteria.BY_EMAIL, this, ctx)); // TODO i18n
		setWidget(0, 2, new TableSetSortingButton(i18n.gender(),TableSortCriteria.BY_GENDER, this, ctx));
		setWidget(0, 3, new TableSetSortingButton(i18n.editor(),TableSortCriteria.BY_EDITOR, this, ctx));
		setWidget(0, 4, new TableSetSortingButton(i18n.sportak(),TableSortCriteria.BY_SPORTAK, this, ctx));
		setWidget(0, 5, new TableSetSortingButton(i18n.fulltime(),TableSortCriteria.BY_FULLTIME, this, ctx));
	}
		
	public void addRow(
			String id, 
			String fullname,
			String email,
			boolean woman, 
			boolean editor, 
			boolean sportak, 
			boolean fulltime) 
	{
		int numRows = getRowCount();
				
		EmployeesTableToEmployeeButton button = new EmployeesTableToEmployeeButton(
				id,
				fullname,
				// TODO css
				"mf-growsTableGoalButton", 
				ctx);
		if(woman) {
			button.addStyleName("s2-female");			
		} else {
			button.addStyleName("s2-male");						
		}
		
		final HTML mailHtml = new HTML(email==null?"":email);
		mailHtml.setStyleName("mf-progressHtml");
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
		setWidget(numRows, 1, mailHtml);
		setWidget(numRows, 2, womanHtml);
		setWidget(numRows, 3, editorHtml);
		setWidget(numRows, 4, sportakHtml);
		setWidget(numRows, 5, fulltimeHtml);
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