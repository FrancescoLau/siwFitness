package it.uniroma3.siw.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
@Transactional
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    
    public RecensioneService(RecensioneRepository recensioneRepository) {
        this.recensioneRepository = recensioneRepository;
    }

    public Recensione save(Recensione recensione) {
        return this.recensioneRepository.save(recensione);
    }

    public Recensione creaRecensione(Recensione recensione, Allenamento allenamento, User utente) {
        recensione.setAllenamento(allenamento);
        recensione.setUtente(utente);
        recensione.setDataCreazione(LocalDateTime.now());
        return this.recensioneRepository.save(recensione);
    }

    public boolean updateRecensione(Long id, Recensione datiAggiornati, User utente) {
        Recensione esistente = this.findById(id);
        if (esistente == null || !esistente.getUtente().getId().equals(utente.getId())) {
            return false;
        }
        esistente.setVoto(datiAggiornati.getVoto());
        esistente.setTesto(datiAggiornati.getTesto());
        esistente.setDataModifica(LocalDateTime.now());
        this.recensioneRepository.save(esistente);
        return true;
    }

    @Transactional(readOnly = true)
    public Recensione findById(Long id) {
        return this.recensioneRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Recensione> findByAllenamentoConsigliato(Allenamento allenamento) {
        return this.recensioneRepository.findByAllenamento(allenamento);
    }

    @Transactional(readOnly = true)
    public List<Recensione> findByUtente(User utente) {
        return this.recensioneRepository.findByUtente(utente);
    }

    public void deleteById(Long id) {
        this.recensioneRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Recensione> findAll() {
        return (List<Recensione>) this.recensioneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsByUtenteAndAllenamento(User utente, Allenamento allenamento) {		
        return this.recensioneRepository.existsByUtenteAndAllenamento(utente, allenamento);
    }

    @Transactional(readOnly = true)
    public Recensione findByAllenamentoIdAndUsername(Long allenamentoId, String username) {
        return this.recensioneRepository.findByAllenamentoIdAndUsername(allenamentoId, username).orElse(null);
    }
}