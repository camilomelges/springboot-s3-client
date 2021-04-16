package br.com.rafamilo.springboots3client.domain.s3.dtos;

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
public class DeleteMediaDTO {

	private ConfigS3DTO configS3DTO;

	@NotNull(message = "s3.dto.deleteMedia.fileUrl.null.error")
	@NotBlank(message = "s3.dto.deleteMedia.fileUrl.blank.error")
	@NotEmpty(message = "s3.dto.deleteMedia.fileUrl.empty.error")
	private String fileUrl;
}
