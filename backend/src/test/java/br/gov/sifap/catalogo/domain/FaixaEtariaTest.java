package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FaixaEtariaTest {

    @Test
    void should_report_no_boundary_when_both_limits_are_zero() { // REQ-008
        FaixaEtaria faixa = FaixaEtaria.semLimite();

        assertAll(
                () -> assertThat(faixa.temLimiteInferior()).isFalse(),
                () -> assertThat(faixa.temLimiteSuperior()).isFalse());
    }

    @ParameterizedTest
    @CsvSource({"0,65", "16,0", "16,65", "18,18"})
    void should_accept_range_when_zero_means_no_boundary(int minima, int maxima) { // REQ-008
        assertThat(new FaixaEtaria(minima, maxima)).isNotNull();
    }

    @Test
    void should_reject_range_when_minimum_exceeds_maximum() { // REQ-008
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> new FaixaEtaria(65, 16));

        assertThat(erro.campo()).isEqualTo("idadeMinima");
    }

    @ParameterizedTest
    @CsvSource({"-1,0,idadeMinima", "0,-1,idadeMaxima"})
    void should_reject_range_when_a_limit_is_negative(int minima, int maxima, String campo) { // REQ-008
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> new FaixaEtaria(minima, maxima));

        assertThat(erro.campo()).isEqualTo(campo);
    }
}
