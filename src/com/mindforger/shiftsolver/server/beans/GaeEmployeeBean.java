package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.Employee;
		
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaeEmployeeBean implements Serializable, GaeBean {
	private static final long serialVersionUID = 248148852063681617L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;
		
	@Persistent
	private String firstname;
	@Persistent
	private String familyname;
	@Persistent
	private String email;
	@Persistent
	private int birthdayYear;
	@Persistent
	private int birthdayMonth;
	@Persistent
	private int birthdayDay;
	@Persistent
	private boolean female;
	@Persistent
	private boolean editor;
	@Persistent
	private boolean sportak;
	@Persistent
	private boolean morningSportak;
	@Persistent
	private boolean fulltime;
	
	public GaeEmployeeBean() {
	}
	
	@Override
	public Key getKey() {
		return key;
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

	public boolean isMorningSportak() {
		return morningSportak;
	}

	public void setMorningSportak(boolean morningSportak) {
		this.morningSportak = morningSportak;
	}

	public boolean isFulltime() {
		return fulltime;
	}

	public void setFulltime(boolean fulltime) {
		this.fulltime = fulltime;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void fromPojo(Employee e) {
		key=ServerUtils.stringToKey(e.getKey());
		familyname=e.getFamilyname();
		firstname=e.getFirstname();
		email=e.getEmail();
		birthdayDay=e.getBirthdayDay();
		birthdayMonth=e.getBirthdayMonth();
		birthdayYear=e.getBirthdayYear();
		editor=e.isEditor();
		female=e.isFemale();
		fulltime=e.isFulltime();
		morningSportak=e.isMortak();
		sportak=e.isSportak();
	}
	
	public Employee toPojo() {
		Employee e=new Employee();
		e.setBirthdayDay(birthdayDay);
		e.setBirthdayMonth(birthdayMonth);
		e.setBirthdayYear(birthdayYear);
		e.setEditor(editor);
		e.setFamilyname(familyname);
		e.setFemale(female);
		e.setFirstname(firstname);
		e.setFulltime(fulltime);
		e.setEmail(email);
		e.setKey(ServerUtils.keyToString(key));
		e.setMorningSportak(morningSportak);
		e.setSportak(sportak);
		return e;
	}
}
