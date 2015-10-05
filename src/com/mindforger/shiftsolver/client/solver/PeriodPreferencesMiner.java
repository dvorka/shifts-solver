package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class PeriodPreferencesMiner {

	PeriodPreferences preferences;
	
	public PeriodPreferencesMiner() {
	}
	
	public int getWorkingDaysCount() {
		int workdays=0;
		int startWeekDay = preferences.getStartWeekDay();
		for(int i=0; i<preferences.getMonthDays(); i++) {
			int day=(i+startWeekDay-1)%7;
			if(day!=0 || day!=6) {
				workdays++;
			}
		}
		return workdays;
	}
}
