package net.wwwfred.framework.util.code;

import net.wwwfred.framework.util.date.DatetimeUtil;

/**
 * @author liq
 * @created 2014-12-18
 */
public class NumberingRuleUtil {
	
	/**
	 * 生成订单编号
	 * @param codeHeader 团购：T；商城：M；机票：A；鲜花：F；酒店：H
	 * @return
	 */
	public static String newOrdercode(String codeHeader) {
		String[] code=new String[]{"A","B","C","D","E","F","G","H","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","1","2","3","4","5","6","7","8","9","0"};
		String rtn=codeHeader+code[DatetimeUtil.getYear()-2013]+code[DatetimeUtil.getMonth()]+code[DatetimeUtil.getDay()]+code[DatetimeUtil.getHour()]+DatetimeUtil.getMinAndSec()+
				getRandomStr(4);
		return rtn;
	}
	
    /**
     * 生成自定义编号
     * @param codePrefix 前缀
     * @param codeCenter 中间码
     * @param codeSuffix 后缀
     * @param ifNeedTime 是否需要时间
     * @param randomLength 随机数位数
     * @return 前缀+日期时间+中间码+随机数+后缀
     */
    public static String newBaseCode(String codePrefix,boolean ifNeedTime,String codeCenter,int randomLength,String codeSuffix) {
        String[] code=new String[]{"A","B","C","D","E","F","G","H","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","1","2","3","4","5","6","7","8","9","0"};
        String datetimeString = "";
        if(ifNeedTime)
        {
            datetimeString = DatetimeUtil.getYear()+code[DatetimeUtil.getMonth()]+code[DatetimeUtil.getDay()]+code[DatetimeUtil.getHour()]+DatetimeUtil.getMinAndSec();
        }
        String randomString = getRandomStr(randomLength);
        return codePrefix + datetimeString + ((codeCenter==null||"".equals(codeCenter.trim()))?"":codeCenter) + randomString + ((codeSuffix==null||"".equals(codeSuffix.trim()))?"":codeSuffix);
    }
    
	/**
	 * 生成随机数 并且以字符串形式返回
	 * @param i 需要的数字长度
	 * @return
	 */
	private static String getRandomStr(int i) 
	{
		String randomStr = "";
		while(randomStr.length() < i)
		{
			randomStr += getRandomNumber();
		}
		return randomStr;
	}
    
    public static String getRandomNumber()
    {
        return new Double(Math.random()*10d).intValue()+"";
    }

}
