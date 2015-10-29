package com.mindforger.shiftsolver.client.ui.comparators;

import java.util.Comparator;

import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class ComparatorPeriodSolutionByModified implements Comparator<PeriodSolution> {

	private int descending; 
	
	public ComparatorPeriodSolutionByModified(boolean descending) {
		if(descending) {
			this.descending=1;						
		} else {
			this.descending=-1;			
		}
	}

	public int compare(PeriodSolution o1, PeriodSolution o2) {
		if(o1!=null && o2!=null) {
			return (int)(o1.getModified()-o2.getModified())*descending;
		}
		return 0;
	}
}
