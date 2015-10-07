package com.mindforger.shiftsolver.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class YesNoDontcareButton extends Button {

	private int value;
	
	public YesNoDontcareButton() {
		value=0;
		
		setHTML("&nbsp;");
		setStylePrimaryName("s2-3stateDontcare");
		
		addClickHandler(new ClickHandler() {						
			@Override
			public void onClick(ClickEvent event) {
				value++;
				switch(value%3) {
				case 0:
					setStylePrimaryName("s2-3stateDontcare");
					break;					
				case 1:
					setStylePrimaryName("s2-3stateYes");
					break;
				case 2:
					setStylePrimaryName("s2-3stateNo");
					break;
				}
			}
		});
	}

	public int getYesNoValue() {
		return value;
	}
}
