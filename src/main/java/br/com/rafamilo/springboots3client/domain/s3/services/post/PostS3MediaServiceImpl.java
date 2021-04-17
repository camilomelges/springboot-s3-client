package br.com.rafamilo.springboots3client.domain.s3.services.post;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import br.com.rafamilo.springboots3client.domain.s3.services.config.GetS3ConfigService;
import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.BadRequest400Exception;
import br.com.rafamilo.springboots3client.utils.string.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import br.com.rafamilo.springboots3client.domain.s3.components.generateuniquename.GenerateUniqueNameComponent;
import br.com.rafamilo.springboots3client.domain.s3.dtos.PostMediaDTO;
import br.com.rafamilo.springboots3client.domain.s3.services.validate.ValidateS3Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostS3MediaServiceImpl implements PostS3MediaService {

	private final GetS3ConfigService getS3ConfigService;
	private final GenerateUniqueNameComponent generateUniqueNameComponent;
	private final ValidateS3Service validateS3Service;

	public String run(final PostMediaDTO postMediaDTO) {
		final AmazonS3 s3Client = getS3ConfigService.run(postMediaDTO.getConfigS3DTO());
		validateS3Service.run(s3Client, postMediaDTO.getConfigS3DTO());

		final String uniqueFileName = mountFileName(postMediaDTO);
		final PutObjectRequest putObjectRequest = mountRequestObject(postMediaDTO, uniqueFileName);
		s3Client.putObject(putObjectRequest);

		deleteFile(putObjectRequest.getFile());

		return s3Client.getUrl(postMediaDTO.getConfigS3DTO().getS3BucketName(), uniqueFileName).toString();
	}



	private String mountFileName(final PostMediaDTO postMediaDTO) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(generateUniqueNameComponent.run());
		stringBuilder.append(".");
		stringBuilder.append(StringUtils.getLastSubstring(postMediaDTO.getFileName(), '.'));

		return stringBuilder.toString();
	}

	private File mountFile(final String uniqueFileName, final byte[] fileContent) {
		try {
			final File file = new File(uniqueFileName);
			final Path path = Paths.get(file.getAbsolutePath());
			Files.write(path, fileContent);

			return file;
		} catch (IOException e) {
			throw new BadRequest400Exception("s3.service.post.createFile.error");
		}
	}

	private void deleteFile(final File file) {
		if (!file.delete()) {
			log.error(String.format("File %s can't be deleted", file.getName()));
		}
	}

	private PutObjectRequest mountRequestObject(final PostMediaDTO postMediaDTO, final String uniqueName) {
		return new PutObjectRequest(
			postMediaDTO.getConfigS3DTO().getS3BucketName(),
			uniqueName,
			mountFile(uniqueName, postMediaDTO.getFileContent())
		);
	}
}
