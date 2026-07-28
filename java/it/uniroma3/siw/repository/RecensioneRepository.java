package it.uniroma3.siw.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.Allenamento;


public interface RecensioneRepository extends CrudRepository<Recensione, Long> {
	
    
	
	List<Recensione> findByAllenamento(Allenamento allenamento);
    
    List<Recensione> findByUtente(User utente);

	boolean existsByUtenteAndAllenamento(User utente, Allenamento allenamento);

	@Query("SELECT r FROM Recensione r WHERE r.allenamento.id = :allenamentoId " +
	           "AND r.utente.id = (SELECT c.user.id FROM Credentials c WHERE c.username = :username)")
	    Recensione findByAllenamentoIdAndUsername(@Param("allenamentoId") Long allenamentoId, 
	                                              @Param("username") String username);
    
}
