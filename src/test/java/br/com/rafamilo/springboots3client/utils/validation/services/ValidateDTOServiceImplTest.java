package br.com.rafamilo.springboots3client.utils.validation.services;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.rafamilo.springboots3client.utils.entrypoint.exceptions.BadRequest400Exception;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ValidateDTOServiceImplTest {

	@InjectMocks
	private ValidateDTOServiceImpl validateDTOService;

	private static final String NOT_NULL_MESSAGE = "dtoForTests.field.null.error";
	private static final String NOT_BLANK_MESSAGE = "dtoForTests.field.blank.error";
	private static final String NOT_EMPTY_MESSAGE = "dtoForTests.field.empty.error";

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DtoForTest {
		@NotNull(message = NOT_NULL_MESSAGE)
		@NotBlank(message = NOT_BLANK_MESSAGE)
		@NotEmpty(message = NOT_EMPTY_MESSAGE)
		private String field;
	}

	@Test
	void deveRetornarBadRequestComTodasAsMensagensQuandoOFieldForNull() {
		final String exceptionMessage = assertThrows(BadRequest400Exception.class, () -> {
			validateDTOService.run(DtoForTest.builder().field(null).build());
		}).getMessage();

		assertTrue(
			exceptionMessage.contains(NOT_NULL_MESSAGE)
				|| exceptionMessage.contains(NOT_BLANK_MESSAGE)
				|| exceptionMessage.contains(NOT_EMPTY_MESSAGE)
		);
	}

	@Test
	void deveRetornarBadRequestSomenteComAMensagemNotBlank() {
		final String exceptionMessage = assertThrows(BadRequest400Exception.class, () -> {
			validateDTOService.run(DtoForTest.builder().field(" ").build());
		}).getMessage();

		assertFalse(exceptionMessage.contains(NOT_NULL_MESSAGE) && exceptionMessage.contains(NOT_EMPTY_MESSAGE));
		assertTrue(exceptionMessage.contains(NOT_BLANK_MESSAGE));
	}

	@Test
	void deveRetornarBadRequestSomenteComAMensagemNotEmptyENotBlankQuandoAMensagemForEmpty() {
		final String exceptionMessage = assertThrows(BadRequest400Exception.class, () -> {
			validateDTOService.run(DtoForTest.builder().field("").build());
		}).getMessage();

		assertFalse(exceptionMessage.contains(NOT_NULL_MESSAGE));
		assertTrue(exceptionMessage.contains(NOT_BLANK_MESSAGE) || exceptionMessage.contains(NOT_EMPTY_MESSAGE));
	}
}
