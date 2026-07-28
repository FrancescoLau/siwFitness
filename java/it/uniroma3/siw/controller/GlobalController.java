package it.uniroma3.siw.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/*Per fare in modo che lo username sia sempre disponibile ai template, possiamo definire una classe, 
	 * annotata @ControlAdvice, che ci permette di associare un attributo al modello per tutte le richieste*/

@ControllerAdvice
public class GlobalController {
  
  @ModelAttribute("userDetails")
  public UserDetails getUser() {
    UserDetails user = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    return user;
  }
}

