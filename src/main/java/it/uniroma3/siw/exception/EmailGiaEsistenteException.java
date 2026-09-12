package it.uniroma3.siw.exception;

public class EmailGiaEsistenteException extends RuntimeException {

    public EmailGiaEsistenteException(String email) {
        super("L'email "+email +" risulta gia registrata");
    }
}