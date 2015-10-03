package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmployeePreferences implements Serializable  {
	private static final long serialVersionUID = 7779646494294778098L;

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
