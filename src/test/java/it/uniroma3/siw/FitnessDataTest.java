package it.uniroma3.siw;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.repository.AllenamentoRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class FitnessDataTest {

    @Autowired
    private AllenamentoRepository allenamentoRepository;

    @Autowired
    private EntityManager entityManager;

    private Statistics stats;

    @BeforeEach
    public void setup() {
        Session session = this.entityManager.unwrap(Session.class);
        this.stats = session.getSessionFactory().getStatistics();
        this.stats.setStatisticsEnabled(true);
    }

    @Test
    @DisplayName("Test comparativo accesso dati: LAZY vs JOIN FETCH")
    public void testBenchmarkAllenamentiRecensioni() {
        System.out.println("\n=== Test accesso agli allenamenti con recensioni ===");

        // --- Strategia 1: LAZY (Problema N+1) ---
        this.stats.clear();
        this.entityManager.clear(); // Svuota la cache di primo livello

        long startLazy = System.currentTimeMillis();
        List<Allenamento> listLazy = this.allenamentoRepository.findByIsPubblicoTrue();
        int recensioniLazy = 0;
        for (Allenamento a : listLazy) {
            recensioniLazy += a.getRecensioni().size(); // Triggera le query aggiuntive N
        }
        long tempoLazy = System.currentTimeMillis() - startLazy;
        long queryLazy = this.stats.getPrepareStatementCount();

        System.out.println("Strategia 1: LAZY");
        System.out.println("Allenamenti caricati: " + listLazy.size());
        System.out.println("Recensioni totali: " + recensioniLazy);
        System.out.println("Query SQL: " + queryLazy);
        System.out.println("Tempo: " + tempoLazy + " ms");
        System.out.println("--------------------------------------------------");

        // --- Strategia 2: JOIN FETCH ---
        this.stats.clear();
        this.entityManager.clear(); // Reset cache di primo livello per parità di condizioni

        long startFetch = System.currentTimeMillis();
        List<Allenamento> listFetch = this.allenamentoRepository.findByIsPubblicoTrueWithRecensioni();
        int recensioniFetch = 0;
        for (Allenamento a : listFetch) {
            recensioniFetch += a.getRecensioni().size(); // Dati già caricati nella query di join
        }
        long tempoFetch = System.currentTimeMillis() - startFetch;
        long queryFetch = this.stats.getPrepareStatementCount();

        System.out.println("Strategia 2: JOIN FETCH");
        System.out.println("Allenamenti caricati: " + listFetch.size());
        System.out.println("Recensioni totali: " + recensioniFetch);
        System.out.println("Query SQL: " + queryFetch);
        System.out.println("Tempo: " + tempoFetch + " ms");
        System.out.println("==================================================\n");
    }
}