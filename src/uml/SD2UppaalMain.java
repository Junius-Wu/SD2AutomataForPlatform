package uml;

import java.io.File;
import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.locks.Condition;

import javax.tools.DocumentationTool.Location;
import javax.xml.transform.Templates;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.Locator;

import com.jun.util.Display;
import com.jun.xml2wjclassutil.PlatformBean2WJclass;
import com.jun.xsPlatFormBean.SequenceDiagramGraph;
import com.jun.xsutil.XML2xmlbeanUtil;
import com.jun.xsutil.XStreamTransformUtil;

import uml.Write;

public class SD2UppaalMain {
	private static HashMap<String , WJFragment> id_fragment = new HashMap<String , WJFragment>();
	
	private static ArrayList<WJMessage> messageList=new ArrayList<WJMessage>();//�������������message
	private static ArrayList<WJFragment> fragmentList=new ArrayList<WJFragment>();//�������������fragment
	private static ArrayList<ArrayList<WJFragment>> table = new ArrayList<ArrayList<WJFragment>>();//��
	private static ArrayList<UppaalTransition> transitionList = new ArrayList<UppaalTransition>();//�������������transition
	private static ArrayList<UppaalLocation> locationList = new ArrayList<UppaalLocation>();	//�������������location
	private static int [][] map ;
	private static int m_id;
	private static int [] altEndTo;
	private static int [] parEndTo;
	private static ArrayList<HashSet <Integer>>  F = new ArrayList<HashSet <Integer>>();
	private static ArrayList<HashSet <Integer>>  F1 = new ArrayList<HashSet <Integer>>();
	private static ArrayList<HashSet <Integer>>  G = new ArrayList<HashSet <Integer>>();
	private static HashSet <Integer> endState = new HashSet <Integer>();
	private static ArrayList <WJLoopFragment> loopFragment = new ArrayList <WJLoopFragment> ();
	private static Stack <WJParFragment> parFragment = new Stack<WJParFragment>();
	private static ArrayList <Integer> exAdd = new ArrayList <Integer>();
	private static String[][] outOfLoopCondition;
	private static String[][] jumpCondition;//��i - 1��->j�ǲ�����opt loop break ������Ծ��ȡ��������(i - 1) j ֮�������һ�������������Ƭ�Σ�
//	private static ArrayList<ArrayList<HashSet<String>>> jumpConditions;
	private static String[][] falseConditions;// outLoopConditon + jumpCodition
	private static String[][] breakIgnoreLoopCondition;
	public static void main(String[] args) throws Exception 
	{
		
		//����ͼ
		ArrayList<WJDiagramsData> DiagramsDataList = new ArrayList<WJDiagramsData>();
		
		//���ͼ��lifelines ������
		ArrayList<WJLifeline> lifeLines=new ArrayList<WJLifeline>();//
		ArrayList<WJMessage> messages=new ArrayList<WJMessage>();
		ArrayList<WJFragment> fragments=new ArrayList<WJFragment>();
		ArrayList<UppaalTemplate> templates=new ArrayList<UppaalTemplate>();
		
		
		HashSet<String > template_names=new HashSet<String>();
		
//   ---��ȡEA������---	0	
		String filePath = "sdtest.xml";
		SAXReader reader=new SAXReader();//��ȡ������
	    Document dom= reader.read(filePath);//����XML��ȡ���������ĵ���dom����
	    Element root=dom.getRootElement();//��ȡ���ڵ�
	    
	    Read uml=new Read();
	    uml.load(root);
	    

	    // �õ�����ͼ��Ӧ����������
	    DiagramsDataList = uml.getUmlAllDiagramData();
//   ***END***
		
		
		
		
//   ---��ȡƽ̨������---	1	
//		Display.println("================================���ڶ�ȡ����������˳��ͼxml================================");
//		String folderPath = "D:\\workspace\\SD2AutomataForPlatform\\platformXMLS\\";
//		File file = new File(folderPath);
//		String fileNames[];
//		fileNames = file.list();
//		Display.println("===>  ɨ�赽" + fileNames.length +"��ͼ��\n");
//		for (int i = 0; i < fileNames.length; i++) {
//			if (!fileNames[i].contains(".seq.violet.xml")) {
//				continue;
//			}
//			Display.println(fileNames[i]);
//			SequenceDiagramGraph sd0 = XML2xmlbeanUtil.getSd(new File(folderPath + fileNames[i]));
//			WJDiagramsData platformDiagram = PlatformBean2WJclass.transform(sd0, fileNames[i].split(".seq.violet.xml")[0]);
//			DiagramsDataList.add(platformDiagram);
//		}
//		Display.println();
//		Read.umlAllDiagramData.addAll(DiagramsDataList);
//		for(WJDiagramsData diagramData : DiagramsDataList) {
//			Read.DFSDiagramByREF(diagramData);
//		}
//   ***END***	    
	
	    
	    // ����ͼDiagramsDataList
	    Iterator<WJDiagramsData> DiagramsDataListIterator = DiagramsDataList.iterator();   
	    while(DiagramsDataListIterator.hasNext())
	    {  
	    	//��õ�i��ͼ
	    	WJDiagramsData diagramDaraI = DiagramsDataListIterator.next();
	    	
	    	Display.println("================================���ڻ�ȡ˳��ͼ��Ϣ================================");
			
		    Display.println("===>  ͼ��Ϊ:"+diagramDaraI.name + "\n");
		    if (diagramDaraI.name.equals("UAV")) {
				System.out.println(1);
			}
		   
		    //��ʼ��
		    Display.println("��ʼ������...\n");
		    lifeLines.clear();
		    messages.clear();
		    fragments.clear();
		    templates.clear();
		    id_fragment.clear();
		    lifeLines = diagramDaraI.getLifelineArray();
		    fragments = diagramDaraI.getFragmentArray();
		    messages = diagramDaraI.getMessageArray();
//		    // �������message�Ķ�������ǰ��
//		    setMessagePreName(messages, lifeLines);
		    
		    
		    //id����fragment hashmap
	    	id_fragment.clear();
	   	    Iterator<WJFragment> fragmentsIterator = fragments.iterator();
		    while(fragmentsIterator.hasNext())
		    {
		    	WJFragment I = (WJFragment) fragmentsIterator.next();	    		    	    	
		    	id_fragment.put(I.getFragId(),I);
		    }
		  
		    WJFragment sd = new WJFragment();//����SD��idΪnull
		    sd.setFragType("SD");
		    sd.setBigId("nothing");
		    id_fragment.put("null", sd);
		    WJFragment y = new WJFragment();
		    id_fragment.put("nothing", y);

		    
		    	UppaalTemplate template=new UppaalTemplate();
		    	messageList.clear();//�������
		    	fragmentList.clear();
		    	table.clear();
		    	transitionList.clear();
		    	locationList.clear();
		    	endState.clear();
		    
		    	//��table�������һ�����ñ߽�
	    		WJFragment none0 = new WJFragment();
		    	none0.setBigId("nothingID");
		    	none0.setFragId("none");
		    	none0.setFragType("none");
		    	ArrayList <WJFragment> temp = new ArrayList <WJFragment>();
		    	temp.add(none0);
		    	table.add(0, temp);
		    	
		    	int I = 0;//��ʾ�ڼ���messageI ���ڽ�����
		    	
		    	
			    
			    if(messages.size() == 0)//û����Ϣ
			    {	    	
			    	Display.println("û���ҵ�message���˳�");
			    	continue;
			    }
			    else
			    {//����Ϣ
			    	Display.println("-------------------------����״̬-------------------------");
			    	
			    	
			    	//����Q0
			    	Display.println("������ʼ״̬Q0\n");
				    String q_id = "_init" ;
				    m_id=0;
				   
				    
				    UppaalLocation location0 = setLocation(q_id,q_id);	 //id,   ״̬name  
				    
				    location0.setInit(true);
				    location0.setObjId(messages.get(0).getFromId());
			    
				    Iterator<WJLifeline> lifelineIteratorForName = lifeLines.iterator();
				    while(lifelineIteratorForName.hasNext())
				    {//��������lifelineȷ��id��Ӧ������
				    	WJLifeline lifeline = lifelineIteratorForName.next();
				    	if (location0.getObjId().substring(13).equals(lifeline.getLifeLineId().substring(13))) {
							location0.setObjName(lifeline.getlifeLineName());
						}
				    }
 
				    template.locations.add(location0);	
				    locationList.add(location0);
				    
				    
			    	Iterator<WJMessage> messageIterator = messages.iterator();
			    	
			    	
			    	
			    	map = new int[messages.size()*2+3][messages.size()*2+3];
			    	
			    	messageIterator = messages.iterator();
			    	int messageIndex = -1;
			    	
			    	while(messageIterator.hasNext())//����message  ��location  ����
			    	{

			    			messageIndex ++;
		    				WJMessage messageI = messageIterator.next();//1toN	
		    				if (messageI.getName().contains("@@")) {
								System.out.println(1);
							}
			    		
			    			I++;
				    		messageList.add(messageI);									//���� ��ʾ��������ߵ�����message
				    		fragmentList.add(id_fragment.get(messageI.getInFragId()));	//��ʾ��������ߵ�����fragment
				    		WJFragment sa=id_fragment.get(messageI.getInFragId());
				    		
				    		ArrayList <WJFragment> tableI = setTableI(sa);//����table
				    		
				    		table.add(I, tableI);
				    		
					    	
					    	
		//���location
				    		Display.println("����"+ messageI.getName() + "��Ϣ���ͺ󵽴��״̬\n");
			    			UppaalLocation location = setLocation(messageI.getConnectorId().substring(4),messageI.getName());
			    			//��һ����Ϣ��DCBM
			    			if (messageIndex != messages.size() - 1) {
			    				location.setTimeDuration(messages.get(messageIndex + 1).getDCBM());
							}
			    			location.setT1(messageI.getT1());
			    			location.setT2(messageI.getT2());
			    			location.setR1(messageI.getR1());
		    				location.setR2(messageI.getR2()); 
		    				location.setEnerge(messageI.getEnerge());
		    				location.setObjId(messageI.getToId());
						    Iterator<WJLifeline> lifelineIteratorForName2 = lifeLines.iterator();
						    while(lifelineIteratorForName2.hasNext())
						    {//��������lifelineȷ��id��Ӧ������
						    	WJLifeline lifeline = lifelineIteratorForName2.next();
						    	
						    	if (location.getObjId().substring(13).equals(lifeline.getLifeLineId().substring(13))) {
									location.setObjName(lifeline.getlifeLineName());
								}	
						    }
//warning���������messageI����Ϣ��location��
			        		template.locations.add(location);
			        		locationList.add(location);
			        		
			        		// ��alt���Ӵ��Ĵ���	        		 ���map
			        		if(I != 0 && hasAlt(table.get(I-1))&&hasAlt(table.get(I)))//��������alt   �������alt�еĽ��Ӵ�
			        		{
			        			int thelength = table.get(I).size()<table.get(I-1).size()? table.get(I).size():table.get(I-1).size();
			        			//��ͬ��break���㷨 ��altֻ��Ҫ���̵�һ���Ϳ�����
			        			for(int c =0;c<thelength;c++)
			        			{
			        				if(table.get(I-1).get(c).getFragType() .equals ("alt")&&
			        						table.get(I).get(c).getFragType() .equals ("alt")&&
			        					!table.get(I-1).get(c).getFragId() .equals (table.get(I).get(c).getFragId())&&
			        						table.get(I-1).get(c).getComId().equals(table.get(I).get(c).getComId())
			        						)//ͬһ��alt�еĲ�ͬ������
			        				{	    
			        					map[I-1][I] = -1; //������� ����������
			        					break;
			        				}
			        			}
			        			
			        		}	
			        		
			        		// ��par���Ӵ��Ĵ���	        		 ���map
			        		if(I != 0 && hasPar(table.get(I-1))&&hasPar(table.get(I)))//��������par   �������par�еĽ��Ӵ�
			        		{
			        			int thelength = table.get(I).size()<table.get(I-1).size()? table.get(I).size():table.get(I-1).size();
			        			//��ͬ��break���㷨 ��parֻ��Ҫ���̵�һ���Ϳ�����
			        			for(int c =0;c<thelength;c++)
			        			{
			        				if(table.get(I-1).get(c).getFragType() .equals ("par")&&
			        						table.get(I).get(c).getFragType() .equals ("par")&&
			        					!table.get(I-1).get(c).getFragId() .equals (table.get(I).get(c).getFragId())&&
			        						table.get(I-1).get(c).getComId().equals(table.get(I).get(c).getComId())
			        						)//ͬһ��alt�еĲ�ͬ������
			        				{	        
			        					Display.println("���par���Ƭ�εĲ�����ָ�λ��");
			        					Display.println("��"+(I-1)+"����Ϣ���"+(I) +"����Ϣ֮���Ǵ��ڲ�ͬ�������par���Ƭ����"); 
			        					map[I-1][I] = -1; //������� ����������
			        					break;
			        				}
			        			}
			        			
			        		}
			    		        				    	    		    	    		
		    	    		
		        		
	
			    		
			    	
			    	}//����message ˳����ӳ���break��alt֮�����transition
			    	
			    	
	//���table
		    	int maxLength = 0;
		    	for(int i = 0 ; i<table.size();i++)
		    	{
		    		if(table.get(i).size()>maxLength)
		    			maxLength = table.get(i).size();
		    	}//����󳤶�
		    	WJFragment none = new WJFragment();
		    	none.setBigId("nothingID");
		    	none.setFragId("none");
		    	none.setFragType("none");
		    	
		    	for(int i = 0 ; i<=table.size();i++)
		    	{//���none
		    		
			    	
		    		if(i==table.size())//���ײ�
		    		{
		    			ArrayList <WJFragment> t = new ArrayList <WJFragment>();
		    			for(int j = 0; j <=maxLength;j++)
		    			t.add(none);
		    			
		    			table.add(i, t);
		    			break;
		    		}
		    		else//����ұ߲���
		    		{
		    			for(int j = table.get(i).size(); j <=maxLength;j++)
		    			table.get(i).add(j, none);
		    		}
		    	}
		    	
		    	altEndTo = new int[table.size() * 2];
		    	parEndTo = new int[table.size() * 2];
		    	Display.println("\n-------------------------������Ϣ���ڵ����Ƭ�Σ��������Ƭ��Ƕ�ױ�-------------------------");
	//��ӡtable  �����ҳ����н��Ӵ�i�ĳ�altλ�÷���altEndTo ����λ������alt�Ľ��Ӵ� while��altEndTo[altEndTo[i]]!=0��{i=altEndTo[i]}return altEndTo[i]
		    	
		    	for(int i = 0;i<table.size();i++)
		    	{
		    		Display.print(String.format("%-2d:", i));
		    		for(int c = 0;c<table.get(i).size();c++)
		    		{
		    			if(i>=1&&table.get(i-1).get(c).getFragType().equals("alt")&&
	    						table.get(i).get(c).getFragType().equals("alt")&&
	    					!table.get(i-1).get(c).getFragId().equals(table.get(i).get(c).getFragId())&&
	    						table.get(i-1).get(c).getComId().equals(table.get(i).get(c).getComId())
	    						)//ͬһ��alt�еĲ�ͬ������ �ǽ��Ӵ�����һ��
		    			altEndTo[i] = findOutOfAlt(i,c);//�ҵ���alt��λ��
		    			
		    			if(i>=1&&table.get(i-1).get(c).getFragType().equals("par")&&
	    						table.get(i).get(c).getFragType().equals("par")&&
	    					!table.get(i-1).get(c).getFragId().equals(table.get(i).get(c).getFragId())&&
	    						table.get(i-1).get(c).getComId().equals(table.get(i).get(c).getComId())
	    						)//ͬһ��par�еĲ�ͬ������ �ǽ��Ӵ�����һ��
		    			parEndTo[i] = findOutOfAlt(i,c);//�ҵ���par��λ��  findOutOfAlt������par
		    			
		    			Display.print(String.format("%-6s", table.get(i).get(c).getFragType()));
		    		}
		    		Display.println();
		    	}
		    	Display.println();
		    	Display.println();
		    	loopFragment = new ArrayList <WJLoopFragment> ();
		    	parFragment.clear();
	//��Ҫ�㷨
		    	
		    	//��ʼ��F & G
		    	F = new ArrayList<HashSet <Integer>>();	   
		    	F1 = new ArrayList<HashSet <Integer>>();	  
		    	G = new ArrayList<HashSet <Integer>>();
	
		    	HashSet <Integer> Fi = new HashSet <Integer>();
		    	F.add(Fi);//�����ܵ���0 ������ӿ�
		    	HashSet <Integer> Gi = new HashSet <Integer>();
		    	Gi.add(1);//0�ܵ���1
		    	G.add(Gi);
		    	
		    	//��ʼ�� falseCondition
		    	jumpCondition = new String[table.size()][table.size()];
		    	breakIgnoreLoopCondition = new String[table.size()][table.size()];
			    for(int i = 1; i < table.size()-1; i++)
			    {
			    	Fi = new HashSet <Integer>();
			    	Gi = new HashSet <Integer>();
			    	
			    	Fi.add(i);
			    	if(!hasLastBreak(i) && map[i][i+1]!=-1)//����Ϣ���� break�����һ����Ϣ && ����alt�Ľ��紦
			    	{			
			    		Gi.add(i+1); // ����ֱ�ӵ���һ����Ϣ
			    	}
			    	for(int c = 2; c < table.get(i).size();c++ )
			    	{
			    		if(table.get(i).get(c).getFragType() .equals( "none"))
			    			break;
			    				    		
			    		if(table.get(i).get(c).getFragId() != table.get(i-1).get(c).getFragId())//�����Ƭ�εĵ�һ����Ϣ
			    		{
			    			Fi.addAll(findAlsoTo(i,c,table.get(i).get(c).getFragType()));
			    		}
			    		
			    		if(table.get(i).get(c).getFragId() != table.get(i+1).get(c).getFragId())///�����Ƭ�ε����һ����Ϣ
			    		{
			    			Gi.addAll(findOutTo(i,c,table.get(i).get(c).getFragType()));	 
			    		}
			    	}
			    	
			    	F.add(Fi);
	    			G.add(Gi);
			    }
			    Fi = new HashSet<Integer>();
			    Fi.add(table.size()-1);
			    F.add(Fi);//���һ��F(i)�������ڣ�������Ӻ���Ϊ��̬���ж���loop��end�ж�
			    for(int i = 0; i < F.size(); i++) {//����һ����Ծ��F �� F1
			    	HashSet<Integer> temp1 = new HashSet<>();
			    	for(int x : F.get(i)) {
			    		temp1.add(x);
			    	}
			    	F1.add(temp1);
			    }
			    fixF();//��F��һ����Ծ���ŵ������Ծ
			    
			    
	
			    //������par��loop�����������  ����map
			    for(int i=0;i<F.size()-1;i++)
			    {
			    	//i���Ե���
		    		for(int Gii :G.get(i)) //i���Ե�Gii
		    		{
		    			for(int Fii: F.get(Gii))//�ܵ���Gii�Ļ��ֿ��ܵ���F(Gii)
				    	{	
		    				if (map[i][Fii] != -1) {
								map[i][Fii] = Fii;
							}
				    	}
		    		}
			    }
	//par����� //�ݲ�����par�������
//			    while(!parFragment.isEmpty())
//			    {
//			    	WJParFragment pfrag = parFragment.pop();//�ȴ�������par�ٴ�������par
//			    	
//			    	int c = pfrag.getC();
//			    	
//			    	for(int i = pfrag.getStart();i<pfrag.getEnd();i++)
//			    	{
//			    		HashSet <Integer> fromList = new HashSet <Integer>();
//				    	HashSet <Integer> toList = new HashSet <Integer>();
//				    	
//			    		String fragId = table.get(i).get(c).getFragId();//i���ڵĲ�����ID
//			    		if(table.get(i).get(c+1).getFragType().equals("none"))
//			    			fromList.add(i);
//			    		else if(
//			    				!table.get(i).get(c+1).getFragType().equals("alt") &&
//			    				!table.get(i).get(c+1).getFragType().equals("par")&&
//			    				!table.get(i).get(c+1).getFragId().equals(table.get(i+1).get(c+1).getFragId())  	    				
//			    				)//���Ƕ�׵Ĳ���alt��par ������Ƕ�׵����һ����Ϣ
//			    		{
//			    			int k = findRightI(i+1);
//			    			for(int j = findStartOfFrag(i,c+1);j<findOutOfFrag(i, c+1);j++)
//			    				if(map[j][k] >= 1)//�˱�Ƕ�׵����Ƭ������Щ��Ϣ�ܹ�����k
//			    					fromList.add(j);
//			    		}
//			    		else if(		    				
//			    				table.get(i).get(c+1).getFragType().equals("alt") || table.get(i).get(c+1).getFragType().equals("par")&&
//			    				!table.get(i).get(c+1).getComId().equals(table.get(i+1).get(c+1).getComId())  	    						
//			    				)//�����alt����par�Ĳ�����  �����ǲ���������һ����Ϣ
//			    		{
//			    			int k = findRightI(i+1);
//			    			for(int j = findStartOfAlt(i,c+1);j<findOutOfAlt(i, c+1);j++)
//			    				if(map[j][k] >= 1)
//			    					fromList.add(j);
//			    		}
//			    		else
//			    		{
//			    			continue;//�����Ƭ�ε��м䲿�� ����
//			    		}
//			    		
//			    		for(int t = pfrag.getStart();t<pfrag.getEnd();t++)//to what?
//			    		{
//			    			if(table.get(t).get(c).getFragId().equals(fragId))
//			    				continue;
//			    			if(table.get(t).get(c+1).getFragType().equals("none"))
//			    				toList.add(t);
//			    			else if(!table.get(t).get(c+1).getFragId().equals(table.get(t-1).get(c+1).getFragId())  	    						
//				    				)// �����Ƭ�εĵ�һ��
//			    			{
//			    				
//			    				for(Object o: F.get(t))
//			    		    	{
//			    					int fii = (int) o;
//			    					if(fii < findOutOfFrag(t, c))//��תʱ��������������֮��
//			    						toList.add(fii);
//			    		    	}
//			    			}		  				
//			    		}
//			    		
//			    		setMap(fromList,toList);//����map[fromList][toList]
//			    			
//			    	}
//			    }
			    
	//����loop��ȥ�����		  

			    for(int k=0;k<loopFragment.size();k++)
			    {
			    	int start = loopFragment.get(k).getStart();
			    	int idEnd = loopFragment.get(k).getEnd();
			    	WJLoopFragment thisFragment = loopFragment.get(k);
			    	int realEnd = findRightLoopEnd(idEnd);//�ҳ���ȷ��end ���ɴ���alt���Ӵ�����һ��
			    	int c = loopFragment.get(k).getC();
			    	//˭���Ե���end
			    	ArrayList<Integer> loopEndList = new ArrayList<Integer>();
			    	for(int i=start;i<idEnd;i++)//|id|
			    	{
			    		if(map[i][realEnd]>0 && !islastbreak(i,c+1))//i���Ե���reaLend ���Ҳ��Ǹ�loop����һ�����һ��break
			    			loopEndList.add(i);
			    	}
			    	for(int i = start + 1; i < idEnd; i++) {
			    		if (F1.get(i).contains(realEnd)) {//���������loop�����������һ���������Ƭ�Σ�������������Ƭ���������Ϣ���Բ���������������Ƭ��ֱ�ӷ���loop��start
			    			//i ����һ����Ծ ����realEnd ����ô˵��i����loop��start�� ����Ҫ������Ծ��������!opt��
							F1.get(i).add(start); //���һ����Ծ
							String condition = jumpCondition[i][realEnd];
							//�����Ծ����
							jumpCondition[i][start] = condition;
						}
			    		if (G.get(i).contains(realEnd)) { // ���һ�����Ե����һ��
							G.get(i).add(start);
						}
			    	}
			    	
			    	//˭��loop�Ŀ�ͷ
			    	ArrayList<Integer> loopStartList = new ArrayList<Integer>();
			    	
			    	for(Object obj2: F.get(start))
			    	{
			    		int startLocation = (int) obj2;
			    		if(startLocation<idEnd && startLocation>=start)//|id|
			    			loopStartList.add(startLocation);
			    	}
			    	//������пɴ�end�ĵ�loop�����п�ͷ
			    	for(int i=0;i<loopEndList.size();i++)
			    		for(int j=0;j<loopStartList.size();j++)
			    			map[loopEndList.get(i)][loopStartList.get(j)] = loopStartList.get(j);
			    	
			    }
			    // ���ó�loop��ȡ������
			    outOfLoopCondition = new String[table.size()][table.size()];
			    Display.println("-------------------------��ȡ����loop������-------------------------");
			    setOutOfLoopCondition();
			    Display.println();
//			   // ��ʼ��falseConditions
//			    jumpConditions = new ArrayList<>();
//			    for(int i = 0; i < table.size(); i++) {
//			    	ArrayList<HashSet<String>> arrayListI = new ArrayList<HashSet<String>>();
//			    	for (int j = 0; j < table.size(); j++) {
//						HashSet<String> set = new HashSet<>();
//						arrayListI.add(set);
//					}
//			    	jumpConditions.add(arrayListI);
//			    }
			
	//����     ��Ծ����	
			    Display.println("-------------------------��ȡ���Ƭ�ε�ȡ������-------------------------");
			    fixF1ForFalseCondition();
			    Display.println();
	//�ڴ˴�������̬ 
			    for(int i=0;i<locationList.size();i++)
			    {
			    	if(map[i][locationList.size()] >= 1)
			    		locationList.get(i).setFnal(true);
			    }
	
			    
	//��ӡmap
			    Display.println("-------------------------�õ��ڽӾ���-------------------------");
			    for(int i=0;i<locationList.size()+1;i++)		
			    {	
			    	Display.print(String.format("%-2d:", i));
			    	for(int j=0;j<locationList.size()+1;j++)
			    		Display.print(String.format("%-3d", map[i][j]));
			    	Display.println();
			    }
			   Display.println("\n");
	//����map
			   Display.println("--------------------------�����ڽӾ�������״̬--------------------------");
			    //Queue receiveAndSend = new LinkedList();
			    boolean isSelfMessage = false;
			    for(int i=0;i<locationList.size();i++) {
		    		for(int j=0;j<locationList.size();j++)
		    		{
		    			if (map[i][j] == -1) { // ״̬��ʱ��Լ��ȡ����һ����Ϣ  ȥ��alt������֮���ʱ��Լ��
		    				// ʵ������ ��֧��״̬������������ͬ��ʱ��Լ�� ��һ���޷���ȡ
							locationList.get(i).setTimeDuration("null");
						}
		    			if(map[i][j] >= 1)
			    		{
		    				
			    			//message
		    				WJMessage messageJ = messageList.get(j-1);
		    				
							
							
							String receiveOrSend = "";
							
							if(messageJ.getReceiveAndSend().equals("null"))//�����Լ����Լ�����Ϣ��ɵ�2���ظ���transition ��Ϊһ����һ����
							{	
						    		
								if(messageJ.getFromId().substring(13).equals(messageJ.getToId().substring(13)))
					    		{	
					    			receiveOrSend+="!?";
					    		}		
							
					    		if(receiveOrSend.equals("!?") && j < messageList.size())
					    		{
					    			messageList.get(j).setReceiveAndSend("?");//��һ���ǣ�
					    			receiveOrSend = "!";//����ĳɣ�
					    			isSelfMessage = true;//���Լ����Լ�����Ϣ
					    		}
					    			
							}
							else
							{
								receiveOrSend = messageJ.getReceiveAndSend();
							}
							
							//��Ҫ����opt��else Ϊ !(opt����)
							String elseCondition = "";
							if (falseConditions[i][j] != null) {
								elseCondition = falseConditions[i][j];
							}
							String outCondition = "";
							if (outOfLoopCondition[i][j] != null) {
								outCondition = outOfLoopCondition[i][j];
							}
				    		
							//location ���
							UppaalLocation lastLocation0=locationList.get(i);
							//location �յ�
							UppaalLocation location=locationList.get(j);
							UppaalTransition transition = new UppaalTransition();
							String T1 = messageJ.getT1();
							String T2 = messageJ.getT2();
							if(isSelfMessage && receiveOrSend.equals("!"))
							{  
								T1 = "0";
								T2 = "0";
								isSelfMessage = false;
								
							}	
							
							String typeAndCondition = getTypeAndnCondition(messageJ) ;
							if(!elseCondition.equals("")) {
								if (typeAndCondition.equals("null")) {
									typeAndCondition = elseCondition;
								} else {
									typeAndCondition = typeAndCondition + "--"+ elseCondition ;
								}
							}
							if(!outCondition.equals("")) {
								if (typeAndCondition.equals("null")) {
									typeAndCondition = outCondition;
								} else {
									typeAndCondition = typeAndCondition + "--"+ outCondition ;
								}
							}
							transition = setTransition(messageJ,
									m_id++,
									messageJ.getName(),
									receiveOrSend,
									typeAndCondition,
		    	    				lastLocation0.getId(),lastLocation0.getName(),
		        					location.getId(),    location.getName(),
		        					messageJ.getT1(),messageJ.getT2());
							template.transitions.add(transition);
		    	    		
							Display.println("����״̬" + i +"��״̬" + j + "\n" +  
									"��Ϣ����" + transition.getNameText()  + 
									"\n��Ϣ�����ڵ����Ƭ���Լ�����" +transition.getTypeAndCondition() + "\n");
		    	    		
		    			}
		    		}
			    }
	//��������map���transition
			    
			    }//message
			    
			    template.setName("template_");
			    //+lifelineI.getlifeLineName());
			    template_names.add("template_");
			    //+lifelineI.getlifeLineName());
			    templates.add(template);  
		    Display.println("\n------------------------------------------------------------------");
		    Display.println("���˳��ͼ���Զ�����ת��������д��ͼ��Ϊ"+diagramDaraI.name+"��xml");
		    Write.creatXML(diagramDaraI.name+".xml",templates,template_names);
		    WriteForXStream.creatXML(diagramDaraI.name+"ForXStream.xml", templates, template_names);
		    Display.println(".....д�����!\n");
	    }//����diagram����
	    Display.println("================================����˳��ͼת�����================================");
	    System.err.println("�ļ�"+ filePath);
	}//end
	


	private static void setMessagePreName(ArrayList<WJMessage> messages, ArrayList<WJLifeline> lifeLines) {
		HashMap<String, WJLifeline> findLifeLineByID = new HashMap<>();
		for(WJLifeline lifeline : lifeLines) {
			findLifeLineByID.put(lifeline.getLifeLineId().substring(13), lifeline);
		}
		for(WJMessage message : messages) {
	    	WJLifeline lifeline = findLifeLineByID.get(message.getToId().substring(13));
	    	message.setName(lifeline.getlifeLineName() + "." +message.getName());
	    }
	}



	private static String getFilePath() throws Exception {
		File file = new File(".\\");
		String fileNames[];
		fileNames = file.list();
		String readFileName = "0";
		for(String fileName : fileNames) {
			if (Character.isDigit(fileName.charAt(0)) && fileName.charAt(1) == '.') {
				if (compareVersion(fileName.replace(".xml", ""), readFileName.replace(".xml", "")) > 0) {
					readFileName = fileName;
				}
			}
		}
		return readFileName;
	}
	public static int compareVersion(String version1, String version2) throws Exception {  
		
	    if (version1 == null || version2 == null) {  
	        throw new Exception("compareVersion error:illegal params.");  
	    }  
	    String[] versionArray1 = version1.split("\\.");//ע��˴�Ϊ����ƥ�䣬������"."��  
	    String[] versionArray2 = version2.split("\\.");  
	    int idx = 0;  
	    int minLength = Math.min(versionArray1.length, versionArray2.length);//ȡ��С����ֵ  
	    int diff = 0;  
	    while (idx < minLength  
	            && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//�ȱȽϳ���  
	            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//�ٱȽ��ַ�  
	        ++idx;  
	    }  
	    //����Ѿ��ֳ���С����ֱ�ӷ��أ����δ�ֳ���С�����ٱȽ�λ�������Ӱ汾��Ϊ��  
	    diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;  
	    return diff;  
	}  


	private static void setOutOfLoopCondition() {
		
		boolean isDisplayed = false;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] > 0) {
					ArrayList<String> loopConditionsI = new ArrayList<>();
					ArrayList<String> loopConditionsJ = new ArrayList<>();
					// �����Ϣi������loop����
					for (int c = 0; c < table.get(i).size(); c++) {
						if (table.get(i).get(c).getFragType().equals("loop")) {
							loopConditionsI.add(table.get(i).get(c).getFragCondition());
						}
					}
					// �����Ϣj������loop����
					for (int c = 0; c < table.get(j).size(); c++) {
						if (table.get(j).get(c).getFragType().equals("loop")) {
							loopConditionsJ.add(table.get(j).get(c).getFragCondition());
						}
					}
					// ���I���� J��û�е�loop ˵��I��J��������Щloop
					loopConditionsI.removeAll(loopConditionsJ);
					if (breakIgnoreLoopCondition[i][j] != null) {// ���i��j����Ϊbreak������ ���������loop��condition
						loopConditionsI.remove(breakIgnoreLoopCondition[i][j]);
					}
					if (loopConditionsI.size() == 0) {
						continue;
					}
					String allConditionsOutOfLoop = "";
					for (int k = 0; k < loopConditionsI.size(); k++) {
						if (k == 0) {
							allConditionsOutOfLoop = becomeFalseCondition(loopConditionsI.get(k));
						} else {
							allConditionsOutOfLoop += "--" + becomeFalseCondition(loopConditionsI.get(k));
						}
					}
					outOfLoopCondition[i][j] = allConditionsOutOfLoop;
					Display.println("��Ϣ" + i + "ֱ�ӵ���Ϣ" + j + "������loop,���������Ϊ��");
					Display.println(allConditionsOutOfLoop + "\n");
					isDisplayed = true;
				}
			}
		}
		if (!isDisplayed) {
			Display.println("----> δ�ҵ��������Ƭ�ε����\n");
		}
	}



	private static void fixF1ForFalseCondition() {
		boolean isDisplayed  = false;
		falseConditions = new String[table.size()][table.size()];
		
		
		int IjumpToK[][] = new int[table.size()][table.size()];
		// ���һ����Ծ���� 
		for(int i = 0; i < jumpCondition.length; i++) {
			for (int j = 0; j < jumpCondition[i].length; j++) {
				if (jumpCondition[i][j] != null) {
					IjumpToK[i][j] = 1; // �����Ѿ�����Ծ���� ���Զ����Ծ
				}
			}
		}
		
		
		for(int i=0;i<F1.size();i++)//�����Ծ ����
		{
			for(int j=i+1; j<F1.size();j++) {
				if(F1.get(i).contains(j)) {//���i�ܵ���j
					F1.get(i).addAll(F1.get(j)); // i�ܵ�  ����j����Ծ��λ��  i->j->k
					
					for(int k : F1.get(j)) {
						if (j == k || map[i][j] == -1) {
							continue;
						}
						if (IjumpToK[i][k] != 1) {
							
							// ����I����J������ Ȼ��Ҳ����J����K������ �����I��K������
							String jumpToJCondition = jumpCondition[i][j];
							String andJumpToKCondition = jumpCondition[j][k];
							if (andJumpToKCondition == null) {
								jumpCondition[i][k] = jumpToJCondition;
							} else {
								jumpCondition[i][k] = jumpToJCondition + "--" + andJumpToKCondition;
							}
							if (jumpCondition[i][k] != null) {
								Display.println("ִ�е�" + i + "����Ϣ���������" + j +"����Ϣ�������" + k + "����Ϣ��������Ϊ��");
								Display.println(jumpCondition[i][k] + "\n");
								isDisplayed = true;
							}
							
							IjumpToK[i][k] = 1;
						}
						
					}
				}
			}
		}
		int i = 0;
		for(HashSet<Integer> Gi :G) {// ��i����Ϣ �ܵ���j
			for (int gotoJ : Gi) {//��������j ֱ������Ծ��k
				for (int jumptoK : F1.get(gotoJ)) {//��������j ֱ������Ծ��k
					String conditionInit = "";
					
					//i �� k ��Ҫ�����Ծ����
					if (gotoJ == jumptoK) { // û����Ծ����
						continue;
					} else { // ������Ծi->j->k
						String andJumpToKCondition = jumpCondition[gotoJ][jumptoK];						
						falseConditions[i][jumptoK] = andJumpToKCondition;

					}
				}
			}
			i++;
		}
		if (!isDisplayed) {
			Display.println("----> û����Ҫȡ�����������\n");
		}
	}

	private static String removeDuplicateCondition(String conditions) {
//		if (!conditions.contains("--")) {
//			return conditions;
//		}
//		conditions = conditions.replaceAll(" ", "");
//		HashSet<String> allConditionsSet = new HashSet<>();
//		
//		String[] strings = conditions.split("--");
//		for (int i = 0; i < strings.length; i++) {
//			allConditionsSet.add(strings[i]);
//		}
//		
//		StringBuilder sb = new StringBuilder();
//		boolean first = true;
//		for(String string : allConditionsSet) {
//			if (first) {
//				sb.append(string);
//				first = false;
//			} else {
//				sb.append("&&");
//				sb.append(string);
//			}
//		}
//			
//		
//		return sb.toString();
		return conditions;
	}

	//������Ϣ �ǲ������breakƬ�ε����һ����Ϣ
	private static boolean islastbreak(int i, int c) {
		if(table.get(i).get(c).getFragType().equals("break")&&
				!table.get(i+1).get(c).getFragId().equals(table.get(i).get(c).getFragId()))//�����һ��break
			return true;
		else
			return false;
	}
	private static void fixF() {
		
		for(int i=0;i<F.size();i++)
		{
			for(int j=i+1; j<F.size();j++)
			if(F.get(i).contains(j))
				F.get(i).addAll(F.get(j));
		}
	}
	public static int findRightLoopEnd(int i)//ֻ��alt�Ϳ����� ����par���ӵ�ʱ�ж������ʱaltEndTo[i]=0 ȡi����
	{
		if(altEndTo[i] == 0 )
			return i;
		
		while(altEndTo[i]!=0)
		{
			//i������ͬʱ��alt��par�Ľ��Ӵ�
				i=altEndTo[i];			
		}
		
		return i;
	}
	//�ҵ���ȷ�ĳ����Ƭ��λ��
	public static int findRightI(int i)//����alt�Ľ��Ӵ� ����par�Ľ��Ӵ����Ҽ���par�Ľ��Ӵ�
	{
		if(altEndTo[i] == 0 && parEndTo[i] == 0)
			return i;
		
		while(altEndTo[i]!=0 || parEndTo[i]!=0)
		{
			if(altEndTo[i]!=0)//i������ͬʱ��alt��par�Ľ��Ӵ�
				i=altEndTo[i];
			else if(parEndTo[i]!=0)
			{	
				exAdd.add(i);//par�Ľ��Ӵ�Ҳ��Ҫ�������
				i=parEndTo[i];
			}
		}
		
		return i;
	}
	//�õ�Gi���� iһ����ĳ�����Ƭ�������һ����Ϣ
	private static HashSet<Integer> findOutTo(int i, int c, String fragType) {//����G��i��
		HashSet<Integer> rt = new HashSet<Integer>();
		exAdd = new ArrayList <Integer>(); //par�Ľ��Ӵ�Ҳ��Ҫ�������
		int I = i; // ����ԭʼ��I
		if(fragType.equals ("opt") || fragType.equals ("loop") )//ֻ��һ��
		{
			//eqwe
			i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			int righti = findRightI(i);
			rt.add(righti);//����alt���ӵ� ������
//			// ���ó�loop������
//			setOutOfLoopCondition(I, c, righti, fragType.equals ("loop"));
		} else if(fragType.equals ("break"))
		{
			c--;
			if(c == 1) //��������break
			{	
				rt.add(table.size()-1);//������̬
				return rt;
			} 
			boolean breakOfLoop = false;
			String breakCondtion = new String();
			if(table.get(i).get(c).getFragType().equals("loop")) {
				breakOfLoop = true;
				breakCondtion = table.get(i).get(c).getFragCondition();
			}
			if(!table.get(i).get(c).getFragType().equals("alt") && !table.get(i).get(c).getFragType().equals("par"))
				i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			else
				i=findOutOfAlt(i, c);
			
			
			int righti = findRightI(i);
			rt.add(righti);//����alt���ӵ� ������
			if (breakOfLoop) {
				breakIgnoreLoopCondition[I][righti] = breakCondtion;
			}
			// ��� goCondition   A && b && c
		}else if(fragType.equals ("alt") || fragType.equals ("par"))
		{
			i=findOutOfAlt(i,c);//�ҵ������Ƭ�ε�i
			int righti = findRightI(i);
			rt.add(righti);//����alt���ӵ� ������
			// ��� goCondition   A && b && c
		}
		
		rt.addAll(exAdd);//par�Ľ��Ӵ�Ҳ��Ҫ�������
		return rt;
	}
	//�õ�Fi һ����Ծ���� ��Ϣiһ����ĳ�����Ƭ�ε�һ����Ϣ
	private static HashSet<Integer> findAlsoTo(int i, int c, String fragType) {//����1����ԾF��i��
		HashSet<Integer> rt = new HashSet<Integer>();
		//exAdd = new ArrayList <Integer>(); //par�Ľ��Ӵ�����Ҫ��� �����ｫ��par��������������
		
		if(fragType.equals ("opt") || fragType.equals ("loop") || fragType.equals ("break"))//ֻ��һ��
		{
			int I = i;
			i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
			if(fragType.equals ("loop"))//��¼loop��λ��
			{
				WJLoopFragment temp = new WJLoopFragment();
				temp.setCondition(table.get(I).get(c).getFragCondition());
				temp.setStart(I);
				temp.setEnd(i);
				temp.setC(c);
				loopFragment.add(temp);
			}
			
			int rightI = findRightI(i);
			//���falseCondition
			setJumpOnceContidion(I, rightI, c);
			rt.add(rightI);//����alt���ӵ� ������
		}
		else if(fragType.equals( "alt" )|| fragType.equals("par"))//�ж��
		{
			String comid = table.get(i).get(c).getComId();
			int I = i;
			while(table.get(i).get(c).getComId().equals( comid))
			{
				i=findOutOfFrag(i,c);//�ҵ������Ƭ�ε�i
				
				if(table.get(i).get(c).getComId().equals(comid))//����alt�ڲ� i��ʱ�ǽ��ӵ����һ��
					rt.add(i);
			}
			
			if(fragType.equals ("par") && !table.get(I-1).get(c).getComId().equals(comid))//��¼par��λ��
			{//��par�����ǵ�һ����Ϣ
				WJParFragment temp = new WJParFragment();
				temp.setStart(I);
				temp.setEnd(i);
				temp.setC(c);
				parFragment.push(temp);
			}
			/*rt.add(findRightI(i));//����alt��ô�����ȷ��i*///���� ��Ϊalt��par��һ�������� ������������alt�Ĳ����򵽴�alt����
			String aString = new String();
		}
		
		return rt;
	}

	// ����һ����Ծ������  I���ϸ����Ƭ�ε��׸���Ϣ rightI���¸����Ƭ�ε��׸���Ϣ
	private static void setJumpOnceContidion(int I, int rightI, int C) {
		ArrayList<String> intoIConditions = new ArrayList<>();
		String intoICondition = "";
		// �õ�C���Լ�֮ǰ������
//		for(int k = 2; k <= C; k++) {
//			intoIConditions.add(table.get(I).get(k).getFragCondition());
//		}
//		// �������н���I������ ���˵�ǰ�����Ƭ�� ���ս����  !(intoICondition)  �Ż�������rightI
//		for(String condition: intoIConditions) {
//			if (intoICondition.equals("")) {
//				intoICondition = condition;
//			} else {
//				intoICondition += "&&" + condition; 
//			}
//		}
//		// �ƺ� ���� ��¼���в������
//		if (jumpCondition[I][rightI] == null) { 
//			if (intoICondition.equals("")) {
//				jumpCondition[I][rightI] = null;
//			} else {//������I�� ������Ծ��rightI
//				jumpCondition[I][rightI] = becomeFalseCondition( + intoICondition + ")";
//			}
//		}
		// ֻ��¼��������Ծ����
		String thisCondition = table.get(I).get(C).getFragCondition();
		if (jumpCondition[I][rightI] == null) { // ֻ��¼��������Ծ����
			if (intoICondition.equals("")) {
				jumpCondition[I][rightI] = becomeFalseCondition(thisCondition);
			} else {
				jumpCondition[I][rightI] = intoICondition + "--" + becomeFalseCondition(thisCondition);
			}
		}
		

	}
	private static String becomeFalseCondition(String thisCondition) {
		if (thisCondition.contains("#")) {
			int indexOfJin = 0;
			for (int i = 0; i < thisCondition.length(); i++) {
				if (thisCondition.charAt(i) == '#') {
					indexOfJin = i;
					break;
				}
			}
//			Display.println("!(" + thisCondition.substring(0, indexOfJin) + ")"
//					+ thisCondition.substring(indexOfJin));
			return "!(" + thisCondition.substring(0, indexOfJin) + ")"
					+ thisCondition.substring(indexOfJin);
		} else {
			return "!(" + thisCondition + ")";
		}
		
	}



	private static boolean hasLastBreak(int i) {
		
		for(int a=2;a<table.get(i).size();a++)//i���Ƿ�������һ��break
			if(table.get(i).get(a).getFragType().equals ("break") && !table.get(i+1).get(a).getFragId().equals (table.get(i).get(a).getFragId()) )
				return true;
		
		return false;
	}
	private static int findStartOfFrag(int i, int c) {
		String id = table.get(i).get(c).getFragId();
		
		while(table.get(i).get(c).getFragId().equals(id) )
			i--;
		
		return i+1;
	}
	private static int findStartOfAlt(int i, int c) {
		String id = table.get(i).get(c).getComId();
		
		while(table.get(i).get(c).getComId().equals(id) )
			i--;
		
		return i+1;
	}
	
	private static int findOutOfFrag(int i, int c) {//�ҵ�����alt��par��ȥ��λ��
		
		String id = table.get(i).get(c).getFragId();
		
		while(table.get(i).get(c).getFragId().equals(id) )
			i++;
		
		return i;
	}
	private static int findOutOfAlt(int i, int c) {//�ҵ�(i,c)alt�ĳ�ȥ��λ�� ������par
		
		String comid = table.get(i).get(c).getComId();
		
		while(table.get(i).get(c).getComId().equals(comid))
			i++;
		
		return i;
	}

	
		
	public static boolean hasBreak(ArrayList <WJFragment> tableI)//messageI����break
	{
		for(int i =0;i<tableI.size();i++)
		{
			if(tableI.get(i).getFragType().equals("break"))
				return true;
		}
		
			return false;
	}
	public static boolean hasAlt(ArrayList <WJFragment> tableI)//messageI����alt
	{
		for(int i =0;i<tableI.size();i++)
		{
			if(tableI.get(i).getFragType().equals("alt"))
				return true;
		}
		
			return false;
	}
	public static boolean hasPar(ArrayList <WJFragment> tableI)//messageI����par
	{
		for(int i =0;i<tableI.size();i++)
		{
			if(tableI.get(i).getFragType().equals("par"))
				return true;
		}
		
			return false;
	}
	public static ArrayList <WJFragment> setTableI(WJFragment fragment)
	{
		ArrayList <WJFragment> temp = new ArrayList <WJFragment>();
		Stack <WJFragment> s = new Stack <WJFragment>();
		while(!fragment.getFragType().equals("SD"))
		{
			s.push(fragment);
			fragment = id_fragment.get(fragment.getBigId());
		}
		s.push(fragment);
		WJFragment none = new WJFragment();
    	none.setBigId("meiyou");
    	none.setFragId("none");
    	none.setFragType("none");
    	s.push(none);
		//��ջ ����arraylist
		while(!s.isEmpty())
		{
			temp.add(s.pop());
		}
		return temp;
	}
	public static UppaalLocation setLocation(String id, String name)
	{
		UppaalLocation location = new UppaalLocation();
			location.setId(id);
		    
		    location.setName(name);
		    
		return location;
	}
	
	public static UppaalTransition setTransition(WJMessage messageI, 
			int message_id, 
			String message_name,
			String receiveOrSend,
			String typeAndCondition,
			String sourceId, 
			String source_name,
			String targetId, 
			String target_name, 
			String T1,String T2 
			)
	{
		UppaalTransition transition = new UppaalTransition();
		transition.setReceiveOrSend(receiveOrSend);
		transition.setTypeAndCondition(typeAndCondition);
		transition.setId(message_id);
		transition.setNameS(source_name);
		transition.setNameT(target_name);
		transition.setSourceId(sourceId);
		transition.setTargetId(targetId);
		transition.setNameText(message_name);
		transition.setT1(T1);
		transition.setT2(T2);
		transition.setDCBM(messageI.DCBM);
		transition.setSEQDC(messageI.SEQDC);
		transition.setSEQDO(messageI.SEQDO);
		transition.setSEQTC(messageI.SEQTC);
		transition.setSEQTO(messageI.SEQTO);
		transition.setInString(messageI.inString);
		transition.setOutString(messageI.outString);
		transition.setTypeId(messageI.getTypeId());
		transition.use = messageI.use;
		transition.def = messageI.def;
		transition.RESET = messageI.RESET;
		return transition;
	}
	
	
	public static void setMap( HashSet <Integer> a,HashSet <Integer> b)
	{//����a��b
		
		Iterator<Integer> ii =a.iterator();
		Iterator<Integer> jj =b.iterator();
		while(ii.hasNext())
		{
			int i = (int)ii.next();
			
			jj =b.iterator();
			while(jj.hasNext())
			{
				int j = (int)jj.next();
				if(map[i][j]!=-1)
				{										
    	    		map[i][j] = j;
				}
			}
		}
		return ;
	}
	public static String getTypeAndnCondition(WJMessage messageI)
	{
		String nCondition = "";
		String type = "";
		String typeId = "";
		String id = messageI.getInFragId();
		boolean isInSameOpt = false;
		WJFragment fragment;
		while(!id.equals("null"))//�������������н���  
		{
			fragment = id_fragment.get(id);
			
			//����
			nCondition =   fragment.getFragCondition()+"--"+nCondition; 
			// ���Ƭ������
			type = fragment.getFragType()+"-"+type;
			// ���Ƭ�ε�id ��alt��parΪcomID��
			if (fragment.getFragType().equals("alt") || fragment.getFragType().equals("par")) {
				if (typeId .equals("")) {
					typeId = fragment.getComId();
				} else {
					typeId = fragment.getComId()+"-"+typeId;
				}
				
			} else {
				if (typeId .equals("")) {
					typeId = fragment.getFragId();
				} else {
					typeId = fragment.getFragId()+"-"+typeId;
				}
			}
			
			id=fragment.getBigId();
		}
		if (typeId.equals("")) {
			messageI.setTypeId("null");
		} else{
			messageI.setTypeId(typeId);
		}
		
		
		if(type.equals("")) {
			messageI.setConditions("null");
			return "null";
		} else {
			messageI.setConditions("["+type.substring(0,type.length()-1)+"]"+"/"+nCondition.substring(0,nCondition.length()-2));
			return "["+type.substring(0,type.length()-1)+"]"+"/"+nCondition.substring(0,nCondition.length()-2);
		}
	}
}
