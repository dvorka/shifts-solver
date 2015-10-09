package com.mindforger.shiftsolver.client.solver;

public class ShiftSolverTimeoutException extends RuntimeException {
	private static final long serialVersionUID = -4150503874248963534L;

	public ShiftSolverTimeoutException(String message) {
		super(message);
	}
}
