package com.pxene.dmp.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public static String getCurrentTime(String format) {
		DateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentTime("YYYY-MM-dd HH:mm:ss.SSSSSS"));
	}
}
