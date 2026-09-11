package br.gov.sifap.auditoria.application;

import br.gov.sifap.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ConcorrenciaDaTrilhaDeAuditoriaTest {

    private final RegistradorAuditoria registrador;

    @Autowired
    ConcorrenciaDaTrilhaDeAuditoriaTest(RegistradorAuditoria registrador) {
        this.registrador = registrador;
    }

    @Test
    void should_assign_distinct_and_increasing_identifiers_when_two_events_race() throws Exception { // REQ-014
        long anterior = registrador.registrar(
                SolicitacaoDeRegistro.inclusaoDePrograma("CADPROG", "0100", "Evento de referencia"));

        CountDownLatch largada = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<Long> primeiro = executor.submit(() -> {
                largada.await();
                return registrador.registrar(SolicitacaoDeRegistro.inclusaoDePrograma(
                        "CADPROG", "0101", "Inclusao concorrente 1"));
            });
            Future<Long> segundo = executor.submit(() -> {
                largada.await();
                return registrador.registrar(SolicitacaoDeRegistro.inclusaoDePrograma(
                        "CADPROG", "0102", "Inclusao concorrente 2"));
            });

            largada.countDown();
            long id1 = primeiro.get(30, TimeUnit.SECONDS);
            long id2 = segundo.get(30, TimeUnit.SECONDS);

            assertAll(
                    () -> assertThat(id1).isNotEqualTo(id2),
                    () -> assertThat(id1).isGreaterThan(anterior),
                    () -> assertThat(id2).isGreaterThan(anterior));
        }
    }
}
