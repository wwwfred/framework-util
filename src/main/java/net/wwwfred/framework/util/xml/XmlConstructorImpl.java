package net.wwwfred.framework.util.xml;

import java.nio.charset.Charset;

public class XmlConstructorImpl implements XmlConstructor{

	@Override
	public String toXmlString(Object obj, Charset charset) {
		return new XmlUtil.XmlDocument(null,charset.name(),null,null,XmlUtil.parseObject(obj,null)).toXmlString();
	}

}
