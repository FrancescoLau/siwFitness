package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.service.AllenamentoService;

@Component
public class AllenamentoValidator implements Validator {

    // Limite dimensione immagine: 5 MB in byte
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; 

    private final AllenamentoService allenamentoService;

    public AllenamentoValidator(AllenamentoService allenamentoService) {
        this.allenamentoService = allenamentoService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Allenamento.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Allenamento allenamento = (Allenamento) target;
        
        // 1. Controllo lunghezza descrizione (con protezione da null)
        if (allenamento.getDescrizione() != null && allenamento.getDescrizione().length() > 1000) {
            errors.rejectValue("descrizione", "Size.allenamento.descrizione");
        }
        
        // 2. Controllo nome (lunghezza e duplicati)
        if (allenamento.getNome() != null && !allenamento.getNome().isBlank()) {
            
            if (allenamento.getNome().length() > 100) {
                errors.rejectValue("nome", "Size.allenamento.nome");
            }
            
            Allenamento esistente = this.allenamentoService.findByNome(allenamento.getNome());
            
            // Se esiste un allenamento con lo stesso nome, ma ha un ID diverso da quello corrente, è un duplicato
            if (esistente != null && !esistente.getId().equals(allenamento.getId())) {
                errors.rejectValue("nome", "allenamento.duplicate");
            }
        }
    }

    /**
     * Metodo helper per validare la dimensione dell'immagine caricata via form
     */
    public void validateImage(MultipartFile imageFile, Errors errors) {
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageFile.getSize() > MAX_IMAGE_SIZE) {
                errors.rejectValue("immagine", "image.tooLarge");
            }
        }
    }
}