/**
 * 
 */
package com.huoli.bmall.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ClassName: MD5 <br>
 * Function : 提供MD5加密方法 <br>
 * Version : 1.0 <br>
 * Since : 2005-10-13 <br>
 * 
 */
public class MD5
{
	/**
	 * 
	 * @param string
	 *            to encode
	 * @return Encoded String
	 */
	public static String crypt(String str)
	{
		if (str == null || str.length() == 0)
		{
			throw new IllegalArgumentException(
					"String to encript cannot be null or zero length");
		}

		StringBuffer hexString = new StringBuffer();
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] hash = md.digest();

			for (int i = 0; i < hash.length; i++)
			{
				if ((0xff & hash[i]) < 0x10)
				{
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else
				{
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}

		return hexString.toString();
	}

	public static byte[] getMD5(byte[] source)
	{
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte[] output = new byte[16];
			int len = md.digest(output, 0, output.length);
			if (len != output.length)
			{
				System.err.println("md5 error");
				return null;
			}
			return output;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getMd5String(byte[] source)
	{
		byte[] md5 = getMD5(source);
		return byte2hex(md5);
	}

	public static String byte2hex(byte[] b)
	{
		String stmp = "";
		StringBuilder builder = new StringBuilder();
		for (int n = 0; n < b.length; n++)
		{
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
			{
				builder.append('0');
			}
			builder.append(stmp);
		}
		return builder.toString();
	}

	public static void main(String[] args)
	{
		String normal = "tnw!@#$%";
		String key = crypt(normal);
		System.out.println(key);
	}
}
