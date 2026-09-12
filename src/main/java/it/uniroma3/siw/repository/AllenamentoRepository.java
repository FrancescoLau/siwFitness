package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.User;

public interface AllenamentoRepository extends CrudRepository<Allenamento, Long> {
    
    List<Allenamento> findByTipoSport(String tipoSport);
    
    List<Allenamento> findAll();    
    
    Integer countByUtente(User utente);
    
    List<Allenamento> findByUtente(User utente);
    
    Integer countByIsPubblico(boolean isPubblico);
    
    List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport);

    List<Allenamento> findByUtenteIdAndTipoSport(Long utenteId, String tipoSport);

    List<Allenamento> findTop3By();

    Allenamento findByNome(String nome);

    boolean existsByNome(String nome);
    
    List<Allenamento> findByIsPubblicoTrueAndNomeContainingIgnoreCaseOrIsPubblicoTrueAndDescrizioneContainingIgnoreCase(String nome, String descrizione);

    List<Allenamento> findByIsPubblicoTrue();
    
    @Query("SELECT a FROM Allenamento a WHERE a.utente = :utente AND a.isPubblico = false AND " +
           "(LOWER(a.nome) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.descrizione) LIKE LOWER(CONCAT('%', :keyword, '%')))")	
    List<Allenamento> allenamentiRicerca(@Param("utente") User utente, @Param("keyword") String keyword);
    
    @Query("SELECT a, COUNT(r) FROM Allenamento a LEFT JOIN a.recensioni r WHERE a.isPubblico = true GROUP BY a")
    List<Object[]> findAllenamentiWithRecensioni();
    
    @Query("SELECT DISTINCT a FROM Allenamento a LEFT JOIN FETCH a.recensioni WHERE a.isPubblico = true")
    List<Allenamento> findByIsPubblicoTrueWithRecensioni();
}