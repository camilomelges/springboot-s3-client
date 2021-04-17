package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import java.util.concurrent.CompletableFuture;

import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.services.delete.DeleteS3MediaService;
import br.com.rafamilo.springboots3client.domain.s3.services.get.GetS3MediaService;
import br.com.rafamilo.springboots3client.domain.s3.services.post.PostS3MediaService;
import br.com.rafamilo.springboots3client.utils.validation.services.ValidateDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3-medias")
@RequiredArgsConstructor
public class S3MediasController {

	private final PostS3MediaService postS3MediaService;
	private final GetS3MediaService getS3MediaService;
	private final DeleteS3MediaService deleteS3MediaService;
	private final ValidateDTOService validateDTOService;

	@PostMapping
	public CompletableFuture<String> postMedia(@RequestBody final PostMediaDTO postMediaDTO) {
		validateDTOService.run(postMediaDTO);
		return postS3MediaService.run(postMediaDTO);
	}

	@GetMapping
	public CompletableFuture<byte[]> getMedia(
		@RequestParam("s3Url") final String s3Url,
		@RequestParam("s3AccessKey") final String s3AccessKey,
		@RequestParam("s3SecretKey") final String s3SecretKey,
		@RequestParam("s3Region") final String s3Region,
		@RequestParam("s3BucketName") final String s3BucketName,
		@RequestParam("fileName") final String fileName) {
		final GetMediaDTO getMediaDTO = GetMediaDTO.builder()
			.configS3DTO(ConfigS3DTO.mountFromParams(s3Url, s3AccessKey, s3SecretKey, s3Region, s3BucketName))
			.fileName(fileName).build();
		validateDTOService.run(getMediaDTO);
		return getS3MediaService.run(getMediaDTO);
	}

	@DeleteMapping
	public void deleteMedia(
		@RequestParam("s3Url") final String s3Url,
		@RequestParam("s3AccessKey") final String s3AccessKey,
		@RequestParam("s3SecretKey") final String s3SecretKey,
		@RequestParam("s3Region") final String s3Region,
		@RequestParam("s3BucketName") final String s3BucketName,
		@RequestParam("fileName") final String fileName) {
		final DeleteMediaDTO deleteMediaDTO = DeleteMediaDTO.builder()
			.configS3DTO(ConfigS3DTO.mountFromParams(s3Url, s3AccessKey, s3SecretKey, s3Region, s3BucketName))
			.fileName(fileName).build();
		validateDTOService.run(deleteMediaDTO);
		CompletableFuture.runAsync(() -> deleteS3MediaService.run(deleteMediaDTO));
	}
}
