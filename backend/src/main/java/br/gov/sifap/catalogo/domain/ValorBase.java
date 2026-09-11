package br.gov.sifap.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * REQ-005 e REQ-006 — valor base do programa. O teto e o do campo compactado
 * {@code P 7,2} de SOCPROG.ddm:L41. O fator de ajuste nunca e aplicado aqui:
 * o legado o aplicava na inclusao e de novo na folha, por formulas incompativeis.
 */
@Embeddable
public record ValorBase(
        @Column(name = "valor_base", nullable = false, precision = 7, scale = 2) BigDecimal valor) {

    public static final BigDecimal TETO = new BigDecimal("99999.99");
    private static final int CASAS = 2;

    public ValorBase {
        if (valor == null) {
            throw new DadoInvalidoException("valorBase", "o valor base e obrigatorio");
        }
        if (valor.signum() < 0) {
            throw new DadoInvalidoException("valorBase", "o valor base nao pode ser negativo");
        }
        if (valor.compareTo(TETO) > 0) {
            throw new DadoInvalidoException("valorBase",
                    "o valor base nao pode exceder 99.999,99");
        }
        if (valor.stripTrailingZeros().scale() > CASAS) {
            throw new DadoInvalidoException("valorBase",
                    "o valor base admite no maximo duas casas decimais");
        }
        valor = valor.setScale(CASAS, RoundingMode.UNNECESSARY);
    }

    public static ValorBase de(String informado) {
        if (informado == null || informado.isBlank()) {
            throw new DadoInvalidoException("valorBase", "o valor base e obrigatorio");
        }
        return new ValorBase(new BigDecimal(informado.strip()));
    }
}
