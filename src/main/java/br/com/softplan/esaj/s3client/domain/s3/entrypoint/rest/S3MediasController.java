package br.com.softplan.esaj.s3client.domain.s3.entrypoint.rest;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.softplan.esaj.s3client.domain.i18n.services.GetMessageService;
import br.com.softplan.esaj.s3client.domain.s3.dtos.GetMediaDTO;
import br.com.softplan.esaj.s3client.domain.s3.dtos.PostMediaDTO;
import br.com.softplan.esaj.s3client.domain.s3.services.get.GetS3MediaService;
import br.com.softplan.esaj.s3client.domain.s3.services.post.PostS3MediaService;
import br.com.softplan.esaj.s3client.utils.entrypoint.exceptions.BadRequest400Exception;

@RestController
@RequestMapping("/s3-medias")
@RequiredArgsConstructor
public class S3MediasController {

	private final GetMessageService getMessageService;
	private final PostS3MediaService postS3MediaService;
	private final GetS3MediaService getS3MediaService;

	@PostMapping
	public String postMedia(@RequestHeader("Accept-Language") final String locale, @RequestBody final PostMediaDTO postMediaDTO) {
		try {
			return postS3MediaService.run(postMediaDTO);
		} catch (BadRequest400Exception e) {
			throw new BadRequest400Exception(getMessageService.run(locale, e.getMessage()));
		}
	}

	@PostMapping("/get-media")
	public byte[] getMedia(@RequestHeader("Accept-Language") final String locale, @RequestBody final GetMediaDTO getMediaDTO) {
		try {
			return getS3MediaService.run(getMediaDTO);
		} catch (BadRequest400Exception e) {
			throw new BadRequest400Exception(getMessageService.run(locale, e.getMessage()));
		}
	}
}
