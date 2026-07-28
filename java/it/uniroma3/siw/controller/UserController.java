package it.uniroma3.siw.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User; // Import per rigenerare il Principal
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validation.CredentialsValidator;
import it.uniroma3.siw.validation.UserValidator;
import jakarta.validation.Valid;

@Controller
public class UserController {
    
    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final CredentialsValidator credentialsValidator;
    
    public UserController(CredentialsService credentialsService, PasswordEncoder passwordEncoder, UserValidator userValidator, CredentialsValidator credentialsValidator) {
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
        this.userValidator=userValidator;
        this.credentialsValidator=credentialsValidator;
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("credentials") Credentials credentials, 
                               BindingResult credentialsBindingResult,
                               @Valid @ModelAttribute("user") it.uniroma3.siw.model.User user, 
                               BindingResult userBindingResult,
                               @RequestParam("confirmPassword") String confirmPassword, // Intercetta il campo di conferma della password
                               Model model) {
    	
    	// 1. Validazione dei modelli tramite i rispettivi Validator di Spring
        this.userValidator.validate(user, userBindingResult);
        this.credentialsValidator.validate(credentials, credentialsBindingResult);
        
    	// Controllo extra sulla coincidenza delle password
        if (credentials.getPassword() != null && !credentials.getPassword().equals(confirmPassword)) {
            credentialsBindingResult.rejectValue("password", "password.mismatch");
        }

     // 3. Se sono emersi errori sintattici (@Valid) o di business (Validator), interrompe la registrazione
        if (credentialsBindingResult.hasErrors() || userBindingResult.hasErrors()) {
            model.addAttribute("credentials", credentials);
            model.addAttribute("user", user);
            return "register"; 
        }
        
        try {
            credentials.setUser(user);
            String encodedPassword = passwordEncoder.encode(credentials.getPassword());
            credentials.setPassword(encodedPassword);
            credentials.setRole(Credentials.DEFAULT_ROLE);
            
            this.credentialsService.saveCredentials(credentials);
            return "redirect:/login?registered=true";
            
        } catch (Exception e) {
            credentialsBindingResult.reject("registration.duplicate", "L'username o l'email inseriti sono già registrati.");
            model.addAttribute("credentials", credentials);
            model.addAttribute("user", user);
            return "register";
        }
    }
    
    @GetMapping("/updateCredentials")
    public String showUpdateCredentials(Model model) {
        Object infoAttuali = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (infoAttuali instanceof UserDetails) {
            String currentUsername = ((UserDetails) infoAttuali).getUsername();
            Credentials credentials = this.credentialsService.getCredentials(currentUsername);
            
            if (credentials == null) {
                return "redirect:/login";
            }
            
            
            credentials.setPassword(""); // Svuotiamo la password per motivi di sicurezza nel form
            model.addAttribute("credentials", credentials);
            return "updateCredentials"; // Nome del file HTML del form di modifica
        }
        
        return "redirect:/login";
    }

    // RIATTIVATO: Processa il salvataggio dei dati modificati dall'utente
 // Processa il salvataggio dei dati modificati dall'utente richiedendo la vecchia password
 // Processa il salvataggio delle modifiche richiedendo vecchia password, nuova e conferma della nuova
    @PostMapping("/updateCredentials")
    public String updateCredentials(@ModelAttribute("credentials") Credentials formData,
                                    BindingResult bindingResult, 
                                    @RequestParam("oldPassword") String oldPassword,       // Recupera la vecchia psw dal form
                                    @RequestParam("confirmNewPassword") String confirmNewPassword, // Recupera la conferma della nuova psw
                                    Model model) {
    	
        try {
            Object infoAttuali = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String currentUsername = ((UserDetails) infoAttuali).getUsername();
            
            // 1. Recuperiamo le credenziali originali dell'utente dal database
            Credentials originalCredentials = this.credentialsService.getCredentials(currentUsername);
            
            // CONTROLLO 1: La vecchia password inserita deve corrispondere a quella registrata
            if (!passwordEncoder.matches(oldPassword, originalCredentials.getPassword())) {
                bindingResult.rejectValue("password", "oldPassword.invalid", "La vecchia password inserita non è corretta.");
                model.addAttribute("credentials", formData);
                return "updateCredentials";
            }
            
            // CONTROLLO 2: Se l'utente vuole cambiare password, la nuova e la conferma devono coincidere
            if (formData.getPassword() != null && !formData.getPassword().isEmpty()) {
                if (!formData.getPassword().equals(confirmNewPassword)) {
                    bindingResult.rejectValue("password", "newPassword.mismatch", "La nuova password e la conferma non coincidono.");
                    model.addAttribute("credentials", formData);
                    return "updateCredentials";
                }
                
                // Se coincidono, effettua la cifratura della nuova password
                String encodedPassword = passwordEncoder.encode(formData.getPassword());
                originalCredentials.setPassword(encodedPassword);
            }
            
            // 3. Aggiorna lo username con quello eventualmente modificato
            originalCredentials.setUsername(formData.getUsername());
            
            // 4. Salva le modifiche definitive nel database
            this.credentialsService.saveCredentials(originalCredentials);
            
            // 5. Aggiorna il Principal di Spring Security in memoria per riflettere le nuove credenziali senza disconnettere l'utente
            UserDetails newUserDetails = User.withUsername(originalCredentials.getUsername())
                    .password(originalCredentials.getPassword())
                    .authorities(((UserDetails) infoAttuali).getAuthorities())
                    .build();
            
            Authentication newAuth = new UsernamePasswordAuthenticationToken(newUserDetails, null, newUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            /*if(formData.getRole()== Credentials.ADMIN_ROLE) {
            	return "redirect:/admin/";
            }*/
            
            return "redirect:/";
            
            
        } catch (Exception e) {
            bindingResult.rejectValue("username", "credentials.duplicate", "Questo username è già registrato.");
            model.addAttribute("credentials", formData);
            return "updateCredentials";
        }
    }
}