package br.com.softplan.esaj.s3client.utils.validation.services;

public interface ValidateDTOService {
	<T> void run(final T dto);
}
