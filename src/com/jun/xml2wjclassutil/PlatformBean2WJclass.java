package com.jun.xml2wjclassutil;

import java.awt.geom.Line2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.jun.xsPlatFormBean.ActivationBarNode;
import com.jun.xsPlatFormBean.CallEdge;
import com.jun.xsPlatFormBean.Children;
import com.jun.xsPlatFormBean.CombinedFragment;
import com.jun.xsPlatFormBean.FragmentPart;
import com.jun.xsPlatFormBean.LifelineNode;
import com.jun.xsPlatFormBean.SequenceDiagramGraph;
import com.jun.xsPlatFormBean.TimeEdge;

import uml.FixFragmentTool;
import uml.REF;
import uml.WJDiagramsData;
import uml.WJFragment;
import uml.WJLifeline;
import uml.WJMessage;
import uml.WJRectangle;

public class PlatformBean2WJclass {

	
	static SequenceDiagramGraph mPlatformSD;
	
	// ��ƽ̨������xml --xstream--> xsPlatformBean --�����������������--> WJDiagramsData
	public static WJDiagramsData transform(SequenceDiagramGraph sd0, String name) {
		
		mPlatformSD = sd0;
		
		WJDiagramsData WJSD = new WJDiagramsData();
		
		initDiagramData(WJSD, name);
		
		setLifeline(WJSD);
		
		setFragment(WJSD);
		
		setMessage(WJSD);
		
		setRefIndex(WJSD);
		
		return WJSD;
	}

	

	private static void setRefIndex(WJDiagramsData WJSD) {
		for(REF ref : WJSD.getRefArray()) {
			ref.setIndex(FixFragmentTool.refIndexInDiagram(ref, WJSD));			
		}
	}

	private static void setMessage(WJDiagramsData WJSD) {
//	V	String inFragId="null";//���ĸ�Ƭ����
//	V	String inFragName="null"; // ���Ƭ�ε����� opt break...
//	V	String id="null";   // ��Ϣ��id
//	V	String sourceId="null"; // �����id
//	V	String tragetId="null"; // �����id
//	V	String fromId;   //���ĸ������ĸ�����
//	V	String toId;	//���ĸ������ĸ�����
//	X?	String name="null"; // ��Ϣ������	
//	X	String inString; // ����
//	X	String outString;// ���
//  X   String SEQTC;// ��Ϣ�ϵ�ʱ��
		
		ArrayList<WJFragment> fragments = WJSD.getFragmentArray();
		ArrayList<WJLifeline> lifelines = WJSD.getLifelineArray();
		for(CallEdge callEdge : mPlatformSD.getEdges().getCallEdges()) {
			WJMessage message = new WJMessage();
			
			message.setConnectorId(callEdge.getEAID());// ��Ϣ��id
			//message.setName(callEdge.getParameterStringValue());// ?????
			message.setSourceId(callEdge.getStart().fromNodeId);// �����id
			message.setTargetId(callEdge.getEnd().toNodeId);// �����id
			message.setName(callEdge.getName());// ��Ϣ������
			message.setInString(callEdge.getInput());// ����
			message.setOutString(callEdge.getOutput());// ���
			message.setSEQTC(callEdge.getTiming());// ʱ��Լ��
			message.setPointY(callEdge.getStartLocation().y);
			for(WJLifeline lifeline : lifelines) { // �ҵ� ���ĸ����� ���ĸ�����
				if (lifeline.getActivationIDs().contains(message.getSourceId())) {
					message.setFromId(lifeline.getLifeLineId());
				}
				if (lifeline.getActivationIDs().contains(message.getTargetId())) {
					message.setToId(lifeline.getLifeLineId());
				}
			}
			if (mPlatformSD.getEdges().getTimeEdges() != null) {
				for(TimeEdge timeEdge : mPlatformSD.getEdges().getTimeEdges()) {
					if (callEdge.getId().equals(timeEdge.getEndEdge().toMessageId)) {
						message.setSEQDO(timeEdge.getTimeStringValue());
					}
				}
			}
			
			
			// ����������Ϣ���� �����С��fragment
			WJFragment theMinAreaContainsThisMessage = new WJFragment();
			double minArea = java.lang.Double.MAX_VALUE;
			for(WJFragment fragment : fragments) {
				if (fragment.getContainsMessageIDs().contains(message.getConnectorId())) {
					WJRectangle rectangle = fragment.getRectangle();
					double area = (rectangle.getRight() - rectangle.getLeft()) *
							(rectangle.getBottom() - rectangle.getTop());
					if (area < minArea) {
						minArea = area;
						theMinAreaContainsThisMessage = fragment;
					}
				}
			}
			// ���� inFrag��Ϣ
			message.setInFragId(theMinAreaContainsThisMessage.getFragId());
			message.setInFragName(theMinAreaContainsThisMessage.getFragType());
			
			
			WJSD.getMessageArray().add(message);
		}
		
	}

	private static void setFragment(WJDiagramsData WJSD) {
		if (mPlatformSD.getNodes().getFragments() == null) {
			return ;
		}
		ArrayList<WJFragment> resFragmentList = new ArrayList<>();
		
		// ����reference ������ �õ�ʵ�ʵ�Type Name
		HashMap<String, String> referenceType = new HashMap<>();
		// ӳ������id �� typeName
		for(CombinedFragment combinedFragment : mPlatformSD.getNodes().getFragments()) {
			if (combinedFragment.getType().name != null) { 
				referenceType.put(combinedFragment.getType().id, combinedFragment.getType().name);
			}
		}
		// �õ����õ�type���� 
		for(CombinedFragment combinedFragment : mPlatformSD.getNodes().getFragments()) {
			if (combinedFragment.getType().name == null) { 
				combinedFragment.getType().name = referenceType.get(combinedFragment.getType().reference);
			}
			// תΪСд
			combinedFragment.getType().name = combinedFragment.getType().name.toLowerCase();
		}
		
		// ��ʽ��ȡ��Ϣ
		for(CombinedFragment combinedFragment : mPlatformSD.getNodes().getFragments()) {
			// ���е�����
			List<String> conditions = combinedFragment.getConditions().strings;
			String fragmentEAID = combinedFragment.getEAID();
			String typeName = combinedFragment.getType().name;
			boolean needComId = typeName.equals("alt") || typeName.equals("par");
			int index = 0;
			for(FragmentPart part : combinedFragment.getFragmentParts().fragmentParts) {
				WJFragment fragment = new WJFragment();
				fragment.setFragType(typeName);
				fragment.setFragId(fragmentEAID + "-" + index);
				fragment.setFragCondition(conditions.get(index++));
//				fragment.getContainsMessageIDs().addAll(part.getMessagesID().messageIDs); 
				// ƽ̨��bug����ֱ�Ӷ�ȡ �� �п���Ϊ�� �����
				// ��Ҫ �Լ��ж���Ϣ�Ŀ�ʼlocation���� ���ڲ���fragment���� ������ҪҪ��һ����С��fragment����setMessage��ʵ�֣�
				fragment.setRectangle(getRectangleByXYAndWH(part, combinedFragment.getWidth(), combinedFragment.getHeight()));
				fragment.getContainsMessageIDs().addAll(getMessageIDsWitchInTheFragment(
						mPlatformSD.getEdges().getCallEdges(), fragment));
				
				if (needComId) { // alt par ��ҪcomId
					fragment.setComId(fragmentEAID);
				}
				if (fragment.getFragType().equals("ref")) {
					REF ref = new REF();
					ref.setDiagramName(fragment.getFragCondition());
					ref.setRefID(fragment.getFragCondition());
					ref.setRectangle(fragment.getRectangle());
					ref.setInFragID("null");
					ref.setInFragName("SD");
					//ref.setIndex(FixFragmentTool.refIndexInDiagram(ref, WJSD));
					WJSD.getRefArray().add(ref);
				} else {
					resFragmentList.add(fragment);
				}
				
			}
		}
		
		WJSD.setFragmentArray(resFragmentList);
		// fix bigId
		FixFragmentTool.fixFragmentsOfOneDiagram(WJSD);
		
	}

//  ��Щ ��Ϣ �����fragment��
	private static Collection<? extends String> getMessageIDsWitchInTheFragment(List<CallEdge> callEdges,
			WJFragment fragment) {
		ArrayList<String> resMessageIds = new ArrayList<>();
		WJRectangle rectangle = fragment.getRectangle();
		
		for(CallEdge callEdge : callEdges) {
			double x = callEdge.getStartLocation().x;
			double y = callEdge.getStartLocation().y;
			if (x > rectangle.getLeft() && x < rectangle.getRight()
					&& y > rectangle.getTop() && y < rectangle.getBottom()) {// ˵��fragment �������calledge
				resMessageIds.add(callEdge.getEAID());
			}
		}
		return resMessageIds;
	}

	// ����������WJrectangle
	private static WJRectangle getRectangleByXYAndWH(FragmentPart part, double width, double height) {
		Double upLine = part.getLine2DLine();
		double partHeight = java.lang.Double.valueOf(part.getHeight());
		double left = upLine.getX1();
		double right = upLine.getX2();
		double top = upLine.getY1();
		double bottom = top + partHeight;
		WJRectangle resRectangle = new WJRectangle(left, top, right, bottom);
		return resRectangle;
	}
	
	// ������ȱ��� activationBarNode �õ����lifeline���������м����id
	private static ArrayList<String> getActivationIDs(LifelineNode lifelineNode) {
		ArrayList<String> resIDs = new ArrayList<>();
		Children c = lifelineNode.getC();
		
		Queue<ActivationBarNode> queue = new LinkedList<>();
		if (lifelineNode.getC().getNodes() == null) {
			return resIDs;
		}
		// ����һ���activationBarNode
		for(ActivationBarNode node : lifelineNode.getC().getNodes()) {
			queue.offer(node);
		}
		//��BFS
		while(!queue.isEmpty()) {
			ActivationBarNode nodeFather = queue.poll();
			resIDs.add(nodeFather.getId());
			
			Children cFather = nodeFather.getC();
			if (cFather.getNodes() != null) {
				for(ActivationBarNode nodeChild : cFather.getNodes()) {
					queue.offer(nodeChild);
				}
			}
			
		}
		return resIDs;
	}
	
	private static void setLifeline(WJDiagramsData WJSD) {
		for (LifelineNode lifelineNode : mPlatformSD.getNodes().getLiflines()) {
			WJLifeline lifeline = new WJLifeline();
			WJSD.getIds().add(lifelineNode.getEAID());
			lifeline.setLifeLineId(lifelineNode.getEAID());
			lifeline.setlifeLineName(lifelineNode.getName().text);
			lifeline.setActivationIDs(getActivationIDs(lifelineNode));
			WJSD.getLifelineArray().add(lifeline);
		}
	}



	private static void initDiagramData(WJDiagramsData WJSD, String name) {
		
		ArrayList <String> ids = new ArrayList<String>(); // ������Ϣ�����Ƭ�Σ������ߵ�EAID
		ArrayList <WJLifeline> lifelineArray = new ArrayList <WJLifeline>();
		ArrayList <WJFragment> fragmentArray = new ArrayList <WJFragment>();
		ArrayList <WJMessage> messageArray = new ArrayList <WJMessage>();
		
		// ������sd�Ļ�����Ϣ
		WJSD.setName(name);
		WJSD.setIds(ids);
		WJSD.setDiagramID(mPlatformSD.getId());
		WJSD.setLifelineArray(lifelineArray);
		WJSD.setMessageArray(messageArray);
		WJSD.setFragmentArray(fragmentArray);
	}
	
}
