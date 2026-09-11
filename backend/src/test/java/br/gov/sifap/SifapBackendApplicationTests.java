package br.gov.sifap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SifapBackendApplicationTests {

    @Test
    void should_load_application_context_when_postgres_is_available() {
    }
}
