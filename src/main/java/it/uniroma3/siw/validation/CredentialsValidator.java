package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class CredentialsValidator implements Validator {

    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;

    public CredentialsValidator(CredentialsService credentialsService, PasswordEncoder passwordEncoder) {
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Credentials.class.equals(aClass); // Ritorna se la classe è supportata
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials credentials = (Credentials) target;
       
        // 1. Controllo Username vuoto
        if (credentials.getUsername() == null || credentials.getUsername().isBlank()) {
            errors.rejectValue("username", "NotNull.credentials.username");
        } 
        // 2. Controllo duplicati (FONDAMENTALE per la registrazione)
        else if (this.credentialsService.getCredentials(credentials.getUsername()) != null) {
            errors.rejectValue("username", "credentials.username.duplicate");
        }

        // 3. Controllo Password vuota
        if (credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            errors.rejectValue("password", "NotNull.credentials.password");
        } 
        // 4. Controllo lunghezza minima (Opzionale ma consigliato)
        else if ((credentials.getPassword().length() < 4)&&(!credentials.getPassword().isBlank())) {
            errors.rejectValue("password", "Size.credentials.password");
        }
    }
}