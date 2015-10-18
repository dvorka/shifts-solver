package com.mindforger.shiftsolver.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class ServerUtils {

	public static String keyToString(Key key) {
    	return (key==null?null:KeyFactory.keyToString(key));
	}

	public static Key stringToKey(String key) {
    	return (key==null?null:KeyFactory.stringToKey(key));
	}
	
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	public static PeriodPreferences countDaysWorkdaysStartDay(PeriodPreferences periodPreferences) {
		Calendar myCalendar = new GregorianCalendar(periodPreferences.getYear(), periodPreferences.getMonth()-1, 1);
		int numberOfDaysInMonth=myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		periodPreferences.setMonthDays(numberOfDaysInMonth);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myCalendar.getTime());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		periodPreferences.setStartWeekDay(dayOfWeek);
		
		int workdays=0, dow=dayOfWeek-1;
		for(int i=0; i<numberOfDaysInMonth; i++) {
			if(dow!=0 && dow!=6) {
				workdays++;
			}
			dow++;
			dow=dow%7;
		}
		periodPreferences.setMonthWorkDays(workdays);
		
		return periodPreferences;
	}
}
