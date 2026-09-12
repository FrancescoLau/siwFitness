package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
@Service
public class CredentialsService {
	private CredentialsRepository credentialsRepository;
	
	public CredentialsService(CredentialsRepository credentialsRepository) {
		this.credentialsRepository = credentialsRepository;
	}
	
	public Credentials getCredentials(String username) {
		return this.credentialsRepository.findByUsername(username).orElse(null);
	}
	public Credentials saveCredentials(Credentials credentials) {
		return this.credentialsRepository.save(credentials);
	}

}
