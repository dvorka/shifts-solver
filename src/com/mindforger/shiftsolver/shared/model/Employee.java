package com.mindforger.shiftsolver.shared.model;

import java.io.Serializable;

public class Employee implements Serializable  {
	private static final long serialVersionUID = 3788399085593900403L;

	private String key;
	private String firstname;
	private String familyname;
	private String email;
	private int birthdayYear;
	private int birthdayMonth;
	private int birthdayDay;
	private boolean female;
	private boolean editor;
	private boolean sportak;
	private boolean morningSportak;
	private boolean fulltime;
		
	public Employee() {		
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFamilyname() {
		return familyname;
	}

	public void setFamilyname(String familyname) {
		this.familyname = familyname;
	}

	public String getFullName() {
		return (getFirstname()!=null?getFirstname():"")+" "+(getFamilyname()!=null?getFamilyname():"");
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isFemale() {
		return female;
	}
	
	public void setFemale(boolean female) {
		this.female = female;
	}
	
	public boolean isEditor() {
		return editor;
	}
	
	public void setEditor(boolean editor) {
		this.editor = editor;
	}
	
	public boolean isSportak() {
		return sportak;
	}
	
	public void setSportak(boolean sportak) {
		this.sportak = sportak;
	}
	
	public boolean isFulltime() {
		return fulltime;
	}
	
	public void setFulltime(boolean fulltime) {
		this.fulltime = fulltime;
	}

	public int getBirthdayYear() {
		return birthdayYear;
	}

	public void setBirthdayYear(int birthdayYear) {
		this.birthdayYear = birthdayYear;
	}

	public int getBirthdayMonth() {
		return birthdayMonth;
	}

	public void setBirthdayMonth(int birthdayMonth) {
		this.birthdayMonth = birthdayMonth;
	}

	public int getBirthdayDay() {
		return birthdayDay;
	}

	public void setBirthdayDay(int birthdayDay) {
		this.birthdayDay = birthdayDay;
	}

	public boolean isMortak() {
		return morningSportak;
	}

	public void setMorningSportak(boolean morningSportak) {
		this.morningSportak = morningSportak;
	}
}
