package br.com.rafamilo.springboots3client.domain.s3.services.delete;

import br.com.rafamilo.springboots3client.domain.s3.dtos.DeleteMediaDTO;

public interface DeleteS3MediaService {
	void run(final DeleteMediaDTO deleteMediaDTO);
}
