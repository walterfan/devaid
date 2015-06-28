/* 
* 
* Copyright (c) jwhat (China) Software Co., Ltd. HeFei Branch 
* No. 308 Xiangzhang Drive, Hefei New and High Technology Area,  
* Hefei, Anhui, China 
* All Rights Reserved. 
* 
*/
package com.github.walterfan.devaid.webmonitor;
/** 
*  Composite Event container
* 
* @version 1.0 6/3/2008 
* @author Walter Fan Ya Min 
*/
import java.util.LinkedList;
import java.util.List;

public class CompositeEvent extends TimingEvent {
	private LinkedList<TimingEvent> subEvents = new LinkedList<TimingEvent>();
	//private static Log logger = LogFactory.getLog(CompositeEvent.class);
	
	public CompositeEvent() {
		super();
	}
	public void addSubEvent(TimingEvent event) {
		subEvents.addLast(event);
	}
	
	public List<TimingEvent> getSubEvents() {
		return this.subEvents;
	}
	
	public int size() {
		return subEvents.size();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append(super.toString() + "\n");		
		for (TimingEvent te : subEvents) {
			sb.append("\t" + te.toString() + "\n");
		}
		return sb.toString();
	}
	
}
