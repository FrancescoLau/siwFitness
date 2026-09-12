package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validation.CredentialsValidator;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	private final AllenamentoService allenamentoService;
	private final CredentialsService credentialsService; 
	private final PasswordEncoder passwordEncoder;       

	public HomeController(AllenamentoService allenamentoService, 
	                      CredentialsService credentialsService, 
	                      PasswordEncoder passwordEncoder) {
	    this.allenamentoService = allenamentoService;
	    this.credentialsService = credentialsService;
	    this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("/")
    public String homepage(Model model) {
        
        
        // Prendi al massimo i primi 3 elementi per la homepage (come da specifiche delle slide)
        List<Allenamento> primiTre = allenamentoService.findTop3By();
        // Cambiato il nome dell'attributo in "allenamenti" per combaciare con l'HTML
       
        model.addAttribute("allenamenti", primiTre);
        
        return "homepage";
    }
	
	@GetMapping("/login")
	public String login() {
		
		return "login";
	}

	
	
	@GetMapping("/register")
	public String register(Model model){
		Credentials credentials=new Credentials();
		User user=new User();
		credentials.setUser(user);
		model.addAttribute("user", user);
		model.addAttribute("credentials", credentials);
		return "register";
	}
	
	
}