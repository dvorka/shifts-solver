package com.mindforger.shiftsolver.shared.model;

import java.util.ArrayList;
import java.util.List;

public class EmployeePreferences {

	private List<DayPreference> preferences;
	
	public EmployeePreferences() {
		preferences=new ArrayList<DayPreference>();
	}

	public void addPreference(DayPreference preference) {
		preferences.add(preference);
	}
	
	public List<DayPreference> getPreferences() {
		return preferences;
	}

	public void setPreferences(List<DayPreference> preferences) {
		this.preferences = preferences;
	}
}
