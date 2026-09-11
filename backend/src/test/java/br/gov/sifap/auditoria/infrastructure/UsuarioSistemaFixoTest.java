package br.gov.sifap.auditoria.infrastructure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UsuarioSistemaFixoTest {

    @Test
    void should_return_the_provisional_system_user() { // REQ-014
        String usuario = new UsuarioSistemaFixo().obter();

        assertAll(
                () -> assertThat(usuario).isEqualTo("SIFAPSYS"),
                () -> assertThat(usuario).hasSize(8));
    }
}
