package com.jun.xsPlatFormBean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

// �����
//@XStreamConverter(ActivationBarNodeConverter.class)
@XStreamAlias("ActivationBarNode")
public class ActivationBarNode extends Entity{

	@XStreamAsAttribute
	public String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	// ���а����ļ����
	@XStreamAlias("children")
	Children c;
	public void setC(Children c) {
		this.c = c;
	}
	public String getId() {
		return id;
	}
	public Children getC() {
		return c;
	}
	
	
}
	