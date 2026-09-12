package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.RecensioneService;

@Component
public class RecensioneValidator implements Validator {

    private final RecensioneService recensioneService;

    public RecensioneValidator(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Recensione.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Recensione recensione = (Recensione) target;
        
        
        if (recensione.getAllenamento() != null) {
            // BLOCCO 1: Se l'allenamento NON è pubblico, rifiuta immediatamente la recensione
            if ((recensione.getAllenamento().getIsPubblico())==false) {
                errors.reject("recensione.non.pubblica", "Non puoi recensire un allenamento privato.");
                return;
            }
        }

        if (recensione.getUtente() != null && recensione.getAllenamento() != null) {
            boolean giaRecensito = this.recensioneService.existsByUtenteAndAllenamento(
                recensione.getUtente(), 
                recensione.getAllenamento()
            );

            if (giaRecensito && recensione.getId() == null) {
                errors.reject("recensione.duplicata", "Hai già lasciato una recensione per questo allenamento.");
            }
        }
    }
}