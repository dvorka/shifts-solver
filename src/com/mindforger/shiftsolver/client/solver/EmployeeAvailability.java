package com.mindforger.shiftsolver.client.solver;

import com.mindforger.shiftsolver.client.RiaContext;
import com.mindforger.shiftsolver.client.RiaMessages;

public class EmployeeAvailability {
	
	private boolean roleMatch;
	private boolean notWant;
	private boolean shiftCapacity;
	private boolean nightCapacity;
	private boolean notAllocatedToday;
	private boolean notAllocated5Days;
	private boolean warnEditorToContinue;
	
	private RiaMessages i18n;
	
	public EmployeeAvailability(RiaContext ctx) {
		this.i18n=ctx.getI18n();
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
		if(!roleMatch) result+="!"+i18n.role();
		if(!notWant) result+=" !"+i18n.want();
		if(!shiftCapacity) result+=" !"+i18n.capacity();
		// TODO night
		if(!notAllocatedToday) result+=" "+i18n.today();
		if(!notAllocated5Days) result+=" 5"+i18n.days();
		if(warnEditorToContinue) result+=" "+i18n.editorFriSun();
		
		return result; 
	}
}
