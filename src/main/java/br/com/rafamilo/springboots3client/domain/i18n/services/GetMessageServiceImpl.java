package br.com.rafamilo.springboots3client.domain.i18n.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.rafamilo.springboots3client.domain.i18n.enums.I18nLocale;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;

@Service
public class GetMessageServiceImpl implements GetMessageService {

	private static final String defaultLocale = "pt_BR";

	@Value("${s3Client.i18n.dir}")
	private String i18nDir;

	public String run(final String locale, final String propertyKey) {
		return getEnum(locale).getProperty(i18nDir, propertyKey);
	}

	private I18nLocale getEnum(final String locale) {
		return I18nLocale.valueOf(!StringUtils.isEmptyOrBlank(locale) ? StringUtils.trim(locale) : defaultLocale);
	}
}
