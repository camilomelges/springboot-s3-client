package br.com.rafamilo.springboots3client.domain.i18n.enums;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import br.com.rafamilo.springboots3client.utils.string.StringUtils;

public enum I18nLocale {
	es_CO, pt_BR;

	private static final String DEFAULT_ERROR = "i18n.defaultError";

	public String getProperty(final String i18nDir, final String propertyKey) {
		final String resourceName = i18nDir.concat(this.name()).concat(".properties");
		final File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile());

		try (final InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			final Properties properties = new Properties();
			properties.load(inputStreamReader);

			final String value = properties.getProperty(propertyKey);
			return !StringUtils.isEmptyOrBlank(value) ? value : properties.getProperty(DEFAULT_ERROR);
		} catch (IOException e) {
			return "Occurred a error when try to get i18n property";
		}
	}
}
