package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValorBaseTest {

    @Test
    void should_keep_the_amount_exactly_as_informed() { // REQ-005
        assertThat(ValorBase.de("600.00").valor()).isEqualByComparingTo("600.00");
    }

    @Test
    void should_accept_the_representable_ceiling() { // REQ-006
        assertThat(ValorBase.de("99999.99").valor()).isEqualByComparingTo("99999.99");
    }

    @ParameterizedTest
    @ValueSource(strings = {"100000.00", "99999999.99"})
    void should_reject_amount_when_it_exceeds_the_ceiling(String informado) { // REQ-006
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class, () -> ValorBase.de(informado));

        assertThat(erro)
                .hasMessageContaining("99.999,99");
        assertThat(erro.campo()).isEqualTo("valorBase");
    }

    @Test
    void should_reject_amount_when_it_is_negative() { // REQ-006
        assertThrows(DadoInvalidoException.class, () -> ValorBase.de("-0.01"));
    }

    @Test
    void should_reject_amount_when_it_has_more_than_two_decimals() { // REQ-005
        assertThrows(DadoInvalidoException.class, () -> ValorBase.de("600.005"));
    }

    @Test
    void should_normalize_scale_to_two_decimals() { // REQ-005
        assertThat(new ValorBase(new BigDecimal("600")).valor().scale()).isEqualTo(2);
    }
}
