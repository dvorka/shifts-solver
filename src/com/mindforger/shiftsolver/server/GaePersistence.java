package com.mindforger.shiftsolver.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.beans.GaeEmployeeBean;
import com.mindforger.shiftsolver.server.beans.GaeEmployeeDayPreferenceBean;
import com.mindforger.shiftsolver.server.beans.GaePeriodPreferencesBean;
import com.mindforger.shiftsolver.server.beans.GaePeriodSolutionBean;
import com.mindforger.shiftsolver.shared.model.Employee;
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
	public PeriodPreferences savePeriodPreferences(PeriodPreferences bean) {
		LOG.log(Level.INFO,"savePeriodPreferences() "+bean.toString());
		
		GaePeriodPreferencesBean gaeResult=new GaePeriodPreferencesBean();
		gaeResult.fromPojo(bean);
		
		PersistenceManager pm = getPm();
		Transaction tx=null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			gaeResult = (GaePeriodPreferencesBean)pm.makePersistent(gaeResult);
			tx.commit();
		} finally {
	        if (tx!=null && tx.isActive()) {
	            tx.rollback();
	        }
	        pm.close();
		}
		
		return gaeResult.toPojo();
	}

	@Override
	public void deletePeriodPreferences(String key) {
		LOG.log(Level.INFO,"deletePeriodPreferences() "+key);		
		Transaction tx=null;
		PersistenceManager pm=getPm();
		try {
			tx = pm.currentTransaction();
			tx.begin();
			GaePeriodPreferencesBean result 
				= pm.getObjectById(GaePeriodPreferencesBean.class, ServerUtils.stringToKey(key));
			pm.deletePersistent(result);
			tx.commit();
			LOG.log(Level.INFO,"  Deleted!");
		} finally {
	        if (tx!=null && tx.isActive()) {
	            tx.rollback();
	        }
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
			
			if (gaeResult==null || gaeResult.isEmpty()) {
				return new PeriodPreferences[0];
			} else {
				PeriodPreferences[] result=new PeriodPreferences[gaeResult.size()];
				for (int i = 0; i < result.length; i++) {
					result[i]=gaeResult.get(i).toPojo();
				}			
				return result;
			}								
		} finally {
			pm.close();
		}		
	}

	@Override
	public PeriodSolution createPeriodSolution(PeriodSolution bean) {
		LOG.log(Level.INFO,"newPeriodSolution()");				
		return savePeriodSolution(bean);
	}

	@Override
	public PeriodSolution savePeriodSolution(PeriodSolution bean) {
		LOG.log(Level.INFO,"savePeriodSolution() "+bean.toString());
		
		GaePeriodSolutionBean gaeResult=new GaePeriodSolutionBean();
		gaeResult.fromPojo(bean);
		
		PersistenceManager pm = getPm();
		Transaction tx=null;
		try {
			tx = pm.currentTransaction();
			tx.begin();
			gaeResult = (GaePeriodSolutionBean)pm.makePersistent(gaeResult);
			tx.commit();
		} finally {
	        if (tx!=null && tx.isActive()) {
	            tx.rollback();
	        }
	        pm.close();
		}
		
		return gaeResult.toPojo();
	}

	@Override
	public void deletePeriodSolution(String key) {
		LOG.log(Level.INFO,"deletePeriodSolution() "+key);		
		Transaction tx=null;
		PersistenceManager pm=getPm();
		try {
			tx = pm.currentTransaction();
			tx.begin();
			GaePeriodSolutionBean result 
				= pm.getObjectById(GaePeriodSolutionBean.class, ServerUtils.stringToKey(key));
			pm.deletePersistent(result);
			tx.commit();
			LOG.log(Level.INFO,"  Deleted!");
		} finally {
	        if (tx!=null && tx.isActive()) {
	            tx.rollback();
	        }
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PeriodSolution[] getPeriodSolution() {
		LOG.log(Level.INFO,"getPeriodSolutions()");

		PersistenceManager pm = getPm();
		Query query = pm.newQuery(GaePeriodSolutionBean.class);
		LOG.log(Level.INFO,"Query: "+query.toString());
		List<GaePeriodSolutionBean> gaeResult=null;
		try {
			gaeResult = (List<GaePeriodSolutionBean>)query.execute();
			LOG.log(Level.INFO,"  Result: "+gaeResult.size());
			
			if (gaeResult==null || gaeResult.isEmpty()) {
				return new PeriodSolution[0];
			} else {
				PeriodSolution[] result=new PeriodSolution[gaeResult.size()];
				for (int i = 0; i < result.length; i++) {
					result[i]=gaeResult.get(i).toPojo();
				}			
				return result;
			}								
		} finally {
			pm.close();
		}
	}
}
