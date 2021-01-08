package com.neurospeech.wsclient;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class XmlDOMHelper {
	
	public static void registerNamespace(Document d, String targetNamespace){
		
		if(targetNamespace == null || targetNamespace.length()==0)
			return;
		
		Element e = d.getDocumentElement();
		int i = 0;
		NamedNodeMap map = e.getAttributes();
		
		String prefix = "ns" + String.valueOf(map.getLength());
		
		int n = map.getLength();
		for(i=0;i<n;i++){
			/*Node node = map.item(i);
			if(node.getNodeType()!=Node.ATTRIBUTE_NODE)
				continue;*/
			Attr attr = (Attr)map.item(i);
			String nsUri = attr.getNamespaceURI();
			if(nsUri==null)
				continue;
			if(nsUri.equals("http://www.w3.org/2000/xmlns/")){
				if(targetNamespace.equals(attr.getValue()))
					return;
			}
		}
		
		Attr atr = d.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix);
		atr.setValue(targetNamespace);
		
		e.setAttributeNode(atr);
	}
	
	public static Element createNode(Document d, String nameSpace, String name)
	{
		if(nameSpace == null || nameSpace.length()==0)
			return d.createElement(name);
		Element e = d.createElementNS(nameSpace, name);
		
		// get prefix...
		NamedNodeMap map = d.getDocumentElement().getAttributes();
		int i = 0;
		int n = map.getLength();
		for(i=0;i<n;i++){
			Attr attr = (Attr)map.item(i);
			String nsUri = attr.getNamespaceURI();
			if(nsUri==null)
				continue;
			if(nsUri.equals("http://www.w3.org/2000/xmlns/") && attr.getValue().equals(nameSpace)){
				// found prefix...
				e.setPrefix(attr.getLocalName());
			}
		}
		
		return e;
	}
}
