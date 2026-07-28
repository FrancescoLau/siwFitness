package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.repository.RecensioneRepository;
import java.util.List;

@Service
public class RecensioneService {

    private RecensioneRepository recensioneRepository;
    
    public RecensioneService(RecensioneRepository recensioneRepository) {
    	this.recensioneRepository=recensioneRepository;
    }

    private List<Recensione> whereCondiction(List<Recensione> recensioni) {
    	for (Recensione recensione : recensioni) {
    		if (!recensione.getAllenamento().getIsPubblico()) {
    			recensioni.remove(recensione);
    		}
    	}
    	return recensioni;
    }
        
    public Recensione save(Recensione recensione) {
        return recensioneRepository.save(recensione);
    }
    public Recensione findById(Long id) {
        return recensioneRepository.findById(id).orElse(null);
    }

    public List<Recensione> findByAllenamentoConsigliato(Allenamento Allenamento) {
        return whereCondiction(recensioneRepository.findByAllenamento(Allenamento));
    }

    public List<Recensione> findByUtente(User utente) {
        return whereCondiction(recensioneRepository.findByUtente(utente));
    }

    public void deleteById(Long id) {
        recensioneRepository.deleteById(id);
    }
    
    
    // **NUOVO METODO: Trova tutte le recensioni (per admin)**
    public List<Recensione> findAll() {
        return (List<Recensione>) recensioneRepository.findAll();
    }

	public boolean existsByUtenteAndAllenamento(User utente, Allenamento allenamento) {		
		return recensioneRepository.existsByUtenteAndAllenamento(utente, allenamento);
	}

	public Recensione findByAllenamentoIdAndUsername(Long allenamentoId, String username) {
	    return this.recensioneRepository.findByAllenamentoIdAndUsername(allenamentoId, username);
	}
}
