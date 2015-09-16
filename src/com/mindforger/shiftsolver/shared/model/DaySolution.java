package com.mindforger.shiftsolver.shared.model;

public class DaySolution {

	private ShiftSolution morning;
	private ShiftSolution afternoon;
	private ShiftSolution night;
	
	public DaySolution() {
	}

	public ShiftSolution getMorning() {
		return morning;
	}

	public void setMorning(ShiftSolution morning) {
		this.morning = morning;
	}

	public ShiftSolution getAfternoon() {
		return afternoon;
	}

	public void setAfternoon(ShiftSolution afternoon) {
		this.afternoon = afternoon;
	}

	public ShiftSolution getNight() {
		return night;
	}

	public void setNight(ShiftSolution night) {
		this.night = night;
	}
}
