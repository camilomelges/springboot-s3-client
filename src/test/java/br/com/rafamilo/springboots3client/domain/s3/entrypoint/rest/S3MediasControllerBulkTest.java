package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageServiceImpl;
import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.testcontainers.S3ContainerSingleton;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
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

	private int allCount;

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

	private PostMediaDTO mountPostDTO(final File file) {
		try {
			return PostMediaDTO.builder()
				.configS3DTO(mountConfigS3DTO())
				.fileContent(Files.readAllBytes(Path.of(file.getAbsolutePath())))
				.fileName(file.getName()).build();
		} catch (final IOException e) {
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
		HttpEntity<?> entity = new HttpEntity<>(postMediaDTO, createHeaders());

		final ResponseEntity<String> resultPost;
		try {
			resultPost = CompletableFuture.completedFuture(testRestTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class)).get();
			Assertions.assertTrue(resultPost.getStatusCode().is2xxSuccessful());
			log.info("post");

			final GetMediaDTO getMediaDTO = mountGetDTO(StringUtils.getLastSubstring(Objects.requireNonNull(resultPost.getBody()), '/'));
			entity = new HttpEntity<>(createHeaders());
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
