package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {

    List<Recensione> findByAllenamento(Allenamento allenamento);
    
    List<Recensione> findByUtente(User utente);

    boolean existsByUtenteAndAllenamento(User utente, Allenamento allenamento);

    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.allenamento = :allenamento")
    Double findMediaVotiByAllenamento(@Param("allenamento") Allenamento allenamento);

    Long countByAllenamento(Allenamento allenamento);

    @Query("SELECT r FROM Recensione r JOIN Credentials c ON c.user = r.utente WHERE r.allenamento.id = :allenamentoId AND c.username = :username")
    Optional<Recensione> findByAllenamentoIdAndUsername(@Param("allenamentoId") Long allenamentoId, @Param("username") String username);
}
