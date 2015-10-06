package com.mindforger.shiftsolver.shared;

public interface ShiftSolverConstants {

	String CODENAME="ShiftSolver";
	
	// types
	int NO_SHIFT = 0;	
	int SHIFT_MORNING=1;
	int SHIFT_MORNING_6=2;
	int SHIFT_MORNING_7=3;
	int SHIFT_MORNING_8=4;
	int SHIFT_AFTERNOON=5;
	int SHIFT_NIGHT=6;
	
	// URLs                                                        
    String BASE_URL = "http://shiftsolver.appspot.com";
    String GRAVATAR_BASE_URL = "http://www.gravatar.com/avatar/";
	
	// HTML
	String CONTAINER_STATUS_LINE="solverStatusLineContainer";
	String CONTAINER_MENU="solverLeftMenuContainer";
	String CONTAINER_PAGE_TITLE= "pageTitleContainer";
	String CONTAINER_HOME="solverHomeContainer";	
	String CONTAINER_EMPLOYEES_TABLE="solverEmployeesTableContainer";
	String CONTAINER_EMPLOYEE_EDITOR="solverEmployeeEditorContainer";
	String CONTAINER_DLOUHAN_TABLE="solverDlouhanTableContainer";
	String CONTAINER_DLOUHAN_EDITOR="solverDlouhanEditorContainer";
	String CONTAINER_SOLUTION_TABLE="solverSolutionTableContainer";
	String CONTAINER_SOLUTION_VIEW="solverSolutionViewContainer";	
	String CONTAINER_SOLVER_PROGRESS="solverProgressContainer";	
	
	// CSS
	String CSS_HELP_STYLE="s2-statusHelp";
	String CSS_INFO_STYLE="s2-statusInfo";
	String CSS_PROGRESS_STYLE="s2-statusProgress";
	String CSS_ERROR_STYLE="s2-statusError";
	
	String CSS_SHIFT_FREE="s2-shiftFree";
	String CSS_SHIFT_NA="s2-shiftNa";
	String CSS_SHIFT_VACATIONS="s2-shiftVacations";
	String CSS_SHIFT_MORNING="s2-shiftMorning";
	String CSS_SHIFT_AFTERNOON="s2-shiftAfternoon";
	String CSS_SHIFT_NIGHT="s2-shiftNight";

}