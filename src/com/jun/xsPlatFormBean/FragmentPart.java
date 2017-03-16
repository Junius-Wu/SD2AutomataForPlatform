package com.jun.xsPlatFormBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.awt.geom.Line2D.Double;
import java.util.List;
// ������
public class FragmentPart extends Entity{
	
	// �����������
	@XStreamAlias("conditionText")
	String conditionText;
	
	// ��������Ϣ��EAID
	@XStreamAlias("coveredMessagesID")
	coveredMessagesID MessagesID;
	@XStreamAlias("coveredMessagesID")
	public class coveredMessagesID {
		@XStreamImplicit(itemFieldName = "string")
		public List<String> messageIDs;
	}
	// ������ĸ߶�
	@XStreamAlias("size")
	String height;
	
	// lin2DLine.x1,y1 ���Ͻǵĵ� x2,y2 ���Ͻǵĵ�  ����ע����line2D.Double
	@XStreamAlias("borderline")
	Double line2DLine;

	public String getConditionText() {
		return conditionText;
	}

	public void setConditionText(String conditionText) {
		this.conditionText = conditionText;
	}

	public coveredMessagesID getMessagesID() {
		return MessagesID;
	}

	public void setMessagesID(coveredMessagesID messagesID) {
		MessagesID = messagesID;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public Double getLine2DLine() {
		return line2DLine;
	}

	public void setLine2DLine(Double line2dLine) {
		line2DLine = line2dLine;
	}
	
	
}
