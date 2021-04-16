package br.com.softplan.esaj.s3client.domain.s3.components.generateuniquename;

import java.util.UUID;

import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class GenerateUniqueNameComponent {

	public String run() {
		String ts = String.valueOf(System.currentTimeMillis());
		String rand = UUID.randomUUID().toString();
		return Sha512DigestUtils.shaHex(ts + rand);
	}
}
