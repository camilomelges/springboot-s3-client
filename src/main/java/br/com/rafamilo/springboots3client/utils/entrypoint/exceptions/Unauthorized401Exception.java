package br.com.rafamilo.springboots3client.utils.entrypoint.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class Unauthorized401Exception extends RuntimeException {

	public Unauthorized401Exception(String message) { super(message); }
}
