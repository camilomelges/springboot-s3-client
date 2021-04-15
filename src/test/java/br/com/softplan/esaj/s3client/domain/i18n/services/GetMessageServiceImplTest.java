package br.com.softplan.esaj.s3client.domain.i18n.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GetMessageServiceImplTest {

	@InjectMocks
	private GetMessageServiceImpl getMessageService;

	private static final String TEST_MENSAGEM_PT_BR = "Sem mensagem.";
	private static final String TEST_MENSAGEM_ES_CO = "Sin mensaje.";
	private static final String TEST_DEFAULT_ERROR_PT_BR = "Ocorreu um erro na tradução da mensagem.";
	private static final String TEST_DEFAULT_ERROR_ES_CO = "Hubo un error al traducir el mensaje.";
	private static final String PT_BR = "pt_BR";
	private static final String ES_CO = "es_CO";

	@Test
	void deveRetornarUmaMensagemDefaultQuandoNaoEncontrarAPropertyKey() {
		final String property = "foobar";
		Assertions.assertEquals(TEST_DEFAULT_ERROR_PT_BR, getMessageService.run(PT_BR, property));
		Assertions.assertEquals(TEST_DEFAULT_ERROR_ES_CO, getMessageService.run(ES_CO, property));
	}

	@Test
	void deveRetornarAsMensagensDeAcordoComOLocale() {
		final String property = "teste.mensagem";
		Assertions.assertEquals(TEST_MENSAGEM_PT_BR, getMessageService.run(PT_BR, property));
		Assertions.assertEquals(TEST_MENSAGEM_ES_CO, getMessageService.run(ES_CO, property));
	}
}
