package net.wwwfred.framework.util.code;

/**
 * EmptyChecker
 * @author wangwwy
 * createdDatetime 2014年8月28日 下午1:45:19
 */
public interface EmptyChecker {
	
	/**
	 * 对象是否为null,数组，集合，Map长度是否为0
	 * @author wangwwy
	 * createdDatetime 2014年8月28日 下午1:46:28
	 * @param obj
	 * @return
	 */
	boolean isEmpty(Object obj);
}
