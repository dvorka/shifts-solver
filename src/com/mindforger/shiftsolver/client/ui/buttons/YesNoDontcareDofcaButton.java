package com.mindforger.shiftsolver.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class YesNoDontcareDofcaButton extends YesNoDontcareButton {

	private int value;
	
	public YesNoDontcareDofcaButton() {
		value=0;
		
		setHTML("&nbsp;");
		setStylePrimaryName("s2-3stateDontcare");
		
		addClickHandler(new ClickHandler() {						
			@Override
			public void onClick(ClickEvent event) {
				value++;
				switch(value%4) {
				case 0:
					setStylePrimaryName("s2-3stateDontcare");
					break;					
				case 1:
					setStylePrimaryName("s2-3stateNo");
					break;
				case 2:
					setStylePrimaryName("s2-3stateYes");
					break;
				case 3:
					setStylePrimaryName("s2-3stateDofca");
					break;
				}
			}
		});
	}

	public void setYesNoValue(int value) {
		this.value=value;
	}
	
	public int getYesNoValue() {
		return value%4;
	}
}
