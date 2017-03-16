package com.jun.xsPlatFormBean;

import java.awt.geom.Line2D.Double;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("CallEdge")
public class CallEdge extends Entity{
	//���ڵ� ����id
	@XStreamAsAttribute
	String id;
	
	@XStreamAlias("ID")
	String EAID;
	
	@XStreamAlias("name")
	String name;
	
	// ����
	@XStreamAlias("parameter")
	String parameter;
	
	@XStreamAlias("input")
	String input;
	
	@XStreamAlias("output")
	String output;
	
	@XStreamAlias("timing")
	String timing;
	
	// ��ʼ�ļ����
	Start start;
	@XStreamAlias("start")
	public class Start {
		@XStreamAsAttribute
		@XStreamAlias("reference")
		public String fromNodeId;
	}
	
	// �����ļ����
	End end;
	@XStreamAlias("end")
	public class End {
		@XStreamAsAttribute
		@XStreamAlias("reference")
		public String toNodeId;
	}
	@XStreamAlias("startLocation")
	PointStart startLocation;
	@XStreamAlias("startLocation")
	public class PointStart {
		@XStreamAsAttribute
		public double x;
		@XStreamAsAttribute
		public double y;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getTiming() {
		return timing;
	}
	public void setTiming(String timing) {
		this.timing = timing;
	}
	public PointStart getStartLocation() {
		return startLocation;
	}
	public void setStartLocation(PointStart startLocation) {
		this.startLocation = startLocation;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEAID() {
		return EAID;
	}
	public void setEAID(String eAID) {
		EAID = eAID;
	}
	public String getParameterStringValue() {
		return parameter;
	}
	public void setParameterStringValue(String parameterStringValue) {
		this.parameter = parameterStringValue;
	}
	public Start getStart() {
		return start;
	}
	public void setStart(Start start) {
		this.start = start;
	}
	public End getEnd() {
		return end;
	}
	public void setEnd(End end) {
		this.end = end;
	}
	
	
}
