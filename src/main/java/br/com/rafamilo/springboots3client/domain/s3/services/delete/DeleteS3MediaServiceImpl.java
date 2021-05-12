package br.com.rafamilo.springboots3client.domain.s3.services.delete;

import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.services.config.GetS3ConfigService;
import br.com.rafamilo.springboots3client.domain.s3.services.validate.ValidateS3Service;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteS3MediaServiceImpl implements DeleteS3MediaService {

	private final GetS3ConfigService getS3ConfigService;
	private final ValidateS3Service validateS3Service;

	@Async("asyncExecutor")
	public void run(final DeleteMediaDTO deleteMediaDTO) {
		final AmazonS3 s3Client = getS3ConfigService.run(deleteMediaDTO.getConfigS3DTO());
		validateS3Service.run(s3Client, deleteMediaDTO.getConfigS3DTO());

		s3Client.deleteObject(deleteMediaDTO.getConfigS3DTO().getS3BucketName(), deleteMediaDTO.getFileName());
	}
}
