package com.mindforger.shiftsolver.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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
	
    public static boolean isSameDay(Date d1, Date d2) {
    	Calendar c1=Calendar.getInstance();
    	c1.setTime(d1);
    	Calendar c2=Calendar.getInstance();
    	c2.setTime(d2);
    	return isSameDay(c1,c2);
    }
    
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
    	return
    	   cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
	       cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
	       cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    private static final int CREATED_TODAY = 1;
    private static final int CREATED_6_DAYS = 2;
    private static final int CREATED_THIS_YEAR = 4;
    private static final int CREATED = 8;
                                       // sec+min+hour+day+6days
    private static final long SIX_DAYS = 1000 * 60 * 60 * 24 * 6;
    
    public static String getPrettyTimestampHtml(long timestamp) {
        int type = CREATED;

        Date date = new Date(timestamp);
        Calendar dateCalendar=Calendar.getInstance();
        dateCalendar.setTime(date);

        Date today = new Date(System.currentTimeMillis());
        Calendar todayCalendar=Calendar.getInstance();
        todayCalendar.setTime(today);

        SimpleDateFormat simpleDateFormat;

        if (isSameDay(date, today)) {
            type = CREATED_TODAY;
        } else {
            if (System.currentTimeMillis() - timestamp < SIX_DAYS) {
                type = CREATED_6_DAYS;
            } else {
                //if (date.getYear() == today.getYear()) {
                if (dateCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)) {
                    type = CREATED_THIS_YEAR;
                }
            }
        }

        String backgroundColor = null, text = null;
        switch (type) {
        case CREATED:
            backgroundColor = "bbbbbb";
            simpleDateFormat = new SimpleDateFormat("yyyy");
            text = simpleDateFormat.format(date);
            break;
        case CREATED_6_DAYS:
            backgroundColor = "555555";
            simpleDateFormat = new SimpleDateFormat("EEEEEEEEE", new Locale("en", "US"));
            text = simpleDateFormat.format(date);
            break;
        case CREATED_THIS_YEAR:
            backgroundColor = "888888";
            simpleDateFormat = new SimpleDateFormat("MMMMMMMMM dd", new Locale("en", "US"));
            text = simpleDateFormat.format(date);
            break;
        case CREATED_TODAY:
            backgroundColor = "000000";
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            text = simpleDateFormat.format(date);
            break;
        }

        // note that long timestamp is added to the HTML - it is convenient for parsing/sorting/processing
        return "<div style='background-color: #" + backgroundColor + "; color: #ffffff; text-align: center; white-space: pre;'>" +
        		"&nbsp;&nbsp;" + text + "&nbsp;&nbsp;" +
                "</div>";
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
