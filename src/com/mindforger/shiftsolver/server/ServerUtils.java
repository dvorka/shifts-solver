package com.mindforger.shiftsolver.server;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ServerUtils {

	public static String keyToString(Key key) {
    	return (key==null?null:KeyFactory.keyToString(key));
	}

	public static Key stringToKey(String key) {
    	return (key==null?null:KeyFactory.stringToKey(key));
	}
	
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
}
