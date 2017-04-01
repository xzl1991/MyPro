package com.test.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TestDate {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		System.out.println(new Date());
//		System.out.println(new Date("20161001"));
	SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
	Date date = sdf.parse("20171001");
	Date date1 = sdf.parse("20171001");
//	 int a = 	org.apache.commons.lang.time.DateUtils.truncatedCompareTo(date,date1, Calendar.DATE);
//	 System.out.println(a);
	}

}
