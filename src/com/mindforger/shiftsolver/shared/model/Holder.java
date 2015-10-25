package com.mindforger.shiftsolver.shared.model;

public class Holder<T> {

	private T v;
	
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
