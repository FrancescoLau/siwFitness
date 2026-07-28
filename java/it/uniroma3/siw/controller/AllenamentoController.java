package it.uniroma3.siw.controller;

import java.time.LocalTime;
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
import org.springframework.web.multipart.MultipartFile;

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
    public String getAllenamenti(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        
        if (credentials == null || credentials.getUser() == null) {
            return "redirect:/login?error=true";
        }
        
        User currentUser = credentials.getUser();
        
        model.addAttribute("allenamenti", currentUser.getAllenamenti());
        model.addAttribute("totaleAllenamenti", currentUser.getAllenamenti().size());
        
        return "allenamenti";
    }

    /* GET: Pagina dei dettagli di un allenamento */
    @GetMapping("/allenamento/{id}")
    public String getAllenamento(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id);
        
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
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        this.allenamentoValidator.validate(allenamento, bindingResult);
        this.allenamentoValidator.validateImage(imageFile, bindingResult);

        if (bindingResult.hasErrors()) {
            return "formNewAllenamento";
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        allenamento.setUtente(credentials.getUser());
        allenamento.setIsPubblico(false);

        this.allenamentoService.save(allenamento, imageFile);
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
                                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                            Model model,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        
        this.allenamentoValidator.validate(allenamento, bindingResult);
        this.allenamentoValidator.validateImage(imageFile, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/formNewAllenamentoConsigliato";
        }
                
        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        if (credentials != null) {
            allenamento.setUtente(credentials.getUser());
        }
        
        allenamento.setIsPubblico(true);
        this.allenamentoService.save(allenamento, imageFile);
       
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
        
        if (allenamento == null || allenamento.getUtente() == null || 
            !allenamento.getUtente().getId().equals(credentials.getUser().getId())) {
            return "error/errorAllenamento";
        }

        model.addAttribute("allenamento", allenamento);
        return "editAllenamento"; 
    }

    /* POST: Modifica allenamento privato (UTENTE) - Include il controllo sui 5MB dell'immagine */
    @PostMapping("/editAllenamento/{id}")
    public String updateAllenamento(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                    Model model) { 
        
        allenamento.setId(id);
        this.allenamentoValidator.validate(allenamento, bindingResult);
        this.allenamentoValidator.validateImage(imageFile, bindingResult);

        // Se ci sono errori (incluso il superamento dei 5MB dell'immagine), ricarica la pagina di modifica
        if (bindingResult.hasErrors()) {
            return "editAllenamento"; 
        }

        Allenamento allenamentoEsistente = this.allenamentoService.findById(id);

        if (allenamentoEsistente != null) {
            allenamentoEsistente.setNome(allenamento.getNome());
            allenamentoEsistente.setTipoSport(allenamento.getTipoSport());
            allenamentoEsistente.setData(allenamento.getData());
            allenamentoEsistente.setDurata(allenamento.getDurata());
            allenamentoEsistente.setLivelloDifficolta(allenamento.getLivelloDifficolta());
            allenamentoEsistente.setDescrizione(allenamento.getDescrizione());
            allenamentoEsistente.setIsPubblico(false);

            this.allenamentoService.save(allenamentoEsistente, imageFile);
        }
        
        else if(allenamentoEsistente==null) {
        	return "error/errorAllenamento";
        }
        
        return "redirect:/allenamenti";
    }
    
    /* GET: Form modifica allenamento consigliato (ADMIN) */
    @GetMapping("/admin/editAllenamentoConsigliato/{id}")
    public String editAllenamentoConsigliato(@PathVariable("id") Long id, Model model) {
        Allenamento allenamentoConsigliato = allenamentoService.findById(id);
        
        if (allenamentoConsigliato == null || Boolean.FALSE.equals(allenamentoConsigliato.getIsPubblico())) {
            return "error/errorAllenamento";
        }
        
        model.addAttribute("allenamento", allenamentoConsigliato);
        return "admin/editAllenamentoConsigliato"; 
    }

    /* POST: Modifica allenamento consigliato (ADMIN) - Include il controllo sui 5MB dell'immagine */
    @PostMapping("/admin/editAllenamentoConsigliato/{id}")
    public String updateAllenamentoConsigliato(@PathVariable("id") Long id,
                                               @Valid @ModelAttribute("allenamento") Allenamento allenamento,
                                               BindingResult bindingResult,
                                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                               Model model) {

        allenamento.setId(id);
        this.allenamentoValidator.validate(allenamento, bindingResult);

        // Controllo dimensione: se l'immagine supera i 5MB, lancia l'eccezione per aprire errorImage.html
        if (imageFile != null && !imageFile.isEmpty() && imageFile.getSize() > (5 * 1024 * 1024)) {
            throw new it.uniroma3.siw.exception.ImageUploadException("⚠️ L'immagine selezionata supera la dimensione massima consentita di 5MB!");
        }

        this.allenamentoValidator.validateImage(imageFile, bindingResult);

        // Se ci sono altri errori di validazione sui campi di testo, ricarica la pagina di modifica admin
        if (bindingResult.hasErrors()) {
            return "admin/editAllenamentoConsigliato";
        }

        Allenamento allenamentoEsistente = this.allenamentoService.findById(id);

        if (allenamentoEsistente != null) {
            allenamentoEsistente.setNome(allenamento.getNome());
            allenamentoEsistente.setTipoSport(allenamento.getTipoSport());
            allenamentoEsistente.setData(allenamento.getData());
            allenamentoEsistente.setDurata(allenamento.getDurata());
            allenamentoEsistente.setLivelloDifficolta(allenamento.getLivelloDifficolta());
            allenamentoEsistente.setDescrizione(allenamento.getDescrizione());
            allenamentoEsistente.setIsPubblico(true);

            this.allenamentoService.save(allenamentoEsistente, imageFile); // nota: usa imageFile
        }

        return "redirect:/allenamentiConsigliati";
    }
    
    /* GET: Recensioni di un allenamento consigliato */
    @GetMapping("/allenamento/{id}/recensioni")
    public String getRecensioniAllenamentoConsigliato(@PathVariable("id") Long id, Model model) {
        Allenamento allenamento = this.allenamentoService.findById(id);
        if (allenamento != null && Boolean.TRUE.equals(allenamento.getIsPubblico())) {
            List<Recensione> recensioniAllenamento = recensioneService.findByAllenamentoConsigliato(allenamento);
            model.addAttribute("Allenamento", allenamento);
            model.addAttribute("recensioni", recensioniAllenamento);
            
            return "recensioniAllenamento";
        }
        return "redirect:/";
    }

    /* GET: Lista tutti gli allenamenti consigliati - ORA GESTITA DA REACT */
    @GetMapping("/allenamentiConsigliati")
    public String getAllenamentiConsigliati() {
        return "forward:/index.html"; // Redirige all'app React
    }
}
