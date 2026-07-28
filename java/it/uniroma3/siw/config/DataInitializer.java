package it.uniroma3.siw.config;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AllenamentoService allenamentoService;
    private final UserService userService;

    public DataInitializer(AllenamentoService allenamentoService, UserService userService) {
        this.allenamentoService = allenamentoService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        if (allenamentoService.countByIsPubblico() == 0) {
            
            User admin = userService.findByEmail("admin@fitness.com");
            
            if (admin != null) {
            
                // 1. Allenamento Principiante
                Allenamento a1 = new Allenamento();
                a1.setNome("Circuito Brucia Grassi Full Body");
                a1.setTipoSport("Cardio");
                a1.setData(LocalDate.now());
                a1.setDurata(LocalTime.of(0, 30));
                a1.setDescrizione("Un circuito rapido ad alta intensità ideale per chi ricomincia ad allenarsi. 4 giri: 30s lavoro, 30s recupero.");
                a1.setIsPubblico(true);
                a1.setUtente(admin);
                a1.setLivelloDifficolta("Principiante");
                
                byte[] bytes1 = loadImageBytes("/images/cardio.jpg");
                if (bytes1 != null) {
                    a1.setImmagine(new Immagine(bytes1));
                }
                allenamentoService.save(a1);

                // 2. Allenamento Intermedio
                Allenamento a2 = new Allenamento();
                a2.setNome("Sviluppo Forza e Ipertrofia: Spinta");
                a2.setTipoSport("Forza");
                a2.setData(LocalDate.now());
                a2.setDurata(LocalTime.of(1, 0));
                a2.setDescrizione("Focus su Petto, Spalle e Tricipiti. Esercizi multiarticolari fondamentali con recuperi completi di 2 minuti.");
                a2.setIsPubblico(true);
                a2.setUtente(admin);
                a2.setLivelloDifficolta("Intermedio");
                
                byte[] bytes2 = loadImageBytes("/images/forza.jpg");
                if (bytes2 != null) {
                    a2.setImmagine(new Immagine(bytes2));
                }
                allenamentoService.save(a2);

                // 3. Allenamento Avanzato
                Allenamento a3 = new Allenamento();
                a3.setNome("Resistenza Estrema e Corsa");
                a3.setTipoSport("Corsa");
                a3.setData(LocalDate.now());
                a3.setDurata(LocalTime.of(1, 30));
                a3.setDescrizione("Sessione avanzata di corsa con variazioni di ritmo e pendenza. Ottimo per la preparazione atletica.");
                a3.setIsPubblico(true);   
                a3.setUtente(admin);
                a3.setLivelloDifficolta("Avanzato");
                
                byte[] bytes3 = loadImageBytes("/images/corsa.jpg");
                if (bytes3 != null) {
                    a3.setImmagine(new Immagine(bytes3));
                }
                allenamentoService.save(a3);
                
                System.out.println(">> Database inizializzato con 3 allenamenti consigliati e immagini salvate!");
            }
        }
    }

    /*Metodo per caricare le 3 immagini */
    private byte[] loadImageBytes(String path) {
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        
        if (is == null && !resourcePath.startsWith("static/")) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/" + resourcePath);
        }

        if (is == null) {
            is = getClass().getResourceAsStream(path.startsWith("/") ? path : "/" + path);
        }

        try (InputStream stream = is) {
            if (stream != null) {
                byte[] bytes = stream.readAllBytes();
                System.out.println(">>> OK: Immagine caricata " + path + " (" + bytes.length + " byte)");
                return bytes;
            } else {
                System.err.println(">>> ERRORE: File non trovato nel classpath: " + path);
            }
        } catch (Exception e) {
            System.err.println(">>> ERRORE durante la lettura: " + path);
        }
        return null;
    }
}