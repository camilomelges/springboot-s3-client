package br.com.softplan.esaj.s3client.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("rawtypes")
@Testcontainers
public class S3ContainerSingleton {

	protected static LocalStackContainer S3_CONTAINER;
	private static final String IMAGE_NAME = "localstack/localstack";
	private static final String IMAGE_VERSION = "latest";

	@BeforeAll
	public static void s3Container() {
		if (S3_CONTAINER == null) {
			S3_CONTAINER = new LocalStackContainer(DockerImageName.parse(getS3Image()))
				.withServices(LocalStackContainer.Service.S3)
				.withEnv("DEFAULT_REGION", "us-east-1");

			S3_CONTAINER.start();
		}
	}

	private static String getS3Image() {
		return IMAGE_NAME + ":" + IMAGE_VERSION;
	}
}
