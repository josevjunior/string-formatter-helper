package io.github.josevjunior;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {
	
	private static final Pattern FORMAT_SYMBOLS_PATTERN = Pattern.compile("%[dsf]|%\\d+(\\.\\d+)?[bBhH]?[diuoxXeEfgGncps]");
	
	public static Matcher matcher(CharSequence text) {
		return FORMAT_SYMBOLS_PATTERN.matcher(text);
	}
	
	public static boolean matches(CharSequence text) {
		return matcher(text).find();
	}
	
}
