package br.com.softplan.esaj.s3client.domain.s3.dtos;

import java.io.File;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostMediaDTO {

	private ConfigS3DTO s3Configs;

	@NotNull(message = "File can't be null")
	private File file;
}
