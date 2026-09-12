package it.uniroma3.siw.model;

import jakarta.annotation.Nullable;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity									
@Table(name = "allenamenti")			
public class Allenamento {
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;																		
    
    @NotBlank							
    @Size(min = 1, max = 100)		
    private String nome;
    
    @NotBlank
    private String tipoSport;
    
    private String livelloDifficolta;
    
    @NotNull
    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
    
    @NotNull
    private LocalTime durata;
    
    @Size(max = 1000)
    private String descrizione;
   @JsonIgnore
    @ManyToOne
    private User utente;
    
    private Boolean isPubblico;
    
    @OneToMany(mappedBy = "allenamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni;

	// Aggiungi i rispettivi Getter e Setter pubblici per entrambi
	public Allenamento() {}
	
    
    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipoSport() { return tipoSport; }
    public void setTipoSport(String tipoSport) { this.tipoSport = tipoSport; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public LocalTime getDurata() { return durata; }
    public void setDurata(LocalTime durata) { this.durata = durata; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public User getUtente() {return utente; }
	public void setUtente(User utente) {this.utente = utente;}	
	
	public List<Recensione> getRecensioni() {return recensioni;}

	public void setRecensioni(List<Recensione> recensioni) {this.recensioni = recensioni;}
	
	public String getLivelloDifficolta() {return livelloDifficolta;}
	
		public void setLivelloDifficolta(String livelloDifficolta) {
			this.livelloDifficolta = livelloDifficolta;
		}
	
		public Boolean getIsPubblico() {
			return isPubblico;
		}
	
		public void setIsPubblico(Boolean isPubblico) {
			this.isPubblico = isPubblico;
		}

	@Override
   	public int hashCode() {
   		return Objects.hash(nome);
   	}

   	@Override
   	public boolean equals(Object obj) {
   		if (this == obj)
   			return true;
   		if (obj == null)
   			return false;
   		if (getClass() != obj.getClass())
   			return false;
   		Allenamento other = (Allenamento) obj;
   		return Objects.equals(nome, other.nome);
   	}

	
}
