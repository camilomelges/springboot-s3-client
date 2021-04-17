package br.com.rafamilo.springboots3client.domain.s3.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostMediaDTO {

	private ConfigS3DTO configS3DTO;

	@ToString.Exclude
	@NotNull(message = "s3.dto.postMedia.fileContent.null.error")
	@NotEmpty(message = "s3.dto.postMedia.fileContent.empty.error")
	private byte[] fileContent;

	@NotNull(message = "s3.dto.postMedia.fileName.null.error")
	@NotBlank(message = "s3.dto.postMedia.fileName.blank.error")
	@NotEmpty(message = "s3.dto.postMedia.fileName.empty.error")
	private String fileName;
}
