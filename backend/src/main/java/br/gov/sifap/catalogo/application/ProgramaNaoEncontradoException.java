package br.gov.sifap.catalogo.application;

/**
 * REQ-012 — nenhum programa com o codigo informado existe no catalogo.
 */
public class ProgramaNaoEncontradoException extends RuntimeException {

    private final String codigo;

    public ProgramaNaoEncontradoException(String codigo) {
        super("programa social nao encontrado: " + codigo);
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
