package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DominiosDoProgramaTest {

    @ParameterizedTest
    @CsvSource({"A,ASSISTENCIA", "P,PREVIDENCIA", "T,TRABALHO", "a,ASSISTENCIA"})
    void should_map_code_to_program_type_when_it_belongs_to_the_ddm_domain(String codigo, TipoPrograma esperado) { // REQ-007
        assertThat(TipoPrograma.de(codigo)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"X", "1", "AB", ""})
    void should_reject_program_type_when_it_is_outside_the_domain(String codigo) { // REQ-007
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class, () -> TipoPrograma.de(codigo));

        assertThat(erro.campo()).isEqualTo("tipo");
    }

    @Test
    void should_expose_the_legacy_single_character_code() { // REQ-007
        assertAll(
                () -> assertThat(TipoPrograma.ASSISTENCIA.codigo()).isEqualTo('A'),
                () -> assertThat(TipoPrograma.PREVIDENCIA.codigo()).isEqualTo('P'),
                () -> assertThat(TipoPrograma.TRABALHO.codigo()).isEqualTo('T'));
    }

    @ParameterizedTest
    @CsvSource({"A,ATIVO", "I,INATIVO", "E,ENCERRADO"})
    void should_map_code_to_program_status_when_it_belongs_to_the_ddm_domain(String codigo, SituacaoPrograma esperada) { // REQ-003
        assertThat(SituacaoPrograma.de(codigo)).isEqualTo(esperada);
    }

    @Test
    void should_reject_program_status_when_it_is_outside_the_domain() { // REQ-003
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class, () -> SituacaoPrograma.de("Z"));

        assertThat(erro.campo()).isEqualTo("situacao");
    }
}
