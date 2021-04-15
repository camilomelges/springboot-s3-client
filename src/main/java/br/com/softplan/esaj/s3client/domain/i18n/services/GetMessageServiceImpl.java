package br.com.softplan.esaj.s3client.domain.i18n.services;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import br.com.softplan.esaj.s3client.domain.i18n.enums.I18nLocale;
import br.com.softplan.esaj.s3client.utils.string.StringUtils;

@Service
public class GetMessageServiceImpl implements GetMessageService {

	private static final String defaultLocale = "pt_BR";

	public String run(final String locale, final String propertyKey) {
		return getEnum(locale).getProperty(propertyKey);
	}

	private I18nLocale getEnum(final String locale) {
		return I18nLocale.valueOf(!StringUtils.isEmptyOrBlank(locale) ? StringUtils.trim(locale) : defaultLocale);
	}
}
