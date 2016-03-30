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

//WADL解析器，读取WADL文件并提取里面的有用（并非所有）信息，这些信息将成为方法构造器（MethodBuilder）构建方法的依据。方法是REST服务匹配单位。
public class WADLParser {
	public Map<String, String> parse(String WADLFile) throws Exception  
    {  
        Map<String, String> result = new HashMap<String, String>();//输出结果
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        DocumentBuilder db = dbf.newDocumentBuilder();  
          
        Document doc = db.parse(new File(WADLFile));  
        //获得根元素节点（WADL里为application）  
        Element root = doc.getDocumentElement();  
        //开始解析资源节点
        NodeList resourceNodes = root.getChildNodes().item(1).getChildNodes();
        
        int resourceCount = 0;
        for(int i = 1; i < resourceNodes.getLength(); i = i + 2)//每次加2的原因是XML每节点之间 都有一个无用的”#text”结点
        {
        	Node resourceNode = resourceNodes.item(i);
        	//资源节点属性
        	NamedNodeMap resourceAttr = resourceNode.getAttributes();
        	resourceCount ++; 
        	
        	String resourceKey = "r" + resourceCount;
        	result.put(resourceKey + "name", resourceNode.getNodeName());
            if(resourceAttr!=null)
            {
            	for(int j = 0; j < resourceAttr.getLength(); j++)  
                {  
                    //获得该元素的每一个属性  
                    Attr attr = (Attr)resourceAttr.item(j);  
                    
                    if(attr.getName().equals("path"))//只处理path属性
                    {
                    	result.put(resourceKey + attr.getName(), attr.getValue());
                    	//System.out.println(attr.getValue());
                    }
                }  
            }
            //resource下可能是param（template风格）也可能是method节点
            NodeList paramAndMethodNodes = resourceNode.getChildNodes();
            
            int methodCount = 0;
            int templateParamCount = 0;
            
            for (int j = 1; j < paramAndMethodNodes.getLength(); j = j + 2)
            {
            	Node paramOrMethodNode = paramAndMethodNodes.item(j);
            	if(paramOrMethodNode.getNodeName().equals("method"))//解析method节点
            	{
            	NamedNodeMap methodAttr = paramOrMethodNode.getAttributes();
            	methodCount++;
            	
            	String methodKey = resourceKey + "m" + methodCount;
            	if(methodAttr!=null)
            	{
            		for (int k = 0; k < methodAttr.getLength(); k++)
            		{
            			Attr attr = (Attr)methodAttr.item(k);
            			
            			if(attr.getName().equals("name"))//method节点只有name属性
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
            	//NodeList paramNodes = paramOrMethodNode.getChildNodes().item(1).getChildNodes();//参数列表在method下两级
            	int paramCount = 0;

            	
            	for (int k = 1; k < paramNodes.getLength(); k = k + 2)//解析参数节点
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
            				
            				switch(attr.getName())//name和type属性用于参数匹配，其它属性用于识别参数的特殊类型
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
            		NodeList optionValueNodes = paramNode.getChildNodes();//参数可选值集合，这里只用于特殊参数处理
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
            	
            	NodeList responseAndRequestNodes = paramOrMethodNode.getChildNodes();//method节点下一级可能是response或者request节点，request不含信息，不考虑        	
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
                			
                			if(attr.getName().equals("status"))//解析服务器返回状态码
                			{
                				result.put(responseKey + attr.getName(), attr.getValue());
                				//System.out.println(attr.getValue());
                			}
                		}
                	}
            		Node representationNode = responseOrRequestNode.getChildNodes().item(1);//representation节点是request或者response的子节点
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
            	else if(paramOrMethodNode.getNodeName().equals("param"))//解析template风格的参数
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
