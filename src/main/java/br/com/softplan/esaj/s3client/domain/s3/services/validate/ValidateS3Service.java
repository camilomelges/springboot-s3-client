package br.com.softplan.esaj.s3client.domain.s3.services.validate;

import com.amazonaws.services.s3.AmazonS3;

import br.com.softplan.esaj.s3client.domain.s3.dtos.ConfigS3DTO;

public interface ValidateS3Service {

	void run(final AmazonS3 s3Client, final ConfigS3DTO configS3DTO);
}
