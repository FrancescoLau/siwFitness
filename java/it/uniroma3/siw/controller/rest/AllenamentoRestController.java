package it.uniroma3.siw.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;

@RestController
@RequestMapping("/rest/allenamenti")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AllenamentoRestController {

    private final AllenamentoService allenamentoService;
    private final CredentialsService credentialsService; // 👈 CORRETTO: Iniettato il servizio credenziali

    public AllenamentoRestController(AllenamentoService allenamentoService, CredentialsService credentialsService) {
        this.allenamentoService = allenamentoService;
        this.credentialsService = credentialsService;
    }

    /**
     * GET /rest/allenamenti/consigliati
     * Restituisce unicamente gli allenamenti pubblici in formato JSON per React.
     */
    @GetMapping("/consigliati")
    public ResponseEntity<List<Allenamento>> getAllenamentiConsigliati() {
        try {
            List<Allenamento> pubblici = this.allenamentoService.findAll().stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsPubblico()))
                    .toList();
            return ResponseEntity.ok(pubblici);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /rest/allenamenti/me
     * Restituisce lo stato dell'utente attualmente autenticato
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();

        if (userDetails == null) {
            response.put("isAuthenticated", false);
            response.put("role", "ANONYMOUS");
            return ResponseEntity.ok(response);
        }

        try {
            Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
            if (credentials == null) {
                response.put("isAuthenticated", false);
                response.put("role", "ANONYMOUS");
                return ResponseEntity.ok(response);
            }

            response.put("isAuthenticated", true);
            response.put("username", credentials.getUsername());
            response.put("role", credentials.getRole());

            if (credentials.getUser() != null) {
                response.put("nome", credentials.getUser().getNome());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("isAuthenticated", false);
            response.put("role", "ANONYMOUS");
            return ResponseEntity.ok(response);
        }
    }
}