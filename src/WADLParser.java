import java.io.File;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
//import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//WADL����������ȡWADL�ļ�����ȡ��������ã��������У���Ϣ����Щ��Ϣ����Ϊ������������MethodBuilder���������������ݡ�������REST����ƥ�䵥λ��
public class WADLParser {
	public Map<String, String> parse(String WADLFile) throws Exception  
    {  
        Map<String, String> result = new HashMap<String, String>();//������
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        DocumentBuilder db = dbf.newDocumentBuilder();  
          
        Document doc = db.parse(new File(WADLFile));  
        //��ø�Ԫ�ؽڵ㣨WADL��Ϊapplication��  
        Element root = doc.getDocumentElement();  
        //��ʼ������Դ�ڵ�
        NodeList resourceNodes = root.getChildNodes().item(1).getChildNodes();
        
        int resourceCount = 0;
        for(int i = 1; i < resourceNodes.getLength(); i = i + 2)//ÿ�μ�2��ԭ����XMLÿ�ڵ�֮�� ����һ�����õġ�#text�����
        {
        	Node resourceNode = resourceNodes.item(i);
        	//��Դ�ڵ�����
        	NamedNodeMap resourceAttr = resourceNode.getAttributes();
        	resourceCount ++; 
        	
        	String resourceKey = "r" + resourceCount;
        	result.put(resourceKey + "name", resourceNode.getNodeName());
            if(resourceAttr!=null)
            {
            	for(int j = 0; j < resourceAttr.getLength(); j++)  
                {  
                    //��ø�Ԫ�ص�ÿһ������  
                    Attr attr = (Attr)resourceAttr.item(j);  
                    
                    if(attr.getName().equals("path"))//ֻ����path����
                    {
                    	result.put(resourceKey + attr.getName(), attr.getValue());
                    	//System.out.println(attr.getValue());
                    }
                }  
            }
            //resource�¿�����param��template���Ҳ������method�ڵ�
            NodeList paramAndMethodNodes = resourceNode.getChildNodes();
            
            int methodCount = 0;
            int templateParamCount = 0;
            
            for (int j = 1; j < paramAndMethodNodes.getLength(); j = j + 2)
            {
            	Node paramOrMethodNode = paramAndMethodNodes.item(j);
            	if(paramOrMethodNode.getNodeName().equals("method"))//����method�ڵ�
            	{
            	NamedNodeMap methodAttr = paramOrMethodNode.getAttributes();
            	methodCount++;
            	
            	String methodKey = resourceKey + "m" + methodCount;
            	if(methodAttr!=null)
            	{
            		for (int k = 0; k < methodAttr.getLength(); k++)
            		{
            			Attr attr = (Attr)methodAttr.item(k);
            			
            			if(attr.getName().equals("name"))//method�ڵ�ֻ��name����
            			{
            				result.put(methodKey + attr.getName(), attr.getValue());
            				//System.out.println(attr.getValue());
            			}
            		}
            	}
            	
            	NodeList paramNodes;
            	NodeList paramOrRepresentationNodes = paramOrMethodNode.getChildNodes().item(1).getChildNodes();
            	if(paramOrRepresentationNodes.item(1).getNodeName().equals("param"))
            		paramNodes = paramOrRepresentationNodes;
            	else 
            		paramNodes = paramOrRepresentationNodes.item(1).getChildNodes();
            	//NodeList paramNodes = paramOrMethodNode.getChildNodes().item(1).getChildNodes();//�����б���method������
            	int paramCount = 0;

            	
            	for (int k = 1; k < paramNodes.getLength(); k = k + 2)//���������ڵ�
            	{
            		Node paramNode = paramNodes.item(k);

            		NamedNodeMap paramAttr = paramNode.getAttributes();
            		paramCount++;
            		
            		String paramKey = methodKey + "p" + paramCount;
            		if(paramAttr!=null)
            		{
            			for (int l = 0 ; l < paramAttr.getLength(); l++)
            			{
            				Attr attr = (Attr)paramAttr.item(l);
            				
            				switch(attr.getName())//name��type�������ڲ���ƥ�䣬������������ʶ���������������
            				{
            					case "name":
            					case "style":
            					case "type":
            					case "fixed":
            					case "isResource":
            					case "required":result.put(paramKey + attr.getName(), attr.getValue());break;
            					//case "fixed":System.out.println(paramKey + attr.getName() + attr.getValue());
            				}
            			}
            		}
            		NodeList optionValueNodes = paramNode.getChildNodes();//������ѡֵ���ϣ�����ֻ���������������
            		if(optionValueNodes!=null)
            		{
            			int optionValueCount = 0;
            			for(int l = 1; l < optionValueNodes.getLength(); l = l + 2)
            			{
            				Node optionValueNode = optionValueNodes.item(l);
            				if(optionValueNode.getNodeName().equals("option"))
            				{
            					
            				NamedNodeMap optionValueAttr = optionValueNode.getAttributes();
            				optionValueCount ++;
            				String optionValueKey = paramKey + "o" + optionValueCount;
            				Attr attr = (Attr)optionValueAttr.item(0);
            				if(attr.getName().equals("value"))
                			{
                				result.put(optionValueKey + attr.getName(), attr.getValue());
                				//System.out.println(attr.getValue());
                			}
            			}
            			}
            			result.put(paramKey + "optionValueCount", Integer.toString(optionValueCount));
            		}
            	}
            	
            	NodeList responseAndRequestNodes = paramOrMethodNode.getChildNodes();//method�ڵ���һ��������response����request�ڵ㣬request������Ϣ��������        	
            	int responseCount = 0;
            	for (int k = 1; k < responseAndRequestNodes.getLength(); k = k + 2)
            	{
            		Node responseOrRequestNode = responseAndRequestNodes.item(k);
            		if(responseOrRequestNode.getNodeName().equals("response")){
            		NamedNodeMap responseAttr = responseOrRequestNode.getAttributes();
            		responseCount++;
            		
            		String responseKey = methodKey + "r" + responseCount;
            		if(responseAttr!=null)
                	{
                		for (int l = 0; l < responseAttr.getLength(); l++)
                		{
                			Attr attr = (Attr)responseAttr.item(l);
                			
                			if(attr.getName().equals("status"))//��������������״̬��
                			{
                				result.put(responseKey + attr.getName(), attr.getValue());
                				//System.out.println(attr.getValue());
                			}
                		}
                	}
            		Node representationNode = responseOrRequestNode.getChildNodes().item(1);//representation�ڵ���request����response���ӽڵ�
                	NamedNodeMap representationAttr = representationNode.getAttributes();
                	if(representationAttr!=null)
                	{
                		for(int l = 0 ; l < representationAttr.getLength(); l++)
                		{
                			Attr attr = (Attr)representationAttr.item(l);
                			
                			if(attr.getName().equals("mediaType"))
                			{
                				result.put(responseKey + attr.getName(), attr.getValue());
                				//System.out.println(attr.getValue());
                			}
                		}
                	}
            	}
            	}
            	result.put(methodKey + "paramCount", Integer.toString(paramCount));
            	result.put(methodKey + "responseCount", Integer.toString(responseCount));
            	
            	
            	}
            	else if(paramOrMethodNode.getNodeName().equals("param"))//����template���Ĳ���
            	{
            		NamedNodeMap templateParamAttr = paramOrMethodNode.getAttributes();
            		templateParamCount++;
            		
            		String templateParamKey = resourceKey + "tp" + templateParamCount;
            		if(templateParamAttr!=null)
            		{
            			for (int k = 0 ; k < templateParamAttr.getLength(); k++)
            			{
            				Attr attr = (Attr)templateParamAttr.item(k);
            				
            				switch(attr.getName())
            				{
            					case "name":
            					case "style":
            					case "type":
            					case "required":result.put(templateParamKey + attr.getName(), attr.getValue());break;
            				}
            			}
            		}
            	}
            }
            
            result.put(resourceKey + "methodCount", Integer.toString(methodCount));
            
            result.put(resourceKey + "templateParamCount", Integer.toString(templateParamCount));
            
        }
        result.put("resourceCount", Integer.toString(resourceCount));
        return result;
    }
}
