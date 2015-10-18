package com.mindforger.shiftsolver.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
		
	public GreetingServiceImpl() {
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

	@Override
	public PeriodPreferences newPeriodPreferences() {
		Calendar calendar = Calendar.getInstance();
		return newPeriodPreferences(
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH)+1);
	}
		
	public PeriodPreferences newPeriodPreferences(int year, int month) {
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		setDaysWorkdaysStartDay(periodPreferences);
		return persistence.createPeriodPreferences(periodPreferences);
	}

	@Override
	public PeriodPreferences setDaysWorkdaysStartDay(PeriodPreferences periodPreferences) {
		return ServerUtils.countDaysWorkdaysStartDay(periodPreferences);
	}

	@Override
	public void savePeriodPreferences(PeriodPreferences periodPreferences) {
		persistence.savePeriodPreferences(periodPreferences);
	}

	@Override
	public void deletePeriodPreferences(String key) {
		persistence.deletePeriodPreferences(key);
	}

	@Override
	public PeriodPreferences[] getPeriodPreferences() {
		return persistence.getPeriodPreferences();
	}
}
