package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;

public class Holder<T> implements Serializable {
	private static final long serialVersionUID = -1312032921161292850L;

	private T v;
	
	public Holder() {
	}
	
	public Holder(T v) {
		this.v=v;
	}

	public T get() {
		return v;
	}

	public void set(T v) {
		this.v = v;
	}
}
