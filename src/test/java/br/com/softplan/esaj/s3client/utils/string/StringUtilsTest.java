package br.com.softplan.esaj.s3client.utils.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ SpringExtension.class })
public class StringUtilsTest {

	@Test
	void deveRetornarTrueQuandoUmaStringForEmptyOuBlank() {
		Assertions.assertTrue(StringUtils.isEmptyOrBlank(""));
		Assertions.assertTrue(StringUtils.isEmptyOrBlank(" "));
	}

	@Test
	void deveRetornarFalseQuandoUmaStringNaoForEmptyENaoForBlank() {
		Assertions.assertFalse(StringUtils.isEmptyOrBlank("Foo Bar"));
		Assertions.assertFalse(StringUtils.isEmptyOrBlank(" Foo"));
		Assertions.assertFalse(StringUtils.isEmptyOrBlank("Bar "));
	}

	@Test
	void deveRemoverOsEspacosEmBrancoNoInicioENoFinalDaString() {
		final String correctString = "Foo Bar";
		Assertions.assertEquals(correctString, StringUtils.trim(" ".concat(correctString)));
		Assertions.assertEquals(correctString, StringUtils.trim(correctString.concat(" ")));
	}

	@Test
	void naoDeveRemoverOsEspacosEmBrancoEntreAString() {
		final String spaceBetween = "Foo Bar";
		Assertions.assertEquals(spaceBetween, StringUtils.trim("Foo Bar"));
	}
}
