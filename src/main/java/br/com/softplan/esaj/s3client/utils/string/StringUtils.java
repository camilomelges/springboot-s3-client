package br.com.softplan.esaj.s3client.utils.string;

public abstract class StringUtils {

	public static Boolean isEmptyOrBlank(final String str) {
		return str.isBlank() || str.isEmpty();
	}

	public static String trim(final String str) {
		return str.trim();
	}
}
