package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long>{
	public Optional<Credentials> findByUsername(String username);

	public Credentials save(Credentials credentials);
}
