package com.mindforger.shiftsolver.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class YesNoDontcareButton extends Button {

	private int value;
	private String key;
	
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
					setStylePrimaryName("s2-3stateNo");
					break;
				case 2:
					setStylePrimaryName("s2-3stateYes");
					break;
				}
			}
		});
	}

	public void setYesNoValue(int value) {
		this.value=value;
	}
	
	public int getYesNoValue() {
		return value%3;
	}

	public void setKey(String key) {
		this.key=key;
	}

	public String getKey() {
		return key;
	}
}
