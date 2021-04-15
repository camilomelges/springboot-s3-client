package br.com.softplan.esaj.s3client.domain.s3.dtos;

import java.io.File;
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

	private ConfigS3DTO s3Configs;

	@NotNull(message = "FileUrl can't be null")
	@NotBlank(message = "FileUrl can't be blank")
	@NotEmpty(message = "FileUrl can't be empty")
	private String fileUrl;
}
