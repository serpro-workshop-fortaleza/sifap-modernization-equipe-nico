package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VigenciaTest {

    private static final LocalDate CRIACAO = LocalDate.of(2026, 1, 15);

    @Test
    void should_report_open_ended_validity_when_closure_date_is_absent() { // REQ-004
        Vigencia vigencia = Vigencia.de(CRIACAO, null);

        assertAll(
                () -> assertThat(vigencia.indeterminada()).isTrue(),
                () -> assertThat(vigencia.encerramento()).isEmpty(),
                () -> assertThat(vigencia.criacao()).isEqualTo(CRIACAO));
    }

    @Test
    void should_accept_closure_date_when_it_is_not_before_creation() { // REQ-009
        Vigencia vigencia = Vigencia.de(CRIACAO, CRIACAO);

        assertAll(
                () -> assertThat(vigencia.indeterminada()).isFalse(),
                () -> assertThat(vigencia.encerramento()).contains(CRIACAO));
    }

    @Test
    void should_reject_closure_date_when_it_precedes_creation() { // REQ-009
        LocalDate anterior = CRIACAO.minusDays(1);

        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> Vigencia.de(CRIACAO, anterior));

        assertThat(erro.campo()).isEqualTo("dataEncerramento");
    }

    @Test
    void should_reject_validity_when_creation_date_is_absent() { // REQ-009
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> Vigencia.de(null, CRIACAO));

        assertThat(erro.campo()).isEqualTo("dataCriacao");
    }
}
