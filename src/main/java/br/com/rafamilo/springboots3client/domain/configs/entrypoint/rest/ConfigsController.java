package br.com.rafamilo.springboots3client.domain.configs.entrypoint.rest;

import br.com.rafamilo.springboots3client.domain.i18n.services.GetMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configs")
@RequiredArgsConstructor
public class ConfigsController {

	private final GetMessageService getMessageService;

	@GetMapping("/get-application-status")
	public String getApplicationStatus(@RequestHeader("Accept-Language") String locale) {
		return getMessageService.run(locale, "configs.controller.getApplication.status");
	}
}
