package br.com.rafamilo.springboots3client.domain.s3.services.post;

import java.util.concurrent.CompletableFuture;

import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;

public interface PostS3MediaService {
	CompletableFuture<String> run(final PostMediaDTO postMediaDTO);
}
