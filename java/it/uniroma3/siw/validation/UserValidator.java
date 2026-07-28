package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Component
public class UserValidator implements Validator {
    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target; 

        // Nome
        if (user.getNome() == null || user.getNome().isBlank()) {
            errors.rejectValue("nome", "NotBlank.user.nome");
        }

        // Cognome
        if (user.getCognome() == null || user.getCognome().isBlank()) {
            errors.rejectValue("cognome", "NotBlank.user.cognome");
        }
        
        // Email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            errors.rejectValue("email", "NotBlank.user.email");
        } else if (this.userService.findByEmail(user.getEmail()) != null) { 
            errors.rejectValue("email", "user.email.duplicate");
        }
    }
	
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }
}