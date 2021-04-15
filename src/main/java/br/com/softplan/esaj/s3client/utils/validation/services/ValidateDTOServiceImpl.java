package br.com.softplan.esaj.s3client.utils.validation.services;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Service;

import br.com.softplan.esaj.s3client.utils.entrypoint.exceptions.BadRequest400Exception;
import br.com.softplan.esaj.s3client.utils.string.StringUtils;

@Service
public class ValidateDTOServiceImpl implements ValidateDTOService {

	public <T> void run(final T dto) {
		throwError(getErrors(dto));
	}

	private void throwError(final Set<ConstraintViolation<Object>> errors) {
		final StringBuilder stringBuilder = new StringBuilder();
		if (!errors.isEmpty()) {
			errors.forEach(error -> stringBuilder.append(error.getMessage().concat("\n")));
			throw new BadRequest400Exception(stringBuilder.toString());
		}
	}

	private <T> Set<ConstraintViolation<Object>> getErrors(final T dto) {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		return validator.validate(dto);
	}
}
