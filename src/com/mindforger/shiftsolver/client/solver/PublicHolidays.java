package com.mindforger.shiftsolver.client.solver;

public class PublicHolidays {

	/*
	  Czech Republic:	
		1. 1.	Den obnovy samostatného českého státu
		1. 1.	Nový rok
		6. 4.	Velikonoční pondělí
		1. 5.	Svátek práce
		8. 5.	Den vítězství
		5. 7.	Den slovanských věrozvěstů Cyrila a Metoděje
		6. 7.	Den upálení mistra Jana Husa
		28. 9.	Den české státnosti
		28. 10.	Den vzniku samostatného československého státu
		17. 11.	Den boje za svobodu a demokracii
		24. 12.	Štědrý den
		25. 12.	1. svátek vánoční
		26. 12.	2. svátek vánoční
	*/
	
	public PublicHolidays() {		
	}
	
	public boolean isHolidays(int year, int month, int day) {
		if(month==1) {
			if(day==1) {
				return true;
			}
		} else {
			if(month==4) {
				// TODO this is moving holidays
				if(day==6) {
					return true;
				}
			} else {
				if(month==5) {
					if(day==1 || day==8) {
						return true;
					}
				} else {
					if(month==7) {
						if(day==5 || day==6) {
							return true;
						}						
					} else {
						if(month==9) {
							if(day==28) {
								return true;
							}							
						} else {
							if(month==10) {
								if(day==28) {
									return true;
								}								
							} else {
								if(month==11) {
									if(day==17) {
										return true;
									}									
								} else {
									if(month==12) {
										if(day==24 || day==25 || day==26) {
											return true;
										}										
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
