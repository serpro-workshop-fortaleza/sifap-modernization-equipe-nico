package br.gov.sifap.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * REQ-008 — faixa etaria de elegibilidade. Zero significa ausencia de limite
 * (SOCPROG.ddm:L57-L58, {@code 0=NONE}), entao a comparacao so vale com ambos definidos.
 */
@Embeddable
public record FaixaEtaria(
        @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "idade_minima", nullable = false) int minima,
        @JdbcTypeCode(SqlTypes.SMALLINT) @Column(name = "idade_maxima", nullable = false) int maxima) {

    private static final int SEM_LIMITE = 0;

    public FaixaEtaria {
        if (minima < SEM_LIMITE) {
            throw new DadoInvalidoException("idadeMinima", "a idade minima nao pode ser negativa");
        }
        if (maxima < SEM_LIMITE) {
            throw new DadoInvalidoException("idadeMaxima", "a idade maxima nao pode ser negativa");
        }
        if (minima != SEM_LIMITE && maxima != SEM_LIMITE && minima > maxima) {
            throw new DadoInvalidoException("idadeMinima",
                    "a idade minima nao pode ser maior que a idade maxima");
        }
    }

    public static FaixaEtaria semLimite() {
        return new FaixaEtaria(SEM_LIMITE, SEM_LIMITE);
    }

    public boolean temLimiteInferior() {
        return minima != SEM_LIMITE;
    }

    public boolean temLimiteSuperior() {
        return maxima != SEM_LIMITE;
    }
}
