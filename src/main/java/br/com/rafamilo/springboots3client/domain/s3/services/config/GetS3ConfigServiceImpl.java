package br.com.rafamilo.springboots3client.domain.s3.services.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.rafamilo.springboots3client.domain.s3.dtos.ConfigS3DTO;

@Service
public class GetS3ConfigServiceImpl implements GetS3ConfigService {

	@Value("${s3Client.configs.S3_CONNECTION_TIMEOUT}")
	private Integer S3_CONNECTION_TIMEOUT;

	@Value("${s3Client.configs.S3_REQUEST_TIMEOUT}")
	private Integer S3_REQUEST_TIMEOUT;

	@Value("${s3Client.configs.S3_SOCKET_TIMEOUT}")
	private Integer S3_SOCKET_TIMEOUT;

	@Value("${s3Client.configs.S3_MAX_POOL_CONNECTION}")
	private Integer S3_MAX_POOL_CONNECTION;

	@Value("${s3Client.configs.S3_MAX_IDLE_MILLIS}")
	private Integer S3_MAX_IDLE_MILLIS;

	@Value("${s3Client.configs.S3_CONNECTION_TTL}")
	private Integer S3_CONNECTION_TTL;

	@Value("${s3Client.configs.AWS_SIGNER_TYPE}")
	private String AWS_SIGNER_TYPE;

	public AmazonS3 run(final ConfigS3DTO configS3DTO) {
		return mountS3Config(configS3DTO);
	}

	private AmazonS3 mountS3Config(final ConfigS3DTO configS3DTO) {
		return AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(configS3DTO.getS3Url(), configS3DTO.getS3Region()))
			.withPathStyleAccessEnabled(true)
			.withClientConfiguration(mountConfiguration())
			.withCredentials(new AWSStaticCredentialsProvider(mountCredentials(configS3DTO)))
			.build();
	}

	private AWSCredentials mountCredentials(final ConfigS3DTO configS3DTO) {
		return new BasicAWSCredentials(configS3DTO.getS3AccessKey(), configS3DTO.getS3SecretKey());
	}

	private ClientConfiguration mountConfiguration() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setConnectionTimeout(S3_CONNECTION_TIMEOUT);
		clientConfiguration.setRequestTimeout(S3_REQUEST_TIMEOUT);
		clientConfiguration.setSocketTimeout(S3_SOCKET_TIMEOUT);
		clientConfiguration.setMaxConnections(S3_MAX_POOL_CONNECTION);
		clientConfiguration.setConnectionMaxIdleMillis(S3_MAX_IDLE_MILLIS);
		clientConfiguration.setConnectionTTL(S3_CONNECTION_TTL);
		clientConfiguration.setSignerOverride(AWS_SIGNER_TYPE);

		return clientConfiguration;
	}
}
