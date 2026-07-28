package it.uniroma3.siw.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validation.RecensioneValidator;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@Controller
public class RecensioneController {

    private final AllenamentoService allenamentoService;
    private final RecensioneService recensioneService;
    private final CredentialsService credentialsService;
    private final RecensioneValidator recensioneValidator;

    public RecensioneController(AllenamentoService allenamentoService, 
                                RecensioneService recensioneService, 
                                CredentialsService credentialsService, 
                                RecensioneValidator recensioneValidator) {
        this.allenamentoService = allenamentoService;
        this.recensioneService = recensioneService;
        this.credentialsService = credentialsService;
        this.recensioneValidator = recensioneValidator;
    }
    
 // 1. GET: Mostra il registro delle recensioni di un allenamento
    @GetMapping("/allenamentoConsigliato/{id}/recensioni")
    public String showRecensioni(@PathVariable("id") Long id, 
                                 Model model, 
                                 @AuthenticationPrincipal UserDetails userDetails) {
    	
        Allenamento allenamento = allenamentoService.findById(id);
        if (allenamento == null) {
            return "error/errorAllenamento";
        }
        
        double mediaVoti = 0.0;
        if (allenamento.getRecensioni() != null && !allenamento.getRecensioni().isEmpty()) {
            mediaVoti = allenamento.getRecensioni().stream().mapToInt(Recensione::getVoto).average().orElse(0.0);
        }

        boolean haGiaRecensito = false;
        Recensione recensioneUtente = null;

        // Controllo se l'utente loggato ha già recensito questo allenamento specifico
        if (userDetails != null) {
            String currentUsername = userDetails.getUsername();
            recensioneUtente = this.recensioneService.findByAllenamentoIdAndUsername(id, currentUsername);
            
            if (recensioneUtente != null) {
                haGiaRecensito = true;
            }
        }

        model.addAttribute("allenamento", allenamento);
        model.addAttribute("numeroRecensioni", allenamento.getRecensioni().size());
        model.addAttribute("recensioni", allenamento.getRecensioni());
        model.addAttribute("mediaVoti", mediaVoti);
        model.addAttribute("haGiaRecensito", haGiaRecensito); 
        model.addAttribute("recensioneUtente", recensioneUtente); 
        
        return "recensioniAllenamento";
    }

    // 2. GET: Mostra il form per scrivere una nuova recensione
    @GetMapping("/allenamentoConsigliato/{id}/nuovaRecensione")
    public String formNewRecensione(@PathVariable("id") Long id, 
                                    Model model, 
                                    @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento == null || !Boolean.TRUE.equals(allenamento.getIsPubblico())) {
            return "error/errorAllenamento";
        }

        // Sicurezza: se ha già recensito, impedisci l'accesso al form di creazione
        String currentUsername = userDetails.getUsername();
        Recensione recensioneEsistente = this.recensioneService.findByAllenamentoIdAndUsername(id, currentUsername);
        if (recensioneEsistente != null) {
            return "redirect:/allenamentoConsigliato/" + id + "/recensioni";
        }

        model.addAttribute("allenamento", allenamento);
        model.addAttribute("recensione", new Recensione());
        return "formNewRecensione";
    }

    // 3. POST: Salva la nuova recensione appena inserita
    @PostMapping("/allenamentoConsigliato/{id}/nuovaRecensione")
    public String saveRecensione(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("recensione") Recensione recensione,
                                 BindingResult recensioneBindingResult,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
    	this.recensioneValidator.validate(recensione, recensioneBindingResult);
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        
        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento == null || !Boolean.TRUE.equals(allenamento.getIsPubblico())) {
            return "error/errorAllenamento";
        }

        // Se ci sono errori di validazione base sintattica (campi vuoti impostati dalle annotazioni)
        if (recensioneBindingResult.hasErrors()) {
            model.addAttribute("allenamento", allenamento);
            model.addAttribute("recensione", recensione);
            return "formNewRecensione"; 
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());

        Recensione nuovaRecensione = new Recensione();
        nuovaRecensione.setUtente(credentials.getUser());
        nuovaRecensione.setAllenamento(allenamento);
        nuovaRecensione.setVoto(recensione.getVoto());
        nuovaRecensione.setTesto(recensione.getTesto());
        nuovaRecensione.setDataCreazione(LocalDateTime.now());
        this.recensioneValidator.validate(recensione, recensioneBindingResult);
        // Sincronizza la cache in memoria per Thymeleaf
        allenamento.getRecensioni().add(nuovaRecensione);

        this.recensioneService.save(nuovaRecensione);
        return "redirect:/allenamentoConsigliato/" + id + "/recensioni";
    }

    // 4. GET: Carica il form di modifica di una recensione esistente
    @GetMapping("/recensione/edit/{id}")
    public String formEditRecensione(@PathVariable("id") Long id, 
                                     @AuthenticationPrincipal UserDetails userDetails, 
                                     Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Recensione recensione = this.recensioneService.findById(id);
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());

        // Sicurezza: controllo esistenza e proprietà della recensione
        if (recensione == null || !recensione.getUtente().getId().equals(credentials.getUser().getId())) {
            return "error/errorRecensione";
        }

        model.addAttribute("recensione", recensione);
        model.addAttribute("allenamento", recensione.getAllenamento());
        return "editRecensione";
    }

    // 5. POST: Salva le modifiche apportate alla recensione
    @PostMapping("/recensione/edit/{id}")
    public String updateRecensione(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("recensione") Recensione recensioneModificata,
                                   BindingResult recensioneBindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Recensione originale = this.recensioneService.findById(id);
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());

        if (originale == null || !originale.getUtente().getId().equals(credentials.getUser().getId())) {
            return "error/errorRecensione";
        }

        if (recensioneBindingResult.hasErrors()) {
            model.addAttribute("allenamento", originale.getAllenamento());
            return "formEditRecensione"; // Ritorna la vista direttamente senza fare redirect per non perdere gli errori
        }

        originale.setVoto(recensioneModificata.getVoto());
        originale.setTesto(recensioneModificata.getTesto());
        originale.setDataModifica(LocalDateTime.now());
        this.recensioneValidator.validate(originale, recensioneBindingResult);
        this.recensioneService.save(originale);
        return "redirect:/allenamentoConsigliato/" + originale.getAllenamento().getId() + "/recensioni";
    }

    // 6. POST: Cancella una recensione dal sistema
    @PostMapping("/deleteRecensione/{id}")
    public String deleteRecensione(@PathVariable("id") Long id, 
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Recensione recensione = this.recensioneService.findById(id);
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());

        if (recensione.getUtente().getId().equals(credentials.getUser().getId()) || Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            Long allenamentoId = recensione.getAllenamento().getId();
            recensione.getAllenamento().getRecensioni().remove(recensione);
            this.recensioneService.deleteById(id);
            return "redirect:/allenamentoConsigliato/" + allenamentoId + "/recensioni";
        }

        return "redirect:/";
    }
}