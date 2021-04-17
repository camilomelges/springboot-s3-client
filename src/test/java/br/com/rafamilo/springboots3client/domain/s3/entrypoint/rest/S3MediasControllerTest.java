package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageServiceImpl;
import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.testcontainers.S3ContainerSingleton;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class S3MediasControllerTest extends S3ContainerSingleton {

	private static final String BUCKET_NAME = "test";
	private final TestRestTemplate testRestTemplate = new TestRestTemplate();
	@Value("${s3Client.auth.basicAuth.userName}")
	private String basicUserName;
	@Value("${s3Client.auth.basicAuth.password}")
	private String basicPassword;
	@InjectMocks
	private GetMessageServiceImpl getMessageService;
	@Value("${server.port}")
	private String port;

	private String getRequestURL() {
		return "http://localhost:" + port + "/s3-medias";
	}

	private HttpHeaders createHeaders(final String language) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(basicUserName, basicPassword);
		headers.add("Accept-Language", language);

		return headers;
	}

	private ConfigS3DTO mountConfigS3DTO() {
		return ConfigS3DTO.builder()
			.s3AccessKey(S3_CONTAINER.getAccessKey())
			.s3SecretKey(S3_CONTAINER.getSecretKey())
			.s3BucketName(BUCKET_NAME)
			.s3Region(S3_CONTAINER.getRegion())
			.s3Url(mountS3Url()).build();
	}

	private String mountS3Url() {
		return "http://"
			+ S3_CONTAINER.getHost().concat(":")
			+ S3_CONTAINER.getFirstMappedPort().toString();
	}

	private PostMediaDTO mountPostDTO() throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return PostMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileContent(fileContent)
			.fileName("foobar.pdf").build();
	}

	private GetMediaDTO mountGetDTO(final String fileName) throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return GetMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileName(fileName).build();
	}

	private DeleteMediaDTO mountDeleteDTO(final String fileName) throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return DeleteMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileName(fileName).build();
	}

	@Test
	@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
	void deveSalvarOArquivoNoS3ERetornarAUrlCorreta() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO();

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		final StringBuilder regex = new StringBuilder();
		regex.append("^[");
		regex.append(postMediaDTO.getConfigS3DTO().getS3Url());
		regex.append("/");
		regex.append(postMediaDTO.getConfigS3DTO().getS3BucketName());

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertTrue(Objects.requireNonNull(result.getBody()).matches(regex.toString().concat("].*[.pdf]")));

		postMediaDTO.setFileName("foobar.txt");
		entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertTrue(Objects.requireNonNull(result.getBody()).matches(regex.toString().concat("].*[.txt]")));
	}

	@Test
	@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
	void deveSalvarEPegarOArquivoEDeveTerOMesmoConteudo() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO();

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		ResponseEntity<String> resultPost = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
		Assertions.assertEquals(HttpStatus.OK, resultPost.getStatusCode());

		String fileName = StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/');

		final GetMediaDTO getMediaDTO = mountGetDTO(fileName);
		entity = new HttpEntity<>(getMediaDTO, createHeaders(null));
		builder.queryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
		builder.queryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.queryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.queryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
		builder.queryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.queryParam("fileName", getMediaDTO.getFileName());

		ResponseEntity<byte[]> resultGet = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class);
		Assertions.assertEquals(HttpStatus.OK, resultGet.getStatusCode());
		Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());
	}

	@Test
	@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
	void deveSalvarPegarOArquivoDeletarEQuandoBuscarDeveRetornarUmErroPtBr() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO();

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		ResponseEntity<String> resultPost = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
		Assertions.assertEquals(HttpStatus.OK, resultPost.getStatusCode());

		String fileName = StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/');

		final GetMediaDTO getMediaDTO = mountGetDTO(fileName);
		entity = new HttpEntity<>(getMediaDTO, createHeaders(null));
		builder.queryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
		builder.queryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.queryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.queryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
		builder.queryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.queryParam("fileName", getMediaDTO.getFileName());

		ResponseEntity<byte[]> resultGet = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class);
		Assertions.assertEquals(HttpStatus.OK, resultGet.getStatusCode());
		Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());

		final DeleteMediaDTO deleteMediaDTO = mountDeleteDTO(fileName);
		entity = new HttpEntity<>(deleteMediaDTO, createHeaders(null));
		builder.replaceQueryParam("s3Url", deleteMediaDTO.getConfigS3DTO().getS3Url());
		builder.replaceQueryParam("s3AccessKey", deleteMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.replaceQueryParam("s3SecretKey", deleteMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.replaceQueryParam("s3Region", deleteMediaDTO.getConfigS3DTO().getS3Region());
		builder.replaceQueryParam("s3BucketName", deleteMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.replaceQueryParam("fileName", deleteMediaDTO.getFileName());

		ResponseEntity<Void> resultDelete = testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, Void.class);
		Assertions.assertEquals(HttpStatus.OK, resultDelete.getStatusCode());

		entity = new HttpEntity<>(getMediaDTO, createHeaders(null));
		builder.replaceQueryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
		builder.replaceQueryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.replaceQueryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.replaceQueryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
		builder.replaceQueryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.replaceQueryParam("fileName", getMediaDTO.getFileName());

		ResponseEntity<String> resultGet2 = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, resultGet2.getStatusCode());
		Assertions.assertEquals(getMessageService.run(null, "s3.service.get.getFile.error404"), resultGet2.getBody());
	}

	@Test
	@Timeout(value = 10000, unit = TimeUnit.MILLISECONDS)
	void deveSalvarPegarOArquivoDeletarEQuandoBuscarDeveRetornarUmErroEsCo() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO();

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders("es_CO"));
		ResponseEntity<String> resultPost = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
		Assertions.assertEquals(HttpStatus.OK, resultPost.getStatusCode());

		String fileName = StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/');

		final GetMediaDTO getMediaDTO = mountGetDTO(fileName);
		entity = new HttpEntity<>(getMediaDTO, createHeaders("es_CO"));
		builder.queryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
		builder.queryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.queryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.queryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
		builder.queryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.queryParam("fileName", getMediaDTO.getFileName());

		ResponseEntity<byte[]> resultGet = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class);
		Assertions.assertEquals(HttpStatus.OK, resultGet.getStatusCode());
		Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());

		final DeleteMediaDTO deleteMediaDTO = mountDeleteDTO(fileName);
		entity = new HttpEntity<>(deleteMediaDTO, createHeaders("es_CO"));
		builder.replaceQueryParam("s3Url", deleteMediaDTO.getConfigS3DTO().getS3Url());
		builder.replaceQueryParam("s3AccessKey", deleteMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.replaceQueryParam("s3SecretKey", deleteMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.replaceQueryParam("s3Region", deleteMediaDTO.getConfigS3DTO().getS3Region());
		builder.replaceQueryParam("s3BucketName", deleteMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.replaceQueryParam("fileName", deleteMediaDTO.getFileName());

		ResponseEntity<Void> resultDelete = testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, Void.class);
		Assertions.assertEquals(HttpStatus.OK, resultDelete.getStatusCode());

		entity = new HttpEntity<>(getMediaDTO, createHeaders("es_CO"));
		builder.replaceQueryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
		builder.replaceQueryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
		builder.replaceQueryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
		builder.replaceQueryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
		builder.replaceQueryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
		builder.replaceQueryParam("fileName", getMediaDTO.getFileName());

		ResponseEntity<String> resultGet2 = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, resultGet2.getStatusCode());
		Assertions.assertEquals(getMessageService.run("es_CO", "s3.service.get.getFile.error404"), resultGet2.getBody());
	}
}
