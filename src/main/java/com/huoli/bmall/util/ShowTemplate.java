package com.huoli.bmall.util;

import java.util.HashMap;

public class ShowTemplate
{
	private static HashMap<String, String> template;

	public static void InitTemplate()
	{
		template = new HashMap<String, String>();
		template.put(
				"smallecode",
				"<p class=\"order-exchange-text\">`input.name`：</p><p class=\"order-exchange-text number-color\">`input.value`</p>");
		template.put(
				"midecode",
				"<p class=\"order-exchange-text\">`input.name`：</p><p class=\"order-exchange-text big-font number-color\">`input.value`</p>");
		template.put(
				"bigcode",
				"<p class=\"order-exchange-text\">`input.name`：</p><p class=\"order-exchange-text bigger-font number-color\">`input.value`</p>");
	}

}
