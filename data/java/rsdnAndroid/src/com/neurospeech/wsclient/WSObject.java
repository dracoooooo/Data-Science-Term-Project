package com.neurospeech.wsclient;

import org.w3c.dom.Element;

public abstract class WSObject {
	public abstract void fillXML(Element root);
	public abstract Element toXMLElement(Element root);
}
