package com.pxene.dmp.common;

public class StringUtils {

	public static String null2Empty(String str) {
		return str == null ? "" : str.trim();
	}
	
	public static boolean isGarbled(String str) {
		return !str.matches("[ 0-9a-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>/?]*");
	}
	
}
