package it.uniroma3.siw.controller.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.service.AllenamentoService;

@RestController
@RequestMapping("/rest/allenamenti")
@CrossOrigin(origins = "http://localhost:5173")
public class AllenamentoRestController {

    private final AllenamentoService allenamentoService;

    public AllenamentoRestController(AllenamentoService allenamentoService) {
        this.allenamentoService = allenamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Allenamento>> getAllenamenti() {
        List<Allenamento> allenamenti = (List<Allenamento>) this.allenamentoService.findAll();
        return ResponseEntity.ok(allenamenti);
    }
}