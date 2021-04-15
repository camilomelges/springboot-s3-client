package br.com.softplan.esaj.s3client.domain.configs.entrypoint.rest;

import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.softplan.esaj.s3client.domain.i18n.enums.I18nLocale;
import br.com.softplan.esaj.s3client.domain.i18n.services.GetMessageServiceImpl;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConfigsControllerTest {

	@Value("${s3Client.auth.basicAuth.userName}")
	private String basicUserName;

	@Value("${s3Client.auth.basicAuth.password}")
	private String basicPassword;

	private final TestRestTemplate testRestTemplate = new TestRestTemplate();

	@InjectMocks
	private GetMessageServiceImpl getMessageService;

	private static final String APPLICATION_STATUS_PROPERTY = "configs.controller.getApplication.status";

	private String getRequestURL() {
		return "http://localhost:" + 5001 + "/configs/get-application-status";
	}

	private HttpHeaders createHeaders(final String language, final String basicToken) {
		final HttpHeaders headers = new HttpHeaders();
		if (basicToken != null) {
			headers.add("Authorization", basicToken);
		}
		headers.add("Accept-Language", language);

		return headers;
	}

	@Test
	void deveRetornar401QuandoNaoInformarOAuthorizationBasic() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());

		HttpEntity<?> entity = new HttpEntity<>(createHeaders(null, null));

		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
	}

	@Test
	void deveRetornar401QuandoNaoOAuthorizationTokenEstiverErrado() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final String basicToken = "Foo bar";

		HttpEntity<?> entity = new HttpEntity<>(createHeaders(null, basicToken));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
	}

	@Test
	void deveRetornar200EAMensagemEmPortuguesQuandoNaoInformarALanguage() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final String basicToken = "Basic " + Base64.getEncoder().encodeToString((basicUserName + ":" + basicPassword).getBytes());

		HttpEntity<?> entity = new HttpEntity<>(createHeaders(null, basicToken));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertEquals(getMessageService.run("pt_BR", APPLICATION_STATUS_PROPERTY), result.getBody());
	}

	@Test
	void deveRetornar200EAMensagemEmPortugues() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final String basicToken = "Basic " + Base64.getEncoder().encodeToString((basicUserName + ":" + basicPassword).getBytes());

		HttpEntity<?> entity = new HttpEntity<>(createHeaders("pt_BR", basicToken));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertEquals(getMessageService.run("pt_BR", APPLICATION_STATUS_PROPERTY), result.getBody());
	}

	@Test
	void deveRetornar200EAMensagemEmEspanholQuandoInformarALanguageES_CO() {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final String basicToken = "Basic " + Base64.getEncoder().encodeToString((basicUserName + ":" + basicPassword).getBytes());

		HttpEntity<?> entity = new HttpEntity<>(createHeaders("es_CO", basicToken));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertEquals(getMessageService.run("es_CO", APPLICATION_STATUS_PROPERTY), result.getBody());
	}
}
