package com.mindforger.shiftsolver.server;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mindforger.shiftsolver.client.GreetingService;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.service.RiaBootImageBean;
import com.mindforger.shiftsolver.shared.service.UserBean;
import com.mindforger.shiftsolver.shared.service.UserSettingsBean;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private Persistence persistence;
	
	@Deprecated
	private AtomicLong sequence;	
	
	public GreetingServiceImpl() {
		sequence=new AtomicLong(1000);
		persistence = new GaePersistence();
	}

	@Override
	public RiaBootImageBean getRiaBootImage() {
		RiaBootImageBean result = new RiaBootImageBean();
		result.setEmployees(persistence.getEmployees());
		result.setPeriodPreferencesList(persistence.getPeriodPreferences());
		result.setUser(new UserBean());
		result.setUserSettings(new UserSettingsBean());
		return result;
	}
	
	@Override
	public Employee newEmployee() {
		return persistence.createEmployee();
	}
	
	@Override
	public void saveEmployee(Employee employee) {
		persistence.saveEmployee(employee);
	}

	@Override
	public Employee[] getEmployees() {
		return persistence.getEmployees();
	}

	@Override
	public void deleteEmployee(String key) {
		persistence.deleteEmployee(key);
	}
	
	
	
	public PeriodPreferences newPeriodPreferences(int year, int month) {
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		periodPreferences.setKey(""+sequence.incrementAndGet());

		Calendar myCalendar = new GregorianCalendar(year, month, 1);
		int numberOfDaysInMonth=myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		periodPreferences.setMonthDays(numberOfDaysInMonth);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myCalendar.getTime());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		periodPreferences.setStartWeekDay(dayOfWeek);
		
		return periodPreferences;
	}

	@Override
	public PeriodPreferences newPeriodPreferences() {
		return newPeriodPreferences(2015, 9);
	}

}
