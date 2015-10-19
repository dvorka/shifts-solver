package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaePeriodPreferencesBean implements Serializable, GaeBean {
	private static final long serialVersionUID = -3749422859603583246L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;

	@Persistent
	int startWeekDay;
	@Persistent
	int monthDays;
	@Persistent
	int monthWorkDays;
	@Persistent
	int year;
	@Persistent
	int month;

	@Persistent
	String lastMonthEditor; 
	
	@Persistent(mappedBy="periodPreferences")
	@Element(dependent = "true")
	List<GaeEmployeeDayPreferenceBean> dayPreferences;
	
	public GaePeriodPreferencesBean() {		
	}
	
	@Override
	public Key getKey() {
		return key;
	}

	public int getStartWeekDay() {
		return startWeekDay;
	}

	public void setStartWeekDay(int startWeekDay) {
		this.startWeekDay = startWeekDay;
	}

	public int getMonthDays() {
		return monthDays;
	}

	public void setMonthDays(int monthDays) {
		this.monthDays = monthDays;
	}

	public int getMonthWorkDays() {
		return monthWorkDays;
	}

	public void setMonthWorkDays(int monthWorkDays) {
		this.monthWorkDays = monthWorkDays;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setKey(Key key) {
		this.key = key;
	}
	
	public String getLastMonthEditor() {
		return lastMonthEditor;
	}

	public void setLastMonthEditor(String lastMonthEditor) {
		this.lastMonthEditor = lastMonthEditor;
	}
	
	public List<GaeEmployeeDayPreferenceBean> getDayPreferences() {
		return dayPreferences;
	}

	public void setDayPreferences(List<GaeEmployeeDayPreferenceBean> dayPreferences) {
		this.dayPreferences = dayPreferences;
	}

	public void fromPojo(PeriodPreferences e) {
		key=ServerUtils.stringToKey(e.getKey());
		month=e.getMonth();
		monthDays=e.getMonthDays();
		monthWorkDays=e.getMonthWorkDays();
		startWeekDay=e.getStartWeekDay();
		year=e.getYear();
		lastMonthEditor=e.getLastMonthEditor();
		
		if(dayPreferences!=null) {
			dayPreferences.clear();
		} else {
			dayPreferences=new ArrayList<GaeEmployeeDayPreferenceBean>();			
		}
		for(String employeeKey:e.getEmployeeToPreferences().keySet()) {
			EmployeePreferences employeePreferences = e.getEmployeeToPreferences().get(employeeKey);
			if(employeePreferences!=null) {
				if(employeePreferences.getPreferences()!=null) {
					for(DayPreference dp:employeePreferences.getPreferences()) {
						GaeEmployeeDayPreferenceBean dpb = new GaeEmployeeDayPreferenceBean();
						dpb.fromPojo(dp);
						dpb.setEmployeeKey(employeeKey);
						dayPreferences.add(dpb);
					}
				}
			}
		}
	}
	
	public PeriodPreferences toPojo() {
		PeriodPreferences periodPreferences=new PeriodPreferences();
		periodPreferences.setKey(ServerUtils.keyToString(key));
		periodPreferences.setMonth(month);
		periodPreferences.setMonthDays(monthDays);
		periodPreferences.setMonthWorkDays(monthWorkDays);
		periodPreferences.setStartWeekDay(startWeekDay);
		periodPreferences.setYear(year);
		periodPreferences.setLastMonthEditor(lastMonthEditor);
		
		if(dayPreferences!=null && !dayPreferences.isEmpty()) {
			Map<String, EmployeePreferences> map = new HashMap<String,EmployeePreferences>();
			periodPreferences.setEmployeeToPreferences(map);
			for(GaeEmployeeDayPreferenceBean dp:dayPreferences) {
				String employeeKey = dp.getEmployeeKey();
				EmployeePreferences employeePreferences;
				if((employeePreferences=map.get(employeeKey))==null) {
					employeePreferences = new EmployeePreferences();
					map.put(employeeKey, employeePreferences);
				}
				DayPreference dayPreference = dp.toPojo();
				employeePreferences.addPreference(dayPreference);
			}
		}
		
		return periodPreferences;
	}
}
