package br.com.softplan.esaj.s3client.domain.i18n.enums;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import br.com.softplan.esaj.s3client.utils.string.StringUtils;

public enum I18nLocale {
	es_CO("es_CO"), pt_BR("pt_BR");

	private static final String DEFAULT_ERROR = "i18n.defaultError";

	private final String locale;

	I18nLocale(String locale) {
		this.locale = locale;
	}

	public String getProperty(final String propertyKey) {
		final String resourceName = "i18n/".concat(locale).concat(".properties");
		final File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile());

		try (final InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			final Properties properties = new Properties();
			properties.load(inputStreamReader);

			final String value = properties.getProperty(propertyKey);
			return StringUtils.isEmptyOrBlank(value) ? properties.getProperty(DEFAULT_ERROR) : value;
		} catch (IOException e) {
			return pt_BR.getProperty(propertyKey);
		}
	}
}
