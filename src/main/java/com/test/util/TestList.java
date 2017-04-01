package com.test.util;

import java.util.ArrayList;
import java.util.Collection;

public class TestList {

	public static void main(String[] args) {
		Collection urlPatterns1 = new ArrayList<String>(){{add("/*");add("123*");}};
		System.out.println(urlPatterns1.size());
		urlPatterns1 = new ArrayList<String>();
		urlPatterns1.add("123");
	}

}
