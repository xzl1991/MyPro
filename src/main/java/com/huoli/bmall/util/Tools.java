package com.huoli.bmall.util;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.points.model.UserAddress;

public class Tools
{
	
	
	public static final SimpleDateFormat curDate = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str)
	{
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str)
	{
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9)
		{
			m = p1.matcher(str);
			b = m.matches();
		} else
		{
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	public static void checkAddress(UserAddress address) throws MyException
	{
		if (address.getProvince() == null || address.getProvince().length() < 2)
			throw new MyException("-401",-401);
		if (address.getCity() == null || address.getCity().length() < 2)
			throw new MyException("-402",-402);
//		if (address.getArea() == null || address.getArea().length() < 2)
//			throw new MyException("-403",-403);
		if (address.getPphone() == null
				|| (!isMobile(address.getPphone()) && !isPhone(address
						.getPphone())))
			throw new MyException("-404",-404);
		if (address.getName() == null || address.getName().length() < 2)
			throw new MyException("-405",-405);
		if (address.getAddress() == null || address.getAddress().length() < 5)
			throw new MyException("-406",-406);
	}
	
	/**
	 * 将javabean实体类转为map类型，然后返回一个map类型的值
	 * @param obj
	 * @return map
	 */
    public static Map<String, Object> beanToMap(Object obj) { 
            Map<String, Object> params = new HashMap<String, Object>(0); 
            try { 
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
                PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
                for (int i = 0; i < descriptors.length; i++) { 
                    String name = descriptors[i].getName(); 
                    if (!"class".equals(name)) { 
                    	if(null!=propertyUtilsBean.getNestedProperty(obj, name))
                        params.put(name, propertyUtilsBean.getNestedProperty(obj, name)); 
                    } 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
            return params; 
    }
    /**
	 * 把字符串的中间值置为*，如183****2885
	 * @param str
	 * @return str
	 */
    public static String getHideStr(String str){
    	int length=str.length();
    	int starLength=(length+1)/3;
    	int begin=(length-starLength)/2;
    	int endIndex=begin+starLength;
    	
    	StringBuffer buffer=new StringBuffer();
    	buffer.append(str.substring(0, begin));
    	for(int i=0;i<starLength;i++){
    		buffer.append("*");
    	}
    	buffer.append(str.substring(endIndex));
    	return buffer.toString();
    }
    
    public static String filterAttackStr(String str ){
    	if(StringUtils.isNotBlank(str)){
    		String rtn=str.replace("\\", "\\\\").replace("'", "‘");
    		return filterEmoji(rtn);
    	}else{
    		return "";
    	}
    }
    
    /**通过淘宝的rest服务获取ip相关的地址信息
     * @return :如{"code":0,"data":{"area":"华北","area_id":"100000","city":"北京市","city_id":"110100","country":"中国","country_id":"CN","county":"","county_id":"-1","ip":"111.200.243.114","isp":"联通","isp_id":"100026","region":"北京市","region_id":"110000"}}
     * 其中code的值的含义为，0：成功，1：失败。
     * 接口说明：http://ip.taobao.com/instructions.php
     */
    public static JSONObject getIpInfo(String ip){
    	String taoBaoIpRestUrl="http://ip.taobao.com/service/getIpInfo.php?ip=";
    	String infoStr=HttpTookit.sendGetRequest(taoBaoIpRestUrl+ip);
		JSONObject rtnJson=JSONObject.parseObject(infoStr);
		return rtnJson;
    }
    
    public static Cookie getCookie(String name, HttpServletRequest request) {

        if (request == null || StringUtils.isEmpty(name)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        for (Cookie cookie : cookies) {
            if (name.equalsIgnoreCase(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
    
    public static String getCookieValue(String name, HttpServletRequest request) {

        Cookie cookie = getCookie(name,request);
        if (cookie != null) {
        	String cookieValue=cookie.getValue();
        	if(StringUtils.isNotBlank(cookieValue)&&!cookieValue.equals("null")&&!cookieValue.equals("undefined"))
               return cookie.getValue();
        }
        return null;
    }
    
    public static String getCookieByHeader(String name, HttpServletRequest request) {

    	try {
    		String cookieStr=request.getHeader("Cookie");
        	String nameStr=cookieStr.substring(cookieStr.indexOf(name+"="));
      	    String arr[]=nameStr.split(";");
            return arr[0];
		} catch (Exception e) {
			return "";
		}
    }
    
    public static String filterEmoji(String source) { 
        if(source != null)
        {
            Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
            Matcher emojiMatcher = emoji.matcher(source);
            if ( emojiMatcher.find())
            {
                source = emojiMatcher.replaceAll("*");
                return source ;
            }
        return source;
       }
       return source; 
    }
    public static int[] descSort(int arr[]) {
		for (int i = 0; i < arr.length - 1; i++) {
			boolean swapFlag = true;
			for (int j = arr.length - 1; j > i; j--) {
				if (arr[j - 1] < arr[j]) {
					int temp = arr[j - 1];
					arr[j - 1] = arr[j];
					arr[j] = temp;

					swapFlag = false;
				}
			}
			if (swapFlag)
				break;
		}
		return arr;
	}
}

