package com.neurospeech.wsclient;

import java.io.StringWriter;
import java.util.Date;
import java.util.Vector;
import org.w3c.dom.*;



public class WSHelper {

	
	static String getString(Document doc)
	{
		StringWriter sw = new StringWriter();
		
		Element e = doc.getDocumentElement();
		
		
		
		Vector<String> nsList = new Vector<String>();
		
		writeElement(sw,e,nsList);
		
		return sw.toString();
	}
	
	private static void writeElement(StringWriter sw, Element e, Vector<String> nsList)
	{
		int count;
		int i;
		
		sw.write("<" + e.getNodeName());
		if(e.hasAttributes())
		{
			NamedNodeMap map = e.getAttributes();
			count = map.getLength();
			for(i=0; i<count; i++)
			{
				Node n = map.item(i);
				String name = n.getNodeName();
				String value = n.getNodeValue();
				sw.write(" " + name + "=\"" + getEncoded(value) + "\"");
				if(name.equalsIgnoreCase("xmlns") || name.startsWith("xmlns:")){
					nsList.add(value);
				}
				
			}
		}
		
		//if(8>Float.valueOf(android.os.Build.VERSION.SDK))
		{
			String ns = e.getNamespaceURI();
			if(ns != null && !nsList.contains(ns))
			{
				nsList.add(ns);
				String prefix = e.getPrefix();
				if(prefix==null)
					prefix = "";
				else
					prefix = ":" + prefix;
					
				sw.write(" xmlns" + prefix + "=\"" + ns + "\"");
			}
		}
		
		if(e.hasChildNodes())
		{
			sw.write(">");
			NodeList list = e.getChildNodes();
			count = list.getLength();
			for(i=0;i<count;i++)
			{
				Vector<String> childNSList = new Vector<String>(nsList);
				
				Node n = list.item(i);
				if(n instanceof Text)
				{
					sw.write(getEncoded(n.getNodeValue()));
				}
				else
				{
					writeElement(sw, (Element)n,childNSList);
				}
			}
			sw.write("</" + e.getNodeName() + ">");
		}
		else
		{
			sw.write("/>");
		}
	}
	
	private static String getEncoded(String text)
	{
		if(text.length()==0)
			return text;
		return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	public static void addChild(Element root, String name, String value, boolean attribute)
	{
		if(value==null)
			return;
		if(attribute){
			root.setAttribute(name, value);
		}
		else
		{
			Element e = root.getOwnerDocument().createElement(name);
			Text txt = root.getOwnerDocument().createTextNode(value);
			e.appendChild(txt);
			root.appendChild(e);
		}
	}
	
	public static void addChildNodeNS(Element root, String nsURI, String name, WSObject obj){
		if(obj==null)
			return;
		Element node = root.getOwnerDocument().createElementNS(nsURI, name);
		obj.fillXML(node);
		root.appendChild(node);
	}
	
	
	public static void addChildNode(Element root, String name, Element child, WSObject obj){
		Element node = root.getOwnerDocument().createElement(name);
		if(child!=null){
			node.appendChild(child);
		}else{
			if(obj==null)
				return;
			obj.fillXML(node);
		}
		root.appendChild(node);
	}
	
	public static void addChildArray(Element root, String targetNamespace, String name, String type, @SuppressWarnings("rawtypes") Vector array){
		if(array==null || array.size()==0)
			return;
		Document d = root.getOwnerDocument();
		
		Element arrayElement;
		
		XmlDOMHelper.registerNamespace(d, targetNamespace);
		
		//if(targetNamespace==null || targetNamespace.length()==0)
			arrayElement = d.createElement(name);
		//else
		//	arrayElement = d.createElementNS(targetNamespace, name);
		
		root.appendChild(arrayElement);
		int i;
		if(type==null){
			for(i=0;i<array.size();i++){
				WSObject st = (WSObject)array.elementAt(i);
				arrayElement.appendChild(st.toXMLElement(arrayElement));
			}
		}else{
			for(i=0;i<array.size();i++){
				Element e;
				e = XmlDOMHelper.createNode(d, targetNamespace, type);
				Object obj = array.elementAt(i);
				String val = obj.toString();
				if(obj.getClass().equals(Date.class)){
					val = WSHelper.stringValueOf((Date)obj);
				}
				Text txt = d.createTextNode(val);
				e.appendChild(txt);
				arrayElement.appendChild(e);
			}
		}
	}
			
	public static void addChildArrayInline(Element root, String targetNamespace, String name, String type, @SuppressWarnings("rawtypes") Vector array){
		if(array==null || array.size()==0)
			return;
		int i;
		Document d = root.getOwnerDocument();
		Element arrayElement = root;
		if(type==null){
			for(i=0;i<array.size();i++){
				WSObject st = (WSObject)array.elementAt(i);
				Element e;
				e = XmlDOMHelper.createNode(d, targetNamespace, name);
				st.fillXML(e);
				arrayElement.appendChild(e);
			}
		}else{
			for(i=0;i<array.size();i++){
				Element e;
				e = XmlDOMHelper.createNode(d, targetNamespace, name);
				Object obj = array.elementAt(i);
				String val = obj.toString();
				if(obj.getClass().equals(Date.class)){
					val = WSHelper.stringValueOf((Date)obj);
				}
				Text txt = d.createTextNode(val);
				e.appendChild(txt);
				arrayElement.appendChild(e);
			}
		}
	}
	
	public static NodeList getChildren(Element root, String name){
		NodeList childList = root.getChildNodes();
		WSNodeList list = new WSNodeList(); 
		int n = childList.getLength();
		for(int i=0;i<n;i++){
			Node node = childList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(name==null)
					list.addElement(node);
				else if(node.getLocalName().equals(name))
					list.addElement(node);
			}
		}
		return list;
	}
	
	public static NodeList getElementChildren(Element root, String name){
		Element e = getElement(root, name);
		if(e==null)
			return null;
		return getChildren(e,null);
	}
	

	public static Element getElement(Element root, String name)
	{
		if(name==null)
			return null;
		NodeList list = getChildren(root,name);
		if(list.getLength()>0)
			return (Element)list.item(0);
		return null;
	}
	
	public static Element getElementNS(Element root, String nsURI, String name)
	{
		if(name==null)
			return null;
		NodeList list = root.getElementsByTagNameNS(nsURI,name);
		if(list.getLength()>0)
			return (Element)list.item(0);
		return null;
	}
	
	/*public static Iterable<Element> getIterator(NodeList list)
	{
		return new NodeIterable(list);
	}*/
	

	/*private static String getAttributeString(Element root, String name)
	{
		if(name==null)
			return null;
		return root.getAttribute(name);
	}*/
	
	public static String getString(Element root, String name, boolean isAttribute)
	{
		if(isAttribute)
		{
			return root.getAttribute(name);	
		}
		if(name==null)
		{
			Text txt = (Text)root.getFirstChild();
			if(txt==null)
				return null;
			return txt.getNodeValue();
		}
		Element child = WSHelper.getElement(root, name);
		if(child==null)
			return null;
		return getString(child, null, isAttribute);
	}
	
	
	public static boolean getBoolean(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return false;
		if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes"))
			return true;
		return false;
	}
	
	public static Date getDate(Element root, String name, boolean isAttribute) throws Exception
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return null;
		return WSDateParser.parse(val);
	}

	public static long getLong(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return 0;
		return Long.parseLong(val);
	}
	
	public static double getDouble(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return 0;
		return Double.parseDouble(val);
	}
	
	public static int getInteger(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return 0;
		return Integer.parseInt(val);
	}
	
	public static float getFloat(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return 0;
		return Float.parseFloat(val);
	}
	
	public static byte[] getByte(Element root, String name, boolean isAttribute)
	{
		String val = WSHelper.getString(root, name, isAttribute);
		if(val==null)
			return new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		return val.getBytes();
	}
	
	
	public static String stringValueOf(Date date){
		return WSDateParser.toString(date);
	}
	
}
