package it.uniroma3.siw.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.exception.ImageUploadException;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.AllenamentoRepository;

import jakarta.transaction.Transactional;

@Service
public class AllenamentoService {
    
    
    private AllenamentoRepository allenamentoRepository;
    
    public AllenamentoService(AllenamentoRepository allenamentoRepository) {
		this.allenamentoRepository = allenamentoRepository;
	}
    public Allenamento save(Allenamento allenamento) {
    	return this.allenamentoRepository.save(allenamento);
    }
    @Transactional
    public Allenamento save(Allenamento allenamento, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 1. Crei l'immagine prima del salvataggio
                Immagine img = new Immagine(imageFile.getBytes());
                
                // 2. La associ all'allenamento prima di salvarlo
                allenamento.setImmagine(img);
                img.setAllenamento(allenamento);
                
            } catch (IOException e) {
                throw new ImageUploadException("Errore nel caricamento dell'immagine", e);
            }
        }

        // 3. Salvi un'unica volta nel DB!
        return this.allenamentoRepository.save(allenamento);
    }
    
    
	public Integer countByIsPubblico() {
		return this.allenamentoRepository.countByIsPubblico(true);
	}
    
    public Allenamento findById(Long id) {
        return allenamentoRepository.findById(id).orElse(null);
    }
    
    public List<Allenamento> findByUtente(User utente) {
        return allenamentoRepository.findByUtente(utente);
    }    
    
    public List<Allenamento> findByTipoSport(String tipoSport) {
        return allenamentoRepository.findByTipoSport(tipoSport);
    }
    
    public List<Allenamento> findByUtenteAndTipoSport(User utente, String tipoSport) {
        return allenamentoRepository.findByUtenteAndTipoSport(utente, tipoSport);
    }
    
    
    // Nuovo metodo per contare allenamenti per utente
    public long countByUtente(User utente) {
        return allenamentoRepository.countByUtente(utente);
    }
    
    
    public void deleteById(Long id) {
        allenamentoRepository.deleteById(id);
    }
    
    public List<Allenamento> findAll() {
        return allenamentoRepository.findAll();
    }
 
    
    public int getTotaleRecensioni(Allenamento allenamento) {
        if (allenamento == null || allenamento.getRecensioni() == null) {
            return 0;
        }
        return allenamento.getRecensioni().size();
    }
    
    public double getMediaVoti(Allenamento allenamento) {
        if (allenamento == null || allenamento.getRecensioni() == null || allenamento.getRecensioni().isEmpty()) {
            return 0.0;
        }
        return allenamento.getRecensioni().stream()
                           .mapToInt(Recensione::getVoto)
                           .average()
                           .orElse(0.0);
    }
    
	public List<Allenamento> findTop3By() {
		return allenamentoRepository.findTop3By();
	}

	public Allenamento findByNome(String nome) {
		return allenamentoRepository.findByNome(nome);
	}

	public boolean existsByNome(String nome) {
		return allenamentoRepository.existsByNome(nome);
	}
	
	public List<Allenamento> searchAllenamentiPerNomeODescrizione(String keyword) {
	    if (keyword == null || keyword.isBlank()) {
	        return this.allenamentoRepository.findByIsPubblicoTrue(); // Oppure il metodo che restituisce tutti i pubblici
	    }
	    return this.allenamentoRepository.findByIsPubblicoTrueAndNomeContainingIgnoreCaseOrIsPubblicoTrueAndDescrizioneContainingIgnoreCase(keyword, keyword);
	}
}
