package br.com.rafamilo.springboots3client.domain.s3.services.validate;

import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;
import com.amazonaws.services.s3.AmazonS3;

public interface ValidateS3Service {

	void run(final AmazonS3 s3Client, final ConfigS3DTO configS3DTO);
}
