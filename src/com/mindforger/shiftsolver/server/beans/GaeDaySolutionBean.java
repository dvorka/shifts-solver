package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Holder;
import com.mindforger.shiftsolver.shared.model.shifts.NightShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WeekendMorningShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayAfternoonShift;
import com.mindforger.shiftsolver.shared.model.shifts.WorkdayMorningShift;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaeDaySolutionBean implements Serializable, GaeBean {
	private static final long serialVersionUID = 3956995769754764763L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;

	@Persistent
	private GaePeriodSolutionBean periodSolution;

	@Persistent
	int day;
	@Persistent
	boolean isWorkday;
	@Persistent
	int weekday;

	@Persistent
	private String workdayMorningEditor;
	@Persistent
	private String workdayMorningStaffer6am;
	@Persistent
	private String workdayMorningStaffer7am;
	@Persistent
	private String workdayMorningStaffer8am1;
	@Persistent
	private String workdayMorningStaffer8am2;
	@Persistent
	private String workdayMorningSportak;

	@Persistent
	private String workdayAfternoonEditor;
	@Persistent
	private String workdayAfternoonStaffer1;
	@Persistent
	private String workdayAfternoonStaffer2;
	@Persistent
	private String workdayAfternoonStaffer3;
	@Persistent
	private String workdayAfternoonStaffer4;
	@Persistent
	private String workdayAfternoonSportak;

	@Persistent
	private String weekendMorningEditor;
	@Persistent
	private String weekendMorningStaffer6am;
	@Persistent
	private String weekendMorningSportak;

	@Persistent
	private String weekendAfternoonEditor;
	@Persistent
	private String weekendAfternoonStaffer;
	@Persistent
	private String weekendAfternoonSportak;
	
	@Persistent
	private String nightStaffer;
	
	public GaeDaySolutionBean() {
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public GaePeriodSolutionBean getPeriodSolution() {
		return periodSolution;
	}

	public void setPeriodSolution(GaePeriodSolutionBean periodSolution) {
		this.periodSolution = periodSolution;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isWorkday() {
		return isWorkday;
	}

	public void setWorkday(boolean isWorkday) {
		this.isWorkday = isWorkday;
	}

	public int getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	public String getWorkdayMorningEditor() {
		return workdayMorningEditor;
	}

	public void setWorkdayMorningEditor(String workdayMorningEditor) {
		this.workdayMorningEditor = workdayMorningEditor;
	}

	public String getWorkdayMorningStaffer6am() {
		return workdayMorningStaffer6am;
	}

	public void setWorkdayMorningStaffer6am(String workdayMorningStaffer6am) {
		this.workdayMorningStaffer6am = workdayMorningStaffer6am;
	}

	public String getWorkdayMorningStaffer7am() {
		return workdayMorningStaffer7am;
	}

	public void setWorkdayMorningStaffer7am(String workdayMorningStaffer7am) {
		this.workdayMorningStaffer7am = workdayMorningStaffer7am;
	}

	public String getWorkdayMorningStaffer8am1() {
		return workdayMorningStaffer8am1;
	}

	public void setWorkdayMorningStaffer8am1(String workdayMorningStaffer8am1) {
		this.workdayMorningStaffer8am1 = workdayMorningStaffer8am1;
	}

	public String getWorkdayMorningStaffer8am2() {
		return workdayMorningStaffer8am2;
	}

	public void setWorkdayMorningStaffer8am2(String workdayMorningStaffer8am2) {
		this.workdayMorningStaffer8am2 = workdayMorningStaffer8am2;
	}

	public String getWorkdayMorningSportak() {
		return workdayMorningSportak;
	}

	public void setWorkdayMorningSportak(String workdayMorningSportak) {
		this.workdayMorningSportak = workdayMorningSportak;
	}

	public String getWorkdayAfternoonEditor() {
		return workdayAfternoonEditor;
	}

	public void setWorkdayAfternoonEditor(String workdayAfternoonEditor) {
		this.workdayAfternoonEditor = workdayAfternoonEditor;
	}

	public String getWorkdayAfternoonStaffer1() {
		return workdayAfternoonStaffer1;
	}

	public void setWorkdayAfternoonStaffer1(String workdayAfternoonStaffer1) {
		this.workdayAfternoonStaffer1 = workdayAfternoonStaffer1;
	}

	public String getWorkdayAfternoonStaffer2() {
		return workdayAfternoonStaffer2;
	}

	public void setWorkdayAfternoonStaffer2(String workdayAfternoonStaffer2) {
		this.workdayAfternoonStaffer2 = workdayAfternoonStaffer2;
	}

	public String getWorkdayAfternoonStaffer3() {
		return workdayAfternoonStaffer3;
	}

	public void setWorkdayAfternoonStaffer3(String workdayAfternoonStaffer3) {
		this.workdayAfternoonStaffer3 = workdayAfternoonStaffer3;
	}

	public String getWorkdayAfternoonStaffer4() {
		return workdayAfternoonStaffer4;
	}

	public void setWorkdayAfternoonStaffer4(String workdayAfternoonStaffer4) {
		this.workdayAfternoonStaffer4 = workdayAfternoonStaffer4;
	}

	public String getWorkdayAfternoonSportak() {
		return workdayAfternoonSportak;
	}

	public void setWorkdayAfternoonSportak(String workdayAfternoonSportak) {
		this.workdayAfternoonSportak = workdayAfternoonSportak;
	}

	public String getWeekendMorningEditor() {
		return weekendMorningEditor;
	}

	public void setWeekendMorningEditor(String weekendMorningEditor) {
		this.weekendMorningEditor = weekendMorningEditor;
	}

	public String getWeekendMorningStaffer6am() {
		return weekendMorningStaffer6am;
	}

	public void setWeekendMorningStaffer6am(String weekendMorningStaffer6am) {
		this.weekendMorningStaffer6am = weekendMorningStaffer6am;
	}

	public String getWeekendMorningSportak() {
		return weekendMorningSportak;
	}

	public void setWeekendMorningSportak(String weekendMorningSportak) {
		this.weekendMorningSportak = weekendMorningSportak;
	}

	public String getWeekendAfternoonEditor() {
		return weekendAfternoonEditor;
	}

	public void setWeekendAfternoonEditor(String weekendAfternoonEditor) {
		this.weekendAfternoonEditor = weekendAfternoonEditor;
	}

	public String getWeekendAfternoonStaffer() {
		return weekendAfternoonStaffer;
	}

	public void setWeekendAfternoonStaffer(String weekendAfternoonStaffer) {
		this.weekendAfternoonStaffer = weekendAfternoonStaffer;
	}

	public String getWeekendAfternoonSportak() {
		return weekendAfternoonSportak;
	}

	public void setWeekendAfternoonSportak(String weekendAfternoonSportak) {
		this.weekendAfternoonSportak = weekendAfternoonSportak;
	}

	public String getNightStaffer() {
		return nightStaffer;
	}

	public void setNightStaffer(String nightStaffer) {
		this.nightStaffer = nightStaffer;
	}

	public void fromPojo(DaySolution d) {
		key=ServerUtils.stringToKey(d.getKey());
		
		day=d.getDay();
		isWorkday=d.isWorkday();
		weekday=d.getWeekday();
		
		if(d.getWorkdayMorningShift()!=null) {
			workdayMorningEditor=d.getWorkdayMorningShift().editor.get();
			workdayMorningStaffer6am=d.getWorkdayMorningShift().staffer6am.get();
			workdayMorningStaffer7am=d.getWorkdayMorningShift().staffer7am.get();
			workdayMorningStaffer8am1=d.getWorkdayMorningShift().staffer8am1.get();
			workdayMorningStaffer8am2=d.getWorkdayMorningShift().staffer8am2.get();
			workdayMorningSportak=d.getWorkdayMorningShift().sportak.get();
		}
		if(d.getWorkdayAfternoonShift()!=null) {
			workdayAfternoonEditor=d.getWorkdayAfternoonShift().editor.get();			
			workdayAfternoonStaffer1=d.getWorkdayAfternoonShift().staffers[0].get();			
			workdayAfternoonStaffer2=d.getWorkdayAfternoonShift().staffers[1].get();			
			workdayAfternoonStaffer3=d.getWorkdayAfternoonShift().staffers[2].get();			
			workdayAfternoonStaffer4=d.getWorkdayAfternoonShift().staffers[3].get();			
			workdayAfternoonSportak=d.getWorkdayAfternoonShift().sportak.get();
		}
		if(d.getWeekendMorningShift()!=null) {
			weekendMorningEditor=d.getWeekendMorningShift().editor.get();						
			weekendMorningStaffer6am=d.getWeekendMorningShift().staffer6am.get();						
			weekendMorningSportak=d.getWeekendMorningShift().sportak.get();						
		}
		if(d.getWeekendAfternoonShift()!=null) {
			weekendAfternoonEditor=d.getWeekendAfternoonShift().editor.get();						
			weekendAfternoonStaffer=d.getWeekendAfternoonShift().staffer.get();						
			weekendAfternoonSportak=d.getWeekendAfternoonShift().sportak.get();									
		}
		if(d.getNightShift()!=null) {
			nightStaffer=d.getNightShift().staffer.get();									
		}
	}

	public DaySolution toPojo() {
		DaySolution s=new DaySolution(day, weekday, isWorkday);
		
		s.setKey(ServerUtils.keyToString(key));
		
		s.setWorkdayMorningShift(new WorkdayMorningShift());
		if(workdayMorningEditor!=null) s.getWorkdayMorningShift().editor=new Holder<String>(workdayMorningEditor);
		if(workdayMorningStaffer6am!=null) s.getWorkdayMorningShift().staffer6am=new Holder<String>(workdayMorningStaffer6am);
		if(workdayMorningStaffer7am!=null) s.getWorkdayMorningShift().staffer7am=new Holder<String>(workdayMorningStaffer7am);
		if(workdayMorningStaffer8am1!=null) s.getWorkdayMorningShift().staffer8am1=new Holder<String>(workdayMorningStaffer8am1);
		if(workdayMorningStaffer8am2!=null) s.getWorkdayMorningShift().staffer8am2=new Holder<String>(workdayMorningStaffer8am2);
		if(workdayMorningSportak!=null) s.getWorkdayMorningShift().sportak=new Holder<String>(workdayMorningSportak);

		s.setWorkdayAfternoonShift(new WorkdayAfternoonShift());
		if(workdayAfternoonEditor!=null) s.getWorkdayAfternoonShift().editor=new Holder<String>(workdayAfternoonEditor);
		if(workdayAfternoonStaffer1!=null) s.getWorkdayAfternoonShift().staffers[0]=new Holder<String>(workdayAfternoonStaffer1);
		if(workdayAfternoonStaffer2!=null) s.getWorkdayAfternoonShift().staffers[1]=new Holder<String>(workdayAfternoonStaffer2);
		if(workdayAfternoonStaffer3!=null) s.getWorkdayAfternoonShift().staffers[2]=new Holder<String>(workdayAfternoonStaffer3);
		if(workdayAfternoonStaffer4!=null) s.getWorkdayAfternoonShift().staffers[3]=new Holder<String>(workdayAfternoonStaffer4);
		if(workdayAfternoonSportak!=null) s.getWorkdayAfternoonShift().sportak=new Holder<String>(workdayAfternoonSportak);

		s.setWeekendMorningShift(new WeekendMorningShift());
		if(weekendMorningEditor!=null) s.getWeekendMorningShift().editor=new Holder<String>(weekendMorningEditor);
		if(weekendMorningStaffer6am!=null) s.getWeekendMorningShift().staffer6am=new Holder<String>(weekendMorningStaffer6am);
		if(weekendMorningSportak!=null) s.getWeekendMorningShift().sportak=new Holder<String>(weekendMorningSportak);

		s.setWeekendAfternoonShift(new WeekendAfternoonShift());
		if(weekendAfternoonEditor!=null) s.getWeekendAfternoonShift().editor=new Holder<String>(weekendAfternoonEditor);
		if(weekendAfternoonStaffer!=null) s.getWeekendAfternoonShift().staffer=new Holder<String>(weekendAfternoonStaffer);
		if(weekendAfternoonSportak!=null) s.getWeekendAfternoonShift().sportak=new Holder<String>(weekendAfternoonSportak);

		s.setNightShift(new NightShift());
		if(nightStaffer!=null) s.getNightShift().staffer=new Holder<String>(nightStaffer);
		
		return s;
	}
}
