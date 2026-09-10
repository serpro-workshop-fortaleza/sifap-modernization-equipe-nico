package br.gov.sifap.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * REQ-004 e REQ-009 — vigencia do programa. A ausencia de encerramento e representada
 * por {@code Optional.empty()}, nao pelo zero do legado (SOCPROG.ddm:L36).
 */
@Embeddable
public class Vigencia {

    @Column(name = "data_criacao", nullable = false)
    private LocalDate criacao;

    @Column(name = "data_encerramento")
    private LocalDate encerramento;

    /** Exigido pelo Hibernate; nenhum codigo de aplicacao deve usar. */
    protected Vigencia() {
    }

    private Vigencia(LocalDate criacao, LocalDate encerramento) {
        if (criacao == null) {
            throw new DadoInvalidoException("dataCriacao", "a data de criacao e obrigatoria");
        }
        if (encerramento != null && encerramento.isBefore(criacao)) {
            throw new DadoInvalidoException("dataEncerramento",
                    "a data de encerramento nao pode ser anterior a data de criacao");
        }
        this.criacao = criacao;
        this.encerramento = encerramento;
    }

    public static Vigencia de(LocalDate criacao, LocalDate encerramento) {
        return new Vigencia(criacao, encerramento);
    }

    public static Vigencia indeterminadaDesde(LocalDate criacao) {
        return new Vigencia(criacao, null);
    }

    public LocalDate criacao() {
        return criacao;
    }

    public Optional<LocalDate> encerramento() {
        return Optional.ofNullable(encerramento);
    }

    public boolean indeterminada() {
        return encerramento == null;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Vigencia vigencia
                && Objects.equals(criacao, vigencia.criacao)
                && Objects.equals(encerramento, vigencia.encerramento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criacao, encerramento);
    }
}
