package com.mindforger.shiftsolver.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mindforger.shiftsolver.client.GreetingService;
import com.mindforger.shiftsolver.shared.FieldVerifier;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	public GreetingServiceImpl() {		
	}
	
	public PeriodPreferences createPeriodPreferences(int year, int month) {
		PeriodPreferences periodPreferences = new PeriodPreferences(year, month);
		
		Calendar myCalendar = new GregorianCalendar(year, month, 1);
		int numberOfDaysInMonth=myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		periodPreferences.setMonthDays(numberOfDaysInMonth);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myCalendar.getTime());
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		periodPreferences.setStartWeekDay(dayOfWeek);
		
		return periodPreferences;
	}
	
	

	
	
	@Deprecated
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
