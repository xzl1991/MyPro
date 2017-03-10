package com.huoli.bmall.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.StringUtils;

import sun.misc.BASE64Encoder;

public class AesUtil
{
	public static String encrypt(String strKey, String strIn, String vector)
			throws Exception
	{
		SecretKeySpec skeySpec = getKey(strKey);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		if (StringUtils.isEmpty(vector))
		{
			vector = "0102030405060708";
		}
		IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(strIn.getBytes());

		return new BASE64Encoder().encode(encrypted);
	}

	private static SecretKeySpec getKey(String strKey) throws Exception
	{
		byte[] arrBTmp = strKey.getBytes();
		byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++)
		{
			arrB[i] = arrBTmp[i];
		}

		SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");

		return skeySpec;
	}
}
