package br.gov.sifap.catalogo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodigoProgramaTest {

    @Test
    void should_accept_code_with_letters_when_it_has_four_positions() { // REQ-002
        assertThat(CodigoPrograma.de("PBF1").valor()).isEqualTo("PBF1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"7", "07", "007", "0007"})
    void should_pad_with_leading_zeros_when_code_is_numeric(String informado) { // REQ-002
        assertThat(CodigoPrograma.de(informado).valor()).isEqualTo("0007");
    }

    @Test
    void should_preserve_code_when_it_is_already_normalized() { // REQ-002
        assertThat(CodigoPrograma.de(" 1234 ").valor()).isEqualTo("1234");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ABCDE", "AB", "12345", "AB C", "AB-1"})
    void should_reject_code_when_length_or_charset_is_invalid(String informado) { // REQ-002
        DadoInvalidoException erro = assertThrows(DadoInvalidoException.class,
                () -> CodigoPrograma.de(informado));

        assertThat(erro.campo()).isEqualTo("codigo");
    }

    @Test
    void should_reject_code_when_it_is_absent() { // REQ-002
        assertThrows(DadoInvalidoException.class, () -> CodigoPrograma.de(null));
    }
}
