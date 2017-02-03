package net.wwwfred.framework.util.xml;

import java.io.InputStream;
import java.nio.charset.Charset;

public interface XmlParse
{
	<T> T toObject(InputStream in, Class<T> clazz);
	
	Charset getXmlEncoding(byte[] byteArray);
}
