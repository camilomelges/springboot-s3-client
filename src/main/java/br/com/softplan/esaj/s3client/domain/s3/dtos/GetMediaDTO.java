package br.com.softplan.esaj.s3client.domain.s3.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMediaDTO {

	private ConfigS3DTO configS3DTO;

	@NotNull(message = "s3.dto.getMedia.fileName.null.error")
	@NotBlank(message = "s3.dto.getMedia.fileName.blank.error")
	@NotEmpty(message = "s3.dto.getMedia.fileName.empty.error")
	private String fileName;
}
