package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageServiceImpl;
import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@ExtendWith({ SpringExtension.class })
public class S3MediasControllerTestCompose {

	private static final String BUCKET_NAME = "test";
	private final TestRestTemplate testRestTemplate = new TestRestTemplate();
	private final String basicUserName = "saj";
	private final String basicPassword = "agesune1";
	private final String port = "5000";
	@InjectMocks
	private GetMessageServiceImpl getMessageService;

	private int allCount;

	private String getRequestURL() {
		return "http://localhost:" + port + "/s3-medias";
	}

	@BeforeEach
	void beforeEach() {
		ReflectionTestUtils.setField(getMessageService, "i18nDir", "file:/home/app/target/classes/i18n/");
	}

	private HttpHeaders createHeaders(final String language) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(basicUserName, basicPassword);
		headers.add("Accept-Language", language);

		return headers;
	}

	private ConfigS3DTO mountConfigS3DTO() {
		return ConfigS3DTO.builder()
			.s3AccessKey("saj")
			.s3SecretKey("test")
			.s3BucketName(BUCKET_NAME)
			.s3Region("us-east-1")
			.s3Url("http://s3-server:4566").build();
	}

	private PostMediaDTO mountPostDTO() throws NoSuchAlgorithmException {
		final byte[] fileContent = new byte[20];
		SecureRandom.getInstanceStrong().nextBytes(fileContent);
		return PostMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileContent(fileContent)
			.fileName("foobar.pdf").build();
	}

	private PostMediaDTO mountPostDTO(final File file) {
		try {
			return PostMediaDTO.builder()
				.configS3DTO(mountConfigS3DTO())
				.fileContent(Files.readAllBytes(Path.of(file.getAbsolutePath())))
				.fileName(file.getName()).build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private GetMediaDTO mountGetDTO(final String fileName) {
		return GetMediaDTO.builder()
			.configS3DTO(mountConfigS3DTO())
			.fileName(fileName).build();
	}

	private DeleteMediaDTO mountDeleteDTO(final String fileName) {
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
	void deveSalvarPegarOArquivoDeletarEQuandoBuscarDeveRetornarUmErro() throws NoSuchAlgorithmException {
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
	}

	@Test
	void deveSalvarBulkEmMenosDeUmMinuto() {
		final String dirName = "medias/";
		final File directory = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(dirName)).getFile());
		final File[] files = Objects.requireNonNull(directory.listFiles());
		allCount = files.length * 20 - 1;

		for (int i = 0; i < 20; i++) {
			for (var file : files) {
				CompletableFuture.runAsync(() -> validateTest(mountPostDTO(file)));
			}
		}

		setTimeout(() -> log.info("timeout"), 3000);
	}

	@Async
	public void validateTest(final PostMediaDTO postMediaDTO) {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRequestURL());
		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders(null));

		final ResponseEntity<String> resultPost;
		try {
			resultPost = CompletableFuture.completedFuture(testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class)).get();
			Assertions.assertTrue(resultPost.getStatusCode().is2xxSuccessful());
			log.info("post");

			final GetMediaDTO getMediaDTO = mountGetDTO(StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/'));
			entity = new HttpEntity<>(createHeaders(null));
			builder.queryParam("s3Url", getMediaDTO.getConfigS3DTO().getS3Url());
			builder.queryParam("s3AccessKey", getMediaDTO.getConfigS3DTO().getS3AccessKey());
			builder.queryParam("s3SecretKey", getMediaDTO.getConfigS3DTO().getS3SecretKey());
			builder.queryParam("s3Region", getMediaDTO.getConfigS3DTO().getS3Region());
			builder.queryParam("s3BucketName", getMediaDTO.getConfigS3DTO().getS3BucketName());
			builder.queryParam("fileName", getMediaDTO.getFileName());

			final ResponseEntity<byte[]> resultGet = CompletableFuture.completedFuture(testRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, byte[].class)).get();
			Assertions.assertTrue(resultGet.getStatusCode().is2xxSuccessful());
			Assertions.assertArrayEquals(postMediaDTO.getFileContent(), resultGet.getBody());
			log.info("get");

			final CompletableFuture<ResponseEntity<Void>> resultDelete = CompletableFuture.completedFuture(testRestTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, Void.class));
			Assertions.assertTrue(resultDelete.get().getStatusCode().is2xxSuccessful());
			log.info("delete-".concat(String.valueOf(--allCount)));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void setTimeout(Runnable runnable, int delay) {
		if (allCount > 0) {
			AtomicBoolean running = new AtomicBoolean(true);
			final Thread thread = new Thread(() -> {
				try {
					runnable.run();
					Thread.sleep(delay);
					running.set(false);
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			});
			thread.start();

			while (running.get()) {}
			setTimeout(() -> log.info("timeout"), 5000);
		}
	}
}
