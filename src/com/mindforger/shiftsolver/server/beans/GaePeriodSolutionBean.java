package com.mindforger.shiftsolver.server.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.ServerUtils;
import com.mindforger.shiftsolver.shared.model.DaySolution;
import com.mindforger.shiftsolver.shared.model.Job;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GaePeriodSolutionBean implements Serializable, GaeBean {
	private static final long serialVersionUID = -8823898756471336238L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	
	private Key key;
	@Persistent
	private long modified;

	@Persistent
	private String periodPreferencesKey;
	@Persistent
	private int year;
	@Persistent
	private int month;
	
	@Persistent(mappedBy="periodSolution")
	@Element(dependent = "true")
	List<GaeJobBean> jobs;

	@Persistent(mappedBy="periodSolution")
	@Element(dependent = "true")
	List<GaeDaySolutionBean> daySolutions;
	
	public GaePeriodSolutionBean() {
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}
	
	public String getPeriodPreferencesKey() {
		return periodPreferencesKey;
	}

	public void setPeriodPreferencesKey(String periodPreferencesKey) {
		this.periodPreferencesKey = periodPreferencesKey;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public List<GaeJobBean> getJobs() {
		return jobs;
	}

	public void setJobs(List<GaeJobBean> jobs) {
		this.jobs = jobs;
	}

	public List<GaeDaySolutionBean> getDaySolutions() {
		return daySolutions;
	}

	public void setDaySolutions(List<GaeDaySolutionBean> daySolutions) {
		this.daySolutions = daySolutions;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public Key getKey() {
		return key;
	}
	
	public void fromPojo(PeriodSolution s) {
		key=ServerUtils.stringToKey(s.getKey());
		year=s.getYear();
		month=s.getMonth();
		periodPreferencesKey=s.getPeriodPreferencesKey();
		jobs=new ArrayList<GaeJobBean>();
		if(s.getEmployeeJobs()!=null) {
			for(String k:s.getEmployeeJobs().keySet()) {
				GaeJobBean gaeJobBean = new GaeJobBean();
				gaeJobBean.setPeriodSolution(this);
				gaeJobBean.setEmployeeKey(k);
				gaeJobBean.fromPojo(s.getEmployeeJobs().get(k));
				jobs.add(gaeJobBean);
			}
		}
		daySolutions=new ArrayList<GaeDaySolutionBean>();
		if(s.getDays()!=null) {
			for(DaySolution d:s.getDays()) {
				GaeDaySolutionBean gaeDaySolutionBean = new GaeDaySolutionBean();
				gaeDaySolutionBean.setPeriodSolution(this);
				gaeDaySolutionBean.fromPojo(d);
				daySolutions.add(gaeDaySolutionBean);
			}			
		}
	}	
	
	public PeriodSolution toPojo() {
		PeriodSolution s=new PeriodSolution();

		List<DaySolution> days=new ArrayList<DaySolution>();
		if(daySolutions!=null) {
			for(GaeDaySolutionBean ds:daySolutions) {
				days.add(ds.toPojo());
			}					
		}
		s.setDays(days);
		
		Map<String,Job> employeeJobs=new HashMap<String, Job>();
		if(jobs!=null) {
			for(GaeJobBean j:jobs) {
				Job pojo = j.toPojo();
				employeeJobs.put(j.getEmployeeKey(), pojo);
			}
		}
		s.setEmployeeJobs(employeeJobs);
		
		s.setKey(ServerUtils.keyToString(key));
		s.setMonth(month);
		s.setPeriodPreferencesKey(periodPreferencesKey);
		s.setYear(year);
		s.setModified(modified);
		s.setModifiedPretty(ServerUtils.getPrettyTimestampHtml(modified));
		
		return s;
	}		
}
