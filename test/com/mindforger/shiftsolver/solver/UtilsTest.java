package com.mindforger.shiftsolver.solver;

import com.ibm.icu.util.Calendar;
import com.mindforger.shiftsolver.client.Utils;

public class UtilsTest {
	
	public UtilsTest() {		
	}
	
	/**
	 * Test workday/weekend day for November 2015.
	 */
	public void testWorkdayWeekday() {
		// start week day is Sun/.../Sat as java.util.Calendar has 1/.../7 
		int startWeekDay=Calendar.SUNDAY;
		// day parameter starts with 1 (as in case of month)
		for(int day=1; day<=30; day++) {
			boolean weekend = Utils.isWeekend(day, startWeekDay);
			System.out.println(day+". "+(weekend?"weekend day":""));			
		}		
	}
	
	public static void main(String[] args) {
		UtilsTest utilsTest=new UtilsTest();
		utilsTest.testWorkdayWeekday();
	}
}
