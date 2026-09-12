package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "recensioni")
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;

    @NotBlank
    @Size(min = 1, max = 500)
    private String testo;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;
    
    
    private LocalDateTime dataModifica;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utente_id", nullable = false)
    private User utente;

    @ManyToOne
    @JoinColumn(name = "allenamento_id", nullable = false) // Mappa esplicitamente la colonna nel DB
    @JsonIgnore
    private Allenamento allenamento;
    
    // Costruttori
    public Recensione() {
        this.dataCreazione = LocalDateTime.now();
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVoto() { return voto; }
    public void setVoto(Integer voto) { this.voto = voto; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    
    public Allenamento getAllenamento() {return allenamento;}
	public void setAllenamento(Allenamento allenamento) {this.allenamento = allenamento;}

	public User getUtente() {
		return utente;
	}

	public void setUtente(User utente) {
		this.utente = utente;
	}

	public Recensione(@NotBlank User utente, Allenamento allenamentoConsigliato) {
		super();
		this.utente = utente;
		this.allenamento = allenamentoConsigliato;
	}

	@Override
	public int hashCode() {
		return Objects.hash(allenamento, utente);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recensione other = (Recensione) obj;
		return Objects.equals(allenamento, other.allenamento) && Objects.equals(utente, other.utente);
	}

	
}
