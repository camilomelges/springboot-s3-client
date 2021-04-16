package br.com.softplan.esaj.s3client.domain.s3.services.get;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import br.com.softplan.esaj.s3client.domain.s3.dtos.GetMediaDTO;
import br.com.softplan.esaj.s3client.domain.s3.services.config.GetS3ConfigService;
import br.com.softplan.esaj.s3client.domain.s3.services.validate.ValidateS3Service;
import br.com.softplan.esaj.s3client.utils.entrypoint.exceptions.BadRequest400Exception;

@Service
@RequiredArgsConstructor
public class GetS3MediaServiceImpl implements GetS3MediaService {

	private final GetS3ConfigService getS3ConfigService;
	private final ValidateS3Service validateS3Service;

	public byte[] run(final GetMediaDTO getMediaDTO) {
		final AmazonS3 s3Client = getS3ConfigService.run(getMediaDTO.getConfigS3DTO());
		validateS3Service.run(s3Client, getMediaDTO.getConfigS3DTO());

		return mountResponseByte(s3Client, getMediaDTO);
	}

	private byte[] mountResponseByte(final AmazonS3 s3Client, final GetMediaDTO getMediaDTO) {
		try {
			return s3Client.getObject(
				getMediaDTO.getConfigS3DTO().getS3BucketName(),
				getMediaDTO.getFileName()
			).getObjectContent().readAllBytes();
		} catch (IOException e) {
			throw new BadRequest400Exception("s3.service.get.createFile.error");
		}
	}
}
