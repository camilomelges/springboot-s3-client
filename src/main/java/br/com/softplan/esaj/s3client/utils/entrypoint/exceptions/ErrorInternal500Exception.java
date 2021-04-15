package br.com.softplan.esaj.s3client.utils.entrypoint.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ErrorInternal500Exception extends RuntimeException {

	public ErrorInternal500Exception(String message) {
		super(message);
	}
}
