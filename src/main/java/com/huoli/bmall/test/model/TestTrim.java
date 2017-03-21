package com.huoli.bmall.test.model;

import java.util.HashMap;
import java.util.Map;

public class TestTrim {

	public static void main(String[] args) {
		String s = "ecms : TaskManage : multiDelJob";
		System.out.println(s.toLowerCase().replace(" ", ""));
		Map<String,String> m = new HashMap<String,String>();
		System.out.println(m.get("123")!=null);
		System.out.println("dll : "+System.getProperty("java.library.path"));
	}

}
