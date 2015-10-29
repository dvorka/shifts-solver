package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class ComparatorPeriodPreferencesByModified implements Comparator<PeriodPreferences> {

	private int descending; 
	
	public ComparatorPeriodPreferencesByModified(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(PeriodPreferences o1, PeriodPreferences o2) {
		if(o1!=null && o2!=null) {
			return (int)(o1.getModified()-o2.getModified())*descending;
		}
		return 0;
	}
}
