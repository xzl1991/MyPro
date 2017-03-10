package com.huoli.bmall.util;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.model.jsonresp.Input;
import com.huoli.bmall.points.model.Product;
import com.huoli.bmall.points.model.UserInfo;
import com.huoli.bmall.points.model.ZOrder;

/**
 * @author WangYing
 * 
 *         根据模板获得输出显示结果
 */
public class UniversalUtil {
	private static Logger log = Logger.getLogger(UniversalUtil.class);

	public static void main(String[] args) throws MyException {
		String str = "gtgj`sign.str;aes.netease-baoxian$`13141078133";
		UserInfo user = new UserInfo();
		ZOrder order = new ZOrder();
		JSONObject input = new JSONObject();
		Product product = new Product();
		List<Input> list = new ArrayList<Input>();
		Input in = new Input();
		in.setCode("phone");
		list.add(in);
		product.setParams(list);
		String result = getResultString(str, user, order, input, input,
				product, System.currentTimeMillis() + "");
		System.out.println(result);
	}

	public static String getJsonString(String field, JSONObject values) {
		try {
			String[] key = field.split("[.]");
			for (int i = 0; i < key.length - 1; i++) {
				if (values.getString(key[i]).startsWith("[")) {
					values = values.getJSONArray(key[i]).getJSONObject(0);
				} else
					values = values.getJSONObject(key[i]);

			}
			return values.getString(key[key.length - 1]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @author WangYing
	 * 
	 *         替换指定字符串，获取结果
	 * @param template
	 * @param user
	 * @param order
	 * @param input
	 * @param product
	 * @param timestamp
	 * @return
	 * @throws MyException
	 */
	public static String getResultString(String template, UserInfo user,
			ZOrder order, JSONObject input, JSONObject param, Product product,
			String timestamp) throws MyException {
		try {
			// log.info("template的值为："+template);
			if (template.contains("##")) {
				String[] steps = template.split(";;");
				Map<String, String> temp = new HashMap<String, String>();
				for (String step : steps) {
					String str = step.split("##")[0];
					str = convert(str, user, order, input, param, product,
							timestamp, temp);
					temp.put(step.split("##")[1], str);
				}
				return temp.get("sign");
			} else {
				return convert(template, user, order, input, param, product,
						timestamp, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("get result string error : " + template);
			String detail = "get result string error : " + template;
			throw new MyException(detail, -2);
		}
		/*
		 * List<Integer> charIndex = new ArrayList<Integer>(); String result =
		 * template; for(int i = 0 ; i < template.length() ; i ++){ String temp
		 * = template.charAt(i)+""; if(StringUtils.equals("`", temp)){
		 * charIndex.add(i); } } for(int j = 0 ; j < charIndex.size() ; j += 2){
		 * String temp = template.substring(charIndex.get(j)+1,
		 * charIndex.get(j+1)); String[] split = temp.split(";"); String value =
		 * getValue(split, user, order, input, product); result =
		 * result.replace("`" + temp + "`", value); }
		 */
		/*
		 * try{ int a = 0; int b = -1; StringBuffer result = new StringBuffer();
		 * // System.out.println("templet:" + template); while (b <
		 * template.length()-1 && (a=template.indexOf("`",b+1)) >=0) {
		 * result.append(template.substring(b+1,a)); b =
		 * template.indexOf("`",a+1); String temp = template.substring(a+1, b);
		 * String[] split = temp.split(";"); String value = getValue(split,
		 * user, order, input, product, timestamp); result.append(value); } if
		 * (b+1 < template.length()) result.append(template.substring(b+1)); //
		 * System.out.println("convert:" + result.toString()); return template;
		 * } catch(Exception e){ log.error(e);
		 * log.error("get result string error : " + template); throw new
		 * MyException(-2); }
		 */
	}

	private static String convert(String template, UserInfo user, ZOrder order,
			JSONObject input, JSONObject param, Product product,
			String timestamp, Map<String, String> temp) throws MyException {
		try {
			int a = 0;
			int b = -1;
			StringBuffer result = new StringBuffer();
			// System.out.println("templet:" + template);
			while (b < template.length() - 1
					&& (a = template.indexOf("`", b + 1)) >= 0) {
				result.append(template.substring(b + 1, a));
				b = template.indexOf("`", a + 1);
				String tem = template.substring(a + 1, b);
				String[] split = tem.split(";");
				String value = getValue(split, user, order, input, param,
						product, timestamp, temp);
				result.append(value);
			}
			if (b + 1 < template.length())
				result.append(template.substring(b + 1));
			// System.out.println("convert:" + result.toString());
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("convert string error : " + template);
			String detail = "convert string error : " + template;
			throw new MyException(detail, -2);
		}
	}

	/**
	 * @author WangYing
	 * 
	 *         获取最终处理后的value值
	 * @param split
	 * @param user
	 * @param order
	 * @param input
	 * @param product
	 * @param timestamp
	 * @param temp
	 * @return
	 * @throws MyException
	 */
	private static String getValue(String[] split, UserInfo user, ZOrder order,
			JSONObject input, JSONObject param, Product product,
			String timestamp, Map<String, String> temp) throws MyException {
		try {
			String valueparam = split[0];
			String value = getRealValues(valueparam, user, order, input, param,
					product, temp);
			String resultvalue = value;
			if (split.length > 1) {
				for (int i = 1; i < split.length; i++) {
					String method[] = split[i].split("[.]");
					if (StringUtils.equalsIgnoreCase("urlencode", method[0])) {
						resultvalue = urlEncodeValue(resultvalue, method[1]);
					} else if (StringUtils.equalsIgnoreCase("aes", method[0])) {
						if (method.length == 2)
							resultvalue = aesSecurity(resultvalue, method[1],
									null);
						else
							resultvalue = aesSecurity(resultvalue, method[1],
									method[2]);
					} else if (method[0].startsWith("DES")) {
						resultvalue = desSecurity(resultvalue, method[1],
								method[0], method[2]);
					} else if (StringUtils.equalsIgnoreCase("md5", method[0])) {
						resultvalue = md5Security(resultvalue, method[1]);
					} else if (StringUtils
							.equalsIgnoreCase("base64", method[0])) {
						resultvalue = base64Enocde(resultvalue, method[1]);
					} else if (StringUtils.equalsIgnoreCase("upper", method[0])) {
						resultvalue = resultvalue.toUpperCase();
					} else if (StringUtils.equalsIgnoreCase("lower", method[0])) {
						resultvalue = resultvalue.toLowerCase();
					} else if (StringUtils.equalsIgnoreCase("sha1", method[0])) {
						resultvalue = sha1Security(resultvalue);
					} else if (StringUtils.equalsIgnoreCase("timestamp",
							method[0])) {
						resultvalue = timestamp;
					}
				}
			}
			return resultvalue == null ? "" : resultvalue;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			String detail = "通过模板获取value时出错 : " + split[0];
			throw new MyException(detail, -2);
		}
	}

	public static String sha1Security(String resultvalue) {
		SHA1Util sha = new SHA1Util();
		String authkey = sha.getDigestOfString(resultvalue.getBytes());
		return authkey;
	}

	/**
	 * @author WangYing
	 * 
	 *         对字符串进行md5加密
	 * @param value
	 * @param string
	 * @return
	 */
	public static String md5Security(String value, String method) {
		return MD5.crypt(value);
	}

	/**
	 * @author WangYing
	 * 
	 *         对字符串进行des加密
	 * @param value
	 * @param string
	 * @return
	 * @throws MyException
	 */
	public static String desSecurity(String value, String method,
			String desMethod, String code) throws MyException {
		try {
			DesSecurityUtil des = new DesSecurityUtil(method, desMethod);
			if (StringUtils.equalsIgnoreCase("base64", code)) {
				return des.encryptBase64(value);
			} else {
				return des.encrypt(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("UniversalUtil:des encrypt error;value=" + value);
			String detail = "UniversalUtil:des encrypt error;vlue=" + value;
			throw new MyException(detail, -2);
		}
	}

	/**
	 * @author WangYing
	 * 
	 *         对字符串进行aes加密
	 * @param value
	 * @param method2
	 * @param string
	 * @return
	 * @throws MyException
	 */
	public static String aesSecurity(String value, String method, String vector)
			throws MyException {
		try {
			return AesUtil.encrypt(method, value, vector);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("UniversalUtil:aes encrypt error; value=" + value);
			String detail = "UniversalUtil:aes encrypt error;vlue=" + value;
			throw new MyException(detail, -2);
		}
	}

	/**
	 * @author WangYing
	 * 
	 *         对字符串进行urlEncode编码
	 * @param value
	 * @return
	 * @throws MyException
	 */
	public static String urlEncodeValue(String value, String method)
			throws MyException {
		try {
			String key = method;
			if (StringUtils.isEmpty(key)) {
				key = "UTF-8";
			}
			return URLEncoder.encode(value, key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("UniversalUtil:urlEncoder error; value=" + value);
			String detail = "UniversalUtil:urlEncoder error;vlue=" + value;
			throw new MyException(detail, -2);
		}
	}

	/**
	 * @author WangYing
	 * 
	 *         对字符串进行base64编码
	 * @param signstr
	 * @param thirdpartCode
	 * @return
	 */
	public static String base64Enocde(String signstr, String thirdpartCode) {
		return new BASE64Encoder().encode(signstr.getBytes());
	}

	public static String getRealValues(String valueparam, UserInfo user,
			ZOrder order, JSONObject input, JSONObject param, Product product,
			Map<String, String> temp) throws MyException {
		int idx = valueparam.indexOf("[");
		if (idx >0)
		{
			int idx1 = valueparam.indexOf("]", idx+1);
			String arr = valueparam.substring(0,idx);
			String format = valueparam.substring(idx+1, idx1);
			String arrString = getRealValue(arr, user, order, input, param, product, temp);
			JSONArray array = JSONArray.parseArray(arrString);
			return getArrayValue(array, format);
		}
				
		return getRealValue(valueparam, user,order, input, param, product, temp);
	}

	private static String getArrayValue(JSONArray array, String format) {
		StringBuffer ret = new StringBuffer();
		String[] formats = format.split("~");
		for(int i = 0; i < array.size(); i ++)
		{
			JSONObject item = array.getJSONObject(i);
			for(int j = 0; j < formats.length; j ++)
			{
				if (i == array.size()-1 && j == formats.length-1)
					continue;
				if (formats[j].startsWith("^"))
					ret.append(formats[j].substring(1));
				else {
					ret.append(getJsonString(formats[j], item));
				}
			}
		}
		return ret.toString();
	}

	/**
	 * @author WangYing
	 * 
	 *         获取实际参数的值
	 * @param valueparam
	 * @param user
	 * @param order
	 * @param input
	 * @param product
	 * @param temp
	 * @return
	 * @throws MyException
	 */
	private static String getRealValue(String valueparam, UserInfo user,
			ZOrder order, JSONObject input, JSONObject param, Product product,
			Map<String, String> temp) throws MyException {
		try {
			if (!valueparam.contains(".")) {
				return valueparam;
			}
			String[] split = valueparam.split("[.]");
			String classes = split[0];
			String field = split[1];
			if (StringUtils.equals("input", classes)) {
				String re = "";
				re = input.getString(field);
				// System.out.println("get "+ field + " from input :"+re);
				if (split.length > 2 && StringUtils.isNotEmpty(re)) {
					if (StringUtils.equals(split[2], "birth")) {
						return IdCardUtils.getBirthday(re);
					} else if (StringUtils.equals(split[2], "sex")) {
						return IdCardUtils.getSex(re) + "";
					}
					String key = valueparam.substring(6);
					re = getJsonString(key, input);
				}
				return re == null ? "" : re;
			} else if (StringUtils.equals("param", classes)) {
				String re = "";
				re = param.getString(field);
				// System.out.println("get "+ field + " from input :"+re);
				if (split.length > 2 && StringUtils.isNotEmpty(re)) {
					if (StringUtils.equals(split[2], "birth")) {
						return IdCardUtils.getBirthday(re);
					} else if (StringUtils.equals(split[2], "sex")) {
						return IdCardUtils.getSex(re) + "";
					}
					String key = valueparam.substring(6);
					re = getJsonString(key, param);
				}
				return re == null ? "" : re;
			} else if (StringUtils.equals("user", classes)) {
				return getJavaBeanValue(user, field);
			} else if (StringUtils.equals("order", classes)) {
				return getJavaBeanValue(order, field);
			} else if (StringUtils.equals("product", classes)) {
				return getJavaBeanValue(product, field);
			} else if (StringUtils.equals("temp", classes)) {
				String re = temp.get(field);
				return re == null ? "" : re;
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("get real value error :" + valueparam);
			String detail = "get real value error :" + valueparam;
			throw new MyException(detail, -2);
		}
		/*
		 * List<Input> params = product.getParams(); for(Input in : params){
		 * if(StringUtils.equals(in.getCode(), field)){ if(split.length > 2){
		 * String value = input.getString(in.getCode());
		 * if(StringUtils.equals(split[1], "birth")){ return
		 * IdCardUtils.getBirthday(value); }else if(StringUtils.equals(split[1],
		 * "sex")){ return IdCardUtils.getSex(value)+""; } } return
		 * input.getString(in.getCode()) == null ? "" :
		 * input.getString(in.getCode()); } } }else{ try { Object javaBean =
		 * Class.forName(classes).newInstance(); Method[] methods =
		 * user.getClass().getDeclaredMethods(); for(Method method : methods){
		 * if(method.getName().startsWith("get")){ String methodname =
		 * method.getName(); methodname =
		 * methodname.substring(methodname.indexOf("get")+3);
		 * if(StringUtils.equalsIgnoreCase(methodname, field)){ return
		 * method.invoke(javaBean, (Object[]) null).toString(); } } } } catch
		 * (Exception e) { return field; } }
		 */
	}

	/**
	 * @author WangYing
	 * 
	 *         通过类名.方法名获取参数值
	 * @param javaBean
	 * @param field
	 * @return
	 * @throws MyException
	 */
	private static String getJavaBeanValue(Object javaBean, String field)
			throws MyException {
		try {
			Method[] methods = javaBean.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					String methodname = method.getName();
					methodname = methodname
							.substring(methodname.indexOf("get") + 3);
					if (StringUtils.equalsIgnoreCase(methodname, field)) {
						return method.invoke(javaBean, (Object[]) null)
								.toString();
					}
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.error("获取参数值出错" + javaBean.getClass().getName() + ":" + field);
			String detail = "获取参数值出错" + javaBean.getClass().getName() + ":"
					+ field;
			throw new MyException(detail, -2);
		}
	}

}
