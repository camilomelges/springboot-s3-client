package br.com.softplan.esaj.s3client.domain.s3.services.get;

import br.com.softplan.esaj.s3client.domain.s3.dtos.GetMediaDTO;

public interface GetS3MediaService {
	byte[] run(final GetMediaDTO getMediaDTO);
}
