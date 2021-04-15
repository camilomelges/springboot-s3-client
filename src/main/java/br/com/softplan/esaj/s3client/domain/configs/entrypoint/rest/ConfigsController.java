package br.com.softplan.esaj.s3client.domain.configs.entrypoint.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.softplan.esaj.s3client.domain.i18n.services.GetMessageService;
import br.com.softplan.esaj.s3client.utils.entrypoint.exceptions.ErrorInternal500Exception;

@RestController
@RequestMapping("/configs")
@RequiredArgsConstructor
public class ConfigsController {

	private final GetMessageService getMessageService;

	@GetMapping("/get-application-status")
	public String getApplicationStatus(@RequestHeader("Accept-Language") String locale) {
		try {
			return getMessageService.run(locale, "configs.controller.getApplication.status");
		} catch (Exception e) {
			throw new ErrorInternal500Exception(getMessageService.run(locale, "configs.controller.getApplication.status.error500"));
		}
	}
}
