package br.gov.sifap.auditoria.infrastructure;

import br.gov.sifap.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * REQ-014 — a trilha e imutavel: nem alteracao nem remocao sao aceitas, e a regra
 * vive na trigger do banco, nao na disciplina da aplicacao.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TrilhaDeAuditoriaImutavelTest {

    private final JdbcTemplate jdbc;

    TrilhaDeAuditoriaImutavelTest(@Autowired JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private long registrarEvento() {
        return jdbc.queryForObject("""
                INSERT INTO evento_auditoria (
                    data_evento, hora_evento, marca_temporal, codigo_acao,
                    modulo_origem, descricao_acao, tipo_entidade, id_entidade, usuario)
                VALUES (CURRENT_DATE, LOCALTIME, LOCALTIMESTAMP, 'IN',
                        'CADPROG', 'Inclusao de programa social', 'PROG', '0001', 'SIFAPSYS')
                RETURNING id
                """, Long.class);
    }

    @Test
    void should_reject_update_when_event_is_already_recorded() { // REQ-014
        long id = registrarEvento();

        DataAccessException erro = assertThrows(DataAccessException.class, () ->
                jdbc.update("UPDATE evento_auditoria SET descricao_acao = ? WHERE id = ?", "adulterado", id));

        assertThat(erro).hasMessageContaining("imutavel");
        assertThat(jdbc.queryForObject(
                "SELECT descricao_acao FROM evento_auditoria WHERE id = ?", String.class, id))
                .isEqualTo("Inclusao de programa social");
    }

    @Test
    void should_reject_delete_when_event_is_already_recorded() { // REQ-014
        long id = registrarEvento();

        DataAccessException erro = assertThrows(DataAccessException.class, () ->
                jdbc.update("DELETE FROM evento_auditoria WHERE id = ?", id));

        assertThat(erro).hasMessageContaining("imutavel");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM evento_auditoria WHERE id = ?", Long.class, id))
                .isEqualTo(1L);
    }
}
