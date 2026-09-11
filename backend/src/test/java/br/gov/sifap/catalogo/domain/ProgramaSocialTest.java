package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProgramaSocialTest {

    private static final LocalDate CRIACAO = LocalDate.of(2026, 1, 15);

    private static ProgramaSocial programaCom(String nome, BigDecimal fator) {
        return ProgramaSocial.incluir(
                CodigoPrograma.de("PBF1"),
                nome,
                TipoPrograma.ASSISTENCIA,
                ValorBase.de("600.00"),
                fator,
                "E0001",
                Vigencia.indeterminadaDesde(CRIACAO),
                new BigDecimal("218.00"),
                new FaixaEtaria(0, 17));
    }

    @Test
    void should_start_as_active_when_program_is_created() { // REQ-003
        assertThat(programaCom("Bolsa Familia", BigDecimal.ZERO).situacao())
                .isEqualTo(SituacaoPrograma.ATIVO);
    }

    @Test
    void should_keep_base_amount_untouched_when_adjustment_factor_is_present() { // REQ-005
        ProgramaSocial programa = programaCom("Bolsa Familia", new BigDecimal("0.0500"));

        assertAll(
                () -> assertThat(programa.valorBase().valor()).isEqualByComparingTo("600.00"),
                () -> assertThat(programa.fatorAjuste()).isEqualByComparingTo("0.0500"));
    }

    @Test
    void should_compose_all_value_objects_when_program_is_created() { // REQ-001
        ProgramaSocial programa = programaCom("Bolsa Familia", null);

        assertAll(
                () -> assertThat(programa.codigo().valor()).isEqualTo("PBF1"),
                () -> assertThat(programa.nome()).isEqualTo("Bolsa Familia"),
                () -> assertThat(programa.tipo()).isEqualTo(TipoPrograma.ASSISTENCIA),
                () -> assertThat(programa.codigoElegibilidade()).contains("E0001"),
                () -> assertThat(programa.rendaPercapitaMaxima()).isPresent(),
                () -> assertThat(programa.vigencia().indeterminada()).isTrue(),
                () -> assertThat(programa.fatorAjuste()).isEqualByComparingTo("0"));
    }

    @Test
    void should_reject_program_when_name_is_absent() { // REQ-001
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> programaCom("   ", BigDecimal.ZERO));

        assertThat(erro.campo()).isEqualTo("nome");
    }

    @Test
    void should_reject_program_when_adjustment_factor_is_negative() { // REQ-005
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> programaCom("Bolsa Familia", new BigDecimal("-0.0001")));

        assertThat(erro.campo()).isEqualTo("fatorAjuste");
    }
}
