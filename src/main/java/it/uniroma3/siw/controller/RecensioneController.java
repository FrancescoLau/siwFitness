package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.validation.RecensioneValidator;
import jakarta.validation.Valid;

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
    @GetMapping({"/allenamentoConsigliato/{id}/recensioni", "/allenamento/{id}/recensioni"})
    public String showRecensioni(@PathVariable("id") Long id, 
                                 Model model, 
                                 @AuthenticationPrincipal UserDetails userDetails) {
    	
        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento == null) {
            return "error/errorAllenamento";
        }
        
        List<Recensione> recensioni = this.recensioneService.findByAllenamentoConsigliato(allenamento);
        double mediaVoti = this.allenamentoService.getMediaVoti(allenamento);

        boolean haGiaRecensito = false;
        Recensione recensioneUtente = null;

        if (userDetails != null) {
            recensioneUtente = this.recensioneService.findByAllenamentoIdAndUsername(id, userDetails.getUsername());
            if (recensioneUtente != null) {
                haGiaRecensito = true;
            }
        }

        model.addAttribute("allenamento", allenamento);
        model.addAttribute("numeroRecensioni", recensioni != null ? recensioni.size() : 0);
        model.addAttribute("recensioni", recensioni);
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

        Recensione recensioneEsistente = this.recensioneService.findByAllenamentoIdAndUsername(id, userDetails.getUsername());
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
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento == null || !Boolean.TRUE.equals(allenamento.getIsPubblico())) {
            return "error/errorAllenamento";
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        
        recensione.setAllenamento(allenamento);
        recensione.setUtente(credentials.getUser());
        this.recensioneValidator.validate(recensione, recensioneBindingResult);

        if (recensioneBindingResult.hasErrors()) {
            model.addAttribute("allenamento", allenamento);
            return "formNewRecensione"; 
        }

        this.recensioneService.creaRecensione(recensione, allenamento, credentials.getUser());
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

        if (recensione == null || credentials == null || !recensione.getUtente().getId().equals(credentials.getUser().getId())) {
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

        if (originale == null || credentials == null || !originale.getUtente().getId().equals(credentials.getUser().getId())) {
            return "error/errorRecensione";
        }

        recensioneModificata.setId(id);
        recensioneModificata.setAllenamento(originale.getAllenamento());
        recensioneModificata.setUtente(credentials.getUser());
        this.recensioneValidator.validate(recensioneModificata, recensioneBindingResult);

        if (recensioneBindingResult.hasErrors()) {
            model.addAttribute("allenamento", originale.getAllenamento());
            return "editRecensione";
        }

        this.recensioneService.updateRecensione(id, recensioneModificata, credentials.getUser());
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

        if (recensione != null && credentials != null && 
           (recensione.getUtente().getId().equals(credentials.getUser().getId()) || Credentials.ADMIN_ROLE.equals(credentials.getRole()))) {
            Long allenamentoId = recensione.getAllenamento().getId();
            this.recensioneService.deleteById(id);
            return "redirect:/allenamentoConsigliato/" + allenamentoId + "/recensioni";
        }

        return "redirect:/";
    }
}