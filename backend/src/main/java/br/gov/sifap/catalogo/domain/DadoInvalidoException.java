package br.gov.sifap.catalogo.domain;

/**
 * Recusa de dado do programa social na fronteira do dominio. O campo permite que a
 * camada REST informe qual entrada foi recusada, conforme os cenarios de REQ-001.
 */
public class DadoInvalidoException extends RuntimeException {

    private final String campo;

    public DadoInvalidoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String campo() {
        return campo;
    }
}
