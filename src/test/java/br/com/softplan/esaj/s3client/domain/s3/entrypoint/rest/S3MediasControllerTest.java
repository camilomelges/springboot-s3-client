package br.com.softplan.esaj.s3client.domain.s3.entrypoint.rest;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.softplan.esaj.s3client.domain.i18n.services.GetMessageServiceImpl;
import br.com.softplan.esaj.s3client.domain.s3.dtos.ConfigS3DTO;
import br.com.softplan.esaj.s3client.domain.s3.dtos.GetMediaDTO;
import br.com.softplan.esaj.s3client.domain.s3.dtos.PostMediaDTO;
import br.com.softplan.esaj.s3client.testcontainers.S3ContainerSingleton;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class S3MediasControllerTest extends S3ContainerSingleton {

	@Value("${s3Client.auth.basicAuth.userName}")
	private String basicUserName;

	@Value("${s3Client.auth.basicAuth.password}")
	private String basicPassword;

	private final TestRestTemplate testRestTemplate = new TestRestTemplate();

	@InjectMocks
	private GetMessageServiceImpl getMessageService;

	@Value("${server.port}")
	private String port;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String BUCKET_NAME = "test";

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
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("http://");
		stringBuilder.append(S3_CONTAINER.getHost().concat(":"));
		stringBuilder.append(S3_CONTAINER.getFirstMappedPort().toString());

		return stringBuilder.toString();
	}

	private PostMediaDTO mountPostDTO(final String fileName) throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return PostMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileContent(fileContent)
			.fileName(fileName != null ? fileName : "foobar.pdf").build();
	}

	private GetMediaDTO mountGetDTO(final String fileName) throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return GetMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileName(fileName).build();
	}

	private MultiValueMap mountMultiValueMap(final Object object) throws NoSuchAlgorithmException {
		return new LinkedMultiValueMap<>(objectMapper.convertValue(object, Map.class));
	}

	@Test
	void deveSalvarOArquivoNoS3ERetornarAUrlCorreta() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO(null);

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		ResponseEntity<String> result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		final StringBuilder regex = new StringBuilder();
		regex.append("^[");
		regex.append(postMediaDTO.getConfigS3DTO().getS3Url());
		regex.append("/");
		regex.append(postMediaDTO.getConfigS3DTO().getS3BucketName());

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertTrue(result.getBody().matches(regex.toString().concat("].*[.pdf]")));

		postMediaDTO.setFileName("foobar.txt");
		entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		result = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertTrue(result.getBody().matches(regex.toString().concat("].*[.txt]")));
	}

	@Test
	void deveSalvarEPegarOArquivoEDeveTerOMesmoConteudo() throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		final PostMediaDTO postMediaDTO = mountPostDTO(null);

		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));
		ResponseEntity<String> resultPost = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
		Assertions.assertEquals(HttpStatus.OK, resultPost.getStatusCode());

		String fileName = resultPost.getBody().substring(resultPost.getBody().lastIndexOf('/') + 1);

		final GetMediaDTO getMediaDTO = mountGetDTO(fileName);
		entity = new HttpEntity<>(getMediaDTO, createHeaders(null));

		ResponseEntity<byte[]> resultGet = testRestTemplate.exchange(builder.toUriString().concat("/get-media"), HttpMethod.POST, entity, byte[].class);
		Assertions.assertEquals(HttpStatus.OK, resultGet.getStatusCode());
		Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());
	}
}
