package br.com.rafamilo.springboots3client.domain.s3.services.get;

import java.util.concurrent.CompletableFuture;

import br.com.rafamilo.springboots3client.domain.s3.dtos.GetMediaDTO;

public interface GetS3MediaService {
	CompletableFuture<byte[]> run(final GetMediaDTO getMediaDTO);
}
