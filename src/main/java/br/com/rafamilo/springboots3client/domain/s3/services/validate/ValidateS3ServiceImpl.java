package br.com.rafamilo.springboots3client.domain.s3.services.validate;

import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Service;

@Service
public class ValidateS3ServiceImpl implements ValidateS3Service {

	public void run(final AmazonS3 s3Client, final ConfigS3DTO configS3DTO) {
		if (!s3Client.doesBucketExistV2(configS3DTO.getS3BucketName())) {
			s3Client.createBucket(configS3DTO.getS3BucketName());
		}
	}
}
