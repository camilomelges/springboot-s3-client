package br.com.softplan.esaj.s3client.domain.s3.services.post;

import br.com.softplan.esaj.s3client.domain.s3.dtos.PostMediaDTO;

public interface PostS3MediaService {
	String run(final PostMediaDTO postMediaDTO);
}
