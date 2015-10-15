package com.mindforger.shiftsolver.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.mindforger.shiftsolver.server.beans.GaeEmployeeBean;
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
	public PeriodPreferences createPeriodPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeriodPreferences savePeriodPreferences(PeriodPreferences periodPreferences) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePeriodPreferences(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PeriodPreferences[] getPeriodPreferences() {
		// TODO tbd
		return new PeriodPreferences[0];
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
