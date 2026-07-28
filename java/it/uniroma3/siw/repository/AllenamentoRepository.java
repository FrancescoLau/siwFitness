package it.uniroma3.siw.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.User;


public interface AllenamentoRepository extends CrudRepository<Allenamento, Long> {
    
    public List<Allenamento> findByTipoSport(String tipoSport);
    
    public List<Allenamento> findAll();    
       
    
    // Metodo per contare allenamenti per utente
    public long countByUtente(User utente);
    
    public Allenamento save(Allenamento allenamento);
    
    public List<Allenamento> findByUtente(User utente);
    
    public Integer countByIsPubblico(boolean isPubblico);
    
 // Se passi direttamente l'oggetto User
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport);

    // Oppure, se stavi cercando di filtrare per l'ID dell'utente:
    public List<Allenamento> findByUtenteIdAndTipoSport(Long utenteId, String tipoSport);


	public List<Allenamento> findTop3By();

	public Allenamento findByNome(String nome);
	public boolean existsByNome(String nome);
	
	List<Allenamento> findByIsPubblicoTrueAndNomeContainingIgnoreCaseOrIsPubblicoTrueAndDescrizioneContainingIgnoreCase(String nome, String descrizione);

	public List<Allenamento> findByIsPubblicoTrue();
}
