package com.mindforger.shiftsolver.server.security;

import java.util.HashSet;
import java.util.Set;

public class ShiftSolverSecurity {

	private static Set<String> admins;
	
	static {
		admins=new HashSet<String>();
		admins.add("marina.dvorakova@gmail.com");
		admins.add("martin.dvorak@mindforger.com");
		admins.add("ultradvorka@gmail.com");
		admins.add("martin.dvorak.tm@gmail.com");
		admins.add("test@example.com");
	}
	
	public static boolean isAdmin(String admin) {
		return admin!=null?admins.contains(admin):false;
	}
}
