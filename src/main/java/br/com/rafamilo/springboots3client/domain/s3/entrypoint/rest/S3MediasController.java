package br.com.rafamilo.springboots3client.domain.s3.entrypoint.rest;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageService;
import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.services.get.GetS3MediaService;
import br.com.rafamilo.springboots3client.domain.s3.services.post.PostS3MediaService;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.BadRequest400Exception;
import br.com.rafamilo.springboots3client.utils.validation.services.ValidateDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3-medias")
@RequiredArgsConstructor
public class S3MediasController {

	private final GetMessageService getMessageService;
	private final PostS3MediaService postS3MediaService;
	private final GetS3MediaService getS3MediaService;
	private final ValidateDTOService validateDTOService;

	@PostMapping
	public String postMedia(@RequestHeader("Accept-Language") final String locale, @RequestBody final PostMediaDTO postMediaDTO) {
		try {
			validateDTOService.run(postMediaDTO);
			return postS3MediaService.run(postMediaDTO);
		} catch (BadRequest400Exception e) {
			throw new BadRequest400Exception(getMessageService.run(locale, e.getMessage()));
		}
	}

	@GetMapping
	public byte[] getMedia(
		@RequestHeader("Accept-Language") final String locale,
		@RequestParam("s3Url") final String s3Url,
		@RequestParam("s3AccessKey") final String s3AccessKey,
		@RequestParam("s3SecretKey") final String s3SecretKey,
		@RequestParam("s3Region") final String s3Region,
		@RequestParam("s3BucketName") final String s3BucketName,
		@RequestParam("fileName") final String fileName) {
		try {
			final GetMediaDTO getMediaDTO = GetMediaDTO.builder()
				.configS3DTO(ConfigS3DTO.mountFromParams(s3Url, s3AccessKey, s3SecretKey, s3Region, s3BucketName))
				.fileName(fileName).build();
			validateDTOService.run(getMediaDTO);
			return getS3MediaService.run(getMediaDTO);
		} catch (BadRequest400Exception e) {
			throw new BadRequest400Exception(getMessageService.run(locale, e.getMessage()));
		}
	}
}
