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
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.validation.AllenamentoValidator;
import jakarta.validation.Valid;

@Controller
public class AllenamentoController {

    private final AllenamentoService allenamentoService;
    private final CredentialsService credentialsService;
    private final RecensioneService recensioneService;
    private final AllenamentoValidator allenamentoValidator;
   
    public AllenamentoController(AllenamentoService allenamentoService, 
                                 CredentialsService credentialsService, 
                                 RecensioneService recensioneService, 
                                 AllenamentoValidator allenamentoValidator) {
        this.allenamentoService = allenamentoService;
        this.credentialsService = credentialsService;
        this.recensioneService = recensioneService;
        this.allenamentoValidator = allenamentoValidator;
    }
 
    /* GET: Ottenere la pagina degli allenamenti privati dell'utente */
    @GetMapping("/allenamenti")
    public String getAllenamenti(@AuthenticationPrincipal UserDetails userDetails, 
                                 @RequestParam(value = "keyword", required = false) String keyword, 
                                 Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        if (credentials == null || credentials.getUser() == null) {
            return "redirect:/login?error=true";
        }
        
        User currentUser = credentials.getUser();
        model.addAttribute("allenamenti", this.allenamentoService.findAllenamentiFiltrati(currentUser, keyword));
        model.addAttribute("totaleAllenamenti", this.allenamentoService.countByUtente(currentUser));
        model.addAttribute("keyword", keyword);
        return "allenamenti";
    }

    /* GET: Pagina dei dettagli di un allenamento */
    @GetMapping("/allenamento/{id}")
    public String getAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento == null) {
            return "error/errorAllenamento";
        }
        model.addAttribute("allenamento", allenamento);
        return "allenamento";
    }

    /* GET: Form per nuovo allenamento privato */
    @GetMapping("/formNewAllenamento")
    public String formNewAllenamento(Model model) {
        model.addAttribute("allenamento", new Allenamento());
        return "formNewAllenamento";
    }

    /* POST: Salva nuovo allenamento privato (UTENTE) */
    @PostMapping("/saveAllenamento")
    public String saveAllenamento(@Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                  BindingResult bindingResult,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        this.allenamentoValidator.validate(allenamento, bindingResult);
        if (bindingResult.hasErrors()) {
            return "formNewAllenamento";
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        this.allenamentoService.creaAllenamentoUtente(allenamento, credentials.getUser());
        return "redirect:/allenamenti";
    }
    
    /* POST: Cancella un allenamento */
    @PostMapping("/deleteAllenamento/{id}")
    public String deleteAllenamento(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        this.allenamentoService.deleteById(id);
        
        if (userDetails != null) {
            Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
            if (credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
                return "redirect:/allenamentiConsigliati";
            }
        }
        return "redirect:/";
    }
    
    /* GET: Form nuovo allenamento CONSIGLIATO (ADMIN) */
    @GetMapping("/admin/formNewAllenamentoConsigliato")
    public String formNewAllenamentoConsigliato(Model model) {
        model.addAttribute("allenamento", new Allenamento());
        return "admin/formNewAllenamentoConsigliato";
    }
    
    /* POST: Salva nuovo allenamento CONSIGLIATO (ADMIN) */
    @PostMapping("/admin/formNewAllenamentoConsigliato")
    public String newAllenamentoConsigliato(@Valid @ModelAttribute("allenamento") Allenamento allenamento, 
                                            BindingResult bindingResult, 
                                            Model model,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        
        this.allenamentoValidator.validate(allenamento, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/formNewAllenamentoConsigliato";
        }
                
        Credentials credentials = (userDetails != null) ? this.credentialsService.getCredentials(userDetails.getUsername()) : null;
        User adminUser = (credentials != null) ? credentials.getUser() : null;
        
        this.allenamentoService.creaAllenamentoConsigliato(allenamento, adminUser);
        return "redirect:/allenamentiConsigliati";
    }
    
    /* GET: Form modifica allenamento privato (UTENTE) */
    @GetMapping("/editAllenamento/{id}")
    public String editAllenamento(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Allenamento allenamento = this.allenamentoService.findById(id);
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        
        if (allenamento == null || credentials == null || !this.allenamentoService.isProprietario(allenamento, credentials.getUser())) {
            return "error/errorAllenamento";
        }

        model.addAttribute("allenamento", allenamento);
        return "editAllenamento"; 
    }

    /* POST: Aggiorna allenamento privato (UTENTE) */
    @PostMapping("/editAllenamento/{id}")
    public String updateAllenamento(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) { 
        
        allenamento.setId(id);
        this.allenamentoValidator.validate(allenamento, bindingResult);

        if (bindingResult.hasErrors()) {
            return "editAllenamento"; 
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        boolean aggiornato = this.allenamentoService.updateAllenamentoUtente(id, allenamento, credentials.getUser());
        
        if (!aggiornato) {
            return "error/errorAllenamento";
        }
        
        return "redirect:/allenamenti";
    }
    
    /* GET: Form modifica allenamento consigliato (ADMIN) */
    @GetMapping("/admin/editAllenamentoConsigliato/{id}")
    public String editAllenamentoConsigliato(@PathVariable("id") Long id, Model model) {
        Allenamento allenamentoConsigliato = this.allenamentoService.findById(id);
        
        if (allenamentoConsigliato == null || Boolean.FALSE.equals(allenamentoConsigliato.getIsPubblico())) {
            return "error/errorAllenamento";
        }
        
        model.addAttribute("allenamento", allenamentoConsigliato);
        return "admin/editAllenamentoConsigliato"; 
    }

    /* POST: Aggiorna allenamento consigliato (ADMIN) */
    @PostMapping("/admin/editAllenamentoConsigliato/{id}")
    public String updateAllenamentoConsigliato(@PathVariable("id") Long id,
                                               @Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                               BindingResult bindingResult,
                                               Model model) {
        allenamento.setId(id);
        this.allenamentoValidator.validate(allenamento, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/editAllenamentoConsigliato";
        }

        boolean aggiornato = this.allenamentoService.updateAllenamentoConsigliato(id, allenamento);
        if (!aggiornato) {
            return "error/errorAllenamento";
        }

        return "redirect:/allenamentiConsigliati";
    }
    
    

    /* GET: Lista tutti gli allenamenti consigliati (Thymeleaf, accessibile ad anonimi e loggati) */
    @GetMapping("/allenamentiConsigliati")
    public String getAllenamentiConsigliati(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
            if (credentials != null) {
                model.addAttribute("currentUser", credentials.getUser());
                model.addAttribute("currentCredentials", credentials);
            }
        }

        List<Object[]> allenamentiConsigliati = this.allenamentoService.findAllenamentiWithRecensioni();
        
        model.addAttribute("allenamentiConsigliati", allenamentiConsigliati);
        return "allenamentiConsigliati";
    }
}