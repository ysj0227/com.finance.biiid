package com.finance.commonlib.utils;

public class NumberHelper {
	
	public static double toDouble(Object obj) {
		return toDouble(obj, 0d);
	}

	public static double toDouble(Object obj, double defaultValue) {
		if (obj == null) return defaultValue;
		if (obj instanceof Double) return (Double)obj;
		String s = obj.toString();
		if ("".equals(s)) return defaultValue;
		s = s.replace(",", "");
		try {
			return Double.parseDouble(s);
		} catch (RuntimeException ex) {
			return defaultValue;
		}
	}
	
	public static int toInt(Object obj) {
		return toInt(obj, 0);
	}
	
	public static int toInt(Object obj, int defaultValue) {
		if (obj == null) return defaultValue;
		if (obj instanceof Integer) return (Integer)obj;
		if (obj instanceof Long) return (int)(long)(Long)obj;
		if (obj instanceof Double) return (int)(double)(Double)obj;
		String s = obj.toString();
		if ("".equals(s)) return defaultValue;
		s = s.replace(",", "");
		try {
			return (int) Double.parseDouble(s);
		} catch (RuntimeException ex) {
			return defaultValue;
		}
	}
	
	public static long toLong(Object obj) {
		return toLong(obj, 0L);
	}
	
	public static long toLong(Object obj, long defaultValue) {
		if (obj == null) return defaultValue;
		if (obj instanceof Integer) return (Integer)obj;
		if (obj instanceof Long) return (Long)obj;
		if (obj instanceof Double) return (long)(double)(Double)obj;
		String s = obj.toString();
		if ("".equals(s)) return defaultValue;
		s = s.replace(",", "");
		try {
			return (long) Double.parseDouble(s);
		} catch (RuntimeException ex) {
			return defaultValue;
		}
	}
}
