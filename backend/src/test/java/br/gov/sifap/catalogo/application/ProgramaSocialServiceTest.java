package br.gov.sifap.catalogo.application;

import br.gov.sifap.TestcontainersConfiguration;
import br.gov.sifap.catalogo.domain.DadoInvalidoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ProgramaSocialServiceTest {

    private static final LocalDate CRIACAO = LocalDate.of(2026, 1, 15);

    private final ProgramaSocialService servico;
    private final JdbcTemplate jdbc;

    @Autowired
    ProgramaSocialServiceTest(ProgramaSocialService servico, JdbcTemplate jdbc) {
        this.servico = servico;
        this.jdbc = jdbc;
    }

    private static InclusaoDeProgramaSocial solicitacao(String codigo, BigDecimal valorBase) {
        return new InclusaoDeProgramaSocial(codigo, "Bolsa Familia", "A", valorBase,
                new BigDecimal("0.0500"), "E0001", CRIACAO, null, new BigDecimal("218.00"), 0, 17);
    }

    private long eventosDoPrograma(String codigo) {
        return jdbc.queryForObject(
                "SELECT count(*) FROM evento_auditoria WHERE tipo_entidade = 'PROG' AND id_entidade = ?",
                Long.class, codigo);
    }

    @Test
    void should_persist_program_as_active_and_audit_the_insertion() { // REQ-001, REQ-015
        ProgramaSocialConsultado incluido = servico.incluir(solicitacao("A001", new BigDecimal("600.00")));

        assertAll(
                () -> assertThat(incluido.codigo()).isEqualTo("A001"),
                () -> assertThat(incluido.situacao()).isEqualTo("A"),
                () -> assertThat(incluido.valorBase()).isEqualByComparingTo("600.00"),
                () -> assertThat(eventosDoPrograma("A001")).isEqualTo(1L),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT cpf_afetado FROM evento_auditoria WHERE id_entidade = 'A001'", String.class))
                        .isNull(),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT modulo_origem FROM evento_auditoria WHERE id_entidade = 'A001'", String.class))
                        .isEqualTo("CADPROG"));
    }

    @Test
    void should_store_the_adjustment_factor_without_applying_it() { // REQ-005
        servico.incluir(solicitacao("A002", new BigDecimal("600.00")));

        assertAll(
                () -> assertThat(jdbc.queryForObject(
                        "SELECT valor_base FROM programa_social WHERE codigo = 'A002'", BigDecimal.class))
                        .isEqualByComparingTo("600.00"),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT fator_ajuste FROM programa_social WHERE codigo = 'A002'", BigDecimal.class))
                        .isEqualByComparingTo("0.0500"));
    }

    @Test
    void should_not_audit_when_the_insertion_is_refused() { // REQ-015
        assertThrows(DadoInvalidoException.class,
                () -> servico.incluir(solicitacao("A003", new BigDecimal("100000.00"))));

        assertAll(
                () -> assertThat(eventosDoPrograma("A003")).isZero(),
                () -> assertThat(jdbc.queryForObject(
                        "SELECT count(*) FROM programa_social WHERE codigo = 'A003'", Long.class))
                        .isZero());
    }

    @Test
    void should_return_only_the_six_published_fields_when_program_exists() { // REQ-011
        servico.incluir(solicitacao("A004", new BigDecimal("600.00")));

        ProgramaSocialConsultado consultado = servico.consultar("A004");

        assertAll(
                () -> assertThat(consultado.nome()).isEqualTo("Bolsa Familia"),
                () -> assertThat(consultado.tipo()).isEqualTo("A"),
                () -> assertThat(consultado.codigoElegibilidade()).isEqualTo("E0001"),
                () -> assertThat(ProgramaSocialConsultado.class.getRecordComponents()).hasSize(6));
    }

    @Test
    void should_not_audit_the_query() { // REQ-016
        servico.incluir(solicitacao("A005", new BigDecimal("600.00")));
        servico.consultar("A005");

        assertThat(eventosDoPrograma("A005")).isEqualTo(1L);
    }

    @Test
    void should_reject_query_when_program_does_not_exist() { // REQ-012
        ProgramaNaoEncontradoException erro =
                assertThrows(ProgramaNaoEncontradoException.class, () -> servico.consultar("9999"));

        assertThat(erro.codigo()).isEqualTo("9999");
    }
}
