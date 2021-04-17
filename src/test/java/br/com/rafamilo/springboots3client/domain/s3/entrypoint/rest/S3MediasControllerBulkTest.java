package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageServiceImpl;
import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.testcontainers.S3ContainerSingleton;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class S3MediasControllerBulkTest extends S3ContainerSingleton {

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

	private HttpHeaders createHeaders() {
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(basicUserName, basicPassword);
		headers.add("Accept-Language", null);

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

	private PostMediaDTO mountPostDTO(final File file) throws IOException {
		return PostMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileContent(Files.readAllBytes(Path.of(file.getAbsolutePath())))
			.fileName(file.getName()).build();
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
	@Timeout(value = 32000, unit = TimeUnit.MILLISECONDS)
	void deveSalvarOArquivoNoS3ERetornarAUrlCorreta() throws IOException, NoSuchAlgorithmException {
		final String folderName = "medias/";
		final File directory = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(folderName)).getFile());
		final File[] files = directory.listFiles();

		assert files != null;
		for (int i = 0; i < 20; i++) {
			Arrays.stream(files).parallel().forEach(file -> {
				try {
					validateTest(mountPostDTO(file));
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void validateTest(final PostMediaDTO postMediaDTO) throws NoSuchAlgorithmException {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders());

		ResponseEntity<String> resultPost = testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

		Assertions.assertEquals(HttpStatus.OK, resultPost.getStatusCode());
		String regex = "^["
			+ postMediaDTO.getConfigS3DTO().getS3Url()
			+ "/"
			+ postMediaDTO.getConfigS3DTO().getS3BucketName();
		Assertions.assertTrue(Objects.requireNonNull(resultPost.getBody()).matches(regex.concat("].*[.".concat(StringUtils.getLastSubstring(postMediaDTO.getFileName(), '.')).concat("]"))));

//		String fileName = StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/');
//		final GetMediaDTO getMediaDTO = mountGetDTO(fileName);
//		entity = new HttpEntity<>(getMediaDTO, createHeaders());
//		builder.queryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
//		builder.queryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
//		builder.queryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
//		builder.queryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
//		builder.queryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
//		builder.queryParam("fileName", getMediaDTO.getFileName());
//
//		ResponseEntity<byte[]> resultGet = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class);
//		Assertions.assertEquals(HttpStatus.OK, resultGet.getStatusCode());
//		Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());
//
//		final DeleteMediaDTO deleteMediaDTO = mountDeleteDTO(fileName);
//		entity = new HttpEntity<>(deleteMediaDTO, createHeaders());
//		builder.replaceQueryParam("s3Url", deleteMediaDTO.getConfigS3DTO().getS3Url());
//		builder.replaceQueryParam("s3AccessKey", deleteMediaDTO.getConfigS3DTO().getS3AccessKey());
//		builder.replaceQueryParam("s3SecretKey", deleteMediaDTO.getConfigS3DTO().getS3SecretKey());
//		builder.replaceQueryParam("s3Region", deleteMediaDTO.getConfigS3DTO().getS3Region());
//		builder.replaceQueryParam("s3BucketName", deleteMediaDTO.getConfigS3DTO().getS3BucketName());
//		builder.replaceQueryParam("fileName", deleteMediaDTO.getFileName());
//
//		ResponseEntity<Void> resultDelete = testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, Void.class);
//		Assertions.assertEquals(HttpStatus.OK, resultDelete.getStatusCode());
//
//		entity = new HttpEntity<>(getMediaDTO, createHeaders());
//		builder.replaceQueryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
//		builder.replaceQueryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
//		builder.replaceQueryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
//		builder.replaceQueryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
//		builder.replaceQueryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
//		builder.replaceQueryParam("fileName", getMediaDTO.getFileName());
//
//		ResponseEntity<String> resultGet2 = testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
//		Assertions.assertEquals(HttpStatus.NOT_FOUND, resultGet2.getStatusCode());
//		Assertions.assertEquals(getMessageService.run(null, "s3.service.get.getFile.error404"), resultGet2.getBody());
	}
}
