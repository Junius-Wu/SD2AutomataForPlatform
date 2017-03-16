package com.jun.xsPlatFormBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;



@XStreamAlias("TimeEdge")
public class TimeEdge extends Entity{
	
	// ʱ����ֵ
	@XStreamAlias("middleLabel")
	String timeStringValue;
		
	// ��ʼ����Ϣ
	StartEdge startEdge;
	@XStreamAlias("startEdge")
	class StartEdge {
		@XStreamAsAttribute
		@XStreamAlias("reference")
		public String fromMessageId;
	}
	
	// ��������Ϣ
	EndEdge endEdge;
	@XStreamAlias("endEdge")
	public class EndEdge {
		@XStreamAsAttribute
		@XStreamAlias("reference")
		public String toMessageId;
	}
	public String getTimeStringValue() {
		return timeStringValue;
	}
	public void setTimeStringValue(String timeStringValue) {
		this.timeStringValue = timeStringValue;
	}
	public StartEdge getStartEdge() {
		return startEdge;
	}
	public void setStartEdge(StartEdge startEdge) {
		this.startEdge = startEdge;
	}
	public EndEdge getEndEdge() {
		return endEdge;
	}
	public void setEndEdge(EndEdge endEdge) {
		this.endEdge = endEdge;
	}
	
	
}
