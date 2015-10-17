package com.mindforger.shiftsolver.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.beans.GaeEmployeeBean;
import com.mindforger.shiftsolver.server.beans.GaeEmployeeDayPreferenceBean;
import com.mindforger.shiftsolver.server.beans.GaePeriodPreferencesBean;
import com.mindforger.shiftsolver.shared.model.DayPreference;
import com.mindforger.shiftsolver.shared.model.Employee;
import com.mindforger.shiftsolver.shared.model.EmployeePreferences;
import com.mindforger.shiftsolver.shared.model.PeriodPreferences;
import com.mindforger.shiftsolver.shared.model.PeriodSolution;

public class GaePersistence implements Persistence {
	private static final Logger LOG=Logger.getLogger("GaePersistence");

	static final PersistenceManagerFactory persistenceManagerFactory;

	static {
		persistenceManagerFactory=JDOHelper.getPersistenceManagerFactory("transactions-optional");		
	}
	
	/**
	 *  For each request to the big table, new persistence manager MUST be created.
	 *  It can be reused only if it is used on the same entity group.
	 *  Once it is not needed, it must be released with close() method.
	 */
	static synchronized PersistenceManager getPm() {
		return persistenceManagerFactory.getPersistenceManager();
	}	
	
	public GaePersistence() {		
	}
		
	@Override
	public Employee createEmployee() {
		LOG.log(Level.INFO,"newEmployee()");				
		return saveEmployee(new Employee());
	}

	@Override
	public Employee saveEmployee(Employee bean) {
		LOG.log(Level.INFO,"saveEmployee() "+bean.toString());
		boolean create=bean.getKey()==null;
		GaeEmployeeBean gaeResult;
		PersistenceManager pm = getPm();
		try {
			if(!create) {
				Key key = ServerUtils.stringToKey(bean.getKey());
				gaeResult = pm.getObjectById(GaeEmployeeBean.class, key);
			} else {
				gaeResult=new GaeEmployeeBean();
			}
			gaeResult.fromPojo(bean);
			gaeResult = (GaeEmployeeBean)pm.makePersistent(gaeResult);
		} finally {
			pm.close();
		}
		return gaeResult.toPojo();
	}

	@Override
	public void deleteEmployee(String key) {
		LOG.log(Level.INFO,"deleteEmployee() "+key);		
		PersistenceManager pm=getPm();
		try {
			GaeEmployeeBean result = pm.getObjectById(GaeEmployeeBean.class, ServerUtils.stringToKey(key));
			pm.deletePersistent(result);
			LOG.log(Level.INFO,"  Deleted!");
		} finally {
			pm.close();
		}
		
		deleteDayPreferencesOfEmployee(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Employee[] getEmployees() {
		LOG.log(Level.INFO,"getEmployees()");

		PersistenceManager pm = getPm();
		Query query = pm.newQuery(GaeEmployeeBean.class);
		LOG.log(Level.INFO,"Query: "+query.toString());
		List<GaeEmployeeBean> gaeResult=null;
		try {
			gaeResult = (List<GaeEmployeeBean>)query.execute();
			LOG.log(Level.INFO,"  Result: "+gaeResult.size());						
		} finally {
			pm.close();
		}
		
		if (gaeResult==null || gaeResult.isEmpty()) {
			return new Employee[0];
		} else {
			Employee[] result=new Employee[gaeResult.size()];
			for (int i = 0; i < result.length; i++) {
				result[i]=gaeResult.get(i).toPojo();
			}			
			return result;
		}									
	}

	@Override
	public PeriodPreferences createPeriodPreferences(PeriodPreferences bean) {
		LOG.log(Level.INFO,"newPeriodPreferences()");				
		return savePeriodPreferences(bean);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PeriodPreferences savePeriodPreferences(PeriodPreferences bean) {
		LOG.log(Level.INFO,"savePeriodPreferences() "+bean.toString());
		boolean create=bean.getKey()==null;
		GaePeriodPreferencesBean gaeResult;
		PersistenceManager pm = getPm();
		try {
			if(!create) {
				Key key = ServerUtils.stringToKey(bean.getKey());
				gaeResult = pm.getObjectById(GaePeriodPreferencesBean.class, key);
			} else {
				gaeResult=new GaePeriodPreferencesBean();
			}
			gaeResult.fromPojo(bean);
			gaeResult = (GaePeriodPreferencesBean)pm.makePersistent(gaeResult);
		} finally {
			pm.close();
		}

		PeriodPreferences result = gaeResult.toPojo();
		
		List<GaeEmployeeDayPreferenceBean> oldDayPrefsInBigTable=null;		
		Map<String,GaeEmployeeDayPreferenceBean> newDayPrefsBean=new HashMap<String,GaeEmployeeDayPreferenceBean>();		
		List<GaeEmployeeDayPreferenceBean> dayPrefsToDelete;
		List<GaeEmployeeDayPreferenceBean> dayPrefsToSave=new ArrayList<GaeEmployeeDayPreferenceBean>();

		for(String employeeKey:bean.getEmployeeToPreferences().keySet()) {
			int k=1;
			for(DayPreference dp:bean.getEmployeeToPreferences().get(employeeKey).getPreferences()) {
				GaeEmployeeDayPreferenceBean gdp = new GaeEmployeeDayPreferenceBean();
				gdp.fromPojo(dp);
				gdp.setEmployeeKey(employeeKey);
				if(dp.getKey()==null) {
					newDayPrefsBean.put("FAKE-KEY"+k++,gdp);					
				} else {
					newDayPrefsBean.put(dp.getKey(),gdp);					
				}
			}			
		}
		pm=getPm();
		try {
			Query query=pm.newQuery(GaeEmployeeDayPreferenceBean.class);
			query.setFilter("periodPreferencesKey == newPreferencesKey");
			query.declareParameters("String newPreferencesKey");
			LOG.log(Level.INFO,"Query: "+query);
			oldDayPrefsInBigTable = (List<GaeEmployeeDayPreferenceBean>)query.execute(result.getKey());
			LOG.log(Level.INFO,"  Old day prefs: "+oldDayPrefsInBigTable.size());		
			
			dayPrefsToDelete = new ArrayList<GaeEmployeeDayPreferenceBean>();
			if(!oldDayPrefsInBigTable.isEmpty()) {
				for(GaeEmployeeDayPreferenceBean gdp:oldDayPrefsInBigTable) {
					if(!newDayPrefsBean.containsKey(ServerUtils.keyToString(gdp.getKey()))) {
						LOG.log(Level.INFO,"Deleting day pref: "+gdp.getKey());
						dayPrefsToDelete.add(gdp);
					}
				}
			}
			pm.deletePersistentAll(dayPrefsToDelete);		
		} finally {
			pm.close();
		}

		pm=getPm();
		try {			
			for(GaeEmployeeDayPreferenceBean dp:newDayPrefsBean.values()) {
				GaeEmployeeDayPreferenceBean odp;
				if(dp.getKey()!=null) {
					// update
					odp=pm.getObjectById(GaeEmployeeDayPreferenceBean.class, dp.getKey());
					odp.fromPojo(dp.toPojo());
					odp.setEmployeeKey(dp.getEmployeeKey());
					dayPrefsToSave.add(odp);
				} else {
					// create
					dp.setPeriodPreferencesKey(result.getKey());
					dayPrefsToSave.add(dp);
				}
				LOG.log(Level.INFO,"Storing day preference");
			}
		} finally {
			pm.close();
		}

		if(dayPrefsToSave.size()>0) {
			pm=getPm();		
			try {			
				pm.makePersistentAll(dayPrefsToSave);
				LOG.log(Level.INFO,"  Stored: "+gaeResult.toString());
			} finally {
				pm.close();
			}
			
			Map<String,EmployeePreferences> eToP=new HashMap<String,EmployeePreferences>();
			for(GaeEmployeeDayPreferenceBean gdp:dayPrefsToSave) {
				EmployeePreferences ep=eToP.get(gdp.getEmployeeKey());
				if(ep==null) {
					ep=new EmployeePreferences();
					eToP.put(gdp.getEmployeeKey(), ep);
				}
				ep.addPreference(gdp.toPojo());
			}
			result.setEmployeeToPreferences(eToP);
		}
				
		return result;
	}

	@Override
	public void deletePeriodPreferences(String key) {
		LOG.log(Level.INFO,"deletePeriodPreferences() "+key);		
		PersistenceManager pm=getPm();
		try {
			GaePeriodPreferencesBean result 
				= pm.getObjectById(GaePeriodPreferencesBean.class, ServerUtils.stringToKey(key));
			pm.deletePersistent(result);
			LOG.log(Level.INFO,"  Deleted!");
		} finally {
			pm.close();
		}
	}
	
	private void deleteDayPreferencesOfEmployee(String employeeKey) {
		PersistenceManager pm=getPm();
		try {
			Query query=pm.newQuery(GaeEmployeeDayPreferenceBean.class);
			query.setFilter("employeeKey == queryEmployeeKey");
			query.declareParameters("String queryEmployeeKey");
			LOG.log(Level.INFO,"Query: "+query);
			query.deletePersistentAll(employeeKey);
		} finally {
			pm.close();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PeriodPreferences[] getPeriodPreferences() {
		LOG.log(Level.INFO,"getPeriodPreferences()");

		PersistenceManager pm = getPm();
		Query query = pm.newQuery(GaePeriodPreferencesBean.class);
		LOG.log(Level.INFO,"Query: "+query.toString());
		List<GaePeriodPreferencesBean> gaeResult=null;
		try {
			gaeResult = (List<GaePeriodPreferencesBean>)query.execute();
			LOG.log(Level.INFO,"  Result: "+gaeResult.size());						
		} finally {
			pm.close();
		}

		pm = getPm();
		query = pm.newQuery(GaeEmployeeDayPreferenceBean.class);
		LOG.log(Level.INFO,"Query: "+query.toString());
		List<GaeEmployeeDayPreferenceBean> gaeDayResult=null;
		try {
			gaeDayResult = (List<GaeEmployeeDayPreferenceBean>)query.execute();
			LOG.log(Level.INFO,"  Result: "+gaeDayResult.size());						
		} finally {
			pm.close();
		}
		
		if (gaeResult==null || gaeResult.isEmpty()) {
			return new PeriodPreferences[0];
		} else {
			Map<String,PeriodPreferences> pps=new HashMap<String,PeriodPreferences>();
			PeriodPreferences[] result=new PeriodPreferences[gaeResult.size()];
			for (int i = 0; i < result.length; i++) {
				result[i]=gaeResult.get(i).toPojo();
				pps.put(result[i].getKey(), result[i]);
			}			
			
			for(GaeEmployeeDayPreferenceBean gdp:gaeDayResult) {
				PeriodPreferences periodPreferences = pps.get(gdp.getPeriodPreferencesKey());
				if(periodPreferences!=null) {
					Map<String, EmployeePreferences> eps = periodPreferences.getEmployeeToPreferences();
					if(eps==null) {
						eps=new HashMap<String,EmployeePreferences>();
						periodPreferences.setEmployeeToPreferences(eps);
					}
					EmployeePreferences ep=eps.get(gdp.getEmployeeKey());
					if(ep==null) {
						ep=new EmployeePreferences();
						eps.put(gdp.getEmployeeKey(), ep);
					}
					ep.addPreference(gdp.toPojo());
				}
			}
			return result;
		}								
	}

	@Override
	public PeriodSolution createPeriodSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeriodSolution savePeriodSolution(PeriodSolution periodSolution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePeriodSolution(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PeriodSolution[] getPeriodSolution() {
		// TODO Auto-generated method stub
		return null;
	}
}
