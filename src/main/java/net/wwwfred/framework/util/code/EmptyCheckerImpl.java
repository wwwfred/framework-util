package net.wwwfred.framework.util.code;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * EmptyCheckerImpl
 * @author wangwwy
 * createdDatetime 2014年8月28日 下午1:46:07
 */
public class EmptyCheckerImpl implements EmptyChecker{

	public boolean isEmpty(Object obj) {
		if(obj==null)
			return true;
		
		Class<?> clazz = obj.getClass();
		if(clazz.isArray())
		{
			return ((Object[])obj).length==0;
		}
		else if(obj instanceof Collection)
		{
			return ((Collection<?>)obj).size()==0;
		}
		else if(obj instanceof Map)
		{
			return ((Map<?,?>)obj).size()==0;
		}
		else if(obj instanceof String)
		{
			return "".equals(((String) obj).trim());
		}
		else if(obj instanceof Number)
        {
		    BigDecimal number = new BigDecimal(obj.toString());
		    return number.compareTo(new BigDecimal(0))==0;
//            return new Integer(0).equals(((Number)obj).intValue());
        }
		else
		{
			return false;
		}
	}

}
