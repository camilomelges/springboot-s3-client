package br.com.softplan.esaj.s3client.utils.validation.services;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Service;

import br.com.softplan.esaj.s3client.utils.entrypoint.exceptions.BadRequest400Exception;

@Service
public class ValidateDTOServiceImpl implements ValidateDTOService {

	public <T> void run(final T dto) {
		throwError(getErrors(dto));
	}

	private void throwError(final Set<ConstraintViolation<Object>> errors) {
		if (!errors.isEmpty()) {
			errors.forEach(error -> {
				throw new BadRequest400Exception(error.getMessage());
			});
		}
	}

	private <T> Set<ConstraintViolation<Object>> getErrors(final T dto) {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		return validator.validate(dto);
	}
}
