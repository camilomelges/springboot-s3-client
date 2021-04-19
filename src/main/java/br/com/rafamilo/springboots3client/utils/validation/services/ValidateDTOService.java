package br.com.rafamilo.springboots3client.utils.validation.services;

public interface ValidateDTOService {
	<T> void run(final T dto);
}
