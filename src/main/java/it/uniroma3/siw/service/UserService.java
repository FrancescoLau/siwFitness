package it.uniroma3.siw.service;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.exception.EmailGiaEsistenteException;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    
    public User saveUser(User user) {
    	if(this.userRepository.existsByEmail(user.getEmail())) {
    		throw new EmailGiaEsistenteException(user.getEmail());
    	}
    	return this.userRepository.save(user);
    }
    
 // Dentro il tuo UserService.java
    public User registraUtente(User utente) {
        
        return userRepository.save(utente); 
    }
    
 // Dentro il tuo UserService.java
    public User findById(Long id) {
         
        Optional<User> utente = userRepository.findById(id);                
        return utente.orElse(null); 
    }


	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}


	public boolean existsByEmail(String email) {
		return this.userRepository.existsByEmail(email);
	}
	
}
