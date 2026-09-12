package it.uniroma3.siw.config;


import java.io.InputStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private AllenamentoService allenamentoService;
    private UserService userService;
    private RecensioneService recensioneService;

    public DataInitializer(AllenamentoService allenamentoService, UserService userService, RecensioneService recensioneService) {
        this.allenamentoService = allenamentoService;
        this.userService = userService;
        this.recensioneService=recensioneService;
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
                
                
                allenamentoService.save(a3);
                
                
                
                User user=this.userService.findByEmail("user@fitness.com");
                if(user!=null) {
                	Allenamento a4=new Allenamento();
                	a4.setUtente(user);
                	a4.setIsPubblico(false);
                	a4.setNome("Allenamento privato di boxe");
                	a4.setDescrizione("Allenamento di Boxe");
                	a4.setDurata(LocalTime.of(2, 00));
                	a4.setData(LocalDate.now());
                	a4.setTipoSport("Boxe");
                	allenamentoService.save(a4);
                	
                	
                	Recensione r1=new Recensione();
                	r1.setUtente(user);
                	r1.setVoto(5);
                	r1.setAllenamento(a1);
                	r1.setTesto("Allenamento super consigliato");
                	r1.setDataCreazione(LocalDateTime.now());
                	recensioneService.save(r1);
                	
                	System.out.println(">> Database inizializzato con 3 allenamenti consigliati");
                	System.out.println(">> Database inizializzato con 1 allenamento privato");
                	System.out.println(">> Database inizializzato con 1 recensione");
                	
            }
            
            }
        }
    }

   
}