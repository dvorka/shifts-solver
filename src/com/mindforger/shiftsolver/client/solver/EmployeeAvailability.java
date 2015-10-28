package com.mindforger.shiftsolver.client.solver;

public class EmployeeAvailability {
	
	private boolean roleMatch;
	private boolean notWant;
	private boolean shiftCapacity;
	private boolean nightCapacity;
	private boolean notAllocatedToday;
	private boolean notAllocated5Days;
	private boolean warnEditorToContinue;
	
	public EmployeeAvailability() {		
	}
	
	public boolean isAvailable() {
		return roleMatch && notWant && shiftCapacity /* && nightCapacity */ && notAllocatedToday && notAllocated5Days;
	}

	public boolean isRoleMatch() {
		return roleMatch;
	}

	public void setRoleMatch(boolean roleMatch) {
		this.roleMatch = roleMatch;
	}

	public boolean isNotBusy() {
		return notWant;
	}

	public void setNotBusy(boolean notBusy) {
		this.notWant = notBusy;
	}

	public boolean isShiftCapacity() {
		return shiftCapacity;
	}

	public void setShiftCapacity(boolean shiftCapacity) {
		this.shiftCapacity = shiftCapacity;
	}

	public boolean isNightCapacity() {
		return nightCapacity;
	}

	public void setNightCapacity(boolean nightCapacity) {
		this.nightCapacity = nightCapacity;
	}

	public boolean isNotAllocatedToday() {
		return notAllocatedToday;
	}

	public void setNotAllocatedToday(boolean notAllocatedToday) {
		this.notAllocatedToday = notAllocatedToday;
	}

	public boolean isNotAllocated5Days() {
		return notAllocated5Days;
	}

	public void setNotAllocated5Days(boolean notAllocated5Days) {
		this.notAllocated5Days = notAllocated5Days;
	}

	public boolean isWarnEditorToContinue() {
		return warnEditorToContinue;
	}

	public void warnEditorToContinue(boolean notEditorToContinue) {
		this.warnEditorToContinue = notEditorToContinue;
	}

	@Override
	public String toString() {
		String result="";
		if(!roleMatch) result+="!role";
		if(!notWant) result+=" !want";
		if(!shiftCapacity) result+=" !capacity";
		// TODO night
		if(!notAllocatedToday) result+=" today";
		if(!notAllocated5Days) result+=" 5days";
		if(warnEditorToContinue) result+=" editor-fri-sun";
		
		return result; 
	}
}
