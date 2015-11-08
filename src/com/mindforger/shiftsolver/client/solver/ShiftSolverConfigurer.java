package com.mindforger.shiftsolver.client.solver;

public interface ShiftSolverConfigurer {
	
	boolean isEnforceAfternoonTo8am();
	void setEnforceAfternoonTo8am(boolean enforceAfternoonTo8am);
	boolean isEnforceNightToAfternoon();
	void setEnforceNightToAfternoon(boolean enforceNightToAfternoon);
	void setIterationsLimit(long limit);
	Boolean isEnforceMorningAfternoonBalancing();
	void setEnforceMorningAfternoonBalancing(Boolean enforceMorningAfternoonBalancing);
}
