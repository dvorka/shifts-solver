package com.mindforger.shiftsolver.shared.model;

public class Employee {

	private String key;
	private String firstname;
	private String familyname;
	private boolean female;
	private boolean editor;
	private boolean sportak;
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
		return getFirstname()+" "+getFamilyname();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (editor ? 1231 : 1237);
		result = prime * result
				+ ((familyname == null) ? 0 : familyname.hashCode());
		result = prime * result
				+ ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + (fulltime ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + (sportak ? 1231 : 1237);
		result = prime * result + (female ? 1231 : 1237);
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
		if (familyname == null) {
			if (other.familyname != null)
				return false;
		} else if (!familyname.equals(other.familyname))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (fulltime != other.fulltime)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (sportak != other.sportak)
			return false;
		if (female != other.female)
			return false;
		return true;
	}
}
