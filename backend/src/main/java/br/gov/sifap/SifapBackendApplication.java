package br.gov.sifap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class SifapBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SifapBackendApplication.class, args);
    }

    /** Relogio injetavel: permite congelar o tempo nos testes da trilha de auditoria. */
    @Bean
    public Clock relogio() {
        return Clock.systemDefaultZone();
    }
}
