package br.com.rafamilo.springboots3client.utils.string;

public abstract class StringUtils {

	public static Boolean isEmptyOrBlank(final String str) {
		return str == null || str.isBlank() || str.isEmpty();
	}

	public static String trim(final String str) {
		return str.trim();
	}
}
