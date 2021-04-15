package br.com.softplan.esaj.s3client.domain.i18n.services;

public interface GetMessageService {
	String run(final String locale, final String propertyKey);
}
