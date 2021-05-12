package br.com.rafamilo.springboots3client.utils.entrypoint.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFound404Exception extends RuntimeException {

	public NotFound404Exception(String message) { super(message); }
}
