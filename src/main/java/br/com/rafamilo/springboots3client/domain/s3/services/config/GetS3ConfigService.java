package br.com.rafamilo.springboots3client.domain.s3.services.config;

import com.amazonaws.services.s3.AmazonS3;

import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;

public interface GetS3ConfigService {
	AmazonS3 run(final ConfigS3DTO configS3DTO);
}
