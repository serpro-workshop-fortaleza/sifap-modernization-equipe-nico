package br.gov.sifap.auditoria.application;

import br.gov.sifap.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RegistradorAuditoriaTest {

    private final RegistradorAuditoria registrador;
    private final TransactionTemplate transacao;
    private final JdbcTemplate jdbc;

    @Autowired
    RegistradorAuditoriaTest(RegistradorAuditoria registrador, TransactionTemplate transacao,
                             JdbcTemplate jdbc) {
        this.registrador = registrador;
        this.transacao = transacao;
        this.jdbc = jdbc;
    }

    private long quantidadeDeEventos(long id) {
        return jdbc.queryForObject("SELECT count(*) FROM evento_auditoria WHERE id = ?", Long.class, id);
    }

    @Test
    void should_persist_event_with_the_provisional_user_when_action_is_auditable() { // REQ-014
        long id = registrador.registrar(
                SolicitacaoDeRegistro.inclusaoDePrograma("CADPROG", "0007", "Inclusao de programa social"));

        assertAll(
                () -> assertThat(id).isPositive(),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT usuario FROM evento_auditoria WHERE id = ?", String.class, id))
                        .isEqualTo("SIFAPSYS"),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT codigo_acao FROM evento_auditoria WHERE id = ?", String.class, id))
                        .isEqualTo("IN"),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT tipo_entidade FROM evento_auditoria WHERE id = ?", String.class, id))
                        .isEqualTo("PROG"));
    }

    @Test
    void should_refuse_registration_when_action_is_a_query() { // REQ-016
        SolicitacaoDeRegistro consulta = new SolicitacaoDeRegistro(
                "CO", "CONPROG", "Consulta de programa social", "PROG", "0007", null);

        AcaoNaoAuditavelException erro =
                assertThrows(AcaoNaoAuditavelException.class, () -> registrador.registrar(consulta));

        assertAll(
                () -> assertThat(erro.codigoAcao()).isEqualTo("CO"),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT count(*) FROM evento_auditoria WHERE codigo_acao = 'CO'", Long.class))
                        .isZero());
    }

    @Test
    void should_keep_event_when_the_business_transaction_rolls_back() { // REQ-017
        AtomicLong idRegistrado = new AtomicLong();

        assertThrows(IllegalStateException.class, () -> transacao.execute(status -> {
            idRegistrado.set(registrador.registrar(SolicitacaoDeRegistro.inclusaoDePrograma(
                    "CADPROG", "0008", "Inclusao que sera desfeita")));
            throw new IllegalStateException("falha de negocio simulada apos o registro");
        }));

        assertThat(quantidadeDeEventos(idRegistrado.get())).isEqualTo(1L);
    }
}
