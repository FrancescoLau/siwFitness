package it.uniroma3.siw.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.AllenamentoRepository;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
@Transactional
public class AllenamentoService {
    
    private final AllenamentoRepository allenamentoRepository;
    private final RecensioneRepository recensioneRepository;
    
    public AllenamentoService(AllenamentoRepository allenamentoRepository, RecensioneRepository recensioneRepository) {
        this.allenamentoRepository = allenamentoRepository;
        this.recensioneRepository = recensioneRepository;
    }

    public Allenamento save(Allenamento allenamento) {
        return this.allenamentoRepository.save(allenamento);
    }

    // Caso d'uso: Creazione di un allenamento privato associato all'utente
    public Allenamento creaAllenamentoUtente(Allenamento allenamento, User utente) {
        allenamento.setUtente(utente);
        allenamento.setIsPubblico(false);
        return this.allenamentoRepository.save(allenamento);
    }

    // Caso d'uso: Creazione di un allenamento consigliato pubblico
    public Allenamento creaAllenamentoConsigliato(Allenamento allenamento, User admin) {
        allenamento.setUtente(admin);
        allenamento.setIsPubblico(true);
        return this.allenamentoRepository.save(allenamento);
    }
    
    @Transactional(readOnly = true)
    public Integer countByIsPubblico() {
        return this.allenamentoRepository.countByIsPubblico(true);
    }
    
    @Transactional(readOnly = true)
    public Allenamento findById(Long id) {
        return this.allenamentoRepository.findById(id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findByUtente(User utente) {
        return this.allenamentoRepository.findByUtente(utente);
    }    
    
    @Transactional(readOnly = true)
    public List<Allenamento> findByTipoSport(String tipoSport) {
        return this.allenamentoRepository.findByTipoSport(tipoSport);
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport) {
        return this.allenamentoRepository.findByUtenteAndTipoSport(utente, tipoSport);
    }
    
    @Transactional(readOnly = true)
    public Integer countByUtente(User utente) {
        return this.allenamentoRepository.countByUtente(utente);
    }
    
    public void deleteById(Long id) {
        this.allenamentoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findAll() {
        return this.allenamentoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findAllenamentiFiltrati(User utente, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return this.allenamentoRepository.findByUtente(utente);
        }
        return this.allenamentoRepository.allenamentiRicerca(utente, keyword);
    }
    
    @Transactional(readOnly = true)
    public int getTotaleRecensioni(Allenamento allenamento) {
        if (allenamento == null) {
            return 0;
        }
        Long totale = this.recensioneRepository.countByAllenamento(allenamento);
        return (totale != null) ? totale.intValue() : 0;
    }
    
    @Transactional(readOnly = true)
    public double getMediaVoti(Allenamento allenamento) {
        if (allenamento == null) {
            return 0.0;
        }
        Double media = this.recensioneRepository.findMediaVotiByAllenamento(allenamento);
        return (media != null) ? media : 0.0;
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findTop3By() {
        return this.allenamentoRepository.findTop3By();
    }

    @Transactional(readOnly = true)
    public Allenamento findByNome(String nome) {
        return this.allenamentoRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public boolean existsByNome(String nome) {
        return this.allenamentoRepository.existsByNome(nome);
    }

    // Regola di Business: verifica se l'utente corrente è proprietario dell'allenamento
    @Transactional(readOnly = true)
    public boolean isProprietario(Allenamento allenamento, User utente) {
        if (allenamento == null || utente == null || allenamento.getUtente() == null) {
            return false;
        }
        return allenamento.getUtente().getId().equals(utente.getId());
    }

    // Caso d'uso: Aggiornamento allenamento privato dell'utente
    public boolean updateAllenamentoUtente(Long id, Allenamento datiAggiornati, User utente) {
        Allenamento allenamentoEsistente = this.findById(id);
        if (allenamentoEsistente == null || !isProprietario(allenamentoEsistente, utente)) {
            return false;
        }

        allenamentoEsistente.setNome(datiAggiornati.getNome());
        allenamentoEsistente.setTipoSport(datiAggiornati.getTipoSport());
        allenamentoEsistente.setData(datiAggiornati.getData());
        allenamentoEsistente.setDurata(datiAggiornati.getDurata());
        allenamentoEsistente.setLivelloDifficolta(datiAggiornati.getLivelloDifficolta());
        allenamentoEsistente.setDescrizione(datiAggiornati.getDescrizione());
        allenamentoEsistente.setIsPubblico(false);

        this.allenamentoRepository.save(allenamentoEsistente);
        return true;
    }

    // Caso d'uso: Aggiornamento allenamento consigliato (admin)
    public boolean updateAllenamentoConsigliato(Long id, Allenamento datiAggiornati) {
        Allenamento allenamentoEsistente = this.findById(id);
        if (allenamentoEsistente == null || Boolean.FALSE.equals(allenamentoEsistente.getIsPubblico())) {
            return false;
        }

        allenamentoEsistente.setNome(datiAggiornati.getNome());
        allenamentoEsistente.setTipoSport(datiAggiornati.getTipoSport());
        allenamentoEsistente.setData(datiAggiornati.getData());
        allenamentoEsistente.setDurata(datiAggiornati.getDurata());
        allenamentoEsistente.setLivelloDifficolta(datiAggiornati.getLivelloDifficolta());
        allenamentoEsistente.setDescrizione(datiAggiornati.getDescrizione());
        allenamentoEsistente.setIsPubblico(true);

        this.allenamentoRepository.save(allenamentoEsistente);
        return true;
    }
    
    @Transactional(readOnly = true)
    public List<Allenamento> findByIsPubblicoTrue() {
        return this.allenamentoRepository.findByIsPubblicoTrue();
    }
    
    @Transactional(readOnly=true)
    public List<Object[]> findAllenamentiWithRecensioni(){
    	return this.allenamentoRepository.findAllenamentiWithRecensioni();
    }
}