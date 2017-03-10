/**
 * 项目名称 : derive
 * 创建用户 : lanlei
 * 文档名称 : Base64.java
 * 文档位置 : com.huoli.util
 * 最后变更 : lanlei
 * 变更日期 : Sep 3, 2009
 * 当前版本 : Revision: 1 
 * 
 * Copyright (c) 2007 Wh-Mj Engineering Corp, Inc. All Rights Reserved.
 *
 */
package com.huoli.bmall.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 作用说明： 创建用户：lanlei 生成日期：Sep 3, 2009
 * 
 */
public class Base64
{

	private Base64()
	{
		super();
	}

	/**
	 * Encode some data and return a String.
	 */
	public final static String encode(byte[] d)
	{
		if (d == null)
			return null;
		byte data[] = new byte[d.length + 2];
		System.arraycopy(d, 0, data, 0, d.length);
		byte dest[] = new byte[(data.length / 3) * 4];

		// 3-byte to 4-byte conversion
		for (int sidx = 0, didx = 0; sidx < d.length; sidx += 3, didx += 4)
		{
			dest[didx] = (byte) ((data[sidx] >>> 2) & 077);
			dest[didx + 1] = (byte) ((data[sidx + 1] >>> 4) & 017 | (data[sidx] << 4) & 077);
			dest[didx + 2] = (byte) ((data[sidx + 2] >>> 6) & 003 | (data[sidx + 1] << 2) & 077);
			dest[didx + 3] = (byte) (data[sidx + 2] & 077);
		}

		// 0-63 to ascii printable conversion
		for (int idx = 0; idx < dest.length; idx++)
		{
			if (dest[idx] < 26)
				dest[idx] = (byte) (dest[idx] + 'A');
			else if (dest[idx] < 52)
				dest[idx] = (byte) (dest[idx] + 'a' - 26);
			else if (dest[idx] < 62)
				dest[idx] = (byte) (dest[idx] + '0' - 52);
			else if (dest[idx] < 63)
				dest[idx] = (byte) '+';
			else
				dest[idx] = (byte) '/';
		}

		// add padding
		for (int idx = dest.length - 1; idx > (d.length * 4) / 3; idx--)
		{
			dest[idx] = (byte) '=';
		}
		return new String(dest);
	}

	/**
	 * Encode a String using Base64 using the default platform encoding
	 */
	public final static String encode(String s)
	{
		return encode(s.getBytes());
	}

	/**
	 * Decode data and return bytes.
	 */
	public final static byte[] decode(String str)
	{
		if (str == null)
			return null;
		byte data[] = str.getBytes();
		return decode(data);
	}

	/**
	 * Decode data and return bytes. Assumes that the data passed in is ASCII
	 * text.
	 */
	public final static byte[] decode(byte[] data)
	{
		int tail = data.length;
		while (data[tail - 1] == '=')
		{
			tail--;
		}
		byte dest[] = new byte[tail - data.length / 4];

		// ascii printable to 0-63 conversion
		for (int idx = 0; idx < data.length; idx++)
		{
			if (data[idx] == '=')
				data[idx] = 0;
			else if (data[idx] == '/')
				data[idx] = 63;
			else if (data[idx] == '+')
				data[idx] = 62;
			else if (data[idx] >= '0' && data[idx] <= '9')
				data[idx] = (byte) (data[idx] - ('0' - 52));
			else if (data[idx] >= 'a' && data[idx] <= 'z')
				data[idx] = (byte) (data[idx] - ('a' - 26));
			else if (data[idx] >= 'A' && data[idx] <= 'Z')
				data[idx] = (byte) (data[idx] - 'A');
		}

		// 4-byte to 3-byte conversion
		int sidx, didx;
		for (sidx = 0, didx = 0; didx < dest.length - 2; sidx += 4, didx += 3)
		{
			dest[didx] = (byte) (((data[sidx] << 2) & 255) | ((data[sidx + 1] >>> 4) & 3));
			dest[didx + 1] = (byte) (((data[sidx + 1] << 4) & 255) | ((data[sidx + 2] >>> 2) & 017));
			dest[didx + 2] = (byte) (((data[sidx + 2] << 6) & 255) | (data[sidx + 3] & 077));
		}
		if (didx < dest.length)
		{
			dest[didx] = (byte) (((data[sidx] << 2) & 255) | ((data[sidx + 1] >>> 4) & 3));
		}
		if (++didx < dest.length)
		{
			dest[didx] = (byte) (((data[sidx + 1] << 4) & 255) | ((data[sidx + 2] >>> 2) & 017));
		}
		return dest;
	}

	public static String getBASE64(String s)
	{
		if (s == null)
			return null;
		return (new BASE64Encoder()).encode(s.getBytes());
	}

	public static String getFromBASE64(String s)
	{
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * A simple test that encodes and decodes the first commandline argument.
	 */
	public static final void main(String[] args)
	{
		String e = Base64.encode("该数据已被系统自动过滤".getBytes());
		System.out.println("==" + e);
		String d = new String(Base64.decode(e));
		System.out.println("==" + d);
		// if (args.length != 1)
		// {
		// System.out.println("Usage: Base64 string");
		// System.exit(0);
		// }
		// try
		// {
		// String e = Base64.encode(args[0].getBytes());
		// String d = new String(Base64.decode(e));
		// System.out.println("Input   = '" + args[0] + "'");
		// System.out.println("Encoded = '" + e + "'");
		// System.out.println("Decoded = '" + d + "'");
		// }
		// catch (Exception x)
		// {
		// x.printStackTrace();
		// }
	}
}
