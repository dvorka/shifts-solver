package com.mindforger.shiftsolver.shared.model;

public class Employee {

	private String firstname;
	private String familyname;
	private boolean woman;
	private boolean editor;
	private boolean sportak;
	private boolean fulltime;
	
	public Employee() {		
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
		return getFirstname()+" "+getFamilyname();
	}
	
	public boolean isWoman() {
		return woman;
	}
	public void setWoman(boolean woman) {
		this.woman = woman;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (editor ? 1231 : 1237);
		result = prime * result + (fulltime ? 1231 : 1237);
		result = prime * result + (sportak ? 1231 : 1237);
		result = prime * result + (woman ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (editor != other.editor)
			return false;
		if (fulltime != other.fulltime)
			return false;
		if (sportak != other.sportak)
			return false;
		if (woman != other.woman)
			return false;
		return true;
	}
}
