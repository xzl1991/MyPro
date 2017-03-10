package com.huoli.bmall.util;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 加密工具
 * 
 * @author wangming 2013-03-25
 */
public class EncryptUtil
{
	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	private static final String suffix = "&*^%@%$^#&";
	private static final String innerSuffix = "*(%GK(%%(!@$#";
	public static final String partnerSuffix = "&*$%##^#weLK!";

	private static final String INJ_STR = "null@‘@'@and@exec@insert@select@delete@update@count@*@%@chr@mid@master@truncate@char@declare@;@or@lock table@grant@drop@ascii@-@+@,";

	public static String getGjSid(String msg)
	{
		String md5 = md5(msg + suffix);
		return "" + md5.charAt(4) + md5.charAt(1) + md5.charAt(16)
				+ md5.charAt(9) + md5.charAt(19) + md5.charAt(30)
				+ md5.charAt(28) + md5.charAt(22);
	}
	
	public static String getGTRedSid(String msg)
	{
		String md5 = encodeToHex(msg + suffix, "UTF-8", "MD5");
		return "" + md5.charAt(4) + md5.charAt(1) + md5.charAt(16) + md5.charAt(9) + md5.charAt(19) + md5.charAt(30) + md5.charAt(28) + md5.charAt(22);
	
	}

	/**
	 * 获得内部请求的sid
	 * 
	 * @return 内部请求的sid
	 */
	public static String getInnerSid(String msg)
	{
		String md5 = md5(msg + innerSuffix);
		return "" + md5.charAt(3) + md5.charAt(6) + md5.charAt(5)
				+ md5.charAt(9) + md5.charAt(20) + md5.charAt(30)
				+ md5.charAt(25) + md5.charAt(22);
	}
	
	public static String getInterFaceSid(String str)
	{
		String key = "&*^%@%$^#&";
		String md5 = DigestUtils.md5Hex(str + key).toUpperCase();
		// 5,2,17,10,20,31,29,23 取值位数
		return md5.charAt(4) + "" + md5.charAt(1) + "" + md5.charAt(16) + ""
				+ md5.charAt(9) + "" + md5.charAt(19) + "" + md5.charAt(30)
				+ "" + md5.charAt(28) + "" + md5.charAt(22);
	}

	/**
	 * 获得合作方请求的sid
	 * 
	 * @return 合作方请求的sid
	 */
	public static String getPartnerSid(String msg)
	{
		String md5 = md5(msg + partnerSuffix);
		return "" + md5.charAt(1) + md5.charAt(30) + md5.charAt(27)
				+ md5.charAt(5) + md5.charAt(20) + md5.charAt(15)
				+ md5.charAt(17);
	}

	/**
	 * MD5加密
	 * 
	 * @param origin
	 *            待加密字串
	 * @return 加密后的字串
	 */
	public static String md5(String origin)
	{
		return encodeToHex(origin, "UTF-8", "MD5");
	}

	public static String sha1(String origin)
	{
		return encodeToHex(origin, "UTF-8", "SHA1");
	}

	public static String encodeToHex(String origin, String charsetname,
			String type)
	{
		String resultString = null;
		try
		{
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance(type);
			if (charsetname == null || "".equals(charsetname))
			{
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes("UTF-8")));
			} else
			{
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
			}
		} catch (Exception exception)
		{
			return null;
		}
		return resultString;
	}

	public static String encodeBase64(String s)
	{
		if (s == null)
			return null;
		try
		{
			return (new BASE64Encoder()).encode(s.getBytes("UTF-8"));
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String encodeBase64(byte[] bs)
	{
		if (bs == null)
			return null;
		return (new BASE64Encoder()).encode(bs);
	}

	public static String decodeBase64(String s)
	{
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(s);
			return new String(b, "UTF-8");
		} catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] decodeBase64Forb(String s)
	{
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			return decoder.decodeBuffer(s);
		} catch (Exception e)
		{
			return null;
		}
	}

	public static byte[] encodeAESForb(String content, String password)
	{
		try
		{
			Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, password);
			return cipher.doFinal(content.getBytes("UTF-8"));
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String encodeAES(String content, String password)
	{
		try
		{
			byte[] result = encodeAESForb(content, password);
			return parseByte2HexStr(result);
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String decodeAES(byte[] bs, String password)
	{
		try
		{
			Cipher cipher = initCipher(Cipher.DECRYPT_MODE, password);
			byte[] result = cipher.doFinal(bs);
			return new String(result, "UTF-8");
		} catch (Exception e)
		{
			return null;
		}
	}

	public static String encodeAESAndBase64(String content, String key)
	{
		byte[] bs1 = encodeAESForb(content, key);
		return encodeBase64(bs1);
	}

	/**
	 * 先解base64，再解AES
	 * 
	 * @param content
	 *            密文
	 * @param key
	 *            密钥
	 * @return 明文
	 */
	public static String decodeBase64AndAES(String content, String key)
	{
		byte[] bs = decodeBase64Forb(content);
		return decodeAES(bs, key);
	}

	private static Cipher initCipher(int mode, String password)
			throws Exception
	{
		// KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// kgen.init(128, new SecureRandom(password.getBytes()));
		// SecretKey secretKey = kgen.generateKey();
		// byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
		// 创建密码器
		Cipher cipher = Cipher.getInstance("AES");
		// 初始化
		cipher.init(mode, key);
		return cipher;
	}

	public static String encryptDES(String seed, String cleartext)
			throws Exception
	{
		byte[] rawKey = seed.getBytes("UTF-8");
		byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));
		return parseByte2HexStr(result);
	}

	public static String decryptDES(String seed, String encrypted)
			throws Exception
	{
		byte[] rawKey = seed.getBytes("UTF-8");
		byte[] enc = parseHexStr2Byte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result, "UTF-8");
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "DES");
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, zeroIv);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "DES");
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, zeroIv);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static void main(String[] args) throws Exception
	{
		// byte[] bs1 = encodeAESForb("zhangl@huoli.com/openet123",
		// "$@()^Yj&J>xeu?:N");
		// String str = encodeBase64(bs1);
		// System.out.println(str);
		// String destr = decodeBase64AndAES("u+uWr3q11W16FpBfadw+hw==",
		// GtConstant.GTGJ_ACCOUNT_PASSWD_KEY);
		// System.out.println(destr);
		// String destr = decryptDES("oiuy87FJ", "DA214042B95AE739");
//		System.out.println(getGTSid("gtgjtest1234"));
		// System.err.println(encodeAESAndBase64("12314",
		// GtConstant.GTGJ_ACCOUNT_PASSWD_KEY));
		// System.out.println(encodeAESAndBase64("RKi/Ck0UjIgXHeHemIEWOA==",
		// "aimee011819!@#$%"));

		// System.err.println(getGTSid("45PEKWUH2014-01-23"));
		// String p = "BCbaiduApp,android,4.4.2,gtgj,2.3,SM-N9009,0";
		// String ua = "370976876948288/B88A35118AC34FD6731C31EFA2355B1C";
		// String orderid = "E425602275";
		// String uid = "1e87219a9c0001a19";
		// String uuid = "4556925b-f658-487a-8e38-a71eca2bec94";
		// long gtgjtime = System.currentTimeMillis();
		// String url =
		// "http://jt.rsscc.com/trainnet/assistant/flyTicketCoupon.action?p=%s&uid=%s&uuid=%s&gtgjtime=%s&sid=%s&ua=%s&orderid=%s";
		// String sid = EncryptUtil.getGTSid(uid + uuid + gtgjtime);
		// url = String.format(url, p, uid, uuid, gtgjtime, sid, ua, orderid);
		// System.out.println(url);
	}

	private static String byteArrayToHexString(byte b[])
	{
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			int n = b[i];
			if (n < 0)
			{
				n += 256;
			}
			int d1 = n / 16;
			int d2 = n % 16;
			resultSb.append(hexDigits[d1] + hexDigits[d2]);
		}
		return resultSb.toString();
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++)
		{
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr)
	{
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++)
		{
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * sql关键字过滤
	 * 
	 * @param str
	 * @return
	 */
	public static String sqlInfusion(String str)
	{
		String inj_stra[] = INJ_STR.split("@");
		str = dealNull(str);
		str = str.toUpperCase();
		for (int i = 0; i < inj_stra.length; i++)
		{
			if (str.indexOf(inj_stra[i]) >= 0)
			{
				str = strReplace(str, inj_stra[i]);
			}
		}
		return str;
	}

	private static String strReplace(String str, String restr)
	{
		return str.replace(restr, "");
	}

	private static String dealNull(String str)
	{
		String returnstr = null;
		if (str == null)
			returnstr = "";
		else
			returnstr = str;
		return returnstr;
	}
	
	public static String signWithAESMd5(String data, String key, long signTime, String charset) {
		String resultHex = null;
		byte[] dataResult = null;
		if (data == null || key == null || charset == null || signTime == 0) {
			return resultHex;
		}
		try {
			byte[] dataBytes = data.getBytes(charset);
			byte[] keyBytes = Base64.decode(key);
			if (keyBytes == null || keyBytes.length == 0) {
				return resultHex;
			}

			byte[] timeBytes = String.valueOf(signTime).getBytes(charset);

			// 将数据使用KEY和AES算法加密得到加密数据A
			dataResult = encrypt(dataBytes, keyBytes,"AES");
			if (dataResult == null) {
				return resultHex;
			}

			// 将二进制加密数据A做MD5摘要得到数据B
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(dataResult);
			dataResult = md.digest();
			if (dataResult == null) {
				return resultHex;
			}

			// 将B和签名时间顺序做MD5摘要得到结果C
			md.reset();
			md.update(dataResult);// 数据B
			md.update(timeBytes);// 签名时间
			dataResult = md.digest();
			if (dataResult == null) {
				return resultHex;
			}

			// 将16字节的数据C转换为HEX格式字符串(32个字符)得到D
			resultHex = toHex(dataResult);
			if (resultHex != null && resultHex.length() == 32) {
				resultHex = resultHex.toUpperCase();
			} else {
				resultHex = null;
			}
		} catch (Exception e) {
			resultHex = null;
			e.printStackTrace();
		}
		return resultHex;
	}
	
	public static byte[] encrypt(byte[] data, byte[] password,String type) {
		if (password == null || password.length < 16) {
			return null;
		}
		try {
			SecretKeySpec key = new SecretKeySpec(password, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String toHex(byte input[]){
        if(input == null)
            return null;
        StringBuffer output = new StringBuffer(input.length * 2);
        for(int i = 0; i < input.length; i++){
            int current = input[i] & 0xff;
            if(current < 16)
                output.append("0");
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }
	
	public static String connectToString(Map<String, String> para) {
		StringBuffer sb =new StringBuffer();
		for (Entry<String, String> e : para.entrySet()) {
			sb.append(e.getKey());
			sb.append("=");
			sb.append(e.getValue());
			sb.append("&");
		}
		return sb.substring(0, sb.length() - 1).toString();
	}

}
