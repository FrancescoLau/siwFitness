package it.uniroma3.siw.model;

import java.util.Base64;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "immagini")
public class Immagine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "dati")
    private byte[] dati;

    @OneToOne(mappedBy = "immagine")
    @JsonIgnore
    private Allenamento allenamento;

    public Immagine() {}

    public Immagine(byte[] dati) {
        this.dati = dati;
    }

    /**
     * Getter dinamico annotato con @Transient:
     * Converte l'array di byte in una stringa Data URL Base64 
     * leggibile direttamente dal tag <img> in Thymeleaf.
     */
    @Transient
    public String getUrl() {
        if (this.dati != null && this.dati.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(this.dati);
            return "data:image/jpeg;base64," + base64;
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDati() {
        return dati;
    }

    public void setDati(byte[] dati) {
        this.dati = dati;
    }

    public Allenamento getAllenamento() {
        return allenamento;
    }

    public void setAllenamento(Allenamento allenamento) {
        this.allenamento = allenamento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Immagine other = (Immagine) obj;
        return Objects.equals(id, other.id);
    }
}