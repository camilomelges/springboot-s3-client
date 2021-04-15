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
public class ConfigS3DTO {

	@NotNull(message = "S3Url can't be null")
	@NotBlank(message = "S3Url can't be blank")
	@NotEmpty(message = "S3Url can't be empty")
	private String s3Url;

	@NotNull(message = "S3AccessKey can't be null")
	@NotBlank(message = "S3AccessKey can't be blank")
	@NotEmpty(message = "S3AccessKey can't be empty")
	private String s3AccessKey;

	@NotNull(message = "S3SecretKey can't be null")
	@NotBlank(message = "S3SecretKey can't be blank")
	@NotEmpty(message = "S3SecretKey can't be empty")
	private String s3SecretKey;

	@NotNull(message = "S3Region can't be null")
	@NotBlank(message = "S3Region can't be blank")
	@NotEmpty(message = "S3Region can't be empty")
	private String s3Region;

	@NotNull(message = "S3BucketName can't be null")
	@NotBlank(message = "S3BucketName can't be blank")
	@NotEmpty(message = "S3BucketName can't be empty")
	private String s3BucketName;
}
