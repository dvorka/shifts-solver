package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.PeriodPreferences;

public class ComparatorPeriodPreferencesByYearAndMonth implements Comparator<PeriodPreferences> {

	private int descending; 
	
	public ComparatorPeriodPreferencesByYearAndMonth(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(PeriodPreferences o1, PeriodPreferences o2) {
		if(o1!=null && o2!=null) {
			if(o1.getYear()>o2.getYear()) {
				return 1*descending;
			} else {
				if(o1.getYear()<o2.getYear()) {
					return -1*descending;
				} else {
					if(o1.getMonth()>o2.getMonth()) {
						return 1*descending;
					} else {
						if(o1.getMonth()<o2.getMonth()) {
							return -1*descending;
						} else {
							return 0;
						}						
					}
				}
			}
		}
		return 0;
	}
}
