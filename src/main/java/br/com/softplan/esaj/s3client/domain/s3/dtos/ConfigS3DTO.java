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

	@NotNull(message = "s3.dto.configS3.s3Url.null.error")
	@NotBlank(message = "s3.dto.configS3.s3Url.blank.error")
	@NotEmpty(message = "s3.dto.configS3.s3Url.empty.error")
	private String s3Url;

	@NotNull(message = "s3.dto.configS3.s3AccessKey.null.error")
	@NotBlank(message = "s3.dto.configS3.s3AccessKey.blank.error")
	@NotEmpty(message = "s3.dto.configS3.s3AccessKey.empty.error")
	private String s3AccessKey;

	@NotNull(message = "s3.dto.configS3.s3SecretKey.null.error")
	@NotBlank(message = "s3.dto.configS3.s3SecretKey.blank.error")
	@NotEmpty(message = "s3.dto.configS3.s3SecretKey.empty.error")
	private String s3SecretKey;

	@NotNull(message = "s3.dto.configS3.s3Region.null.error")
	@NotBlank(message = "s3.dto.configS3.s3Region.blank.error")
	@NotEmpty(message = "s3.dto.configS3.s3Region.empty.error")
	private String s3Region;

	@NotNull(message = "s3.dto.configS3.s3BucketName.null.error")
	@NotBlank(message = "s3.dto.configS3.s3BucketName.blank.error")
	@NotEmpty(message = "s3.dto.configS3.s3BucketName.empty.error")
	private String s3BucketName;
}
