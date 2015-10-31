package com.mindforger.shiftsolver.client;

import java.util.Random;

import com.mindforger.shiftsolver.shared.model.Employee;

public class Utils {

	public static boolean isWeekend(int day, int startWeekDay) {
		int sundayBeginningDayNumber=(day-1+startWeekDay-1)%7;
		if(sundayBeginningDayNumber==0 || sundayBeginningDayNumber==6) {
			return true;
		} else {
			return false;
		}
	}
			
	public static int getWeekdayNumber(int day, int startWeekDay) {
		return (day-1+startWeekDay-1)%7;
	}
	
	public static String getDayLetter(int day, int startWeekDay, RiaMessages i18n) {
		switch(getWeekdayNumber(day, startWeekDay)) {
		case 0:
			return i18n.sundayLetter();
		case 1:
			return i18n.mondayLetter();
		case 2:
			return i18n.tuesdayLetter();
		case 3:
			return i18n.wednesdayLetter();
		case 4:
			return i18n.thursdayLetter();
		case 5:
			return i18n.fridayLetter();
		case 6:
			return i18n.saturdayLetter();
		}
		return "";
	}
	
	public static void shuffleArray(Employee[] array) {
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			Employee a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}
	
	public static boolean isWorkday(int day, int dayOfWeek, int numberOfDaysInMonth) {
		int dow=dayOfWeek-1;
		for(int i=0; i<numberOfDaysInMonth; i++) {
			if(dow!=0 && dow!=6) {
				if(day==i) {
					return true;
				} else {
					return false;
				}
			}
			dow++;
			dow=dow%7;
		}
		return false;
	}	
}
