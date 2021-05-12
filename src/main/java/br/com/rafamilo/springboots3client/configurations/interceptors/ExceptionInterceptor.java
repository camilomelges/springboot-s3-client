package br.com.rafamilo.springboots3client.configurations.interceptors;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageService;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.BadRequest400Exception;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.InternalError500Exception;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.NotFound404Exception;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.Unauthorized401Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionInterceptor {

	private final GetMessageService getMessageService;

	private String mountExceptionMessage(final WebRequest request, final String message) {
		return getMessageService.run(request.getHeader("Accept-Language"), message);
	}

	private HttpHeaders mountExceptionHeaders() {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));

		return headers;
	}

	@ExceptionHandler({AuthenticationException.class, AccessDeniedException.class, HttpClientErrorException.Unauthorized.class })
	public ResponseEntity<String> handle401Exception(final Exception e, final WebRequest request) {
		return new ResponseEntity<>(mountExceptionMessage(request, "s3.controller.unauthorized.error"), mountExceptionHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadRequest400Exception.class)
	public ResponseEntity<String> handle400Exception(final BadRequest400Exception e, final WebRequest request) {
		return new ResponseEntity<>(mountExceptionMessage(request, e.getMessage()), mountExceptionHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Unauthorized401Exception.class)
	public ResponseEntity<String> handle401Exception(final Unauthorized401Exception e, final WebRequest request) {
		return new ResponseEntity<>(mountExceptionMessage(request, e.getMessage()), mountExceptionHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(NotFound404Exception.class)
	public ResponseEntity<String> handle404Exception(final NotFound404Exception e, final WebRequest request) {
		return new ResponseEntity<>(mountExceptionMessage(request, e.getMessage()), mountExceptionHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InternalError500Exception.class)
	public ResponseEntity<String> handle500Exception(final InternalError500Exception e, final WebRequest request) {
		return new ResponseEntity<>(mountExceptionMessage(request, e.getMessage()), mountExceptionHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
